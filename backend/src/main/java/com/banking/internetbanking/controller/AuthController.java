package com.banking.internetbanking.controller;

import com.banking.internetbanking.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:8080" })
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            System.out.println("=== ユーザー登録リクエスト受信 ===");
            System.out.println("Request body: " + request);

            String username = request.get("username");
            String email = request.get("email");
            String password = request.get("password");
            String firstName = request.get("firstName");
            String lastName = request.get("lastName");
            String phoneNumber = request.get("phoneNumber");

            System.out.println("Username: " + username);
            System.out.println("Email: " + email);
            System.out.println("First Name: " + firstName);
            System.out.println("Last Name: " + lastName);

            // バリデーション
            if (username == null || email == null || password == null) {
                System.out.println("バリデーションエラー: 必須項目が不足しています");
                return ResponseEntity.badRequest().body(Map.of("error", "必須項目が不足しています"));
            }

            // ユーザー名の重複チェック
            if (userService.getUserByUsername(username).isPresent()) {
                System.out.println("ユーザー名が既に存在します: " + username);
                return ResponseEntity.badRequest().body(Map.of("error", "このユーザー名は既に使用されています"));
            }

            // メールアドレスの重複チェック
            if (userService.getUserByEmail(email).isPresent()) {
                System.out.println("メールアドレスが既に存在します: " + email);
                return ResponseEntity.badRequest().body(Map.of("error", "このメールアドレスは既に使用されています"));
            }

            // ユーザー作成
            System.out.println("ユーザー作成を開始します...");
            userService.createUser(username, email, password, firstName, lastName, phoneNumber);
            System.out.println("ユーザー作成が完了しました: " + username);

            return ResponseEntity.ok(Map.of("message", "ユーザー登録が完了しました"));
        } catch (Exception e) {
            System.err.println("ユーザー登録エラー: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "登録に失敗しました"));
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
