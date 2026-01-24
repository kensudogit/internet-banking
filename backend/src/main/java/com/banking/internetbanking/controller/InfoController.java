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
        
        // フロントエンドURLが設定されていない場合は、詳細なガイドを表示
        String setupGuide = """
            <!DOCTYPE html>
            <html lang="ja">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Internet Banking API - セットアップガイド</title>
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
                        max-width: 800px;
                        margin: 50px auto;
                        padding: 20px;
                        line-height: 1.6;
                        background-color: #f5f5f5;
                    }
                    .container {
                        background: white;
                        padding: 30px;
                        border-radius: 8px;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                    }
                    h1 {
                        color: #333;
                        border-bottom: 3px solid #4CAF50;
                        padding-bottom: 10px;
                    }
                    h2 {
                        color: #555;
                        margin-top: 30px;
                    }
                    .api-info {
                        background: #f9f9f9;
                        padding: 15px;
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                    .endpoint {
                        font-family: monospace;
                        color: #0066cc;
                        margin: 5px 0;
                    }
                    .warning {
                        background: #fff3cd;
                        border-left: 4px solid #ffc107;
                        padding: 15px;
                        margin: 20px 0;
                    }
                    .success {
                        background: #d4edda;
                        border-left: 4px solid #28a745;
                        padding: 15px;
                        margin: 20px 0;
                    }
                    code {
                        background: #f4f4f4;
                        padding: 2px 6px;
                        border-radius: 3px;
                        font-family: monospace;
                    }
                    ol {
                        margin-left: 20px;
                    }
                    li {
                        margin: 10px 0;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>🌐 Internet Banking API</h1>
                    
                    <div class="api-info">
                        <p><strong>バージョン:</strong> 1.0.0</p>
                        <p><strong>ステータス:</strong> <span style="color: #28a745;">✅ 稼働中</span></p>
                    </div>
                    
                    <div class="warning">
                        <strong>⚠️ フロントエンドが設定されていません</strong>
                        <p>フロントエンドサービスを表示するには、Railwayでフロントエンドサービスをデプロイし、環境変数を設定する必要があります。</p>
                    </div>
                    
                    <h2>📋 利用可能なAPIエンドポイント</h2>
                    <div class="api-info">
                        <div class="endpoint">POST /api/auth/register</div>
                        <div class="endpoint">POST /api/auth/login</div>
                        <div class="endpoint">GET /api/accounts</div>
                        <div class="endpoint">GET /api/transactions</div>
                        <div class="endpoint">GET /api/actuator/health</div>
                        <div class="endpoint">GET /api/info</div>
                    </div>
                    
                    <h2>🚀 フロントエンドのデプロイ手順</h2>
                    
                    <h3>ステップ1: Railway Dashboardでフロントエンドサービスを作成</h3>
                    <ol>
                        <li><a href="https://railway.app/dashboard" target="_blank">Railway Dashboard</a>にログイン</li>
                        <li>プロジェクトを選択</li>
                        <li>「<strong>+ New</strong>」→「<strong>GitHub Repo</strong>」を選択</li>
                        <li>同じリポジトリを選択</li>
                    </ol>
                    
                    <h3>ステップ2: Settings タブで設定</h3>
                    <ul>
                        <li><strong>Root Directory:</strong> <code>frontend</code></li>
                        <li><strong>Dockerfile Path:</strong> <code>Dockerfile</code></li>
                        <li><strong>Start Command:</strong> <code>serve -s build -l 3000</code></li>
                    </ul>
                    
                    <h3>ステップ3: Variables タブで環境変数を設定</h3>
                    <pre style="background: #f4f4f4; padding: 15px; border-radius: 5px; overflow-x: auto;">
REACT_APP_API_URL=https://internet-banking-production-b084.up.railway.app/api
REACT_APP_USE_MOCK_API=false</pre>
                    
                    <h3>ステップ4: 公開ドメインを生成</h3>
                    <ol>
                        <li>フロントエンドサービス → <strong>Settings</strong> → <strong>Networking</strong></li>
                        <li>「<strong>Generate Domain</strong>」をクリック</li>
                        <li>生成されたドメインをコピー（例: <code>internet-banking-frontend-production.up.railway.app</code>）</li>
                    </ol>
                    
                    <h3>ステップ5: バックエンドの環境変数を更新</h3>
                    <ol>
                        <li>バックエンドサービス → <strong>Variables</strong> タブ</li>
                        <li><code>FRONTEND_URL</code> を追加/更新：
                            <pre style="background: #f4f4f4; padding: 10px; border-radius: 5px; margin-top: 10px;">
FRONTEND_URL=https://[フロントエンドの公開URL]</pre>
                        </li>
                        <li>保存後、自動的に再デプロイされます</li>
                    </ol>
                    
                    <div class="success">
                        <strong>✅ 完了後</strong>
                        <p>バックエンドのルートURL（このページ）にアクセスすると、自動的にフロントエンドにリダイレクトされます。</p>
                    </div>
                    
                    <h2>📚 詳細なドキュメント</h2>
                    <p>より詳細な手順については、以下のドキュメントを参照してください：</p>
                    <ul>
                        <li><a href="https://github.com/your-repo/internet-banking/blob/main/RAILWAY_FRONTEND_404_FIX.md" target="_blank">フロントエンド404エラー修正ガイド</a></li>
                        <li><a href="https://github.com/your-repo/internet-banking/blob/main/RAILWAY_DEPLOY.md" target="_blank">Railway デプロイガイド</a></li>
                    </ul>
                </div>
            </body>
            </html>
            """;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return new ResponseEntity<>(setupGuide, headers, HttpStatus.OK);
    }

    /**
     * フロントエンドURLを正規化する
     * 完全なURL（https://で始まる）のみを許可します。
     * 
     * @param url フロントエンドURL
     * @param request HTTPリクエスト
     * @return 正規化されたURL、無効な場合はnull
     */
    private String normalizeFrontendUrl(String url, HttpServletRequest request) {
        if (url == null || url.isEmpty() || url.trim().isEmpty()) {
            return null;
        }
        
        // 前後の空白を削除
        url = url.trim();
        
        // localhostは開発環境のみ許可
        if (url.equals("http://localhost:3000")) {
            return null; // 本番環境では許可しない
        }
        
        // 完全なURL（https://で始まる）のみを許可
        if (!url.startsWith("https://")) {
            // http://で始まる場合も本番環境では許可しない（セキュリティのため）
            if (url.startsWith("http://")) {
                return null;
            }
            // 相対パスや不完全なURLは許可しない
            return null;
        }
        
        // 自分自身へのリダイレクトを防ぐ
        String currentHost = request.getHeader("Host");
        if (currentHost != null) {
            // URLからホスト部分を抽出して比較
            try {
                java.net.URI uri = java.net.URI.create(url);
                String urlHost = uri.getHost();
                if (urlHost != null && urlHost.equals(currentHost)) {
                    return null; // 自分自身へのリダイレクトは許可しない
                }
            } catch (IllegalArgumentException e) {
                // URLが不正な形式の場合はnullを返す
                return null;
            }
        }
        
        return url;
    }

    /**
     * Faviconエンドポイント
     * ブラウザのfaviconリクエストに対応します。
     * 
     * @return 204 No Content（faviconなし）
     */
    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        // faviconがない場合は204 No Contentを返す
        return ResponseEntity.noContent().build();
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
