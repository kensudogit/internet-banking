# Railway データベース接続エラーの解決方法

このドキュメントでは、Railwayで発生するデータベース接続エラー（`Connection to localhost:5432 refused`）の解決方法を説明します。

## 🔴 エラーの詳細

**エラーメッセージ:**
```
Connection to localhost:5432 refused. Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections.
Application run failed
```

**原因:**
バックエンドが `localhost:5432` に接続しようとしています。これは、環境変数 `SPRING_DATASOURCE_URL` が正しく設定されていないか、デフォルト値が使用されていることを示しています。

## ✅ 即座に解決する手順

### ステップ1: PostgreSQL サービスの接続情報を取得

1. Railway Dashboard にログイン
2. プロジェクトを選択
3. **PostgreSQL サービス**を選択
4. 「**Variables**」タブを開く
5. 以下の値をコピー：
   - `PGHOST`（例: `containers-us-west-xxx.railway.app`）
   - `PGPORT`（例: `5432`）
   - `PGUSER`（例: `postgres`）
   - `PGPASSWORD`（例: `xxxxx`）
   - `PGDATABASE`（例: `railway`）

### ステップ2: バックエンドサービスの環境変数を設定

1. **バックエンドサービス**を選択
2. 「**Variables**」タブを開く
3. 以下の環境変数を設定（`[PGHOST]` などを実際の値に置き換えてください）：

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]?connectTimeout=10&socketTimeout=30
SPRING_DATASOURCE_USERNAME=[PGUSER]
SPRING_DATASOURCE_PASSWORD=[PGPASSWORD]
```

**例:**
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://containers-us-west-xxx.railway.app:5432/railway?connectTimeout=10&socketTimeout=30
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your-password-here
```

### ステップ3: その他の必須環境変数を確認

以下の環境変数も設定されているか確認：

```env
JWT_SECRET=your-very-long-and-secure-secret-key-minimum-32-characters
SPRING_SQL_INIT_MODE=always
CORS_ALLOWED_ORIGINS=https://*.railway.app
```

### ステップ4: バックエンドサービスを再デプロイ

1. バックエンドサービス → 「**Deployments**」タブ
2. 「**Redeploy**」ボタンをクリック
3. ビルドログと実行ログを確認

### ステップ5: ログを確認

再デプロイ後、ログを確認：

1. バックエンドサービス → 「**Logs**」タブ
2. 以下のメッセージが表示されることを確認：

**✅ 正常な起動:**
```
Started InternetBankingApplication in X.XXX seconds
Tomcat started on port(s): [PORT番号] (http)
```

**❌ まだエラーが表示される場合:**
- 環境変数が正しく設定されているか再確認
- PostgreSQL サービスの接続情報が正しいか確認

## 🔍 環境変数の設定方法（詳細）

### Railway Dashboard での設定手順

1. **バックエンドサービス**を選択
2. 「**Variables**」タブを開く
3. 「**+ New Variable**」をクリック
4. 変数名と値を入力：
   - **Key**: `SPRING_DATASOURCE_URL`
   - **Value**: `jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]?connectTimeout=10&socketTimeout=30`
5. 「**Add**」をクリック
6. 同様に、`SPRING_DATASOURCE_USERNAME` と `SPRING_DATASOURCE_PASSWORD` を設定

### 環境変数の値の取得方法

#### PostgreSQL サービスの Variables から取得

1. PostgreSQL サービス → 「**Variables**」タブ
2. 以下の値を確認・コピー：

| PostgreSQL の変数 | バックエンドの変数 | 説明 |
|------------------|-------------------|------|
| `PGHOST` | `SPRING_DATASOURCE_URL` の一部 | データベースホスト名 |
| `PGPORT` | `SPRING_DATASOURCE_URL` の一部 | データベースポート（通常5432） |
| `PGUSER` | `SPRING_DATASOURCE_USERNAME` | データベースユーザー名 |
| `PGPASSWORD` | `SPRING_DATASOURCE_PASSWORD` | データベースパスワード |
| `PGDATABASE` | `SPRING_DATASOURCE_URL` の一部 | データベース名 |

