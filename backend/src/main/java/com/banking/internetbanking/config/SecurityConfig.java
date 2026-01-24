package com.banking.internetbanking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * セキュリティ設定クラス
 * 
 * Spring Securityの設定を行います。
 * パスワードエンコーダー、セキュリティフィルターチェーン、CORS設定を定義します。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * パスワードエンコーダーのBean定義
     * BCryptアルゴリズムを使用してパスワードをハッシュ化します。
     * 
     * @return BCryptPasswordEncoderインスタンス（強度12）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * セキュリティフィルターチェーンの設定
     * CORS設定、CSRF無効化、セッション管理、認可設定を行います。
     * 
     * @param http HttpSecurityオブジェクト
     * @return SecurityFilterChainインスタンス
     * @throws Exception 設定エラー
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/**").permitAll() // 開発環境ではすべてのAPIを許可
                        .anyRequest().permitAll()); // 開発環境ではすべてを許可

        return http.build();
    }

    /**
     * CORS設定ソースのBean定義
     * 環境変数から許可するオリジンを取得し、CORS設定を構成します。
     * 
     * @return CorsConfigurationSourceインスタンス
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 環境変数から許可するオリジンを取得（カンマ区切り）
        String allowedOrigins = System.getenv("CORS_ALLOWED_ORIGINS");
        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        } else {
            // デフォルト: ローカル開発環境とRailwayの一般的なドメイン
            configuration.setAllowedOrigins(Arrays.asList(
                    "http://localhost:3000",
                    "https://*.railway.app",
                    "https://*.vercel.app"));
        }

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
