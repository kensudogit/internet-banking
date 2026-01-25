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
     * SecurityConfigのcorsConfigurationSource()を使用します。
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration(
            org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource) {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(
                new CorsFilter(corsConfigurationSource));
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
