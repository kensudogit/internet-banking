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

#### 方法1: Railway Dashboard で設定を確認・修正

1. Railway Dashboard にログイン
2. プロジェクトを選択
3. バックエンドサービスを選択
4. 「**Settings**」タブを開く
5. 「**Source**」セクションで以下を確認：
   - **Root Directory**: `backend` に設定されているか確認
   - **Dockerfile Path**: `Dockerfile` に設定されているか確認
6. 設定が間違っている場合は修正し、保存

#### 方法2: サービスを再作成

1. バックエンドサービスを削除
2. 新しいサービスを作成
3. 「**+ New**」→「**GitHub Repo**」→ 同じリポジトリを選択
4. **Settings タブで以下を設定**：
   - **Root Directory**: `backend`
   - **Dockerfile Path**: `Dockerfile`
   - **Start Command**: `java -jar app.jar`
5. 環境変数を再設定
6. デプロイを開始

#### 方法3: ビルドコンテキストを確認

Railwayのビルドログで、ビルドコンテキストが正しいか確認：

```
context: [context-id]
```

Root Directory が `backend` に設定されている場合、ビルドコンテキストは `backend` ディレクトリになります。

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

### エラー6: JARファイルが見つからない

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
