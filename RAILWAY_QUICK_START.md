# Railway クイックスタートガイド

このガイドでは、Railway にインターネットバンキングシステムを**5分で**デプロイする手順を説明します。

## 🚀 クイックデプロイ手順

### 1. Railway プロジェクトの作成（1分）

1. [Railway Dashboard](https://railway.app/dashboard) にログイン
2. 「**New Project**」→「**Deploy from GitHub repo**」
3. リポジトリを選択

### 2. PostgreSQL データベースの追加（30秒）

1. 「**+ New**」→「**Database**」→「**Add PostgreSQL**」
2. 完了！

### 3. バックエンドのデプロイ（2分）

1. 「**+ New**」→「**GitHub Repo**」→ 同じリポジトリを選択

#### Settings タブ
- **Root Directory**: `backend`
- **Dockerfile Path**: `Dockerfile`
- **Start Command**: `java -jar app.jar`

#### Variables タブ（環境変数を設定）

PostgreSQL サービスの Variables から値をコピーして、以下を設定：

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]
SPRING_DATASOURCE_USERNAME=[PGUSER]
SPRING_DATASOURCE_PASSWORD=[PGPASSWORD]
JWT_SECRET=your-secret-key-minimum-32-characters-long
SPRING_SQL_INIT_MODE=always
CORS_ALLOWED_ORIGINS=https://*.railway.app
```

### 4. フロントエンドのデプロイ（1分）

1. 「**+ New**」→「**GitHub Repo**」→ 同じリポジトリを選択

#### Settings タブ
- **Root Directory**: `frontend`
- **Dockerfile Path**: `Dockerfile`
- **Start Command**: `serve -s build -l 3000`

#### Variables タブ（一時的な設定）

```env
REACT_APP_API_URL=https://[backend-service-name].railway.app/api
REACT_APP_USE_MOCK_API=false
```

### 5. 公開ドメインの設定（30秒）

1. **バックエンド**: Settings → Networking → Generate Domain
2. **フロントエンド**: Settings → Networking → Generate Domain

### 6. 環境変数の更新（1分）

#### フロントエンドの Variables を更新
```env
REACT_APP_API_URL=https://[backend-public-domain]/api
```

#### バックエンドの Variables を更新
```env
CORS_ALLOWED_ORIGINS=https://[frontend-public-domain]
```

### 7. 再デプロイ（自動）

環境変数を更新すると、自動的に再デプロイが開始されます。

## ✅ 完了！

フロントエンドの公開URLにアクセスして、アプリケーションが動作していることを確認してください。

## 📚 詳細な手順

より詳細な手順やトラブルシューティングについては、`RAILWAY_DEPLOY_COMPLETE.md` を参照してください。

## 🔧 環境変数の取得方法

### PostgreSQL の接続情報を取得

1. Railway Dashboard → PostgreSQL サービス
2. 「**Variables**」タブを開く
3. 以下の値をコピー：
   - `PGHOST`
   - `PGPORT`
   - `PGUSER`
   - `PGPASSWORD`
   - `PGDATABASE`

### バックエンドの公開URLを取得

1. Railway Dashboard → バックエンドサービス
2. 「**Settings**」タブ → 「**Networking**」セクション
3. 「**Generate Domain**」をクリック
4. 生成されたドメインをコピー（例: `backend-production.up.railway.app`）

### フロントエンドの公開URLを取得

1. Railway Dashboard → フロントエンドサービス
2. 「**Settings**」タブ → 「**Networking**」セクション
3. 「**Generate Domain**」をクリック
4. 生成されたドメインをコピー（例: `frontend-production.up.railway.app`）
