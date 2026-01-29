# Railway環境変数修正ガイド

## 🔴 問題

Railway Dashboardで環境変数が間違って設定されています：

### フロントエンドサービス（`internet-banking-front`）

**現在の設定（間違い）**:
- `REACT_APP_API_URL`: `https://internet-banking-front-production.up.railway.app/api` ❌

**正しい設定**:
- `REACT_APP_API_URL`: `https://internet-banking-production-b084.up.railway.app/api` ✅

### バックエンドサービス（`internet-banking`）

**現在の設定（確認が必要）**:
- `CORS_ALLOWED_ORIGINS`: `https://internet-banking-front-production.up.railway.app/api` ❌

**正しい設定**:
- `CORS_ALLOWED_ORIGINS`: `https://internet-banking-front-production.up.railway.app,https://*.railway.app,https://*.up.railway.app` ✅

## ✅ 修正手順

### ステップ1: バックエンドサービスの公開URLを確認

1. Railway Dashboardでプロジェクトを開く
2. **`internet-banking`**サービス（バックエンド）を選択
3. 「**Settings**」タブ → 「**Networking**」セクション
4. **公開URLをコピー**（例: `https://internet-banking-production-b084.up.railway.app`）

### ステップ2: フロントエンドサービスの環境変数を修正

1. Railway Dashboardで**`internet-banking-front`**サービスを選択
2. 「**Variables**」タブを開く
3. **`REACT_APP_API_URL`を編集**:
   - 現在の値: `https://internet-banking-front-production.up.railway.app/api` ❌
   - **正しい値**: `https://internet-banking-production-b084.up.railway.app/api` ✅
   - （バックエンドの公開URL + `/api`）
4. **保存**

### ステップ3: バックエンドサービスのCORS設定を修正

1. Railway Dashboardで**`internet-banking`**サービスを選択
2. 「**Variables**」タブを開く
3. **`CORS_ALLOWED_ORIGINS`を編集**:
   - 現在の値: `https://internet-banking-front-production.up.railway.app/api` ❌
   - **正しい値**: `https://internet-banking-front-production.up.railway.app,https://*.railway.app,https://*.up.railway.app` ✅
   - （フロントエンドの公開URL、末尾に`/api`は不要）
4. **保存**

### ステップ4: サービスを再デプロイ

環境変数を変更した後、両方のサービスを再デプロイ：

1. **フロントエンドサービス**:
   - 「**Deployments**」タブ → 最新のデプロイメント → 「**Redeploy**」

2. **バックエンドサービス**:
   - 「**Deployments**」タブ → 最新のデプロイメント → 「**Redeploy**」

### ステップ5: 動作確認

1. フロントエンドアプリケーションを開く
2. ブラウザの開発者ツール（F12）を開く
3. 「**Console**」タブで以下を確認：
   ```
   API_BASE_URL: https://internet-banking-production-b084.up.railway.app/api
   ```
4. ログインを試行して、エラーが発生しないことを確認

## 📋 環境変数の正しい設定値

### フロントエンドサービス（`internet-banking-front`）

| 変数名 | 値 | 説明 |
|--------|-----|------|
| `REACT_APP_API_URL` | `https://internet-banking-production-b084.up.railway.app/api` | バックエンドの公開URL + `/api` |
| `REACT_APP_USE_MOCK_API` | `false` | モックAPIを無効化 |

### バックエンドサービス（`internet-banking`）

| 変数名 | 値 | 説明 |
|--------|-----|------|
| `CORS_ALLOWED_ORIGINS` | `https://internet-banking-front-production.up.railway.app,https://*.railway.app,https://*.up.railway.app` | フロントエンドの公開URL（末尾に`/api`は不要） |

## ⚠️ 重要な注意事項

1. **`REACT_APP_API_URL`はバックエンドのURLを指定**
   - ❌ 間違い: `https://internet-banking-front-production.up.railway.app/api`（フロントエンドのURL）
   - ✅ 正しい: `https://internet-banking-production-b084.up.railway.app/api`（バックエンドのURL）

2. **`CORS_ALLOWED_ORIGINS`はフロントエンドのURLを指定**
   - ❌ 間違い: `https://internet-banking-front-production.up.railway.app/api`（末尾に`/api`が不要）
   - ✅ 正しい: `https://internet-banking-front-production.up.railway.app`（フロントエンドのURLのみ）

3. **環境変数を変更した後は必ず再デプロイ**
   - 環境変数の変更は、サービスを再デプロイしないと反映されません

## 🔍 確認方法

### Railway Dashboardで確認

1. 各サービスの「**Variables**」タブで環境変数を確認
2. 値が正しいことを確認

### ブラウザのコンソールで確認

1. フロントエンドアプリケーションを開く
2. 開発者ツール（F12）を開く
3. 「**Console**」タブで以下を確認：
   ```
   API_BASE_URL: https://internet-banking-production-b084.up.railway.app/api
   ```
   - この値がバックエンドのURLになっていれば正しい設定です

## トラブルシューティング

### 環境変数が反映されない場合

1. サービスを再デプロイしたか確認
2. 環境変数の名前が正しいか確認（`REACT_APP_API_URL`）
3. ビルドログで環境変数が正しく読み込まれているか確認

### まだエラーが発生する場合

1. バックエンドサービスのステータスを確認（「Online」になっているか）
2. バックエンドのログを確認（エラーがないか）
3. ブラウザのNetworkタブでリクエストを確認（CORSエラーがないか）
