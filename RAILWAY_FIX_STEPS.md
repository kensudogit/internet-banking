# Railway 502エラー 解決手順（簡易版）

## 🔴 現在のエラー

```
/favicon.ico:1 Failed to load resource: the server responded with a status of 502 ()
```

**注意**: `content.js` エラーはブラウザ拡張機能のエラーなので無視して問題ありません。

## ✅ 解決手順（3ステップ）

### ステップ1: Railway Dashboard で環境変数を設定

1. [Railway Dashboard](https://railway.app/dashboard) にログイン
2. プロジェクトを選択
3. **バックエンドサービス**を選択
4. 「**Variables**」タブを開く
5. 以下の3つの環境変数を**必ず**設定してください：

| 変数名 | 値 |
|--------|-----|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://postgres.railway.internal:5432/railway?connectTimeout=10&socketTimeout=30` |
| `SPRING_DATASOURCE_USERNAME` | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | `xduqlioTiXIZrsMeMzdTJUEjILwtOKIY` |

**重要**: `SPRING_DATASOURCE_URL` に `localhost` が含まれていないことを確認してください。

### ステップ2: JWT_SECRET を設定（まだ設定されていない場合）

`JWT_SECRET` が設定されていない場合は、以下のいずれかで生成して設定してください：

#### 方法1: オンラインツール（推奨）

1. [Random.org](https://www.random.org/strings/?num=1&len=64&digits=on&upperalpha=on&loweralpha=on&unique=on&format=html&rnd=new) にアクセス
2. 64文字のランダムな文字列を生成
3. 生成された文字列をコピー
4. Railway Dashboard → バックエンドサービス → Variables → `JWT_SECRET` に設定

#### 方法2: 簡単な値（開発環境のみ）

- **Key**: `JWT_SECRET`
- **Value**: `internet-banking-secret-key-for-production-minimum-32-characters-long`

### ステップ3: 再デプロイ（必須）

**重要**: 環境変数を設定した後、**必ず**再デプロイしてください。

1. バックエンドサービス → 「**Deployments**」タブ
2. 「**Redeploy**」ボタンをクリック
3. ビルドログと実行ログを確認

### ステップ4: ログを確認

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

## 📋 チェックリスト

502エラーを解決するために、以下を**順番に**確認してください：

1. [ ] Railway Dashboard でバックエンドサービスを選択
2. [ ] 「Variables」タブを開く
3. [ ] `SPRING_DATASOURCE_URL` が設定されている（`postgres.railway.internal` を使用）
4. [ ] `SPRING_DATASOURCE_USERNAME` が `postgres` に設定されている
5. [ ] `SPRING_DATASOURCE_PASSWORD` が正しく設定されている
6. [ ] `JWT_SECRET` が設定されている（64文字以上）
7. [ ] 環境変数を設定した後、「Redeploy」ボタンをクリック
8. [ ] ログに「Started InternetBankingApplication」が表示されている
9. [ ] 502エラーが解消された

## 💡 ヒント

- `content.js` エラーは無視して問題ありません（ブラウザ拡張機能のエラー）
- 502エラーは、バックエンドが起動していないことを示しています
- 環境変数を設定した後、必ず再デプロイしてください
- ログを確認して、正常に起動していることを確認してください
