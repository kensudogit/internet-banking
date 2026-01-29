# CORS設定の修正

## 問題

RailwayでデプロイしたフロントエンドからバックエンドAPIにアクセスする際、以下のCORSエラーが発生していました：

```
Access to fetch at 'https://internet-banking-production-b084.up.railway.app/api/auth/login' 
from origin 'https://internet-banking-front-production.up.railway.app' 
has been blocked by CORS policy: Response to preflight request doesn't pass access control check: 
No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

## 原因

1. **プリフライトリクエスト（OPTIONS）が正しく処理されていない**
   - Spring SecurityのCORS設定が正しく動作していなかった
   - `AuthController`の`@CrossOrigin`アノテーションとSpring SecurityのCORS設定が競合していた可能性

2. **CORS設定の不備**
   - `setAllowedHeaders`に`"*"`を設定していたが、Spring Securityでは動作しない
   - 環境変数からCORS設定を読み込む際の前後の空白処理が不十分

## 修正内容

### 1. `SecurityConfig.java`の改善

- **環境変数からのCORS設定読み込みを改善**
  - `CORS_ALLOWED_ORIGINS`環境変数から許可するオリジンを読み込み
  - カンマ区切りの値を前後の空白を削除して処理
  - 環境変数が設定されていない場合は、すべてのオリジンを許可（開発用）

- **CORS設定の詳細化**
  - `setAllowedHeaders`を具体的なヘッダーリストに変更
  - プリフライトリクエストに必要なヘッダーを明示的に指定
  - デバッグ用のログ出力を追加

### 2. `AuthController.java`の修正

- **`@CrossOrigin`アノテーションを削除**
  - CORS設定は`SecurityConfig`で一元管理するため
  - アノテーションとSpring Securityの設定が競合する可能性を排除

- **`handleOptions`メソッドを削除**
  - Spring SecurityのCORS設定がプリフライトリクエストを自動処理するため不要

## Railwayでの環境変数設定

バックエンドサービス（`internet-banking`）の環境変数に以下を設定してください：

```
CORS_ALLOWED_ORIGINS=https://internet-banking-front-production.up.railway.app,https://*.railway.app,https://*.up.railway.app
```

**重要**: 
- フロントエンドのURLは**末尾にスラッシュなし**で設定
- カンマ区切りで複数のオリジンを指定可能
- ワイルドカードパターン（`*`）も使用可能

## 確認手順

### 1. バックエンドのログを確認

Railway Dashboard → `internet-banking`サービス → Logsタブで、以下のログが表示されることを確認：

```
=== CORS設定（環境変数から）===
  Allowed Origin Patterns: [https://internet-banking-front-production.up.railway.app, https://*.railway.app, https://*.up.railway.app]
  Allowed Methods: [GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD]
  Allowed Headers: [Authorization, Content-Type, X-Requested-With, ...]
  Allow Credentials: false
  Max Age: 3600
===========================
```

### 2. プリフライトリクエストを確認

ブラウザの開発者ツール（F12）→ Networkタブで、ログインを試行した際に：

1. **OPTIONSリクエスト**が`/api/auth/login`に送信される
2. **ステータスコード200**が返される
3. **レスポンスヘッダー**に以下が含まれる：
   - `Access-Control-Allow-Origin: https://internet-banking-front-production.up.railway.app`
   - `Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD`
   - `Access-Control-Allow-Headers: Authorization, Content-Type, ...`

### 3. 実際のリクエストを確認

OPTIONSリクエストの後、**POSTリクエスト**が`/api/auth/login`に送信され、正常に処理されることを確認。

## トラブルシューティング

### CORSエラーが続く場合

1. **環境変数の確認**
   - Railway Dashboardで`CORS_ALLOWED_ORIGINS`が正しく設定されているか確認
   - フロントエンドのURLが完全一致しているか確認（プロトコル、ドメイン、ポート）

2. **バックエンドの再デプロイ**
   - コードを変更した場合は、GitHubにプッシュしてRailwayで再デプロイ
   - 環境変数を変更した場合は、サービスを再起動

3. **ブラウザのキャッシュをクリア**
   - 開発者ツール（F12）→ Networkタブ → 「Disable cache」を有効化
   - または、シークレットモードでテスト

4. **バックエンドのログを確認**
   - Railway Dashboard → Logsタブで、CORS設定のログが正しく出力されているか確認
   - エラーメッセージがないか確認

## 次のステップ

1. コードをGitHubにプッシュ
2. Railwayでバックエンドサービスを再デプロイ
3. フロントエンドからログインを試行
4. ブラウザの開発者ツールでCORSエラーが解消されているか確認
