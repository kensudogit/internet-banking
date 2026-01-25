package com.banking.internetbanking.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * CORSフィルター
 * 
 * すべてのリクエストに対してCORSヘッダーを追加します。
 * Spring SecurityのCORS処理を迂回して、シンプルにCORSヘッダーを設定します。
 */
// CorsConfigでSpringのCorsFilterを使用するため、このクラスは無効化
// @Component
// @Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // すべてのオリジンを許可（ワイルドカードを使用）
        response.setHeader("Access-Control-Allow-Origin", "*");

        // すべてのメソッドを許可
        response.setHeader("Access-Control-Allow-Methods", 
                "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");

        // すべてのヘッダーを許可（明示的に指定）
        String requestedHeaders = request.getHeader("Access-Control-Request-Headers");
        if (requestedHeaders != null && !requestedHeaders.isEmpty()) {
            response.setHeader("Access-Control-Allow-Headers", requestedHeaders);
        } else {
            response.setHeader("Access-Control-Allow-Headers", 
                    "Authorization, Content-Type, X-Requested-With, Accept, Origin, " +
                    "Access-Control-Request-Method, Access-Control-Request-Headers, " +
                    "X-CSRF-TOKEN, Cache-Control, Pragma, If-Modified-Since, " +
                    "If-None-Match, ETag, Last-Modified");
        }

        // 認証情報を許可しない（ワイルドカードを使用する場合）
        response.setHeader("Access-Control-Allow-Credentials", "false");

        // プリフライトリクエストのキャッシュ時間
        response.setHeader("Access-Control-Max-Age", "3600");

        // 公開するヘッダー
        response.setHeader("Access-Control-Expose-Headers", "*");

        // OPTIONSリクエスト（プリフライト）の場合は、ここで処理を終了
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // 次のフィルターに進む
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初期化処理は不要
    }

    @Override
    public void destroy() {
        // クリーンアップ処理は不要
    }
}
