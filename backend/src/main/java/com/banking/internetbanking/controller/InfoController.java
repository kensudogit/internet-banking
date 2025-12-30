package com.banking.internetbanking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:8080" })
public class InfoController {

    // ルートパス（/）にアクセスした場合のエンドポイント
    @GetMapping("/")
    public ResponseEntity<?> root() {
        return ResponseEntity.ok(Map.of(
                "message", "Internet Banking API",
                "version", "1.0.0",
                "status", "running",
                "endpoints", Map.of(
                        "auth", "/api/auth",
                        "accounts", "/api/accounts",
                        "transactions", "/api/transactions",
                        "health", "/api/actuator/health",
                        "info", "/api/info")));
    }

    @GetMapping("/api/info")
    public ResponseEntity<?> info() {
        return ResponseEntity.ok(Map.of(
                "application", "Internet Banking",
                "version", "1.0.0",
                "status", "running"));
    }
}
