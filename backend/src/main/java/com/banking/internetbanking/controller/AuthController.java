package com.banking.internetbanking.controller;

import com.banking.internetbanking.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String email = request.get("email");
            String password = request.get("password");
            String firstName = request.get("firstName");
            String lastName = request.get("lastName");
            String phoneNumber = request.get("phoneNumber");

            // バリデーション
            if (username == null || email == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "必須項目が不足しています"));
            }

            // ユーザー作成
            userService.createUser(username, email, password, firstName, lastName, phoneNumber);

            return ResponseEntity.ok(Map.of("message", "ユーザー登録が完了しました"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            if (username == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "ユーザー名とパスワードが必要です"));
            }

            if (userService.authenticateUser(username, password)) {
                // 実際の実装ではJWTトークンを生成して返す
                return ResponseEntity.ok(Map.of("message", "ログイン成功"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "認証に失敗しました"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // 実際の実装ではJWTトークンを無効化する
        return ResponseEntity.ok(Map.of("message", "ログアウトしました"));
    }
}
