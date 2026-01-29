# Railwayバックエンド接続確認ガイド

## 現在の状況

- `API_BASE_URL`は正しく設定されている: `https://internet-banking-production-b084.up.railway.app/api`
- しかし、`TypeError: Failed to fetch`エラーが発生している

## 確認手順

### ステップ1: バックエンドサービスの状態を確認

1. Railway Dashboardでプロジェクトを開く
2. **`internet-banking`**サービス（バックエンド）を選択
3. **サービスのステータスを確認**:
   - 「**Online**」になっているか確認
   - 「**Offline**」の場合は、起動を待つか、エラーを確認

### ステップ2: バックエンドのログを確認

1. **`internet-banking`**サービス → 「**Logs**」タブ
2. **エラーメッセージを確認**:
   - データベース接続エラーがないか
   - ポート設定エラーがないか
   - その他の起動エラーがないか

### ステップ3: バックエンドのヘルスチェックエンドポイントを確認

ブラウザで直接アクセスして、バックエンドが動作しているか確認：

```
https://internet-banking-production-b084.up.railway.app/api/health
```

**期待される結果**:
- 200 OK レスポンス
- JSON形式のレスポンス

**エラーが発生する場合**:
- バックエンドサービスが起動していない
- ルーティング設定が間違っている

### ステップ4: CORS設定を確認

1. **`internet-banking`**サービス → 「**Variables**」タブ
2. `CORS_ALLOWED_ORIGINS`を確認：
   - 値: `https://internet-banking-front-production.up.railway.app,https://*.railway.app,https://*.up.railway.app`
   - フロントエンドのURLが含まれているか確認
   - 末尾に`/api`が含まれていないか確認（含まれている場合は削除）

### ステップ5: ブラウザのNetworkタブで確認

1. フロントエンドアプリケーションを開く
2. 開発者ツール（F12）を開く
3. 「**Network**」タブを開く
4. ログインを試行
5. **リクエストを確認**:
   - リクエストURL: `https://internet-banking-production-b084.up.railway.app/api/auth/login`
   - ステータスコード: （200, 404, 500, CORSエラーなど）
   - レスポンスヘッダー: `access-control-allow-origin`が含まれているか

## よくある問題と解決方法

### 問題1: バックエンドサービスが起動していない

**確認方法**:
- Railway Dashboardでサービスのステータスを確認
- ログでエラーメッセージを確認

**解決方法**:
- エラーを修正して再デプロイ
- データベース接続エラーの場合、PostgreSQLサービスの状態を確認

### 問題2: CORSエラー

**症状**:
```
Access to fetch at 'https://...' from origin 'https://...' has been blocked by CORS policy
```

**解決方法**:
1. バックエンドの`CORS_ALLOWED_ORIGINS`にフロントエンドのURLを追加
2. バックエンドサービスを再デプロイ

### 問題3: 404エラー

**症状**:
```
404 Not Found
```

**解決方法**:
1. バックエンドのルーティング設定を確認
2. `/api/auth/login`エンドポイントが正しく設定されているか確認

### 問題4: 500エラー

**症状**:
```
500 Internal Server Error
```

**解決方法**:
1. バックエンドのログを確認
2. データベース接続エラーの場合、PostgreSQLサービスの状態を確認
3. 環境変数が正しく設定されているか確認

## デバッグコマンド

### バックエンドのヘルスチェック

```bash
curl https://internet-banking-production-b084.up.railway.app/api/health
```

### ログインエンドポイントのテスト

```bash
curl -X POST https://internet-banking-production-b084.up.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

## 確認チェックリスト

- [ ] バックエンドサービスが「Online」になっている
- [ ] バックエンドのログにエラーがない
- [ ] ヘルスチェックエンドポイント（`/api/health`）が動作している
- [ ] `CORS_ALLOWED_ORIGINS`にフロントエンドのURLが含まれている
- [ ] ブラウザのNetworkタブでリクエストが正しく送信されている
- [ ] CORSエラーが発生していない
