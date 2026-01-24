# Railway データベース接続エラー 即座に解決する方法

ログを確認したところ、バックエンドがまだ `localhost:5432` に接続しようとしています。これは、環境変数が設定されていないか、設定されていても再デプロイされていないことを示しています。

## 🔴 現在のエラー

```
Connection to localhost:5432 refused. Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections.
```

## ✅ 即座に解決する手順

### ステップ1: Railway Dashboard で環境変数を設定

1. [Railway Dashboard](https://railway.app/dashboard) にログイン
2. プロジェクトを選択
3. **バックエンドサービス**を選択
4. 「**Variables**」タブを開く

### ステップ2: データベース接続環境変数を設定

以下の3つの環境変数を**必ず**設定してください：

#### 1. `SPRING_DATASOURCE_URL`

- **Key**: `SPRING_DATASOURCE_URL`
- **Value**: 
  ```
  jdbc:postgresql://postgres.railway.internal:5432/railway?connectTimeout=10&socketTimeout=30
  ```

#### 2. `SPRING_DATASOURCE_USERNAME`

- **Key**: `SPRING_DATASOURCE_USERNAME`
- **Value**: 
  ```
  postgres
  ```

#### 3. `SPRING_DATASOURCE_PASSWORD`

- **Key**: `SPRING_DATASOURCE_PASSWORD`
- **Value**: 
  ```
  xduqlioTiXIZrsMeMzdTJUEjILwtOKIY
  ```

### ステップ3: JWT_SECRET を設定

`JWT_SECRET` が設定されていない場合は、以下のいずれかの方法で生成して設定してください：

#### 方法1: オンラインツールを使用

1. [Random.org](https://www.random.org/strings/?num=1&len=64&digits=on&upperalpha=on&loweralpha=on&unique=on&format=html&rnd=new) にアクセス
2. 64文字のランダムな文字列を生成
3. 生成された文字列をコピー
4. Railway Dashboard → バックエンドサービス → Variables → `JWT_SECRET` に設定

#### 方法2: PowerShell で生成（Windows）

PowerShellを開いて以下のコマンドを実行：

```powershell
-join ((48..57) + (65..90) + (97..122) | Get-Random -Count 64 | ForEach-Object {[char]$_})
```

生成された文字列をコピーして、`JWT_SECRET` に設定してください。

#### 方法3: 簡単な値を使用（開発環境のみ）

**注意**: 本番環境では使用しないでください。

```
JWT_SECRET=internet-banking-secret-key-for-production-minimum-32-characters-long
```

### ステップ4: その他の環境変数を確認

以下の環境変数も設定されているか確認してください：

- `SPRING_SQL_INIT_MODE` = `always`
- `CORS_ALLOWED_ORIGINS` = `https://*.railway.app`

### ステップ5: 削除すべき環境変数

以下の環境変数が設定されている場合は、**必ず削除**してください：

- ❌ `SERVER_PORT` - 設定されている場合は削除

### ステップ6: 環境変数の確認

設定後、以下のように表示されることを確認してください：

| 変数名 | 値 |
|--------|-----|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://postgres.railway.internal:5432/railway?connectTimeout=10&socketTimeout=30` |
| `SPRING_DATASOURCE_USERNAME` | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | `xduqlioTiXIZrsMeMzdTJUEjILwtOKIY` |
| `JWT_SECRET` | （64文字のランダムな文字列） |
| `SPRING_SQL_INIT_MODE` | `always` |
| `CORS_ALLOWED_ORIGINS` | `https://*.railway.app` |

**重要**: `SPRING_DATASOURCE_URL` に `localhost` が含まれていないことを確認してください。

### ステップ7: バックエンドサービスを再デプロイ

1. バックエンドサービス → 「**Deployments**」タブ
2. 「**Redeploy**」ボタンをクリック
3. ビルドログと実行ログを確認

### ステップ8: ログを確認

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

**❌ まだエラーが表示される場合:**
- 環境変数が正しく設定されているか再確認
- `SPRING_DATASOURCE_URL` に `postgres.railway.internal` が含まれているか確認
- `SPRING_DATASOURCE_URL` に `localhost` が含まれていないか確認
- 再デプロイを実行したか確認

## 🚨 よくある間違い

### 間違い1: 環境変数を設定したが再デプロイしていない

**問題:**
- 環境変数を設定しただけでは、実行中のコンテナには反映されません
- 必ず再デプロイが必要です

**正しい方法:**
- 環境変数を設定した後、必ず「Redeploy」ボタンをクリック

### 間違い2: `localhost` を使用している

**問題:**
- `SPRING_DATASOURCE_URL` に `localhost` が含まれている
- Railwayでは、PostgreSQLは別のコンテナで実行されているため、`localhost` では接続できません

**正しい方法:**
- `postgres.railway.internal` を使用してください

### 間違い3: 環境変数の値が間違っている

**問題:**
- 値のコピー&ペーストで誤りがある
- スペースや改行が含まれている

**正しい方法:**
- 値を正確にコピー&ペースト
- 値の前後にスペースがないことを確認

## 📋 緊急チェックリスト

以下の順番で確認してください：

1. [ ] Railway Dashboard でバックエンドサービスを選択
2. [ ] 「Variables」タブを開く
3. [ ] `SPRING_DATASOURCE_URL` が設定されている（`postgres.railway.internal` を使用）
4. [ ] `SPRING_DATASOURCE_USERNAME` が `postgres` に設定されている
5. [ ] `SPRING_DATASOURCE_PASSWORD` が正しく設定されている
6. [ ] `JWT_SECRET` が設定されている（64文字以上）
7. [ ] `SERVER_PORT` が削除されている（設定されていない）
8. [ ] 環境変数を設定した後、「Redeploy」ボタンをクリック
9. [ ] ログに「Started InternetBankingApplication」が表示されている
10. [ ] データベース接続エラーが解消された

## 📚 参考資料

- `RAILWAY_ENV_SETUP.md` - 環境変数設定の詳細ガイド
- `RAILWAY_DATABASE_CONNECTION_FIX.md` - データベース接続エラーの詳細な解決方法
- `RAILWAY_502_ERROR_FIX.md` - 502エラーの詳細な解決方法
