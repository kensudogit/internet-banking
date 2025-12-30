package com.banking.internetbanking.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * データベース起動検証コンポーネント
 * アプリケーション起動後にデータベースの状態を詳細に検証
 */
@Component
@Order(3) // DatabaseConnectionHealthCheck と DatabaseInitializer の後に実行
public class DatabaseStartupVerifier implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseStartupVerifier.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseStartupVerifier(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("=== データベース起動検証開始 ===");

        try {
            // 1. データベース接続の確認
            verifyConnection();

            // 2. 必須テーブルの存在確認
            verifyRequiredTables();

            // 3. データベース設定の確認
            verifyDatabaseSettings();

            // 4. 接続プールの状態確認
            verifyConnectionPool();

            logger.info("=== データベース起動検証完了: すべて正常 ===");

        } catch (Exception e) {
            logger.error("データベース起動検証中にエラーが発生しました", e);
        }
    }

    private void verifyConnection() {
        logger.info("1. データベース接続の確認...");
        try {
            String version = jdbcTemplate.queryForObject("SELECT version()", String.class);
            logger.info("   ✓ PostgreSQL バージョン: {}",
                    version != null ? version.substring(0, Math.min(50, version.length())) : "Unknown");
        } catch (Exception e) {
            logger.error("   ✗ データベース接続エラー: {}", e.getMessage());
            throw new RuntimeException("データベース接続の確認に失敗しました", e);
        }
    }

    private void verifyRequiredTables() {
        logger.info("2. 必須テーブルの存在確認...");
        String[] requiredTables = { "users", "accounts", "transactions", "fixed_deposits", "loans", "security_logs" };

        for (String tableName : requiredTables) {
            try {
                String query = "SELECT COUNT(*) FROM information_schema.tables " +
                        "WHERE table_schema = 'public' AND table_name = ?";
                Integer count = jdbcTemplate.queryForObject(query, Integer.class, tableName);

                if (count != null && count > 0) {
                    // テーブルの行数を取得
                    Integer rowCount = jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM " + tableName, Integer.class);
                    logger.info("   ✓ テーブル '{}' が存在します (行数: {})", tableName, rowCount != null ? rowCount : 0);
                } else {
                    logger.warn("   ⚠ テーブル '{}' が存在しません", tableName);
                }
            } catch (Exception e) {
                logger.warn("   ⚠ テーブル '{}' の確認中にエラー: {}", tableName, e.getMessage());
            }
        }
    }

    private void verifyDatabaseSettings() {
        logger.info("3. データベース設定の確認...");
        try {
            // データベース名
            String databaseName = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
            logger.info("   ✓ 現在のデータベース: {}", databaseName);

            // 文字エンコーディング
            String encoding = jdbcTemplate.queryForObject("SHOW server_encoding", String.class);
            logger.info("   ✓ 文字エンコーディング: {}", encoding);

            // タイムゾーン
            String timezone = jdbcTemplate.queryForObject("SHOW timezone", String.class);
            logger.info("   ✓ タイムゾーン: {}", timezone);

            // 最大接続数
            String maxConnections = jdbcTemplate.queryForObject("SHOW max_connections", String.class);
            logger.info("   ✓ 最大接続数: {}", maxConnections);

        } catch (Exception e) {
            logger.warn("   データベース設定の確認中にエラー: {}", e.getMessage());
        }
    }

    private void verifyConnectionPool() {
        logger.info("4. 接続プールの状態確認...");
        try {
            // アクティブな接続数
            Map<String, Object> connectionStats = jdbcTemplate.queryForMap(
                    "SELECT " +
                            "  (SELECT count(*) FROM pg_stat_activity WHERE state = 'active') as active, " +
                            "  (SELECT count(*) FROM pg_stat_activity WHERE state = 'idle') as idle, " +
                            "  (SELECT count(*) FROM pg_stat_activity) as total, " +
                            "  (SELECT setting::int FROM pg_settings WHERE name = 'max_connections') as max");

            Integer active = (Integer) connectionStats.get("active");
            Integer idle = (Integer) connectionStats.get("idle");
            Integer total = (Integer) connectionStats.get("total");
            Integer max = (Integer) connectionStats.get("max");

            logger.info("   ✓ アクティブ接続: {}", active);
            logger.info("   ✓ アイドル接続: {}", idle);
            logger.info("   ✓ 合計接続: {}/{}", total, max);

            if (max != null && total != null && total > max * 0.8) {
                logger.warn("   ⚠ 接続数が最大接続数の80%を超えています");
            }

        } catch (Exception e) {
            logger.warn("   接続プールの状態確認中にエラー: {}", e.getMessage());
        }
    }
}
