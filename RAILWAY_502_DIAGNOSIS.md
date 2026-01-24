# Railway 502 Bad Gateway エラー診断ガイド

## 🔴 現在の状況
`https://internet-banking-production-b084.up.railway.app/` にアクセスすると、502 Bad Gatewayエラーが発生しています。

## 📋 診断手順

### ステップ1: バックエンドサービスのログを確認

1. [Railway Dashboard](https://railway.app/dashboard) にログイン
2. **バックエンドサービス**（`internet-banking-production-b084`）を選択
3. 「**Logs**」タブを開く
4. 最新のログを確認

#### 確認すべきポイント

**正常な起動ログ:**
```
Started InternetBankingApplication in X.XXX seconds
Tomcat started on port(s): [PORT番号] (http)
```

**エラーログの例:**
- `Application failed to start`
- `Connection refused`
- `Port already in use`
- `Database connection failed`
- `Exception in thread "main"`
- `Caused by:`

### ステップ2: デプロイメントの状態を確認

1. バックエンドサービス → 「**Deployments**」タブ
2. 最新のデプロイメントの状態を確認：
   - ✅ **Success**: デプロイ成功
   - ❌ **Failed**: デプロイ失敗
   - ⏳ **Building**: ビルド中

### ステップ3: 環境変数の確認

バックエンドサービスの「**Variables**」タブで、以下を確認：

#### 必須の環境変数チェックリスト

- [ ] `SPRING_DATASOURCE_URL` が設定されている
  - 形式: `jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]?connectTimeout=10&socketTimeout=30`
  - PostgreSQLサービスの環境変数から取得
- [ ] `SPRING_DATASOURCE_USERNAME` が設定されている
  - 通常: `postgres` または PostgreSQLサービスの `PGUSER` の値
- [ ] `SPRING_DATASOURCE_PASSWORD` が設定されている
  - PostgreSQLサービスの `PGPASSWORD` の値
- [ ] `JWT_SECRET` が設定されている
  - **32文字以上**である必要があります
- [ ] `SERVER_PORT` 環境変数が**削除されている**（重要！）
  - Railwayが自動的に `$PORT` 環境変数を設定します

### ステップ4: PostgreSQLサービスの確認

1. PostgreSQL サービスを選択
2. サービスの状態を確認（「Running」になっているか）
3. 「**Variables**」タブで以下を確認：
   - `PGHOST`
   - `PGPORT`
   - `PGUSER`
   - `PGPASSWORD`
   - `PGDATABASE`

### ステップ5: よくあるエラーと解決方法

#### エラー1: データベース接続エラー

**ログに表示されるエラー:**
```
Connection refused
Connection timeout
FATAL: password authentication failed
```

**解決方法:**
1. PostgreSQLサービスの環境変数を確認
2. バックエンドの `SPRING_DATASOURCE_URL` が正しく設定されているか確認
3. `SPRING_DATASOURCE_USERNAME` と `SPRING_DATASOURCE_PASSWORD` が正しいか確認

#### エラー2: ポート設定エラー

**ログに表示されるエラー:**
```
Port already in use
Address already in use
```

**解決方法:**
1. `SERVER_PORT` 環境変数が設定されている場合は**削除**
2. `application.yml` で `port: ${PORT:8080}` が設定されているか確認

#### エラー3: ビルドエラー

**ログに表示されるエラー:**
```
gradlew: not found
./gradlew: Permission denied
```

**解決方法:**
1. Dockerfileを確認
2. `gradlew` ファイルが正しくコピーされているか確認
3. 実行権限が付与されているか確認

#### エラー4: 環境変数の不足

**ログに表示されるエラー:**
```
Required environment variable not set
Configuration property 'xxx' is not found
```

**解決方法:**
1. 必須の環境変数がすべて設定されているか確認
2. 特に `JWT_SECRET` が32文字以上であることを確認

### ステップ6: ヘルスチェックエンドポイントの確認

バックエンドが起動したら、以下のエンドポイントにアクセス：

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

## 🔍 ログの確認方法

### Railway Dashboardでの確認

1. Railway Dashboard → バックエンドサービス → 「**Logs**」タブ
2. 最新のログをスクロールして確認
3. エラーメッセージを探す：
   - `ERROR`
   - `Exception`
   - `Failed`
   - `Connection refused`
   - `Application failed to start`

### ログのエクスポート

1. Railway Dashboard → バックエンドサービス → 「**Logs**」タブ
2. ログをコピーして保存
3. エラーメッセージを特定

## 📝 ログファイルの提供方法

502エラーの原因を特定するために、以下のログを提供してください：

1. **バックエンドサービスのログ**
   - Railway Dashboard → バックエンドサービス → 「**Logs**」タブ
   - 最新のログをコピー

2. **デプロイメントログ**
   - Railway Dashboard → バックエンドサービス → 「**Deployments**」タブ
   - 最新のデプロイメントをクリック
   - ビルドログと実行ログを確認

## 🚨 緊急対応

502エラーが続く場合の緊急対応：

1. **サービスを再起動**
   - Railway Dashboard → バックエンドサービス → 「**Deployments**」タブ
   - 「**Redeploy**」ボタンをクリック

2. **環境変数を再確認**
   - すべての必須環境変数が設定されているか確認
   - `SERVER_PORT` が削除されているか確認

3. **PostgreSQLサービスの確認**
   - PostgreSQLサービスが「Running」状態か確認
   - データベース接続情報が正しいか確認

## 📚 参考ドキュメント

- [Railway 502 Bad Gateway エラー修正ガイド](./RAILWAY_502_FIX.md)
- [Railway ポート設定の修正方法](./RAILWAY_PORT_FIX.md)
- [Railway データベース接続修正](./RAILWAY_DATABASE_CONNECTION_FIX.md)
- [Railway 環境変数設定](./RAILWAY_ENV_SETUP.md)
