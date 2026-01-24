# Railway フロントエンド API URL 設定ガイド

## 🔴 問題

フロントエンドからAPIを呼び出した際に、以下のエラーが発生しています：

```
Error registering user: SyntaxError: Unexpected token '<', "<!doctype "... is not valid JSON
```

**原因:**
- フロントエンドの`API_BASE_URL`が`/api`（相対パス）になっている
- Railwayでは、フロントエンドとバックエンドが別々のサービスとしてデプロイされている
- 相対パス`/api`では、フロントエンドのドメインに対してリクエストが送られる
- その結果、HTML（404ページやindex.html）が返ってきてしまう

## ✅ 解決方法

### ステップ1: Railway Dashboardでフロントエンドサービスの環境変数を設定

1. [Railway Dashboard](https://railway.app/dashboard) にログイン
2. プロジェクトを選択
3. **フロントエンドサービス**を選択
4. 「**Variables**」タブを開く
5. 「**+ New Variable**」をクリック
6. 以下の環境変数を追加：

| 変数名 | 値 |
|--------|-----|
| `REACT_APP_API_URL` | `https://internet-banking-production-b084.up.railway.app/api` |

**重要:**
- バックエンドのURLは、Railway Dashboardのバックエンドサービスの「**Settings**」タブ → 「**Generate Domain**」で確認できます
- URLの末尾に`/api`を必ず含めてください
- `https://`で始まる完全なURLを指定してください

### ステップ2: フロントエンドサービスを再デプロイ

環境変数を設定した後、フロントエンドサービスを再デプロイする必要があります：

1. フロントエンドサービス → 「**Deployments**」タブ
2. 「**Redeploy**」ボタンをクリック
3. または、GitHubにプッシュして自動デプロイを待つ

### ステップ3: 確認

再デプロイ後、ブラウザの開発者ツール（F12）のコンソールで以下を確認：

```
API_BASE_URL: https://internet-banking-production-b084.up.railway.app/api
```

このように、完全なURLが表示されていれば正しく設定されています。

## 📋 環境変数の確認方法

### Railway Dashboardで確認

1. フロントエンドサービス → 「**Variables**」タブ
2. `REACT_APP_API_URL`が表示されていることを確認
3. 値が正しいことを確認

### ブラウザのコンソールで確認

1. フロントエンドアプリケーションを開く
2. 開発者ツール（F12）を開く
3. 「**Console**」タブを開く
4. 以下のログを確認：
   ```
   API_BASE_URL: https://internet-banking-production-b084.up.railway.app/api
   ```

## 🔧 トラブルシューティング

### 問題1: 環境変数が反映されない

**原因:**
- 環境変数の名前が間違っている（`REACT_APP_API_URL`である必要がある）
- 再デプロイが完了していない

**解決方法:**
1. 環境変数の名前を確認（`REACT_APP_`で始まる必要がある）
2. フロントエンドサービスを再デプロイ
3. ビルドログで環境変数が正しく読み込まれているか確認

### 問題2: CORSエラーが発生する

**原因:**
- バックエンドのCORS設定で、フロントエンドのドメインが許可されていない

**解決方法:**
1. バックエンドのCORS設定を確認
2. フロントエンドのドメインを許可リストに追加

### 問題3: バックエンドのURLがわからない

**解決方法:**
1. Railway Dashboard → バックエンドサービス → 「**Settings**」タブ
2. 「**Generate Domain**」をクリック
3. 生成されたドメインをコピー
4. 末尾に`/api`を追加して、フロントエンドの環境変数に設定

## 📝 補足

### 環境変数の命名規則

Reactアプリケーションでは、環境変数は`REACT_APP_`で始まる必要があります。これにより、ビルド時に環境変数がコードに埋め込まれます。

### ローカル開発環境

ローカル開発環境では、`REACT_APP_API_URL`が設定されていない場合、自動的に`http://localhost:8080/api`が使用されます。

### 本番環境

本番環境（Railway）では、必ず`REACT_APP_API_URL`を設定してください。設定しないと、相対パス`/api`が使用され、エラーが発生します。
