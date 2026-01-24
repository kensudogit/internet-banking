# Railway 502 Bad Gateway エラー修正ガイド

## 🔴 問題
`https://internet-banking-production-b084.up.railway.app/` にアクセスすると、502 Bad Gatewayエラーが発生する。

## 原因
502エラーは、Railwayのリバースプロキシがバックエンドサーバーに接続できないことを示しています。主な原因は：

1. **アプリケーションが起動していない**
   - ビルドエラー
   - 起動時のエラー（データベース接続エラーなど）
   - クラッシュ

2. **ポート設定の問題**
   - Railwayが割り当てたポートとアプリケーションが使用するポートが一致しない

3. **環境変数の不足**
   - 必須の環境変数が設定されていない

## ✅ 解決方法

### ステップ1: Railway Dashboardでログを確認

1. [Railway Dashboard](https://railway.app/dashboard) にログイン
2. バックエンドサービス（`internet-banking-production-b084`）を選択
3. 「**Logs**」タブを開く
4. 最新のログを確認

#### 正常な起動ログの例
```
Started InternetBankingApplication in X.XXX seconds
Tomcat started on port(s): [PORT番号] (http)
```

#### エラーログの例
- `Application failed to start`
- `Connection refused`
- `Port already in use`
- `Database connection failed`

### ステップ2: デプロイメントの状態を確認

1. バックエンドサービス → 「**Deployments**」タブ
2. 最新のデプロイメントの状態を確認：
   - ✅ **Success**: デプロイ成功
   - ❌ **Failed**: デプロイ失敗（ログを確認）
   - ⏳ **Building**: ビルド中

### ステップ3: 環境変数の確認

バックエンドサービスの「**Variables**」タブで、以下を確認：

#### 必須の環境変数

```env
# データベース設定（PostgreSQLサービスの環境変数から取得）
SPRING_DATASOURCE_URL=jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]?connectTimeout=10&socketTimeout=30
SPRING_DATASOURCE_USERNAME=[PGUSER]
SPRING_DATASOURCE_PASSWORD=[PGPASSWORD]

# JWT設定（32文字以上）
JWT_SECRET=your-very-long-and-secure-secret-key-change-this-in-production-minimum-32-characters
```

#### 重要な注意点

- ❌ **`SERVER_PORT` 環境変数は設定しないでください**（削除してください）
- ✅ Railwayが自動的に `$PORT` 環境変数を設定します
- ✅ `application.yml` で `${PORT:8080}` を使用することで、Railwayのポートを自動的に使用します

### ステップ4: データベース接続の確認

PostgreSQLサービスの環境変数を確認：

1. PostgreSQL サービスを選択
2. 「**Variables**」タブを開く
3. 以下の値を確認：
   - `PGHOST`
   - `PGPORT`
   - `PGUSER`
   - `PGPASSWORD`
   - `PGDATABASE`

4. バックエンドサービスの `SPRING_DATASOURCE_URL` が正しく設定されているか確認：
   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]?connectTimeout=10&socketTimeout=30
   ```

### ステップ5: アプリケーションの再デプロイ

1. バックエンドサービス → 「**Deployments**」タブ
2. 「**Redeploy**」ボタンをクリック
3. ビルドログと実行ログを確認

### ステップ6: ヘルスチェックエンドポイントの確認

バックエンドが起動したら、ヘルスチェックエンドポイントにアクセス：

```
https://internet-banking-production-b084.up.railway.app/api/actuator/health
```

**期待される応答:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

**502エラーが返る場合:**
- アプリケーションが起動していない
- 上記のステップを再度確認

## 🚨 よくある問題と解決方法

### 問題1: データベース接続エラー

**症状:**
- ログに「Connection refused」や「Connection timeout」が表示される

**解決方法:**
1. PostgreSQLサービスの環境変数を確認
2. `SPRING_DATASOURCE_URL` が正しく設定されているか確認
3. PostgreSQLサービスが「Running」状態か確認

### 問題2: ポート設定エラー

**症状:**
- ログに「Port already in use」が表示される
- または、ポート番号が表示されない

**解決方法:**
1. `SERVER_PORT` 環境変数が設定されている場合は**削除**
2. `application.yml` で `port: ${PORT:8080}` が設定されているか確認

### 問題3: ビルドエラー

**症状:**
- デプロイメントが「Failed」状態
- ビルドログにエラーが表示される

**解決方法:**
1. ビルドログのエラー内容を確認
2. よくあるエラー：
   - `gradlew: not found` → Dockerfileを確認
   - `react-scripts: not found` → フロントエンドのDockerfileを確認
   - 依存関係のエラー → `package.json` や `build.gradle.kts` を確認

### 問題4: 環境変数の不足

**症状:**
- ログに「Required environment variable not set」が表示される

**解決方法:**
1. 必須の環境変数がすべて設定されているか確認
2. 特に `JWT_SECRET` が32文字以上であることを確認

## 📋 チェックリスト

502エラーを修正するために、以下を確認してください：

- [ ] Railway Dashboardでログを確認した
- [ ] デプロイメントの状態が「Success」になっている
- [ ] `SPRING_DATASOURCE_URL` が正しく設定されている
- [ ] `SPRING_DATASOURCE_USERNAME` が設定されている
- [ ] `SPRING_DATASOURCE_PASSWORD` が設定されている
- [ ] `JWT_SECRET` が32文字以上で設定されている
- [ ] `SERVER_PORT` 環境変数が**削除されている**
- [ ] PostgreSQLサービスが「Running」状態
- [ ] ヘルスチェックエンドポイント（`/api/actuator/health`）にアクセスできる

## 🔍 デバッグ方法

### ログの確認方法

1. Railway Dashboard → バックエンドサービス → 「**Logs**」タブ
2. 最新のログをスクロールして確認
3. エラーメッセージを探す：
   - `ERROR`
   - `Exception`
   - `Failed`
   - `Connection refused`

### ヘルスチェックの確認

```bash
curl https://internet-banking-production-b084.up.railway.app/api/actuator/health
```

正常な場合：
```json
{"status":"UP"}
```

502エラーの場合：
```
502 Bad Gateway
```

## 📚 参考ドキュメント

- [Railway ポート設定の修正方法](./RAILWAY_PORT_FIX.md)
- [Railway デプロイガイド](./RAILWAY_DEPLOY.md)
- [Railway 環境変数設定](./RAILWAY_ENV_SETUP.md)
