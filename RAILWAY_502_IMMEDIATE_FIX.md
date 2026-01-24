# Railway 502エラー 即座に解決する方法

このドキュメントでは、502 Bad Gatewayエラーを**今すぐ**解決するための具体的な手順を説明します。

## 🔴 現在のエラー

```
GET https://internet-banking-production-b084.up.railway.app/ 502 (Bad Gateway)
/favicon.ico:1 Failed to load resource: the server responded with a status of 502 ()
```

**注意**: `content.js` エラーはブラウザ拡張機能のエラーなので無視して問題ありません。

## ✅ 即座に試す解決手順

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
- **データベース接続エラー**: ステップ4へ
- **ポートエラー**: ステップ6へ
- **その他のエラー**: ステップ3へ

### ステップ3: 環境変数の確認と修正

1. バックエンドサービス → 「**Variables**」タブを開く
2. 以下を確認：

#### 必須の環境変数（設定されているか確認）

| 変数名 | 必須 | 確認方法 |
|--------|------|---------|
| `SPRING_DATASOURCE_URL` | ✅ | PostgreSQL サービスの Variables から取得 |
| `SPRING_DATASOURCE_USERNAME` | ✅ | PostgreSQL サービスの Variables から取得 |
| `SPRING_DATASOURCE_PASSWORD` | ✅ | PostgreSQL サービスの Variables から取得 |
| `JWT_SECRET` | ✅ | 32文字以上のランダムな文字列 |

#### 削除すべき環境変数

- **`SERVER_PORT`**: 設定されている場合は**削除**してください

### ステップ4: PostgreSQL サービスの確認と環境変数の設定（最重要）

**重要**: ログに `Connection to localhost:5432 refused` が表示されている場合、環境変数が正しく設定されていません。

1. **PostgreSQL サービス**を選択
2. サービスの状態を確認（「Running」になっているか）
3. 「**Variables**」タブで接続情報を確認・コピー：
   - `PGHOST`（例: `containers-us-west-xxx.railway.app`）
   - `PGPORT`（通常 `5432`）
   - `PGUSER`（通常 `postgres`）
   - `PGPASSWORD`（長いランダムな文字列）
   - `PGDATABASE`（例: `railway`）

4. **バックエンドサービス**を選択
5. 「**Variables**」タブを開く
6. 以下の環境変数を設定（`[PGHOST]` などを実際の値に置き換えてください）：

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

   **重要**: `localhost` は使用しないでください。PostgreSQL サービスの `PGHOST` を使用してください。

### ステップ5: バックエンドサービスの再起動

1. バックエンドサービス → 「**Deployments**」タブ
2. 「**Redeploy**」ボタンをクリック
3. ビルドログと実行ログを確認

### ステップ6: ポート設定の確認

**重要**: `application.yml` が正しく修正されているか確認

1. GitHubリポジトリで `backend/src/main/resources/application.yml` を確認
2. 以下のようになっているか確認：
   ```yaml
   server:
     port: ${PORT:8080}
   ```
3. もし `port: 8080` になっている場合は、修正が必要です

**修正方法:**
- ローカルで `backend/src/main/resources/application.yml` を編集
- `port: 8080` を `port: ${PORT:8080}` に変更
- GitHubにプッシュ：
  ```bash
  git add backend/src/main/resources/application.yml
  git commit -m "Fix: Use PORT environment variable"
  git push
  ```

## 🔍 デバッグ手順

### 1. バックエンドのログを確認

Railway Dashboard → バックエンドサービス → 「**Logs**」タブ

**正常な起動時のログ:**
```
Started InternetBankingApplication in X.XXX seconds
Tomcat started on port(s): [PORT番号] (http)
```

**エラー時のログ例:**
```
Connection refused
Could not connect to database
Error starting ApplicationContext
Application run failed
```

### 2. ビルドログを確認

Railway Dashboard → バックエンドサービス → 「**Deployments**」タブ → 最新のデプロイメント → 「**Build Logs**」

**確認ポイント:**
- ビルドが成功しているか
- `COPY src ./src` が成功しているか
- `./gradlew clean bootJar` が成功しているか
- JARファイルが生成されているか

### 3. ヘルスチェックエンドポイントにアクセス

ブラウザで以下のURLにアクセス：
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
- バックエンドが起動していない
- 上記のステップを再度確認

## 🚨 よくある問題と即座の解決方法

### 問題1: データベース接続エラー

**ログに表示されるエラー:**
```
Connection refused
Could not connect to database
```

**即座の解決方法:**
1. PostgreSQL サービスが「Running」状態か確認
2. PostgreSQL サービスの Variables から接続情報をコピー
3. バックエンドサービスの環境変数を更新
4. バックエンドサービスを再デプロイ

### 問題2: ポート設定エラー

**ログに表示されるエラー:**
```
Port already in use
Address already in use
```

**即座の解決方法:**
1. Railway Dashboard → バックエンドサービス → 「**Variables**」タブ
2. `SERVER_PORT` 環境変数を削除
3. `application.yml` で `port: ${PORT:8080}` に設定されているか確認
4. バックエンドサービスを再デプロイ

### 問題3: アプリケーション起動エラー

**ログに表示されるエラー:**
```
Error starting ApplicationContext
BeanCreationException
Application run failed
```

**即座の解決方法:**
1. ログの詳細を確認
2. 環境変数が不足していないか確認
3. 必須の環境変数が設定されているか確認
4. バックエンドサービスを再デプロイ

### 問題4: ビルドエラー

**ログに表示されるエラー:**
```
Permission denied
/src: not found
```

**即座の解決方法:**
1. Railway Dashboard → バックエンドサービス → 「**Settings**」タブ
2. 「**Source**」セクションで Root Directory が `backend` に設定されているか確認
3. Dockerfile Path が `Dockerfile` に設定されているか確認
4. バックエンドサービスを再デプロイ

## 📋 緊急チェックリスト

502エラーを解決するために、以下を**順番に**確認してください：

1. [ ] バックエンドサービスが「Running」状態になっている
2. [ ] PostgreSQL サービスが「Running」状態になっている
3. [ ] バックエンドのログにエラーが表示されていない
4. [ ] 環境変数が正しく設定されている（特にデータベース接続情報）
5. [ ] `SERVER_PORT` 環境変数が削除されている
6. [ ] `application.yml` で `port: ${PORT:8080}` に設定されている
7. [ ] バックエンドサービスを再デプロイした
8. [ ] ヘルスチェックエンドポイントにアクセスできる

## 🔧 最も可能性の高い原因

現在のエラーから判断すると、以下のいずれかの可能性が高いです：

1. **ポート設定の問題**（最も可能性が高い）
   - `application.yml` がまだ修正されていない
   - または、`SERVER_PORT` 環境変数が設定されている

2. **バックエンドが起動していない**
   - データベース接続エラー
   - 環境変数が不足している

3. **ビルドエラー**
   - Root Directory が設定されていない
   - Dockerfile のパスが間違っている

## 📞 次のアクション

1. **まず**: Railway Dashboard でバックエンドサービスの状態とログを確認
2. **次に**: 環境変数を確認（特に `SERVER_PORT` を削除）
3. **最後に**: 必要に応じて再デプロイ

詳細な手順は、`RAILWAY_502_ERROR_FIX.md` と `RAILWAY_PORT_FIX.md` を参照してください。
