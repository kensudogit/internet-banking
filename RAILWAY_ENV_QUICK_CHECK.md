# Railway 環境変数クイックチェック

## 🔍 現在の設定値

提供された値：
- **JWT_SECRET**: `un2kW6PfhC1VF4IBrbmJEdt0xajUpvLKqsyG9gD8ZAcTN7HXiOw3eMlSz5oQRY`

**重要:** PostgreSQLのパスワードは、PostgreSQLサービスの `PGPASSWORD` 環境変数から取得してください。JWT_SECRETとは別の値です。

## ✅ Railway Dashboardでの確認手順

### ステップ1: PostgreSQLサービスの環境変数を確認

1. Railway Dashboard → **PostgreSQL サービス**を選択
2. 「**Variables**」タブを開く
3. 以下の値を確認・コピー：
   - `PGHOST`（例: `containers-us-west-xxx.railway.app` または `postgres.railway.internal`）
   - `PGPORT`（通常: `5432`）
   - `PGUSER`（通常: `postgres`）
   - `PGPASSWORD`（提供された値と一致しているか確認）
   - `PGDATABASE`（例: `railway`）

### ステップ2: バックエンドサービスの環境変数を設定

1. Railway Dashboard → **バックエンドサービス**を選択
2. 「**Variables**」タブを開く
3. 以下の環境変数を設定・確認：

#### 必須の環境変数

```env
# データベース設定（PostgreSQLサービスの環境変数から取得）
SPRING_DATASOURCE_URL=jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]?connectTimeout=10&socketTimeout=30
SPRING_DATASOURCE_USERNAME=[PGUSER]
SPRING_DATASOURCE_PASSWORD=[PGPASSWORD]

# JWT設定
JWT_SECRET=un2kW6PfhC1VF4IBrbmJEdt0xajUpvLKqsyG9gD8ZAcTN7HXiOw3eMlSz5oQRY
```

**重要:** 
- `[PGHOST]`, `[PGPORT]`, `[PGDATABASE]`, `[PGUSER]`, `[PGPASSWORD]` をPostgreSQLサービスの実際の値に置き換えてください
- **PostgreSQLのパスワード（`PGPASSWORD`）とJWT_SECRETは別の値です**
- PostgreSQLサービスの「Variables」タブから `PGPASSWORD` の値をコピーして使用してください

#### 例（PostgreSQLサービスの環境変数が以下の場合）:
- `PGHOST` = `containers-us-west-xxx.railway.app`
- `PGPORT` = `5432`
- `PGUSER` = `postgres`
- `PGPASSWORD` = `[PostgreSQLサービスの実際のパスワード]` ← PostgreSQLサービスのVariablesから取得
- `PGDATABASE` = `railway`

**バックエンドサービスの環境変数:**
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://containers-us-west-xxx.railway.app:5432/railway?connectTimeout=10&socketTimeout=30
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=[PostgreSQLサービスのPGPASSWORDの値]
JWT_SECRET=un2kW6PfhC1VF4IBrbmJEdt0xajUpvLKqsyG9gD8ZAcTN7HXiOw3eMlSz5oQRY
```

### ステップ3: 削除すべき環境変数

以下の環境変数が設定されている場合は**削除**してください：

- ❌ `SERVER_PORT`（削除が必要）
- ❌ `PORT`（Railwayが自動設定するため、手動設定不要）

### ステップ4: 環境変数の設定後

1. 環境変数を保存
2. Railwayが自動的に再デプロイを開始します
3. バックエンドサービス → 「**Logs**」タブで起動ログを確認
4. 以下のメッセージが表示されることを確認：
   ```
   Started InternetBankingApplication in X.XXX seconds
   Tomcat started on port(s): [PORT番号] (http)
   ```

## 🚨 よくある間違い

### 間違い1: localhost を使用する

**間違い:**
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/internet_banking
```

**正しい方法:**
PostgreSQLサービスの `PGHOST` を使用：
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]?connectTimeout=10&socketTimeout=30
```

### 間違い2: PostgreSQLのパスワードとJWT_SECRETを混同する

**間違い:**
- PostgreSQLのパスワード（`PGPASSWORD`）とJWT_SECRETを同じ値にする
- JWT_SECRETの値をPostgreSQLのパスワードとして使用する

**正しい方法:**
- PostgreSQLのパスワードは、PostgreSQLサービスの `PGPASSWORD` 環境変数から取得
- JWT_SECRETは別の値（提供された値: `un2kW6PfhC1VF4IBrbmJEdt0xajUpvLKqsyG9gD8ZAcTN7HXiOw3eMlSz5oQRY`）
- セキュリティのため、異なる値を使用することを推奨

### 間違い3: SERVER_PORT を設定する

**間違い:**
```env
SERVER_PORT=8080
```

**正しい方法:**
`SERVER_PORT` 環境変数を削除してください。Railwayが自動的に `$PORT` 環境変数を設定します。

## 📋 チェックリスト

環境変数を設定した後、以下を確認してください：

- [ ] PostgreSQLサービスの `PGHOST` を確認した
- [ ] PostgreSQLサービスの `PGPORT` を確認した
- [ ] PostgreSQLサービスの `PGUSER` を確認した
- [ ] PostgreSQLサービスの `PGPASSWORD` を確認した（PostgreSQLサービスのVariablesから取得）
- [ ] PostgreSQLサービスの `PGDATABASE` を確認した
- [ ] バックエンドサービスの `SPRING_DATASOURCE_URL` を設定した
- [ ] バックエンドサービスの `SPRING_DATASOURCE_USERNAME` を設定した（PostgreSQLサービスの `PGUSER` の値）
- [ ] バックエンドサービスの `SPRING_DATASOURCE_PASSWORD` を設定した（PostgreSQLサービスの `PGPASSWORD` の値）
- [ ] バックエンドサービスの `JWT_SECRET` を設定した（提供された値）
- [ ] `SERVER_PORT` 環境変数を削除した
- [ ] 環境変数を保存した
- [ ] 再デプロイが開始されたことを確認した
- [ ] ログで正常に起動したことを確認した

## 🔍 確認方法

### ヘルスチェックエンドポイント

環境変数を設定し、再デプロイ後、以下のエンドポイントにアクセス：

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
- 環境変数が正しく設定されていない可能性があります
- 上記のチェックリストを再度確認してください

## 📚 参考ドキュメント

- [Railway データベース接続修正](./RAILWAY_DATABASE_CONNECTION_FIX.md)
- [Railway 502 Bad Gateway エラー修正ガイド](./RAILWAY_502_FIX.md)
- [Railway 環境変数設定](./RAILWAY_ENV_SETUP.md)
