# Railway 完全公開モード デプロイ手順

このドキュメントでは、インターネットバンキングシステムを Railway に**完全公開モード**でデプロイする手順を説明します。

## 📋 前提条件

- Railway アカウント（[https://railway.app](https://railway.app)）
- GitHub リポジトリにコードがプッシュされていること
- Railway CLI はオプション（Web UIでも可能）

## 🚀 デプロイ手順

### ステップ1: Railway プロジェクトの作成

1. [Railway Dashboard](https://railway.app/dashboard) にログイン
2. 「**New Project**」をクリック
3. 「**Deploy from GitHub repo**」を選択し、リポジトリを選択

### ステップ2: PostgreSQL データベースサービスの追加

1. プロジェクト内で「**+ New**」をクリック
2. 「**Database**」→「**Add PostgreSQL**」を選択
3. PostgreSQL サービスが作成されます
4. PostgreSQL サービスの「**Variables**」タブで、以下の環境変数を確認・コピー：
   - `PGHOST`
   - `PGPORT`
   - `PGUSER`
   - `PGPASSWORD`
   - `PGDATABASE`
   - `DATABASE_URL`（オプション）

### ステップ3: バックエンドサービスのデプロイ

1. プロジェクト内で「**+ New**」をクリック
2. 「**GitHub Repo**」を選択し、同じリポジトリを選択
3. サービス名を「**backend**」に設定（任意）

#### バックエンド設定（Settings タブ）

- **Root Directory**: `backend`
- **Dockerfile Path**: `Dockerfile`
- **Start Command**: `java -jar app.jar`

#### バックエンド環境変数（Variables タブ）

以下の環境変数を設定します。`[PGHOST]`, `[PGPORT]` などは、PostgreSQL サービスの Variables から取得した値に置き換えてください。

```env
# データベース設定（PostgreSQL サービスの環境変数から取得）
SPRING_DATASOURCE_URL=jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]?connectTimeout=10&socketTimeout=30
SPRING_DATASOURCE_USERNAME=[PGUSER]
SPRING_DATASOURCE_PASSWORD=[PGPASSWORD]

# JWT設定（本番環境では強力な秘密鍵を使用）
JWT_SECRET=your-very-long-and-secure-secret-key-change-this-in-production-minimum-32-characters

# サーバー設定
# 注意: SERVER_PORT は設定しないでください。Railwayが自動的に $PORT を設定します。
# application.yml で ${PORT:8080} を使用することで、Railwayのポートを自動的に使用します。

# CORS設定（フロントエンドの公開URLを後で設定）
# 一時的にワイルドカードを使用（後で更新）
CORS_ALLOWED_ORIGINS=https://*.railway.app

# データベース自動初期化
SPRING_SQL_INIT_MODE=always
APP_DATABASE_AUTO_INIT=true

# セキュリティ設定
SPRING_SECURITY_USER_NAME=admin
SPRING_SECURITY_USER_PASSWORD=admin

# ログ設定
LOGGING_LEVEL_COM_BANKING_INTERNETBANKING=INFO
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY=INFO
```

4. デプロイを開始（自動的に開始されます）

### ステップ4: フロントエンドサービスのデプロイ

1. プロジェクト内で「**+ New**」をクリック
2. 「**GitHub Repo**」を選択し、同じリポジトリを選択
3. サービス名を「**frontend**」に設定（任意）

#### フロントエンド設定（Settings タブ）

- **Root Directory**: `frontend`
- **Dockerfile Path**: `Dockerfile`
- **Start Command**: `serve -s build -l 3000`

#### フロントエンド環境変数（Variables タブ）

**重要**: バックエンドの公開URLが確定するまで、一時的にプレースホルダーを使用します。

```env
# バックエンドAPIのURL（後で更新）
REACT_APP_API_URL=https://[backend-service-name].railway.app/api

# モックAPIを無効化
REACT_APP_USE_MOCK_API=false
```

4. デプロイを開始（自動的に開始されます）

### ステップ5: 公開ドメインの設定

#### 5.1 バックエンドの公開

1. バックエンドサービスを選択
2. 「**Settings**」タブを開く
3. 「**Networking**」セクションで「**Generate Domain**」をクリック
4. 公開ドメインが生成されます（例: `backend-production.up.railway.app`）
5. ドメインをコピー

#### 5.2 フロントエンドの公開

1. フロントエンドサービスを選択
2. 「**Settings**」タブを開く
3. 「**Networking**」セクションで「**Generate Domain**」をクリック
4. 公開ドメインが生成されます（例: `frontend-production.up.railway.app`）
5. ドメインをコピー

### ステップ6: 環境変数の更新

#### 6.1 フロントエンドの環境変数更新

フロントエンドサービスの「**Variables**」タブで、`REACT_APP_API_URL` を更新：

```env
REACT_APP_API_URL=https://[backend-public-domain]/api
```

例：
```env
REACT_APP_API_URL=https://backend-production.up.railway.app/api
```

**重要**: 環境変数を更新した後、フロントエンドサービスを再デプロイする必要があります。

#### 6.2 バックエンドのCORS設定更新

バックエンドサービスの「**Variables**」タブで、`CORS_ALLOWED_ORIGINS` を更新：

```env
CORS_ALLOWED_ORIGINS=https://[frontend-public-domain]
```

例：
```env
CORS_ALLOWED_ORIGINS=https://frontend-production.up.railway.app
```

複数のオリジンを許可する場合は、カンマ区切りで指定：

```env
CORS_ALLOWED_ORIGINS=https://frontend-production.up.railway.app,https://another-domain.com
```

**重要**: 環境変数を更新した後、バックエンドサービスを再デプロイする必要があります。

### ステップ7: 再デプロイ

環境変数を更新した後、両方のサービスを再デプロイします：

1. バックエンドサービス → 「**Deployments**」タブ → 「**Redeploy**」
2. フロントエンドサービス → 「**Deployments**」タブ → 「**Redeploy**」

または、GitHub にプッシュすると自動的に再デプロイされます。

### ステップ8: 動作確認

1. フロントエンドの公開URLにアクセス（例: `https://frontend-production.up.railway.app`）
2. アプリケーションが正常に表示されることを確認
3. ログイン機能をテスト
4. 各ページ（ダッシュボード、口座情報、取引履歴など）が正常に動作することを確認

## 🔧 環境変数の完全なリスト

### PostgreSQL（自動設定）

Railway が自動的に設定します。手動で設定する必要はありません。

### バックエンド

| 変数名 | 説明 | 必須 | デフォルト値 |
|--------|------|------|-------------|
| `SPRING_DATASOURCE_URL` | PostgreSQL接続URL | ✅ | - |
| `SPRING_DATASOURCE_USERNAME` | データベースユーザー名 | ✅ | - |
| `SPRING_DATASOURCE_PASSWORD` | データベースパスワード | ✅ | - |
| `JWT_SECRET` | JWT署名用の秘密鍵 | ✅ | - |
| `SERVER_PORT` | サーバーポート | ❌ | 8080 |
| `CORS_ALLOWED_ORIGINS` | 許可するCORSオリジン（カンマ区切り） | ❌ | `https://*.railway.app` |
| `SPRING_SQL_INIT_MODE` | Spring SQL初期化モード | ❌ | `always` |
| `APP_DATABASE_AUTO_INIT` | データベース自動初期化 | ❌ | `true` |
| `SPRING_SECURITY_USER_NAME` | デフォルト管理者ユーザー名 | ❌ | `admin` |
| `SPRING_SECURITY_USER_PASSWORD` | デフォルト管理者パスワード | ❌ | `admin` |
| `LOGGING_LEVEL_COM_BANKING_INTERNETBANKING` | ログレベル | ❌ | `INFO` |

### フロントエンド

| 変数名 | 説明 | 必須 | デフォルト値 |
|--------|------|------|-------------|
| `REACT_APP_API_URL` | バックエンドAPIのURL | ✅ | - |
| `REACT_APP_USE_MOCK_API` | モックAPIを使用するか | ❌ | `false` |

## 🐛 トラブルシューティング

### バックエンドが起動しない

1. **環境変数の確認**
   - PostgreSQL の接続情報が正しいか確認
   - `SPRING_DATASOURCE_URL` の形式が正しいか確認（`jdbc:postgresql://host:port/database`）

2. **ログの確認**
   - Railway Dashboard → バックエンドサービス → 「**Logs**」タブ
   - エラーメッセージを確認

3. **データベース接続の確認**
   - PostgreSQL サービスが正常に起動しているか確認
   - PostgreSQL サービスのログで「database system is ready to accept connections」が表示されていることを確認

### フロントエンドがバックエンドに接続できない

1. **環境変数の確認**
   - `REACT_APP_API_URL` が正しく設定されているか確認
   - バックエンドの公開URLが正しいか確認

2. **CORS設定の確認**
   - バックエンドの `CORS_ALLOWED_ORIGINS` にフロントエンドのURLが含まれているか確認
   - ブラウザのコンソールでCORSエラーが表示されていないか確認

3. **バックエンドの起動確認**
   - バックエンドの公開URLに直接アクセスして、APIが応答するか確認
   - 例: `https://backend-production.up.railway.app/api/accounts/user/1`

### データベーススキーマが作成されない

1. **環境変数の確認**
   - `SPRING_SQL_INIT_MODE=always` が設定されているか確認
   - `APP_DATABASE_AUTO_INIT=true` が設定されているか確認

2. **ログの確認**
   - バックエンドのログで「データベース初期化」のメッセージを確認
   - エラーが表示されていないか確認

3. **手動実行**
   - PostgreSQL サービスに接続して、`schema.sql` を手動で実行

### ビルドエラー

#### エラー: "/src": not found

このエラーは、Railwayでバックエンドをビルドする際に `src` ディレクトリが見つからない場合に発生します。

**原因:**
- Root Directory が正しく設定されていない
- ビルドコンテキストがプロジェクトルートになっている

**解決方法:**

1. **Railway Dashboard で設定を確認**
   - バックエンドサービスを選択
   - 「**Settings**」タブを開く
   - 「**Source**」セクションで以下を確認：
     - **Root Directory**: `backend` に設定されているか確認
     - **Dockerfile Path**: `Dockerfile` に設定されているか確認

2. **設定が正しい場合の対処法**
   - サービスを削除して再作成
   - または、GitHub リポジトリに新しいコミットをプッシュして再デプロイ

3. **ビルドログの確認**
   - Railway Dashboard → サービス → 「**Deployments**」タブ → ビルドログを確認
   - エラーメッセージの詳細を確認

#### その他のビルドエラー

1. **Dockerfile の確認**
   - Root Directory が正しく設定されているか確認
   - Dockerfile Path が正しいか確認

2. **ビルドログの確認**
   - Railway Dashboard → サービス → 「**Deployments**」タブ → ビルドログを確認

## 🔒 セキュリティに関する注意事項

1. **JWT_SECRET**: 本番環境では必ず強力な秘密鍵（32文字以上）を使用してください
2. **データベースパスワード**: Railway が自動生成する強力なパスワードを使用してください
3. **HTTPS**: Railway は自動的にHTTPSを提供します
4. **CORS**: 本番環境では、フロントエンドのURLのみを許可してください（ワイルドカードは避ける）
5. **環境変数**: 機密情報は環境変数で管理し、コードに直接書き込まないでください

## 📊 モニタリング

### ヘルスチェックエンドポイント

- **バックエンドヘルスチェック**: `https://[backend-domain]/api/actuator/health`
- **バックエンド情報**: `https://[backend-domain]/api/actuator/info`

### ログの確認

- **PostgreSQL ログ**: Railway Dashboard → PostgreSQL サービス → 「**Logs**」タブ
- **バックエンドログ**: Railway Dashboard → バックエンドサービス → 「**Logs**」タブ
- **フロントエンドログ**: Railway Dashboard → フロントエンドサービス → 「**Logs**」タブ

## 📚 参考リンク

- [Railway Documentation](https://docs.railway.app/)
- [Railway Discord](https://discord.gg/railway)
- [Railway Pricing](https://railway.app/pricing)

## ✅ デプロイチェックリスト

- [ ] PostgreSQL サービスが作成され、起動している
- [ ] バックエンドサービスが作成され、環境変数が設定されている
- [ ] フロントエンドサービスが作成され、環境変数が設定されている
- [ ] バックエンドの公開ドメインが生成されている
- [ ] フロントエンドの公開ドメインが生成されている
- [ ] フロントエンドの `REACT_APP_API_URL` がバックエンドのURLに設定されている
- [ ] バックエンドの `CORS_ALLOWED_ORIGINS` がフロントエンドのURLに設定されている
- [ ] 両方のサービスが再デプロイされている
- [ ] フロントエンドにアクセスして、アプリケーションが正常に動作している
