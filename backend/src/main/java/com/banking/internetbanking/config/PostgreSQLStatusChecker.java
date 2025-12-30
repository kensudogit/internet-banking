package com.banking.internetbanking.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * PostgreSQL ステータスチェッカー
 * Actuator のヘルスエンドポイントで使用
 */
@Component
public class PostgreSQLStatusChecker implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(PostgreSQLStatusChecker.class);

    private final JdbcTemplate jdbcTemplate;

    public PostgreSQLStatusChecker(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try {
            // データベース接続テスト
            jdbcTemplate.execute("SELECT 1");

            // PostgreSQL のバージョン情報を取得
            String version = jdbcTemplate.queryForObject("SELECT version()", String.class);

            // データベース名を取得
            String databaseName = jdbcTemplate.queryForObject("SELECT current_database()", String.class);

            // 接続数情報を取得
            Map<String, Object> connectionInfo = jdbcTemplate.queryForMap(
                    "SELECT " +
                            "  (SELECT count(*) FROM pg_stat_activity WHERE state = 'active') as active_connections, " +
                            "  (SELECT setting::int FROM pg_settings WHERE name = 'max_connections') as max_connections, "
                            +
                            "  (SELECT count(*) FROM pg_stat_activity) as total_connections");

            // テーブル数を取得
            Integer tableCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public'",
                    Integer.class);

            // データベースサイズを取得
            String dbSize = jdbcTemplate.queryForObject(
                    "SELECT pg_size_pretty(pg_database_size(current_database()))",
                    String.class);

            return Health.up()
                    .withDetail("status", "UP")
                    .withDetail("database", databaseName)
                    .withDetail("version",
                            version != null ? version.substring(0, Math.min(50, version.length())) : "Unknown")
                    .withDetail("active_connections", connectionInfo.get("active_connections"))
                    .withDetail("total_connections", connectionInfo.get("total_connections"))
                    .withDetail("max_connections", connectionInfo.get("max_connections"))
                    .withDetail("table_count", tableCount != null ? tableCount : 0)
                    .withDetail("database_size", dbSize != null ? dbSize : "Unknown")
                    .build();

        } catch (Exception e) {
            logger.error("PostgreSQL ヘルスチェックに失敗しました", e);
            return Health.down()
                    .withDetail("status", "DOWN")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
