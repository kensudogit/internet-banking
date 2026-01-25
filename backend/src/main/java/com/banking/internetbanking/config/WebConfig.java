package com.banking.internetbanking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web設定クラス
 * 
 * Spring MVCの設定を行います。
 * CORS設定をSpring MVCレベルでも追加して、より確実にCORSを処理します。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 環境変数から許可するオリジンを取得（カンマ区切り）
        String allowedOrigins = System.getenv("CORS_ALLOWED_ORIGINS");
        
        String[] origins;
        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            origins = allowedOrigins.split(",");
            // 前後の空白を削除
            for (int i = 0; i < origins.length; i++) {
                origins[i] = origins[i].trim();
            }
        } else {
            // デフォルト: ローカル開発環境とRailwayのフロントエンドURL
            origins = new String[]{
                    "http://localhost:3000",
                    "http://localhost:8080",
                    "https://internet-banking-front-production.up.railway.app",
                    "https://internet-banking-frontend-production.up.railway.app"
            };
        }
        
        System.out.println("=== Spring MVC CORS設定 ===");
        System.out.println("許可するオリジン: " + java.util.Arrays.toString(origins));
        System.out.println("========================");
        
        registry.addMapping("/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
