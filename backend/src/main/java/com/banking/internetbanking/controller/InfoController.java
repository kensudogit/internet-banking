package com.banking.internetbanking.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * フロントエンドURLが設定されている場合はHTMLページを返してリダイレクトします。
     * 設定されていない場合はAPI情報を返します。
     * 
     * @param request HTTPリクエスト
     * @return HTMLレスポンスまたはAPI情報
     */
    @GetMapping("/")
    public ResponseEntity<?> root(HttpServletRequest request) {
        // フロントエンドURLを検証して正規化
        String normalizedFrontendUrl = normalizeFrontendUrl(frontendUrl, request);
        
        // フロントエンドURLが有効な場合、HTMLページを返してリダイレクト
        if (normalizedFrontendUrl != null && !normalizedFrontendUrl.isEmpty()) {
            // HTMLエスケープ処理
            String escapedUrl = normalizedFrontendUrl
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
            
            String html = String.format("""
                <!DOCTYPE html>
                <html lang="ja">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>リダイレクト中...</title>
                    <meta http-equiv="refresh" content="0;url=%s">
                    <script>
                        // JavaScriptでもリダイレクト（フォールバック）
                        window.location.href = '%s';
                    </script>
                </head>
                <body>
                    <p>フロントエンドにリダイレクトしています...</p>
                    <p>自動的にリダイレクトされない場合は、<a href="%s">こちらをクリック</a>してください。</p>
                </body>
                </html>
                """, escapedUrl, escapedUrl, escapedUrl);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            return new ResponseEntity<>(html, headers, HttpStatus.OK);
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
                "note", "このURLはバックエンドAPIです。フロントエンドは別のURLで提供されています。",
                "setup", "Railway Dashboard → バックエンドサービス → Variables → FRONTEND_URL=https://[フロントエンドの公開URL] を設定してください"));
    }

    /**
     * フロントエンドURLを正規化する
     * 相対パスや不完全なURLを完全なURLに変換します。
     * 
     * @param url フロントエンドURL
     * @param request HTTPリクエスト
     * @return 正規化されたURL、無効な場合はnull
     */
    private String normalizeFrontendUrl(String url, HttpServletRequest request) {
        if (url == null || url.isEmpty() || url.equals("http://localhost:3000")) {
            return null;
        }
        
        // 既に完全なURL（http://またはhttps://で始まる）の場合
        if (url.startsWith("http://") || url.startsWith("https://")) {
            // 自分自身へのリダイレクトを防ぐ
            String currentHost = request.getHeader("Host");
            if (currentHost != null && url.contains(currentHost)) {
                return null; // 自分自身へのリダイレクトは許可しない
            }
            return url;
        }
        
        // 相対パスの場合、現在のリクエストのスキームとホストを使用
        String scheme = request.getScheme();
        String host = request.getHeader("Host");
        if (host != null) {
            // 相対パスが/で始まらない場合は/を追加
            String path = url.startsWith("/") ? url : "/" + url;
            return scheme + "://" + host + path;
        }
        
        return null;
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