#### SPRING_DATASOURCE_URL の構築方法

PostgreSQL サービスの Variables から値を取得したら、以下の形式で構築：

```
jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]?connectTimeout=10&socketTimeout=30
```

**例:**
- `PGHOST` = `containers-us-west-xxx.railway.app`
- `PGPORT` = `5432`
- `PGDATABASE` = `railway`

**構築されたURL:**
```
jdbc:postgresql://containers-us-west-xxx.railway.app:5432/railway?connectTimeout=10&socketTimeout=30
```

## 🚨 よくある間違い

### 間違い1: localhost を使用する

**間違い:**
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/internet_banking
```

**問題:**
- Railwayでは、PostgreSQLは別のコンテナで実行されているため、`localhost` では接続できません
- PostgreSQL サービスの `PGHOST` を使用する必要があります

**正しい方法:**
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]
```

### 間違い2: 環境変数を設定しない

**問題:**
- 環境変数が設定されていない場合、`application.yml` のデフォルト値（`localhost:5432`）が使用されます
- Railwayでは接続できません

**正しい方法:**
- PostgreSQL サービスの Variables から接続情報を取得
- バックエンドサービスの環境変数を設定

### 間違い3: 接続情報を間違ってコピーする

**問題:**
- `PGHOST` の値を間違ってコピーする
- ポート番号を間違える
- データベース名を間違える

**正しい方法:**
- PostgreSQL サービスの Variables から正確にコピー
- 値を確認してから設定

## 📋 チェックリスト

データベース接続エラーを解決するために、以下を確認してください：

- [ ] PostgreSQL サービスが「Running」状態になっている
- [ ] PostgreSQL サービスの Variables から接続情報を取得した
- [ ] `SPRING_DATASOURCE_URL` が正しく設定されている（`localhost` ではない）
- [ ] `SPRING_DATASOURCE_USERNAME` が設定されている
- [ ] `SPRING_DATASOURCE_PASSWORD` が設定されている
- [ ] 接続URLの形式が正しい（`jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]`）
- [ ] バックエンドサービスを再デプロイした
- [ ] ログに「Started InternetBankingApplication」が表示されている
- [ ] データベース接続エラーが解消された

## 🔧 デバッグのヒント

### 1. 環境変数の確認方法

Railway Dashboard → バックエンドサービス → 「**Variables**」タブ

**確認ポイント:**
- `SPRING_DATASOURCE_URL` に `localhost` が含まれていないか
- `SPRING_DATASOURCE_URL` に `PGHOST` の値が含まれているか
- すべての必須環境変数が設定されているか

### 2. ログの確認方法

Railway Dashboard → バックエンドサービス → 「**Logs**」タブ

**正常な起動時のログ:**
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
Started InternetBankingApplication in X.XXX seconds
```

**エラー時のログ:**
```
Connection to localhost:5432 refused
HikariPool-1 - Exception during pool initialization
Application run failed
```

### 3. PostgreSQL サービスの確認

Railway Dashboard → PostgreSQL サービス → 「**Logs**」タブ

**確認ポイント:**
- `database system is ready to accept connections` が表示されているか
- エラーが表示されていないか

## 📚 参考資料

- `RAILWAY_502_ERROR_FIX.md` - 502エラーの詳細な解決方法
- `RAILWAY_DEPLOY_COMPLETE.md` - 完全なデプロイ手順
- `railway.env.template` - 環境変数のテンプレート

## 💡 ヒント

- Railwayでは、同じプロジェクト内のサービス間で自動的に接続が提供されます
- PostgreSQL サービスの Variables から接続情報を取得してください
- `localhost` は使用しないでください
- 環境変数を設定した後、必ず再デプロイしてください
