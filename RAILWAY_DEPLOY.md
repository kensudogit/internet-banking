# Railway デプロイ手順

このドキュメントでは、インターネットバンキングシステムを Railway に完全公開モードでデプロイする手順を説明します。

## 前提条件

- Railway アカウント（[https://railway.app](https://railway.app)）
- Railway CLI がインストールされていること（オプション）
- Git リポジトリにコードがプッシュされていること

## デプロイ手順

### 1. Railway プロジェクトの作成

1. [Railway Dashboard](https://railway.app/dashboard) にログイン
2. 「New Project」をクリック
3. 「Deploy from GitHub repo」を選択（または「Empty Project」を選択）

### 2. PostgreSQL データベースの追加

1. プロジェクト内で「+ New」をクリック
2. 「Database」→「Add PostgreSQL」を選択
3. PostgreSQL サービスが作成されます
4. PostgreSQL サービスの「Variables」タブで、以下の環境変数を確認・コピー：
   - `DATABASE_URL`（または `PGHOST`, `PGPORT`, `PGUSER`, `PGPASSWORD`, `PGDATABASE`）

### 3. バックエンドサービスのデプロイ

**重要**: PostgreSQL サービスが先に起動していることを確認してください。バックエンドサービスは自動的に接続をリトライします（最大5回、各試行の間隔は3秒）。

#### 方法1: ルートディレクトリを使用する場合（推奨）

1. プロジェクト内で「+ New」をクリック
2. 「GitHub Repo」を選択し、リポジトリを選択
3. **「Settings」タブで以下を設定**：
   - 右側のサイドバーで「**Source**」をクリック
   - **「Root Directory」を `backend` に設定**（重要：これがないとDockerfileが見つかりません）
   - 「Settings」タブに戻る
   - 「Dockerfile Path」を `Dockerfile` に設定
   - 「Start Command」を `java -jar app.jar` に設定
4. 以下の環境変数を設定（「Variables」タブ）：

```env
# データベース設定（PostgreSQL サービスの環境変数から取得）
SPRING_DATASOURCE_URL=jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]
SPRING_DATASOURCE_USERNAME=[PGUSER]
SPRING_DATASOURCE_PASSWORD=[PGPASSWORD]

# JWT設定
JWT_SECRET=your-secret-key-here-make-it-very-long-and-secure-in-production-change-this

# サーバー設定
SERVER_PORT=8080
SERVER_SERVLET_CONTEXT_PATH=/api

# CORS設定（フロントエンドの公開URLをカンマ区切りで指定）
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.railway.app

# データベース自動初期化（初回起動時のみ）
APP_DATABASE_AUTO_INIT=true

# セキュリティ設定
SPRING_SECURITY_USER_NAME=admin
SPRING_SECURITY_USER_PASSWORD=admin

# ログ設定
LOGGING_LEVEL_COM_BANKING_INTERNETBANKING=INFO
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY=INFO
```

5. デプロイを開始（自動的に開始されます）

#### 方法2: ルートディレクトリを設定しない場合

1. プロジェクト内で「+ New」をクリック
2. 「GitHub Repo」を選択し、リポジトリを選択
3. **「Settings」タブで以下を設定**：
   - 「Root Directory」は空のまま（プロジェクトルートを使用）
   - 「Dockerfile Path」を `backend/Dockerfile` に設定
   - 「Start Command」を `java -jar app.jar` に設定
4. 環境変数を設定（上記の方法1と同じ）
5. デプロイを開始

**推奨**: 方法1（ルートディレクトリを `backend` に設定）を使用することを推奨します。これにより、ビルドコンテキストが `backend` ディレクトリになり、`src` ディレクトリが正しく見つかります。

### 4. フロントエンドサービスのデプロイ

1. プロジェクト内で「+ New」をクリック
2. 「GitHub Repo」を選択し、同じリポジトリを選択
3. ルートディレクトリを `frontend` に設定
4. 以下の環境変数を設定（「Variables」タブ）：

```env
# バックエンドAPIのURL（バックエンドサービスの公開URL）
REACT_APP_API_URL=https://[backend-service-name].railway.app/api

# モックAPIを無効化
REACT_APP_USE_MOCK_API=false
```

5. 「Settings」タブで：
   - 「Root Directory」を `frontend` に設定
   - 「Dockerfile Path」を `Dockerfile` に設定
   - 「Start Command」を `serve -s build -l 3000` に設定

6. デプロイを開始（自動的に開始されます）

### 5. 公開ドメインの設定

#### バックエンドの公開

1. バックエンドサービスを選択
2. 「Settings」タブを開く
3. 「Generate Domain」をクリックして公開ドメインを生成
4. または「Custom Domain」で独自ドメインを設定

#### フロントエンドの公開

1. フロントエンドサービスを選択
2. 「Settings」タブを開く
3. 「Generate Domain」をクリックして公開ドメインを生成
4. または「Custom Domain」で独自ドメインを設定

### 6. 環境変数の更新

#### フロントエンドの環境変数更新

フロントエンドの環境変数 `REACT_APP_API_URL` を、バックエンドの公開URLに更新：

```env
REACT_APP_API_URL=https://[backend-public-domain]/api
```

#### バックエンドのCORS設定更新

バックエンドの環境変数 `CORS_ALLOWED_ORIGINS` を、フロントエンドの公開URLに更新：

```env
CORS_ALLOWED_ORIGINS=https://[frontend-public-domain]
```

複数のオリジンを許可する場合は、カンマ区切りで指定：

```env
CORS_ALLOWED_ORIGINS=https://[frontend-public-domain],https://[another-domain]
```

更新後、両方のサービスを再デプロイします。

### 7. データベース初期化

データベーススキーマの初期化には、以下の3つの方法があります：

#### 方法1: 自動初期化（推奨）

バックエンドサービスの環境変数に以下を追加：

```env
# データベース自動初期化を有効化
APP_DATABASE_AUTO_INIT=true

# または、Spring Bootの初期化機能を使用
SPRING_SQL_INIT_MODE=always
```

アプリケーション起動時に自動的にスキーマが作成されます。

#### 方法2: Railway PostgreSQL の初期化スクリプト

1. PostgreSQL サービスの「Variables」タブを開く
2. 「Raw Editor」をクリック
3. `INIT_SQL` 環境変数に `backend/src/main/resources/init.sql` の内容を設定
4. サービスを再起動

#### 方法3: 手動実行

1. PostgreSQL サービスの「Connect」タブで接続情報を確認
2. ローカルから接続するか、Railway の PostgreSQL コンソールを使用
3. `backend/src/main/resources/schema.sql` を実行
4. `backend/src/main/resources/sample-data.sql` を実行（オプション）

**注意**: 方法1を使用する場合、初回起動時のみスキーマが作成されます。既存のテーブルがある場合はスキップされます。

## 環境変数の一覧

### バックエンド

| 変数名 | 説明 | 必須 |
|--------|------|------|
| `SPRING_DATASOURCE_URL` | PostgreSQL接続URL | ✅ |
| `SPRING_DATASOURCE_USERNAME` | データベースユーザー名 | ✅ |
| `SPRING_DATASOURCE_PASSWORD` | データベースパスワード | ✅ |
| `JWT_SECRET` | JWT署名用の秘密鍵 | ✅ |
| `SERVER_PORT` | サーバーポート（デフォルト: 8080） | ❌ |
| `SERVER_SERVLET_CONTEXT_PATH` | コンテキストパス（デフォルト: /api） | ❌ |
| `CORS_ALLOWED_ORIGINS` | 許可するCORSオリジン（カンマ区切り） | ❌ |
| `APP_DATABASE_AUTO_INIT` | データベース自動初期化（デフォルト: false） | ❌ |
| `SPRING_SQL_INIT_MODE` | Spring SQL初期化モード（always/never/embedded） | ❌ |
| `SPRING_SECURITY_USER_NAME` | デフォルト管理者ユーザー名 | ❌ |
| `SPRING_SECURITY_USER_PASSWORD` | デフォルト管理者パスワード | ❌ |

### フロントエンド

| 変数名 | 説明 | 必須 |
|--------|------|------|
| `REACT_APP_API_URL` | バックエンドAPIのURL | ✅ |
| `REACT_APP_USE_MOCK_API` | モックAPIを使用するか（デフォルト: false） | ❌ |

## トラブルシューティング

### バックエンドが起動しない

- 環境変数が正しく設定されているか確認
- データベース接続情報が正しいか確認
- ログを確認してエラーを特定

### フロントエンドがバックエンドに接続できない

- `REACT_APP_API_URL` が正しく設定されているか確認
- CORS設定が正しいか確認（バックエンドのSecurityConfigを確認）
- バックエンドが正常に起動しているか確認

### データベース接続エラー

- PostgreSQL サービスの環境変数を確認
- 接続URLの形式が正しいか確認（`jdbc:postgresql://host:port/database`）
- PostgreSQL サービスが正常に起動しているか確認（ログを確認）
- バックエンドサービスのログで「データベース接続ヘルスチェック」のメッセージを確認
- ログに「database system is ready to accept connections」が表示されていることを確認
- アプリケーション起動時に「データベース接続が正常です」が表示されることを確認

### データベーススキーマが作成されない

- `APP_DATABASE_AUTO_INIT=true` が設定されているか確認
- アプリケーションのログで初期化メッセージを確認
- 手動でスキーマを実行する場合は、PostgreSQL サービスに接続して `schema.sql` を実行
- テーブルが既に存在する場合は、初期化はスキップされます（正常な動作）

## セキュリティに関する注意事項

1. **JWT_SECRET**: 本番環境では必ず強力な秘密鍵を使用してください
2. **データベースパスワード**: 強力なパスワードを使用してください
3. **HTTPS**: Railway は自動的にHTTPSを提供します
4. **CORS**: 本番環境では適切なCORS設定を行ってください

## ヘルスチェックとモニタリング

### ヘルスチェックエンドポイント

アプリケーション起動後、以下のエンドポイントでデータベースの状態を確認できます：

- **ヘルスチェック**: `https://[backend-domain]/api/actuator/health`
- **詳細情報**: `https://[backend-domain]/api/actuator/info`

ヘルスチェックエンドポイントのレスポンスには、以下の情報が含まれます：
- データベース接続状態
- PostgreSQL バージョン情報
- アクティブ接続数
- テーブル数
- データベースサイズ

### ログの確認

- **PostgreSQL ログ**: Railway Dashboard の PostgreSQL サービス → Logs
- **バックエンドログ**: Railway Dashboard のバックエンドサービス → Logs

正常な起動時は、以下のメッセージが表示されます：
- PostgreSQL: `database system is ready to accept connections`
- バックエンド: `✓ データベース接続テスト成功`

## 参考リンク

- [Railway Documentation](https://docs.railway.app/)
- [Railway Discord](https://discord.gg/railway)

