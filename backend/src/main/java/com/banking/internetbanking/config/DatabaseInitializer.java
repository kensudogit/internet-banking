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
            // テーブルが存在するか確認
            String checkTableQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'users'";
            Integer tableCount = jdbcTemplate.queryForObject(checkTableQuery, Integer.class);

            if (tableCount != null && tableCount > 0) {
                logger.info("データベーススキーマは既に初期化されています。");
                return;
            }

            logger.info("データベーススキーマを初期化しています...");

            // schema.sqlを読み込んで実行
            ClassPathResource schemaResource = new ClassPathResource("schema.sql");
            String schemaSql = StreamUtils.copyToString(schemaResource.getInputStream(), StandardCharsets.UTF_8);

            // SQLを実行（複数行のSQL文に対応）
            // セミコロンで分割し、空でない文のみ実行
            String[] statements = schemaSql.split(";");
            StringBuilder currentStatement = new StringBuilder();

            for (String part : statements) {
                String trimmed = part.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }

                currentStatement.append(trimmed);
                if (trimmed.endsWith(";") || !trimmed.isEmpty()) {
                    String sql = currentStatement.toString().trim();
                    if (!sql.isEmpty() && !sql.startsWith("--")) {
                        try {
                            jdbcTemplate.execute(sql);
                            logger.debug("SQL実行成功: {}", sql.substring(0, Math.min(50, sql.length())) + "...");
                        } catch (Exception e) {
                            // 既に存在するテーブルなどのエラーは無視
                            if (e.getMessage() != null &&
                                    (e.getMessage().contains("already exists") ||
                                            e.getMessage().contains("duplicate"))) {
                                logger.debug("テーブルは既に存在します（無視）: {}", e.getMessage());
                            } else {
                                logger.warn("SQL実行エラー: {}", e.getMessage());
                            }
                        }
                    }
                    currentStatement.setLength(0);
                } else {
                    currentStatement.append(" ");
                }
            }

            logger.info("データベーススキーマの初期化が完了しました。");

            // サンプルデータの挿入（オプション）
            if (sqlInitEnabled) {
                try {
                    ClassPathResource sampleDataResource = new ClassPathResource("sample-data.sql");
                    String sampleDataSql = StreamUtils.copyToString(sampleDataResource.getInputStream(),
                            StandardCharsets.UTF_8);
                    String[] sampleStatements = sampleDataSql.split(";");
                    for (String statement : sampleStatements) {
                        String trimmed = statement.trim();
                        if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                            try {
                                jdbcTemplate.execute(trimmed);
                            } catch (Exception e) {
                                logger.debug("サンプルデータ挿入エラー（無視）: {}", e.getMessage());
                            }
                        }
                    }
                    logger.info("サンプルデータの挿入が完了しました。");
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
