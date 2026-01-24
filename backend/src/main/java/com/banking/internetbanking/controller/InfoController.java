package com.banking.internetbanking.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
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

    /**
     * ルートパス（/）にアクセスした場合のエンドポイント
     * フロントエンドアプリケーションにリダイレクトします。
     * 環境変数FRONTEND_URLが設定されていない場合は、API情報を返します。
     * 
     * @param request HTTPリクエスト
     * @return リダイレクトレスポンスまたはAPI情報
     */
    @GetMapping("/")
    public Object root(HttpServletRequest request) {
        // 環境変数FRONTEND_URLが設定されている場合のみリダイレクト
        if (frontendUrl != null && !frontendUrl.isEmpty() && !frontendUrl.equals("http://localhost:3000")) {
            // 本番環境では絶対URLでリダイレクト
            if (frontendUrl.startsWith("http://") || frontendUrl.startsWith("https://")) {
                return new RedirectView(frontendUrl);
            } else {
                // 相対URLの場合は、現在のリクエストのスキームとホストを使用
                String scheme = request.getScheme();
                String host = request.getHeader("Host");
                if (host != null) {
                    return new RedirectView(scheme + "://" + host + frontendUrl);
                }
            }
        }
        
        // フロントエンドURLが設定されていない場合は、API情報を返す
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
                "note", "フロントエンドにアクセスするには、環境変数FRONTEND_URLを設定してください"));
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
