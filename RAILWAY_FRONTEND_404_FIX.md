# Railway フロントエンド404エラー修正ガイド

## 問題
`https://internet-banking-frontend-production.up.railway.app` にアクセスすると、Railwayの404エラーページが表示される。

## 原因
フロントエンドサービスがRailwayにデプロイされていない、またはドメインが正しく設定されていない可能性があります。

## 解決方法

### ステップ1: フロントエンドサービスの確認

1. [Railway Dashboard](https://railway.app/dashboard) にログイン
2. プロジェクトを選択
3. サービス一覧を確認
   - フロントエンドサービス（例: `frontend`）が存在するか確認
   - 存在しない場合は、ステップ2に進む

### ステップ2: フロントエンドサービスのデプロイ

フロントエンドサービスが存在しない場合：

1. プロジェクト内で「**+ New**」をクリック
2. 「**GitHub Repo**」を選択し、同じリポジトリを選択
3. サービス名を「**frontend**」に設定（任意）

#### Settings タブの設定

- **Root Directory**: `frontend`
- **Dockerfile Path**: `Dockerfile`
- **Start Command**: `serve -s build -l 3000`

#### Variables タブの設定

バックエンドの公開URLを確認してから設定：

```env
# バックエンドAPIのURL（バックエンドの公開URLを設定）
REACT_APP_API_URL=https://internet-banking-production-b084.up.railway.app/api

# モックAPIを無効化
REACT_APP_USE_MOCK_API=false
```

4. デプロイを開始（自動的に開始されます）

### ステップ3: 公開ドメインの設定

フロントエンドサービスが存在する場合：

1. フロントエンドサービスを選択
2. 「**Settings**」タブを開く
3. 「**Networking**」セクションを確認
4. 公開ドメインが設定されていない場合：
   - 「**Generate Domain**」をクリック
   - 生成されたドメインを確認（例: `internet-banking-frontend-production.up.railway.app`）

### ステップ4: デプロイメントの確認

1. フロントエンドサービスを選択
2. 「**Deployments**」タブを開く
3. 最新のデプロイメントの状態を確認：
   - ✅ **Success**: デプロイ成功
   - ❌ **Failed**: デプロイ失敗（ログを確認）
   - ⏳ **Building**: ビルド中

### ステップ5: ログの確認（デプロイが失敗している場合）

1. フロントエンドサービスを選択
2. 「**Deployments**」タブを開く
3. 失敗したデプロイメントをクリック
4. 「**View Logs**」をクリックしてエラーを確認

よくあるエラー：
- `Dockerfile not found`: Root Directoryが正しく設定されていない
- `npm install failed`: 依存関係のインストールエラー
- `npm run build failed`: ビルドエラー

### ステップ6: 環境変数の更新

フロントエンドが正常にデプロイされたら：

1. フロントエンドサービスの「**Variables**」タブを開く
2. `REACT_APP_API_URL` を確認・更新：
   ```
   REACT_APP_API_URL=https://internet-banking-production-b084.up.railway.app/api
   ```
3. バックエンドサービスの「**Variables**」タブを開く
4. `FRONTEND_URL` を設定（フロントエンドの公開URL）：
   ```
   FRONTEND_URL=https://internet-banking-frontend-production.up.railway.app
   ```
5. 両方のサービスを再デプロイ

### ステップ7: 動作確認

1. フロントエンドの公開URLにアクセス
2. 正常に表示されることを確認
3. バックエンドのルートURL（`https://internet-banking-production-b084.up.railway.app/`）にアクセス
4. フロントエンドにリダイレクトされることを確認

## トラブルシューティング

### 問題: デプロイが失敗する

**解決方法:**
- ログを確認してエラー内容を特定
- Root Directoryが `frontend` に設定されているか確認
- Dockerfile Pathが `Dockerfile` に設定されているか確認
- Start Commandが `serve -s build -l 3000` に設定されているか確認

### 問題: デプロイは成功したが404エラーが表示される

**解決方法:**
- 公開ドメインが正しく設定されているか確認
- サービスが正常に起動しているか確認（Deploymentsタブで確認）
- ポートが正しく設定されているか確認（`serve -s build -l 3000`）

### 問題: フロントエンドは表示されるがAPI接続エラーが発生する

**解決方法:**
- `REACT_APP_API_URL` 環境変数が正しく設定されているか確認
- バックエンドのCORS設定を確認
- バックエンドの公開URLが正しいか確認

## 参考

- [Railway デプロイガイド](./RAILWAY_DEPLOY.md)
- [Railway クイックスタート](./RAILWAY_QUICK_START.md)
- [Railway 完全デプロイガイド](./RAILWAY_DEPLOY_COMPLETE.md)
