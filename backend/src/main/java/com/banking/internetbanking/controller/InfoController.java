package com.banking.internetbanking.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

/**
 * 情報コントローラークラス
 * 
 * API情報を提供するREST APIエンドポイントを実装します。
 * アプリケーションのバージョン情報やエンドポイント一覧を返します。
 */
@RestController
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:8080" })
public class InfoController {

    @Value("${FRONTEND_URL:}")
    private String frontendUrl;
    
    private final Environment environment;
    
    public InfoController(Environment environment) {
        this.environment = environment;
    }

    /**
     * ルートパス（/）にアクセスした場合のエンドポイント
     * フロントエンドアプリケーションにリダイレクトします。
     * 環境変数FRONTEND_URLが設定されていない場合は、リクエストのホスト情報から推測します。
     * 
     * @param request HTTPリクエスト
     * @return リダイレクトレスポンスまたはAPI情報
     */
    @GetMapping("/")
    public Object root(HttpServletRequest request) {
        String redirectUrl = null;
        
        // 環境変数FRONTEND_URLが設定されている場合
        if (frontendUrl != null && !frontendUrl.isEmpty() && !frontendUrl.equals("http://localhost:3000")) {
            redirectUrl = frontendUrl;
            // 本番環境では絶対URLでリダイレクト
            if (!redirectUrl.startsWith("http://") && !redirectUrl.startsWith("https://")) {
                // 相対URLの場合は、現在のリクエストのスキームとホストを使用
                String scheme = request.getScheme();
                String host = request.getHeader("Host");
                if (host != null) {
                    redirectUrl = scheme + "://" + host + redirectUrl;
                }
            }
        } else {
            // 環境変数が設定されていない場合、Railwayの環境変数から取得を試みる
            // Railwayでは、同じプロジェクト内の他のサービスのURLを環境変数で取得できる場合がある
            String railwayFrontendUrl = environment.getProperty("RAILWAY_PUBLIC_DOMAIN");
            if (railwayFrontendUrl == null || railwayFrontendUrl.isEmpty()) {
                // リクエストのホスト情報から推測を試みる
                String host = request.getHeader("Host");
                if (host != null && host.contains("railway.app")) {
                    String scheme = request.getScheme();
                    // Railwayのドメインパターンから推測
                    // バックエンド: internet-banking-production-b084.up.railway.app
                    // フロントエンド: internet-banking-frontend-production.up.railway.app など
                    // ただし、正確なURLは環境変数で設定する必要がある
                    redirectUrl = null; // 推測は困難なため、環境変数の設定を促す
                }
            } else {
                redirectUrl = "https://" + railwayFrontendUrl;
            }
        }
        
        // リダイレクトURLが決定した場合はリダイレクト
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            RedirectView redirectView = new RedirectView(redirectUrl);
            redirectView.setStatusCode(HttpStatus.FOUND);
            return redirectView;
        }
        
        // フロントエンドURLが決定できない場合は、API情報を返す
        // ただし、HTMLレスポンスとして、フロントエンドへのリンクを含める
        return ResponseEntity.ok(Map.of(
                "message", "Internet Banking API",
                "version", "1.0.0",
                "status", "running",
                "endpoints", Map.of(
                        "auth", "/api/auth",
                        "accounts", "/api/accounts",
                        "transactions", "/api/transactions",
                        "health", "/api/actuator/health",
                        "info", "/api/info"),
                "note", "フロントエンドにアクセスするには、環境変数FRONTEND_URLを設定してください。",
                "setup", "Railway Dashboard → バックエンドサービス → Variables → FRONTEND_URL=https://[フロントエンドの公開URL] を設定してください"));
    }

    /**
     * API情報エンドポイント
     * アプリケーションの基本情報を返します。
     * 
     * @return API情報
     */
    @GetMapping("/api/info")
    public ResponseEntity<?> info() {
        return ResponseEntity.ok(Map.of(
                "application", "Internet Banking",
                "version", "1.0.0",
                "status", "running"));
    }
}
