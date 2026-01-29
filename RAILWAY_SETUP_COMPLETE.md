# Railway完全公開デプロイ設定完了ガイド

## 現在の状況

Railway Dashboardで`internet-banking-front`サービスの環境変数が設定されていますが、フロントエンドからAPIエンドポイントに接続できていません。

## 解決手順

### ステップ1: バックエンドサービスの公開URLを確認

1. Railway Dashboardでプロジェクトを開く
2. **`internet-banking`**サービス（バックエンド）を選択
3. 「**Settings**」タブを開く
4. 「**Networking**」セクションを確認
5. **公開URLをコピー**（例: `https://internet-banking-production-xxxx.up.railway.app`）

### ステップ2: フロントエンドサービスの環境変数を設定

1. Railway Dashboardで**`internet-banking-front`**サービスを選択
2. 「**Variables**」タブを開く
3. `REACT_APP_API_URL`を確認または追加：

   **変数名**: `REACT_APP_API_URL`
   
   **値**: `https://[バックエンドの公開URL]/api`
   
   **例**: `https://internet-banking-production-xxxx.up.railway.app/api`
   
   **重要**:
   - URLの末尾に`/api`を必ず含める
   - `https://`で始まる完全なURLを指定
   - バックエンドの公開URLは、ステップ1で確認したURLを使用

4. `REACT_APP_USE_MOCK_API`を確認：

   **変数名**: `REACT_APP_USE_MOCK_API`
   
   **値**: `false`
   
   **説明**: モックAPIを無効化して、実際のバックエンドAPIを使用

### ステップ3: バックエンドサービスのCORS設定を確認

1. Railway Dashboardで**`internet-banking`**サービス（バックエンド）を選択
2. 「**Variables**」タブを開く
3. `CORS_ALLOWED_ORIGINS`を確認または追加：

   **変数名**: `CORS_ALLOWED_ORIGINS`
   
   **値**: `https://internet-banking-front-production-xxxx.up.railway.app,https://*.railway.app,https://*.up.railway.app`
   
   **説明**: フロントエンドの公開URLを含める

### ステップ4: フロントエンドサービスを再デプロイ

環境変数を設定した後、フロントエンドサービスを再デプロイする必要があります：

1. **`internet-banking-front`**サービス → 「**Deployments**」タブ
2. 最新のデプロイメントを選択
3. 「**Redeploy**」ボタンをクリック
4. デプロイが完了するまで待つ（数分かかる場合があります）

### ステップ5: バックエンドサービスを再デプロイ（CORS設定を変更した場合）

CORS設定を変更した場合、バックエンドサービスも再デプロイが必要です：

1. **`internet-banking`**サービス → 「**Deployments**」タブ
2. 最新のデプロイメントを選択
3. 「**Redeploy**」ボタンをクリック

### ステップ6: 動作確認

1. フロントエンドアプリケーションを開く（例: `https://internet-banking-front-production-xxxx.up.railway.app`）
2. ブラウザの開発者ツール（F12）を開く
3. 「**Console**」タブを開く
4. 以下のログを確認：

   ```
   API_BASE_URL: https://internet-banking-production-xxxx.up.railway.app/api
   Environment: production
   Should use mock API: false
   ```

5. ログインまたは登録を試行して、エラーが発生しないことを確認

## 環境変数の確認リスト

### フロントエンドサービス（`internet-banking-front`）

| 変数名 | 値 | 必須 |
|--------|-----|------|
| `REACT_APP_API_URL` | `https://[バックエンドURL]/api` | ✅ |
| `REACT_APP_USE_MOCK_API` | `false` | ✅ |
| `REACT_APP_ENV` | `production` | 推奨 |

### バックエンドサービス（`internet-banking`）

| 変数名 | 値 | 必須 |
|--------|-----|------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}?connectTimeout=10&socketTimeout=30` | ✅ |
| `SPRING_DATASOURCE_USERNAME` | `${PGUSER}` | ✅ |
| `SPRING_DATASOURCE_PASSWORD` | `${PGPASSWORD}` | ✅ |
| `SPRING_SQL_INIT_MODE` | `always` | ✅ |
| `CORS_ALLOWED_ORIGINS` | `https://[フロントエンドURL],https://*.railway.app` | ✅ |
| `JWT_SECRET` | （32文字以上のランダムな文字列） | 推奨 |

## トラブルシューティング

### 問題1: 環境変数が反映されない

**原因**:
- 環境変数の名前が間違っている（`REACT_APP_API_URL`である必要がある）
- 再デプロイが完了していない

**解決方法**:
1. 環境変数の名前を確認（`REACT_APP_`で始まる必要がある）
2. フロントエンドサービスを再デプロイ
3. ビルドログで環境変数が正しく読み込まれているか確認

### 問題2: CORSエラーが発生する

**原因**:
- バックエンドのCORS設定で、フロントエンドのドメインが許可されていない

**解決方法**:
1. バックエンドの`CORS_ALLOWED_ORIGINS`にフロントエンドのURLを追加
2. バックエンドサービスを再デプロイ

### 問題3: APIエンドポイントに接続できない

**原因**:
- `REACT_APP_API_URL`が正しく設定されていない
- バックエンドサービスが起動していない

**解決方法**:
1. `REACT_APP_API_URL`の値を確認（完全なURLで、末尾に`/api`を含む）
2. バックエンドサービスのステータスを確認（「Online」になっているか）
3. バックエンドサービスのログを確認

### 問題4: 404エラーが発生する

**原因**:
- バックエンドのURLが間違っている
- バックエンドのルーティング設定が間違っている

**解決方法**:
1. `REACT_APP_API_URL`の値を確認（末尾に`/api`を含む）
2. バックエンドのログを確認して、リクエストが正しく処理されているか確認

## 確認方法

### Railway Dashboardで確認

1. 各サービスの「**Variables**」タブで環境変数を確認
2. 各サービスの「**Deployments**」タブでデプロイ状況を確認
3. 各サービスの「**Logs**」タブでログを確認

### ブラウザのコンソールで確認

1. フロントエンドアプリケーションを開く
2. 開発者ツール（F12）を開く
3. 「**Console**」タブで以下を確認：
   - `API_BASE_URL`の値
   - エラーメッセージ
4. 「**Network**」タブでAPIリクエストを確認：
   - リクエストURLが正しいか
   - レスポンスステータスコード
   - CORSエラーがないか

## 参考資料

- [Railway Documentation](https://docs.railway.app)
- [Railway Environment Variables](https://docs.railway.app/develop/variables)
- [Railway Networking](https://docs.railway.app/develop/networking)
