package com.banking.internetbanking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import java.util.List;
import java.util.stream.Collectors;

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
                // CORS設定: UrlBasedCorsConfigurationSourceがBeanとして存在する場合、自動的に使用されます
                // CORSは認証フィルターの前に処理される必要があります
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // OPTIONSリクエスト（preflight）を明示的に許可
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
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
     * Spring Security 6では、UrlBasedCorsConfigurationSourceをBeanとして返す必要があります。
     * 
     * @return UrlBasedCorsConfigurationSourceインスタンス
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 環境変数から許可するオリジンを取得（カンマ区切り）
        String allowedOrigins = System.getenv("CORS_ALLOWED_ORIGINS");
        System.out.println("=== CORS設定確認 ===");
        System.out.println("CORS_ALLOWED_ORIGINS環境変数: " + (allowedOrigins != null ? allowedOrigins : "未設定"));
        
        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            // カンマ区切りのオリジンをリストに変換（前後の空白を削除）
            List<String> origins = Arrays.asList(allowedOrigins.split(","))
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
            
            configuration.setAllowedOrigins(origins);
            System.out.println("許可するオリジン: " + origins);
        } else {
            // デフォルト: ローカル開発環境とRailwayのフロントエンドURL
            List<String> defaultOrigins = Arrays.asList(
                    "http://localhost:3000",
                    "http://localhost:8080",
                    "https://internet-banking-front-production.up.railway.app",
                    "https://internet-banking-frontend-production.up.railway.app");
            configuration.setAllowedOrigins(defaultOrigins);
            System.out.println("デフォルトオリジンを使用: " + defaultOrigins);
        }

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        // setAllowedHeadersに"*"を設定すると、setAllowCredentials(true)と組み合わせた場合に問題が発生します
        // 代わりに、setAllowedHeaderPatternsを使用してすべてのヘッダーを許可します
        configuration.setAllowedHeaderPatterns(Arrays.asList("*"));
        // 一般的なCORSヘッダーも明示的に許可
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        // デバッグ: 設定内容を確認
        System.out.println("CORS設定詳細:");
        System.out.println("  Allowed Origins: " + configuration.getAllowedOrigins());
        System.out.println("  Allowed Methods: " + configuration.getAllowedMethods());
        System.out.println("  Allowed Headers: " + configuration.getAllowedHeaders());
        System.out.println("  Allowed Header Patterns: " + configuration.getAllowedHeaderPatterns());
        System.out.println("  Allow Credentials: " + configuration.getAllowCredentials());
        System.out.println("  Max Age: " + configuration.getMaxAge());
        System.out.println("===================");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
