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
        // フロントエンドURLが設定されている場合、HTMLページを返してリダイレクト
        if (frontendUrl != null && !frontendUrl.isEmpty() && !frontendUrl.equals("http://localhost:3000")) {
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
                """, frontendUrl, frontendUrl, frontendUrl);
            
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
