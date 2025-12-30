package com.banking.internetbanking.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

/**
 * データベース初期化コンポーネント
 * Railway環境などでスキーマが自動初期化されない場合に使用
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.sql.init.enabled:false}")
    private boolean sqlInitEnabled;

    @Value("${app.database.auto-init:false}")
    private boolean autoInit;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        if (!autoInit && !sqlInitEnabled) {
            logger.info("データベース自動初期化は無効です。スキップします。");
            return;
        }

        try {
            // データベース接続の確認
            logger.info("データベース接続を確認しています...");
            jdbcTemplate.execute("SELECT 1");
            logger.info("データベース接続が正常です。");

            // テーブルが存在するか確認
            String checkTableQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'users'";
            Integer tableCount = jdbcTemplate.queryForObject(checkTableQuery, Integer.class);

            if (tableCount != null && tableCount > 0) {
                logger.info("データベーススキーマは既に初期化されています（usersテーブルが存在します）。");
                return;
            }

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

                    String[] rawStatements = sampleDataSql.split(";");
                    int dataExecutedCount = 0;
                    int dataSkippedCount = 0;

                    for (String rawStatement : rawStatements) {
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
