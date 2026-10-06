# Railway 環境変数設定ガイド（実際の値を使用）

このドキュメントでは、提供されたPostgreSQL接続情報を使用して、バックエンドサービスの環境変数を設定する手順を説明します。

## 📋 提供された接続情報

| 変数名 | 値 |
|--------|-----|
| `PGHOST` | `postgres.railway.internal` |
| `PGPORT` | `5432` |
| `PGUSER` | `postgres` |
| `PGPASSWORD` | `${PGPASSWORD}` |
| `PGDATABASE` | `railway` |

**✅ 確認結果**: すべての値が正しい形式です。`postgres.railway.internal`はRailwayの内部DNS名で、同じプロジェクト内のサービス間で使用されます。

## ✅ バックエンドサービスの環境変数設定

### ステップ1: Railway Dashboard を開く

1. [Railway Dashboard](https://railway.app/dashboard) にログイン
2. プロジェクトを選択
3. **バックエンドサービス**を選択

### ステップ2: 環境変数を設定

1. 「**Variables**」タブを開く
2. 以下の環境変数を**順番に**設定します：

#### 環境変数1: `SPRING_DATASOURCE_URL`

- **Key**: `SPRING_DATASOURCE_URL`
- **Value**: 
  ```
  jdbc:postgresql://postgres.railway.internal:5432/railway?connectTimeout=10&socketTimeout=30
  ```

#### 環境変数2: `SPRING_DATASOURCE_USERNAME`

- **Key**: `SPRING_DATASOURCE_USERNAME`
- **Value**: 
  ```
  postgres
  ```

#### 環境変数3: `SPRING_DATASOURCE_PASSWORD`

- **Key**: `SPRING_DATASOURCE_PASSWORD`
- **Value**: 
  ```
  ${PGPASSWORD}
  ```

### ステップ3: その他の必須環境変数を確認

以下の環境変数も設定されているか確認してください：

#### `JWT_SECRET`（必須）

- **Key**: `JWT_SECRET`
- **Value**: 32文字以上のランダムな文字列（例: `your-very-long-and-secure-secret-key-minimum-32-characters`）
- **注意**: 実際の秘密値はリポジトリへ記載せず、Railway VariablesなどのSecret管理機能で設定してください

#### `SPRING_SQL_INIT_MODE`（推奨）

- **Key**: `SPRING_SQL_INIT_MODE`
- **Value**: 
  ```
  always
  ```

#### `CORS_ALLOWED_ORIGINS`（推奨）

- **Key**: `CORS_ALLOWED_ORIGINS`
- **Value**: 
  ```
  https://*.railway.app
  ```

### ステップ4: 削除すべき環境変数

以下の環境変数が設定されている場合は、**削除**してください：

- ❌ `SERVER_PORT` - Railwayが自動的に`$PORT`を設定するため、手動設定は不要です

### ステップ5: 環境変数の確認

設定後、以下のように表示されることを確認してください：

| 変数名 | 値 |
|--------|-----|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://postgres.railway.internal:5432/railway?connectTimeout=10&socketTimeout=30` |
| `SPRING_DATASOURCE_USERNAME` | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | `${PGPASSWORD}` |
| `JWT_SECRET` | （32文字以上の文字列） |
| `SPRING_SQL_INIT_MODE` | `always` |
| `CORS_ALLOWED_ORIGINS` | `https://*.railway.app` |

### ステップ6: バックエンドサービスを再デプロイ

1. バックエンドサービス → 「**Deployments**」タブ
2. 「**Redeploy**」ボタンをクリック
3. ビルドログと実行ログを確認

### ステップ7: ログを確認

再デプロイ後、ログを確認：

1. バックエンドサービス → 「**Logs**」タブ
2. 以下のメッセージが表示されることを確認：

**✅ 正常な起動:**
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
Started InternetBankingApplication in X.XXX seconds
Tomcat started on port(s): [PORT番号] (http)
```

**❌ エラーが表示される場合:**
- 環境変数が正しく設定されているか再確認
- ログのエラーメッセージを確認
- `RAILWAY_DATABASE_CONNECTION_FIX.md` を参照

## 🔍 設定の確認方法

### Railway Dashboard での確認

1. バックエンドサービス → 「**Variables**」タブ
2. 以下のポイントを確認：
   - ✅ `SPRING_DATASOURCE_URL` に `postgres.railway.internal` が含まれている
   - ✅ `SPRING_DATASOURCE_URL` に `localhost` が含まれていない
   - ✅ `SPRING_DATASOURCE_USERNAME` が `postgres` に設定されている
   - ✅ `SPRING_DATASOURCE_PASSWORD` が正しく設定されている
   - ✅ `JWT_SECRET` が設定されている（32文字以上）
   - ✅ `SERVER_PORT` が設定されていない（削除されている）

## 🚨 よくある間違い

### 間違い1: `localhost` を使用する

**間違い:**
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/railway
```

**正しい方法:**
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres.railway.internal:5432/railway?connectTimeout=10&socketTimeout=30
```

### 間違い2: 外部ホスト名を使用する

**間違い:**
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://containers-us-west-xxx.railway.app:5432/railway
```

**正しい方法:**
同じプロジェクト内のサービス間では、`postgres.railway.internal` を使用してください。これはRailwayの内部DNS名で、より安定した接続を提供します。

### 間違い3: `SERVER_PORT` を設定する

**間違い:**
```env
SERVER_PORT=8080
```

**正しい方法:**
`SERVER_PORT` は設定しないでください。Railwayが自動的に`$PORT`環境変数を設定し、`application.yml`で`${PORT:8080}`を使用することで、正しいポートが使用されます。

## 📋 チェックリスト

環境変数を設定した後、以下を確認してください：

- [ ] `SPRING_DATASOURCE_URL` が正しく設定されている（`postgres.railway.internal`を使用）
- [ ] `SPRING_DATASOURCE_USERNAME` が `postgres` に設定されている
- [ ] `SPRING_DATASOURCE_PASSWORD` が正しく設定されている
- [ ] `JWT_SECRET` が設定されている（32文字以上）
- [ ] `SPRING_SQL_INIT_MODE` が `always` に設定されている
- [ ] `CORS_ALLOWED_ORIGINS` が設定されている
- [ ] `SERVER_PORT` が削除されている（設定されていない）
- [ ] バックエンドサービスを再デプロイした
- [ ] ログに「Started InternetBankingApplication」が表示されている
- [ ] データベース接続エラーが解消された

## 📚 参考資料

- `RAILWAY_DATABASE_CONNECTION_FIX.md` - データベース接続エラーの詳細な解決方法
- `RAILWAY_502_ERROR_FIX.md` - 502エラーの詳細な解決方法
- `RAILWAY_PORT_FIX.md` - ポート設定の問題の解決方法
- `railway.env.template` - 環境変数のテンプレート

## 💡 ヒント

- `postgres.railway.internal` はRailwayの内部DNS名で、同じプロジェクト内のサービス間で使用されます
- 環境変数を設定した後、必ず再デプロイしてください
- ログを確認して、正常に起動していることを確認してください
