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
        // 環境変数から許可するオリジンを取得
        String allowedOriginsEnv = System.getenv("CORS_ALLOWED_ORIGINS");
        
        CorsRegistry.CorsRegistration corsRegistration = registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
        
        if (allowedOriginsEnv != null && !allowedOriginsEnv.isEmpty()) {
            // 環境変数が設定されている場合、実際のオリジンを許可
            String[] origins = Arrays.stream(allowedOriginsEnv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);
            corsRegistration.allowedOrigins(origins);
        } else {
            // 環境変数が設定されていない場合、すべてのオリジンを許可
            corsRegistration.allowedOriginPatterns("*");
        }
    }
}
