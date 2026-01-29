# Railway トラブルシューティングガイド

## 問題: `TypeError: Failed to fetch` エラー

### 症状

フロントエンドからバックエンドAPIを呼び出す際に、以下のエラーが発生：

```
TypeError: Failed to fetch
Error logging in: TypeError: Failed to fetch
```

コンソールには以下のように表示される：
```
API_BASE_URL: https://internet-banking-production-b084.up.railway.app/api
Logging in: https://internet-banking-production-b084.up.railway.app/api/auth/login
```

### 考えられる原因

1. **バックエンドサービスが起動していない**
2. **REACT_APP_API_URLの値が間違っている**
3. **CORS設定の問題**
4. **ネットワーク接続の問題**

## 解決手順

### ステップ1: バックエンドサービスの状態を確認

1. Railway Dashboardでプロジェクトを開く
2. **`internet-banking`**サービス（バックエンド）を選択
3. **サービスのステータスを確認**:
   - 「Online」になっているか確認
   - 「Offline」の場合は、起動を待つか、エラーを確認

4. **ログを確認**:
   - 「**Logs**」タブを開く
   - エラーメッセージがないか確認
   - データベース接続エラーがないか確認

### ステップ2: バックエンドの公開URLを確認

1. **`internet-banking`**サービス → 「**Settings**」タブ
2. 「**Networking**」セクションを確認
3. **公開URLをコピー**（例: `https://internet-banking-production-xxxx.up.railway.app`）

### ステップ3: フロントエンドの環境変数を確認・修正

1. **`internet-banking-front`**サービス → 「**Variables**」タブ
2. `REACT_APP_API_URL`の値を確認：
   - **正しい形式**: `https://internet-banking-production-xxxx.up.railway.app/api`
   - **間違った形式**: `/api`（相対パス）
   - **間違った形式**: `http://localhost:8080/api`（ローカルURL）

3. **値が間違っている場合、修正**:
   - `REACT_APP_API_URL`を編集
   - バックエンドの公開URL + `/api`を設定
   - 保存

### ステップ4: CORS設定を確認

1. **`internet-banking`**サービス → 「**Variables**」タブ
2. `CORS_ALLOWED_ORIGINS`を確認：
   - フロントエンドの公開URLが含まれているか確認
   - 例: `https://internet-banking-front-production-xxxx.up.railway.app,https://*.railway.app`

3. **含まれていない場合、追加**:
   - `CORS_ALLOWED_ORIGINS`を編集
   - フロントエンドの公開URLを追加
   - 保存

### ステップ5: サービスを再デプロイ

環境変数を変更した後、両方のサービスを再デプロイ：

1. **フロントエンドサービス**:
   - 「**Deployments**」タブ → 「**Redeploy**」

2. **バックエンドサービス**（CORS設定を変更した場合）:
   - 「**Deployments**」タブ → 「**Redeploy**」

### ステップ6: ブラウザで確認

1. **フロントエンドアプリケーションを開く**
2. **開発者ツール（F12）を開く**
3. **「Network」タブを開く**
4. **ログインを試行**
5. **リクエストを確認**:
   - リクエストURLが正しいか
   - ステータスコード（200, 404, 500など）
   - CORSエラーがないか

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
1. `REACT_APP_API_URL`の値が正しいか確認（末尾に`/api`を含む）
2. バックエンドのルーティング設定を確認

### 問題4: 500エラー

**症状**:
```
500 Internal Server Error
```

**解決方法**:
1. バックエンドのログを確認
2. データベース接続エラーの場合、PostgreSQLサービスの状態を確認
3. 環境変数が正しく設定されているか確認

## 確認チェックリスト

- [ ] バックエンドサービスが「Online」になっている
- [ ] フロントエンドサービスが「Online」になっている
- [ ] `REACT_APP_API_URL`が正しく設定されている（完全なURL + `/api`）
- [ ] `CORS_ALLOWED_ORIGINS`にフロントエンドのURLが含まれている
- [ ] 両方のサービスが再デプロイされている
- [ ] ブラウザのNetworkタブでリクエストが正しく送信されている
- [ ] バックエンドのログにエラーがない

## デバッグ方法

### ブラウザの開発者ツールを使用

1. **Consoleタブ**:
   - `API_BASE_URL`の値を確認
   - エラーメッセージを確認

2. **Networkタブ**:
   - リクエストURLを確認
   - ステータスコードを確認
   - レスポンス内容を確認
   - CORSエラーがないか確認

### Railway Dashboardを使用

1. **サービスのログ**:
   - 各サービスの「**Logs**」タブでログを確認
   - エラーメッセージを探す

2. **サービスのメトリクス**:
   - 「**Metrics**」タブでCPU、メモリ使用率を確認
   - 異常な値がないか確認

## 参考資料

- [Railway Documentation](https://docs.railway.app)
- [Railway Troubleshooting](https://docs.railway.app/help/troubleshooting)
- [CORS設定ガイド](RAILWAY_SETUP_COMPLETE.md)
