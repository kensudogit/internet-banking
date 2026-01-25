package com.banking.internetbanking.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * シンプルなCORSフィルター
 * 
 * すべてのリクエストに対して確実にCORSヘッダーを追加します。
 * OncePerRequestFilterを使用して、リクエストごとに1回だけ実行されることを保証します。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        System.out.println("=== SimpleCorsFilter実行 ===");
        System.out.println("Method: " + request.getMethod());
        System.out.println("URI: " + request.getRequestURI());
        System.out.println("Origin: " + request.getHeader("Origin"));
        
        // すべてのオリジンを許可
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        // すべてのメソッドを許可
        response.setHeader("Access-Control-Allow-Methods", 
                "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
        
        // すべてのヘッダーを許可
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
        
        // 認証情報を許可しない
        response.setHeader("Access-Control-Allow-Credentials", "false");
        
        // プリフライトリクエストのキャッシュ時間
        response.setHeader("Access-Control-Max-Age", "3600");
        
        // 公開するヘッダー
        response.setHeader("Access-Control-Expose-Headers", 
                "Content-Type, Authorization, X-Requested-With");
        
        // OPTIONSリクエスト（プリフライト）の場合は、ここで処理を終了
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            System.out.println("OPTIONSリクエストを処理します");
            response.setStatus(HttpServletResponse.SC_OK);
            System.out.println("CORSヘッダーを設定しました");
            return;
        }
        
        // 次のフィルターに進む
        System.out.println("次のフィルターに進みます");
        filterChain.doFilter(request, response);
        System.out.println("フィルターチェーン完了");
    }
}
