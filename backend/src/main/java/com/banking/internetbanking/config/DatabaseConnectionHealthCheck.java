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

        try {
            // 接続情報の取得
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();

                logger.info("データベース接続情報:");
                logger.info("  - データベース製品: {}", metaData.getDatabaseProductName());
                logger.info("  - データベースバージョン: {}", metaData.getDatabaseProductVersion());
                logger.info("  - ドライバー名: {}", metaData.getDriverName());
                logger.info("  - ドライバーバージョン: {}", metaData.getDriverVersion());
                logger.info("  - URL: {}", metaData.getURL());
                logger.info("  - ユーザー名: {}", metaData.getUserName());

                // 接続テスト
                jdbcTemplate.execute("SELECT 1");
                logger.info("✓ データベース接続テスト成功");

                // 現在のデータベース名を取得
                String databaseName = connection.getCatalog();
                logger.info("  - 現在のデータベース: {}", databaseName);

                // テーブル一覧の確認
                String tableCountQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public'";
                Integer tableCount = jdbcTemplate.queryForObject(tableCountQuery, Integer.class);
                logger.info("  - 既存テーブル数: {}", tableCount != null ? tableCount : 0);

            } catch (Exception e) {
                logger.error("✗ データベース接続エラー: {}", e.getMessage(), e);
                throw e;
            }

            logger.info("=== データベース接続ヘルスチェック完了 ===");

        } catch (Exception e) {
            logger.error("データベース接続ヘルスチェックに失敗しました", e);
            // エラーが発生してもアプリケーションは起動を続ける（Railway環境では接続が遅延する場合がある）
        }
    }
}
