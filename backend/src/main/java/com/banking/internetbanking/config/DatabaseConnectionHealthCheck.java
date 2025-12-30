package com.banking.internetbanking.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 * データベース接続ヘルスチェックコンポーネント
 * アプリケーション起動時にデータベース接続を確認
 */
@Component
@Order(1) // DatabaseInitializerより先に実行
public class DatabaseConnectionHealthCheck implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionHealthCheck.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public DatabaseConnectionHealthCheck(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        logger.info("=== データベース接続ヘルスチェック開始 ===");

        int maxRetries = 5;
        long retryDelayMs = 3000; // 3秒
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.info("接続試行 {}/{}", attempt, maxRetries);

                // 接続情報の取得
                try (Connection connection = dataSource.getConnection()) {
                    DatabaseMetaData metaData = connection.getMetaData();

                    logger.info("データベース接続情報:");
                    logger.info("  - データベース製品: {}", metaData.getDatabaseProductName());
                    logger.info("  - データベースバージョン: {}", metaData.getDatabaseProductVersion());
                    logger.info("  - ドライバー名: {}", metaData.getDriverName());
                    logger.info("  - ドライバーバージョン: {}", metaData.getDriverVersion());

                    // URLの一部をマスク（セキュリティのため）
                    String url = metaData.getURL();
                    if (url != null && url.contains("@")) {
                        String maskedUrl = url.substring(0, url.indexOf("@") + 1) + "***";
                        logger.info("  - URL: {}", maskedUrl);
                    } else {
                        logger.info("  - URL: {}", url);
                    }
                    logger.info("  - ユーザー名: {}", metaData.getUserName());

                    // 接続テスト
                    jdbcTemplate.execute("SELECT 1");
                    logger.info("✓ データベース接続テスト成功");

                    // 現在のデータベース名を取得
                    String databaseName = connection.getCatalog();
                    logger.info("  - 現在のデータベース: {}", databaseName);

                    // 接続の詳細情報
                    logger.info("  - 読み取り専用: {}", connection.isReadOnly());
                    logger.info("  - 自動コミット: {}", connection.getAutoCommit());
                    logger.info("  - トランザクション分離レベル: {}", connection.getTransactionIsolation());

                    // テーブル一覧の確認
                    String tableCountQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public'";
                    Integer tableCount = jdbcTemplate.queryForObject(tableCountQuery, Integer.class);
                    logger.info("  - 既存テーブル数: {}", tableCount != null ? tableCount : 0);

                    // 接続成功
                    logger.info("=== データベース接続ヘルスチェック完了 ===");
                    return;

                } catch (Exception e) {
                    lastException = e;
                    logger.warn("接続試行 {} 失敗: {}", attempt, e.getMessage());

                    if (attempt < maxRetries) {
                        logger.info("{}秒後に再試行します...", retryDelayMs / 1000);
                        try {
                            Thread.sleep(retryDelayMs);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            logger.error("リトライ待機中に割り込みが発生しました", ie);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                lastException = e;
                logger.error("接続試行 {} で予期しないエラーが発生しました", attempt, e);

                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(retryDelayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        // すべてのリトライが失敗した場合
        logger.error("データベース接続ヘルスチェックに失敗しました（{}回試行後）", maxRetries);
        if (lastException != null) {
            logger.error("最後のエラー: {}", lastException.getMessage(), lastException);
        }
        logger.warn("アプリケーションは起動を続けますが、データベース接続が確立されていない可能性があります。");
    }
}
