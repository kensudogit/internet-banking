# Railway 502エラー 今すぐ解決する方法

## 🔴 現在のエラー

```
/favicon.ico:1 Failed to load resource: the server responded with a status of 502 ()
```

**注意**: `content.js` エラーはブラウザ拡張機能のエラーなので無視して問題ありません。

## ✅ 502エラーを解決する手順

### ステップ1: Railway Dashboard でバックエンドサービスの状態を確認

1. [Railway Dashboard](https://railway.app/dashboard) にログイン
2. プロジェクトを選択
3. **バックエンドサービス**を選択
4. サービスの状態を確認：
   - **Running**（緑色）: ステップ2へ
   - **Stopped**（灰色）: ステップ5へ
   - **Failed**（赤色）: ステップ3へ

### ステップ2: ログを確認（サービスがRunningの場合）

1. バックエンドサービス → 「**Logs**」タブを開く
2. 最新のログを確認

**確認すべきメッセージ:**

#### ✅ 正常な起動
```
Started InternetBankingApplication in X.XXX seconds
Tomcat started on port(s): [PORT番号] (http)
```

#### ❌ エラーが表示されている場合
- **データベース接続エラー** (`Connection to localhost:5432 refused`): ステップ3へ
- **その他のエラー**: ステップ4へ

### ステップ3: データベース接続環境変数を設定（最重要）

ログに `Connection to localhost:5432 refused` が表示されている場合、環境変数が設定されていません。

1. バックエンドサービス → 「**Variables**」タブを開く
2. 以下の3つの環境変数を**必ず**設定してください：

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
  xduqlioTiXIZrsMeMzdTJUEjILwtOKIY
  ```

### ステップ4: JWT_SECRET を設定

`JWT_SECRET` が設定されていない場合は、以下のいずれかの方法で生成して設定してください：

#### 方法1: オンラインツールを使用（推奨）

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

- **Key**: `JWT_SECRET`
- **Value**: 
  ```
  internet-banking-secret-key-for-production-minimum-32-characters-long
  ```

### ステップ5: その他の環境変数を確認

以下の環境変数も設定されているか確認してください：

- `SPRING_SQL_INIT_MODE` = `always`
- `CORS_ALLOWED_ORIGINS` = `https://*.railway.app`

### ステップ6: 削除すべき環境変数

以下の環境変数が設定されている場合は、**必ず削除**してください：

- ❌ `SERVER_PORT` - 設定されている場合は削除

### ステップ7: 環境変数の確認

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

### ステップ8: バックエンドサービスを再デプロイ（必須）

**重要**: 環境変数を設定した後、**必ず**再デプロイしてください。

1. バックエンドサービス → 「**Deployments**」タブ
2. 「**Redeploy**」ボタンをクリック
3. ビルドログと実行ログを確認

### ステップ9: ログを確認

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

### ステップ10: フロントエンドの確認

バックエンドが正常に起動したら、フロントエンドのURLにアクセス：

```
https://[your-frontend-url].railway.app
```

502エラーが解消されていることを確認してください。

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

502エラーを解決するために、以下を**順番に**確認してください：

1. [ ] Railway Dashboard でバックエンドサービスを選択
2. [ ] 「Variables」タブを開く
3. [ ] `SPRING_DATASOURCE_URL` が設定されている（`postgres.railway.internal` を使用）
4. [ ] `SPRING_DATASOURCE_USERNAME` が `postgres` に設定されている
5. [ ] `SPRING_DATASOURCE_PASSWORD` が正しく設定されている
6. [ ] `JWT_SECRET` が設定されている（64文字以上）
7. [ ] `SERVER_PORT` が削除されている（設定されていない）
8. [ ] 環境変数を設定した後、「Redeploy」ボタンをクリック
9. [ ] ログに「Started InternetBankingApplication」が表示されている
10. [ ] 502エラーが解消された

## 📚 参考資料

- `RAILWAY_IMMEDIATE_FIX.md` - データベース接続エラーの詳細な解決方法
- `RAILWAY_ENV_SETUP.md` - 環境変数設定の詳細ガイド
- `RAILWAY_502_ERROR_FIX.md` - 502エラーの詳細な解決方法

## 💡 ヒント

- `content.js` エラーは無視して問題ありません（ブラウザ拡張機能のエラー）
- 502エラーは、バックエンドが起動していないことを示しています
- 環境変数を設定した後、必ず再デプロイしてください
- ログを確認して、正常に起動していることを確認してください
