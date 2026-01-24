# Railway トラブルシューティングガイド

このドキュメントでは、Railwayへのデプロイ時に発生する可能性のある問題とその解決方法を説明します。

## 🔴 よくあるエラーと解決方法

### エラー1: "/src": not found

**エラーメッセージ:**
```
ERROR: failed to build: failed to solve: failed to compute cache key: failed to calculate checksum of ref ...: "/src": not found
```

**原因:**
Railwayでバックエンドをビルドする際に、Root Directoryが正しく設定されていないため、`src` ディレクトリが見つかりません。

**解決方法:**

#### 方法1: Railway Dashboard で設定を確認・修正（最重要）

1. Railway Dashboard にログイン
2. プロジェクトを選択
3. バックエンドサービスを選択
4. 「**Settings**」タブを開く
5. **「Source」セクションを確認**（画面右側のサイドバー）
   - **Root Directory**: `backend` に設定されているか確認
   - **Dockerfile Path**: `Dockerfile` に設定されているか確認
6. **設定が空欄または間違っている場合**:
   - Root Directory に `backend` を入力
   - Dockerfile Path に `Dockerfile` を入力
   - 「**Save**」をクリック
7. サービスを再デプロイ（自動的に開始される場合もあります）

**重要**: Root Directory が空欄の場合、Railwayはプロジェクトルートをビルドコンテキストとして使用します。そのため、`backend/src` ではなく `/src` を探してしまいます。

#### 方法2: サービスを削除して再作成

1. バックエンドサービスを削除
   - サービスを選択 → 「**Settings**」タブ → 最下部の「**Delete Service**」
2. 新しいサービスを作成
   - 「**+ New**」→「**GitHub Repo**」→ 同じリポジトリを選択
3. **Settings タブで以下を設定**（作成直後に設定）：
   - **Root Directory**: `backend`（**必ず設定**）
   - **Dockerfile Path**: `Dockerfile`
   - **Start Command**: `java -jar app.jar`
4. 環境変数を再設定（Variables タブ）
5. デプロイを開始（自動的に開始されます）

#### 方法3: ビルドログでコンテキストを確認

Railwayのビルドログで、ビルドコンテキストが正しいか確認：

```
context: [context-id]
```

Root Directory が `backend` に設定されている場合、ビルドコンテキストは `backend` ディレクトリになります。

#### 方法4: railway.json ファイルを確認

プロジェクトルートの `railway.json` ファイルを確認：

```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "DOCKERFILE",
    "dockerfilePath": "backend/Dockerfile"
  }
}
```

**注意**: `railway.json` で `dockerfilePath` を `backend/Dockerfile` に設定している場合、Root Directory を空欄にすると、Railwayはプロジェクトルートをビルドコンテキストとして使用します。そのため、Root Directory を `backend` に設定する必要があります。

#### 方法5: 一時的な回避策（推奨しない）

Root Directory を設定できない場合の一時的な回避策として、プロジェクトルートに `Dockerfile` を作成し、ビルドコンテキストを変更することもできますが、**推奨しません**。正しい方法は Root Directory を設定することです。

### エラー2: データベース接続エラー

**エラーメッセージ:**
```
Connection refused
Could not connect to database
```

**解決方法:**

1. **PostgreSQL サービスの確認**
   - PostgreSQL サービスが起動しているか確認
   - PostgreSQL サービスのログで「database system is ready to accept connections」が表示されているか確認

2. **環境変数の確認**
   - `SPRING_DATASOURCE_URL` が正しい形式か確認
   - PostgreSQL サービスの Variables から正しい値をコピーしているか確認
   - 接続URLの形式: `jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]`

3. **サービス間の接続確認**
   - バックエンドとPostgreSQLが同じプロジェクト内にあるか確認
   - Railway は同じプロジェクト内のサービス間で自動的に接続を提供します

### エラー3: CORS エラー

