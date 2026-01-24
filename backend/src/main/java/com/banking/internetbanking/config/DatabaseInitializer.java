package com.banking.internetbanking.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;

/**
 * データベース初期化コンポーネント
 * Railway環境などでスキーマが自動初期化されない場合に使用
 * @PostConstructを使用して、Beanの初期化時に実行される
 * @DependsOn("dataSource")により、DataSourceが初期化された後に実行される
 * Spring Bootの標準SQL初期化機能（spring.sql.init.mode=always）が優先されるが、
 * このコンポーネントはフォールバックとして機能する
 */
@Component
@DependsOn("dataSource")
public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    /** SQL初期化モード（always, never, embedded） */
    @Value("${spring.sql.init.mode:never}")
    private String sqlInitMode;

    /** SQL初期化が有効かどうか */
    @Value("${spring.sql.init.enabled:true}")
    private boolean sqlInitEnabled;

    /** データベース自動初期化が有効かどうか */
    @Value("${app.database.auto-init:false}")
    private boolean autoInit;

    /**
     * コンストラクタ
     * 
     * @param jdbcTemplate JdbcTemplateインスタンス
     * @param dataSource DataSourceインスタンス
     */
    @Autowired
    public DatabaseInitializer(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    /**
     * Beanの初期化時に実行される
     * @PostConstructにより、DataSourceが初期化された後に実行される
     * Spring Bootの標準SQL初期化機能（spring.sql.init.mode=always）が優先されるが、
     * このメソッドはフォールバックとして機能する
     */
    @PostConstruct
    public void initialize() {
        // SPRING_SQL_INIT_MODE=always が設定されている場合、Spring Bootの標準機能が実行される
        // このメソッドは、標準機能が実行されなかった場合のフォールバックとして使用される
        
        // SPRING_SQL_INIT_MODE=always が設定されている場合、このメソッドも実行されるが、
        // テーブルが既に存在する場合はスキップされる
        boolean shouldInitialize = "always".equalsIgnoreCase(sqlInitMode) || autoInit;
        
        if (!shouldInitialize) {
            logger.info("データベース自動初期化は無効です（sqlInitMode={}, autoInit={}）。Spring Bootの標準SQL初期化機能に依存します。", 
                    sqlInitMode, autoInit);
            return;
        }
        
        logger.info("データベース初期化を開始します（sqlInitMode={}, autoInit={}）", 
                sqlInitMode, autoInit);
        
        // 少し待機して、Spring Bootの標準SQL初期化機能が実行される機会を与える
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("待機中に割り込みが発生しました", e);
        }
        
        initializeDatabase();
    }
    
    /**
     * データベース初期化処理（フォールバック用）
     */
    private void initializeDatabase() {

        try {
            // データベース接続の確認（リトライ付き）
            logger.info("データベース接続を確認しています...");
            int maxConnectionRetries = 5;
            long retryDelayMs = 2000;
            boolean connectionEstablished = false;

            for (int attempt = 1; attempt <= maxConnectionRetries; attempt++) {
                try {
                    jdbcTemplate.execute("SELECT 1");
                    logger.info("データベース接続が正常です。");
                    connectionEstablished = true;
                    break;
                } catch (Exception e) {
                    logger.warn("接続確認試行 {}/{} 失敗: {}", attempt, maxConnectionRetries, e.getMessage());
                    if (attempt < maxConnectionRetries) {
                        logger.info("{}秒後に再試行します...", retryDelayMs / 1000);
                        try {
                            Thread.sleep(retryDelayMs);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            logger.error("リトライ待機中に割り込みが発生しました", ie);
                            return;
                        }
                    }
                }
            }

            if (!connectionEstablished) {
                logger.error("データベース接続を確立できませんでした。スキーマ初期化をスキップします。");
                return;
            }

            // テーブルが存在するか確認
            String checkTableQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'users'";
            Integer tableCount = jdbcTemplate.queryForObject(checkTableQuery, Integer.class);

            if (tableCount != null && tableCount > 0) {
                logger.info("データベーススキーマは既に初期化されています（usersテーブルが存在します）。スキップします。");
                return;
            }
            
            logger.info("テーブルが存在しないため、スキーマを初期化します。");

            logger.info("データベーススキーマを初期化しています...");

            // schema.sqlを読み込んで実行
            ClassPathResource schemaResource = new ClassPathResource("schema.sql");
            String schemaSql = StreamUtils.copyToString(schemaResource.getInputStream(), StandardCharsets.UTF_8);

            // SQLを実行（より堅牢な方法）
            // セミコロンで分割し、空でない文のみ実行
            String[] rawStatements = schemaSql.split(";");
            int executedCount = 0;
            int skippedCount = 0;

            for (String rawStatement : rawStatements) {
                String statement = rawStatement.trim();

                // 空の文やコメントのみの文をスキップ
                if (statement.isEmpty() ||
                        statement.startsWith("--") ||
                        statement.matches("^\\s*$")) {
                    continue;
                }

                // 複数行のコメントを除去
                statement = statement.replaceAll("(?s)/\\*.*?\\*/", "").trim();

                if (statement.isEmpty()) {
                    continue;
                }

                try {
                    jdbcTemplate.execute(statement);
                    executedCount++;
                    String statementPreview = statement.length() > 60
                            ? statement.substring(0, 60) + "..."
                            : statement;
                    logger.debug("SQL実行成功 [{}]: {}", executedCount, statementPreview);
                } catch (Exception e) {
                    skippedCount++;
                    String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();

                    // 既に存在するテーブルなどのエラーは無視
                    if (errorMsg.contains("already exists") ||
                            errorMsg.contains("duplicate") ||
                            errorMsg.contains("relation") && errorMsg.contains("already exists")) {
                        logger.debug("オブジェクトは既に存在します（無視）: {}", errorMsg);
                    } else {
                        logger.warn("SQL実行エラー [{}]: {} - SQL: {}", skippedCount, errorMsg,
                                statement.length() > 100 ? statement.substring(0, 100) + "..." : statement);
                    }
                }
            }

            logger.info("データベーススキーマの初期化が完了しました。実行: {}, スキップ: {}", executedCount, skippedCount);

            // サンプルデータの挿入（オプション）
            if (sqlInitEnabled) {
                try {
                    logger.info("サンプルデータの挿入を開始します...");
                    ClassPathResource sampleDataResource = new ClassPathResource("sample-data.sql");
                    String sampleDataSql = StreamUtils.copyToString(sampleDataResource.getInputStream(),
                            StandardCharsets.UTF_8);

                    String[] sampleRawStatements = sampleDataSql.split(";");
                    int dataExecutedCount = 0;
                    int dataSkippedCount = 0;

                    for (String rawStatement : sampleRawStatements) {
                        String statement = rawStatement.trim();

                        if (statement.isEmpty() ||
                                statement.startsWith("--") ||
                                statement.matches("^\\s*$")) {
                            continue;
                        }

                        // 複数行のコメントを除去
                        statement = statement.replaceAll("(?s)/\\*.*?\\*/", "").trim();

                        if (statement.isEmpty()) {
                            continue;
                        }

                        try {
                            jdbcTemplate.execute(statement);
                            dataExecutedCount++;
                            logger.debug("サンプルデータ挿入成功 [{}]", dataExecutedCount);
                        } catch (Exception e) {
                            dataSkippedCount++;
                            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();

                            if (errorMsg.contains("duplicate") ||
                                    errorMsg.contains("already exists") ||
                                    errorMsg.contains("violates unique constraint")) {
                                logger.debug("サンプルデータは既に存在します（無視）: {}", errorMsg);
                            } else {
                                logger.warn("サンプルデータ挿入エラー: {}", errorMsg);
                            }
                        }
                    }

                    logger.info("サンプルデータの挿入が完了しました。実行: {}, スキップ: {}", dataExecutedCount, dataSkippedCount);
                } catch (Exception e) {
                    logger.warn("サンプルデータの挿入に失敗しました: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            logger.error("データベース初期化中にエラーが発生しました", e);
            // エラーが発生してもアプリケーションは起動を続ける
        }
    }
}
