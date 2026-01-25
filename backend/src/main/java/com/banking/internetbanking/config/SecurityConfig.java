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
        
        // CORS設定を簡素化: すべてのオリジンを許可（開発/テスト用）
        // 注意: 本番環境では特定のオリジンのみを許可することを推奨
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // allowCredentialsをfalseに設定（すべてのオリジンを許可する場合）
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);
        
        System.out.println("=== CORS設定（簡素化版）===");
        System.out.println("  Allowed Origin Patterns: * (すべてのオリジンを許可)");
        System.out.println("  Allowed Methods: " + configuration.getAllowedMethods());
        System.out.println("  Allowed Headers: * (すべてのヘッダーを許可)");
        System.out.println("  Allow Credentials: false");
        System.out.println("  Max Age: " + configuration.getMaxAge());
        System.out.println("===========================");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