**エラーメッセージ（ブラウザコンソール）:**
```
Access to fetch at 'https://...' from origin 'https://...' has been blocked by CORS policy
```

**解決方法:**

1. **バックエンドの環境変数を確認**
   - `CORS_ALLOWED_ORIGINS` が設定されているか確認
   - フロントエンドの公開URLが含まれているか確認
   - 例: `CORS_ALLOWED_ORIGINS=https://frontend-production.up.railway.app`

2. **環境変数の更新**
   - フロントエンドの公開URLを取得
   - バックエンドの `CORS_ALLOWED_ORIGINS` を更新
   - バックエンドサービスを再デプロイ

3. **一時的な解決策（開発環境のみ）**
   - `CORS_ALLOWED_ORIGINS=https://*.railway.app` を設定（本番環境では推奨しない）

### エラー4: フロントエンドがバックエンドに接続できない

**症状:**
- フロントエンドが表示されるが、APIリクエストが失敗する
- ブラウザコンソールにネットワークエラーが表示される

**解決方法:**

1. **環境変数の確認**
   - `REACT_APP_API_URL` が正しく設定されているか確認
   - バックエンドの公開URLが正しいか確認
   - URLの形式: `https://[backend-domain]/api`

2. **バックエンドの起動確認**
   - バックエンドの公開URLに直接アクセスして、APIが応答するか確認
   - 例: `https://backend-production.up.railway.app/api/actuator/health`

3. **フロントエンドの再ビルド**
   - 環境変数を更新した後、フロントエンドサービスを再デプロイ
   - React アプリはビルド時に環境変数が埋め込まれるため、再ビルドが必要

### エラー5: データベーススキーマが作成されない

**症状:**
- アプリケーションは起動するが、データが表示されない
- APIエンドポイントが404エラーを返す

**解決方法:**

1. **環境変数の確認**
   - `SPRING_SQL_INIT_MODE=always` が設定されているか確認
   - `APP_DATABASE_AUTO_INIT=true` が設定されているか確認

2. **ログの確認**
   - バックエンドのログで「データベース初期化」のメッセージを確認
   - エラーが表示されていないか確認

3. **手動実行**
   - PostgreSQL サービスに接続
   - `schema.sql` を手動で実行

### エラー6: Permission denied (gradlew)

**エラーメッセージ:**
```
/bin/sh: 1: ./gradlew: Permission denied
ERROR: failed to build: failed to solve: process "/bin/sh -c ./gradlew clean bootJar -x test --no-daemon" did not complete successfully: exit code: 126
```

**原因:**
`gradlew` ファイルに実行権限が設定されていないため、Dockerコンテナ内で実行できません。

**解決方法:**

1. **Dockerfile の確認**
   - `gradlew` をコピーした後、実行権限を付与する必要があります
   - Dockerfileに `RUN chmod +x ./gradlew` を追加

2. **修正済みのDockerfile**
   ```dockerfile
   # Gradleラッパーをコピー
   COPY gradle ./gradle
   COPY gradlew ./
   COPY gradlew.bat ./
   
   # gradlewに実行権限を付与
   RUN chmod +x ./gradlew
   ```

3. **再デプロイ**
   - Dockerfileを修正した後、GitHubにプッシュ
   - Railwayで自動的に再デプロイが開始されます

### エラー7: JARファイルが見つからない

**エラーメッセージ:**
```
Error: Unable to access jarfile app.jar
```

**解決方法:**

1. **Dockerfile の確認**
   - JARファイルのパスが正しいか確認
   - `COPY --from=build /app/build/libs/*.jar app.jar` が正しく実行されているか確認

2. **ビルドログの確認**
   - ビルドステージでJARファイルが生成されているか確認
   - `ls -la /app/build/libs/` の出力を確認

3. **Gradleビルドの確認**
   - `./gradlew clean bootJar` が正常に完了しているか確認
   - ビルドエラーが発生していないか確認

