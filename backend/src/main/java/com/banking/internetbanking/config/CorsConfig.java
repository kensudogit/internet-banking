package com.banking.internetbanking.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * CORS設定クラス
 * 
 * CORSフィルターとWebMvcConfigurerの両方でCORSを設定します。
 * 二重の対策により、確実にCORSヘッダーが返されるようにします。
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * CORSフィルターを明示的に登録
     * Spring Securityのフィルターチェーンの前に実行されるように設定します。
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // すべてのオリジンを許可
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // すべてのメソッドを許可
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        
        // すべてのヘッダーを許可（明示的に指定）
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin",
                "Access-Control-Request-Method", "Access-Control-Request-Headers",
                "X-CSRF-TOKEN", "Cache-Control", "Pragma", "If-Modified-Since",
                "If-None-Match", "ETag", "Last-Modified"));
        
        // 認証情報を許可しない
        configuration.setAllowCredentials(false);
        
        // プリフライトリクエストのキャッシュ時間
        configuration.setMaxAge(3600L);
        
        // 公開するヘッダー
        configuration.setExposedHeaders(Arrays.asList(
                "Content-Type", "Authorization", "X-Requested-With"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(
                new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        
        return bean;
    }

    /**
     * WebMvcConfigurerでCORS設定を追加
     * コントローラーレベルでもCORSを設定します。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
                .allowedHeaders("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin",
                        "Access-Control-Request-Method", "Access-Control-Request-Headers",
                        "X-CSRF-TOKEN", "Cache-Control", "Pragma", "If-Modified-Since",
                        "If-None-Match", "ETag", "Last-Modified")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
