package com.banking.internetbanking.controller;

import com.banking.internetbanking.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 認証コントローラークラス
 * 
 * ユーザー認証関連のREST APIエンドポイントを提供します。
 * ユーザー登録、ログイン、ログアウトなどの機能を実装します。
 */
@RestController
@RequestMapping("/api/auth")
// CORS設定はSecurityConfigで一元管理するため、@CrossOriginは削除
// RailwayのフロントエンドドメインはSecurityConfigのCORS設定で許可されます
public class AuthController {

    private final UserService userService;

    /**
     * コンストラクタ
     * 
     * @param userService ユーザーサービス
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * ユーザー登録エンドポイント
     * 
     * @param request ユーザー登録情報（username, email, password, firstName, lastName, phoneNumber）
     * @return 登録結果
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("=== ユーザー登録リクエスト受信 ===");
            System.out.println("Request body: " + request);
            System.out.println("Request body type: " + (request != null ? request.getClass().getName() : "null"));

            String username = request.get("username") != null ? request.get("username").toString() : null;
            String email = request.get("email") != null ? request.get("email").toString() : null;
            String password = request.get("password") != null ? request.get("password").toString() : null;
            String firstName = request.get("firstName") != null ? request.get("firstName").toString() : null;
            String lastName = request.get("lastName") != null ? request.get("lastName").toString() : null;
            String phoneNumber = request.get("phoneNumber") != null ? request.get("phoneNumber").toString() : null;

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

    /**
     * ログインエンドポイント
     * 
     * @param request ログイン情報（username, password）
     * @return ログイン結果
     */
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

    /**
     * ログアウトエンドポイント
     * 
     * @return ログアウト結果
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // 実際の実装ではJWTトークンを無効化する
        return ResponseEntity.ok(Map.of("message", "ログアウトしました"));
    }
}
