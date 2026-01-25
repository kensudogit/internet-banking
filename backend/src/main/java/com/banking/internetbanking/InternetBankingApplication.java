package com.banking.internetbanking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * インターネットバンキングアプリケーションのメインクラス
 * 
 * Spring Bootアプリケーションのエントリーポイントです。
 * アプリケーションの起動と自動設定を行います。
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class InternetBankingApplication {

    /**
     * アプリケーションのメインメソッド
     * 
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        SpringApplication.run(InternetBankingApplication.class, args);
    }
}