## 🔍 デバッグのヒント

### ビルドログの確認方法

1. Railway Dashboard → サービスを選択
2. 「**Deployments**」タブを開く
3. 最新のデプロイメントをクリック
4. 「**Build Logs**」を確認

### 実行時ログの確認方法

1. Railway Dashboard → サービスを選択
2. 「**Logs**」タブを開く
3. リアルタイムのログを確認

### 環境変数の確認方法

1. Railway Dashboard → サービスを選択
2. 「**Variables**」タブを開く
3. 設定されている環境変数を確認

### サービス間の接続確認

Railwayでは、同じプロジェクト内のサービス間で自動的に接続が提供されます。環境変数で接続情報を設定する必要があります。

### エラー8: 502 Bad Gateway エラー

**エラーメッセージ（ブラウザコンソール）:**
```
Failed to load resource: the server responded with a status of 502 ()
/favicon.ico:1 Failed to load resource: the server responded with a status of 502 ()
```

**原因:**
502 Bad Gatewayエラーは、バックエンドサーバーが起動していない、または正しく動作していない場合に発生します。

**解決方法:**

1. **バックエンドサービスの状態を確認**
   - Railway Dashboard → バックエンドサービス
   - サービスが「**Running**」状態になっているか確認
   - 「**Stopped**」または「**Failed**」の場合は、ログを確認

2. **バックエンドのログを確認**
   - Railway Dashboard → バックエンドサービス → 「**Logs**」タブ
   - エラーメッセージを確認
   - アプリケーションが正常に起動しているか確認

3. **よくある原因と解決方法**

   **原因1: データベース接続エラー**
   - ログに「Connection refused」や「Could not connect to database」が表示される
   - PostgreSQL サービスが起動しているか確認
   - 環境変数 `SPRING_DATASOURCE_URL` が正しいか確認

   **原因2: ポート設定の問題**
   - Railwayは自動的にポートを割り当てます
   - `SERVER_PORT` 環境変数を設定しないでください（削除するか、`$PORT` を使用）

   **原因3: アプリケーションの起動エラー**
   - ログにJavaのエラーが表示される
   - JARファイルが正しくビルドされているか確認
   - ビルドログでエラーがないか確認

4. **バックエンドの再起動**
   - Railway Dashboard → バックエンドサービス → 「**Deployments**」タブ
   - 「**Redeploy**」ボタンをクリック

5. **ヘルスチェックエンドポイントの確認**
   - バックエンドの公開URLに直接アクセス
   - 例: `https://backend-production.up.railway.app/api/actuator/health`
   - 応答があるか確認

### エラー9: content.js エラー（ブラウザ拡張機能）

**エラーメッセージ（ブラウザコンソール）:**
```
content.js:1 Uncaught (in promise) The message port closed before a response was received.
```

**原因:**
これはブラウザ拡張機能（Chrome拡張機能など）のエラーで、アプリケーション自体の問題ではありません。

**解決方法:**
- **無視して問題ありません**
- アプリケーションの動作には影響しません
- 気になる場合は、ブラウザ拡張機能を無効化するか、別のブラウザで試してください

## 📞 サポート

問題が解決しない場合は、以下を確認してください：

1. [Railway Documentation](https://docs.railway.app/)
2. [Railway Discord](https://discord.gg/railway)
3. Railway Dashboard の「**Support**」セクション

## ✅ チェックリスト

デプロイ前に以下を確認してください：

- [ ] Root Directory が正しく設定されている（バックエンド: `backend`, フロントエンド: `frontend`）
- [ ] Dockerfile Path が正しく設定されている
- [ ] 環境変数が正しく設定されている
- [ ] PostgreSQL サービスが起動している
- [ ] バックエンドとフロントエンドの公開ドメインが生成されている
- [ ] CORS設定が正しい
- [ ] API URLが正しい
- [ ] バックエンドサービスが「Running」状態になっている
