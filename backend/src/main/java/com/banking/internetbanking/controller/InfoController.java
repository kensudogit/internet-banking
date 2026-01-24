package com.banking.internetbanking.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
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
     * API情報を返します。
     * リダイレクトループを防ぐため、リダイレクトは行いません。
     * 
     * @param request HTTPリクエスト
     * @return API情報
     */
    @GetMapping("/")
    public ResponseEntity<?> root(HttpServletRequest request) {
        // リダイレクトループを防ぐため、API情報を返す
        // フロントエンドは別のURLで提供されるため、直接アクセスしてください
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
                "frontend_url", frontendUrl != null && !frontendUrl.isEmpty() && !frontendUrl.equals("http://localhost:3000") 
                    ? frontendUrl 
                    : "フロントエンドのURLを確認してください"));
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
