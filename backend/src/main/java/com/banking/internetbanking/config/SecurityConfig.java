package com.banking.internetbanking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * セキュリティ設定クラス
 * 
 * Spring Securityの設定を行います。
 * パスワードエンコーダー、セキュリティフィルターチェーンを定義します。
 * CORS設定はCorsFilterで処理します。
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
     * CORS設定ソースのBean定義
     * Spring Securityで使用するCORS設定を提供します。
     * 環境変数CORS_ALLOWED_ORIGINSから許可するオリジンを読み込みます。
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 環境変数から許可するオリジンを取得
        String allowedOriginsEnv = System.getenv("CORS_ALLOWED_ORIGINS");
        if (allowedOriginsEnv != null && !allowedOriginsEnv.isEmpty()) {
            // カンマ区切りで分割し、前後の空白を削除
            String[] origins = Arrays.stream(allowedOriginsEnv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);
            configuration.setAllowedOriginPatterns(Arrays.asList(origins));
            System.out.println("=== CORS設定（環境変数から）===");
            System.out.println("  Allowed Origin Patterns: " + Arrays.toString(origins));
        } else {
            // 環境変数が設定されていない場合、すべてのオリジンを許可（開発用）
            configuration.setAllowedOriginPatterns(Arrays.asList("*"));
            System.out.println("=== CORS設定（デフォルト：すべて許可）===");
            System.out.println("  Allowed Origin Patterns: * (すべてのオリジンを許可)");
        }
        
        // すべてのメソッドを許可
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        
        // すべてのヘッダーを許可（明示的に指定）
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin",
                "Access-Control-Request-Method", "Access-Control-Request-Headers",
                "X-CSRF-TOKEN", "Cache-Control", "Pragma", "If-Modified-Since",
                "If-None-Match", "ETag", "Last-Modified", "X-Auth-Token"));
        
        // 認証情報を許可しない（すべてのオリジンを許可する場合）
        configuration.setAllowCredentials(false);
        
        // プリフライトリクエストのキャッシュ時間（1時間）
        configuration.setMaxAge(3600L);
        
        // 公開するヘッダー
        configuration.setExposedHeaders(Arrays.asList(
                "Content-Type", "Authorization", "X-Requested-With", "Access-Control-Allow-Origin"));
        
        System.out.println("  Allowed Methods: " + configuration.getAllowedMethods());
        System.out.println("  Allowed Headers: " + configuration.getAllowedHeaders());
        System.out.println("  Allow Credentials: " + configuration.getAllowCredentials());
        System.out.println("  Max Age: " + configuration.getMaxAge());
        System.out.println("===========================");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    /**
     * セキュリティフィルターチェーンの設定
     * 認証処理を完全に無効化し、すべてのリクエストを許可します。
     * CORS設定はcorsConfigurationSource()で定義された設定を使用します。
     * 
     * @param http HttpSecurityオブジェクト
     * @return SecurityFilterChainインスタンス
     * @throws Exception 設定エラー
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS設定を有効化
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CSRFを無効化
                .csrf(csrf -> csrf.disable())
                // セッション管理を無効化（STATELESS）
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 認証を完全に無効化
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                // 認証例外ハンドラーを無効化（認証画面を表示しない）
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.OK)))
                // すべてのリクエストを許可（認証不要）
                .authorizeHttpRequests(authz -> authz
                        // OPTIONSリクエスト（プリフライト）を最優先で許可
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // すべてのリクエストを許可
                        .anyRequest().permitAll());

        return http.build();
    }
}
