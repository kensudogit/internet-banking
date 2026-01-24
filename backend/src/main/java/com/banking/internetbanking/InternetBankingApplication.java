package com.banking.internetbanking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.core.env.Environment;

/**
 * インターネットバンキングアプリケーションのメインクラス
 * 
 * Spring Bootアプリケーションのエントリーポイントです。
 * アプリケーションの起動と自動設定を行います。
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class InternetBankingApplication {

    private static final Logger logger = LoggerFactory.getLogger(InternetBankingApplication.class);

    /**
     * アプリケーションのメインメソッド
     * 
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        try {
            logger.info("=== Internet Banking Application 起動開始 ===");
            logger.info("Java バージョン: {}", System.getProperty("java.version"));
            logger.info("作業ディレクトリ: {}", System.getProperty("user.dir"));
            
            var app = new SpringApplication(InternetBankingApplication.class);
            var context = app.run(args);
            var env = context.getEnvironment();
            
            logger.info("=== アプリケーション起動成功 ===");
            logger.info("サーバーポート: {}", env.getProperty("server.port", "8080"));
            logger.info("データベースURL: {}", maskPassword(env.getProperty("spring.datasource.url", "未設定")));
            logger.info("アプリケーション名: {}", env.getProperty("spring.application.name", "未設定"));
            
        } catch (Exception e) {
            logger.error("=== アプリケーション起動失敗 ===", e);
            System.err.println("アプリケーションの起動に失敗しました: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * パスワードをマスクする（ログ出力用）
     */
    private static String maskPassword(String url) {
        if (url == null || url.isEmpty()) {
            return "未設定";
        }
        // パスワード部分をマスク
        return url.replaceAll("password=[^&]*", "password=***");
    }
}
