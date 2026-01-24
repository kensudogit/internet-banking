# Railway 502 Bad Gateway エラーの解決方法

このドキュメントでは、Railwayでデプロイしたアプリケーションで発生する502 Bad Gatewayエラーの解決方法を説明します。

## 🔴 エラーの詳細

**エラーメッセージ（ブラウザコンソール）:**
```
Failed to load resource: the server responded with a status of 502 ()
/favicon.ico:1 Failed to load resource: the server responded with a status of 502 ()
```

**症状:**
- フロントエンドにアクセスできるが、APIリクエストが502エラーを返す
- バックエンドの公開URLにアクセスできない
- ブラウザコンソールに502エラーが表示される

## 🔍 原因の分析

502 Bad Gatewayエラーは、**バックエンドサーバーが起動していない、または正しく動作していない**場合に発生します。

### よくある原因

1. **バックエンドサービスが起動していない**
   - サービスが「Stopped」または「Failed」状態
   - ビルドエラーでデプロイが失敗している

2. **データベース接続エラー**
   - PostgreSQL サービスが起動していない
   - データベース接続情報が間違っている

3. **アプリケーションの起動エラー**
   - Javaアプリケーションがクラッシュしている
   - 環境変数が不足している

4. **ポート設定の問題**
   - Railwayが自動的に割り当てるポートと設定が一致していない

## ✅ 解決方法（ステップバイステップ）

### ステップ1: バックエンドサービスの状態を確認

1. Railway Dashboard にログイン
2. プロジェクトを選択
3. **バックエンドサービス**を選択
4. サービスの状態を確認：
   - **Running**: 正常に動作している（他の原因を確認）
   - **Stopped**: 停止している（再起動が必要）
   - **Failed**: 失敗している（ログを確認）

### ステップ2: バックエンドのログを確認

1. バックエンドサービスを選択
2. 「**Logs**」タブを開く
3. エラーメッセージを確認

**確認すべきエラー:**

#### データベース接続エラー
```
Connection refused
Could not connect to database
```

**解決方法:**
- PostgreSQL サービスが起動しているか確認
- 環境変数 `SPRING_DATASOURCE_URL` が正しいか確認
- PostgreSQL サービスの Variables から正しい値をコピーしているか確認

#### ポート設定エラー
```
Port already in use
Address already in use
```

**解決方法:**
- `SERVER_PORT` 環境変数を削除（Railwayが自動的にポートを割り当てます）
- または、`SERVER_PORT=$PORT` に設定

#### アプリケーション起動エラー
```
Error starting ApplicationContext
BeanCreationException
```

**解決方法:**
- ログの詳細を確認
- 環境変数が不足していないか確認
- 必須の環境変数が設定されているか確認

### ステップ3: 環境変数の確認

バックエンドサービスの「**Variables**」タブで、以下を確認：

#### 必須の環境変数

| 変数名 | 説明 | 確認方法 |
|--------|------|---------|
| `SPRING_DATASOURCE_URL` | PostgreSQL接続URL | PostgreSQL サービスの Variables から取得 |
| `SPRING_DATASOURCE_USERNAME` | データベースユーザー名 | PostgreSQL サービスの Variables から取得 |
| `SPRING_DATASOURCE_PASSWORD` | データベースパスワード | PostgreSQL サービスの Variables から取得 |
| `JWT_SECRET` | JWT署名用の秘密鍵 | 32文字以上のランダムな文字列 |

#### ポート設定

**重要**: Railwayは自動的にポートを割り当て、`$PORT` 環境変数に設定します。

**application.yml の設定:**
```yaml
server:
  port: ${PORT:8080}
```

これにより、Railwayが割り当てたポートを使用し、ローカル開発環境では8080を使用します。

**環境変数の設定:**
- `SERVER_PORT` 環境変数は**設定しないでください**（削除してください）
- Railwayが自動的に `$PORT` 環境変数を設定します
- `application.yml` で `${PORT:8080}` を使用することで、Railwayのポートを自動的に使用します

### ステップ4: PostgreSQL サービスの確認

1. PostgreSQL サービスを選択
2. サービスの状態を確認（「Running」になっているか）
3. 「**Logs**」タブで以下を確認：
   - `database system is ready to accept connections` が表示されているか

### ステップ5: バックエンドの再起動

1. バックエンドサービスを選択
2. 「**Deployments**」タブを開く
3. 「**Redeploy**」ボタンをクリック
4. ビルドログと実行ログを確認

### ステップ6: ヘルスチェックエンドポイントの確認

バックエンドの公開URLに直接アクセスして、応答があるか確認：

```
https://[backend-domain]/api/actuator/health
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

## 🚨 よくある問題と解決方法

### 問題1: バックエンドが起動しない

**症状:**
- ログに「Application failed to start」が表示される
- サービスが「Failed」状態

**解決方法:**
1. ログの詳細を確認
2. 環境変数が正しく設定されているか確認
3. データベース接続情報が正しいか確認
4. 必須の環境変数が不足していないか確認

### 問題2: データベース接続エラー

**症状:**
- ログに「Connection refused」が表示される
- アプリケーションが起動しない

**解決方法:**
1. PostgreSQL サービスが起動しているか確認
2. PostgreSQL サービスの Variables から接続情報を確認
3. バックエンドの環境変数を更新
4. バックエンドサービスを再起動

### 問題3: ポート設定の問題

**症状:**
- ログに「Port already in use」が表示される
- アプリケーションが起動しない

**解決方法:**
1. `SERVER_PORT` 環境変数を削除
2. Railwayが自動的にポートを割り当てるようにする
3. バックエンドサービスを再起動

### 問題4: ビルドは成功するが起動しない

**症状:**
- ビルドログにエラーがない
- しかし、実行ログにエラーが表示される

**解決方法:**
1. 実行ログを確認
2. 環境変数が正しく設定されているか確認
3. JARファイルが正しく生成されているか確認
4. Start Command が正しいか確認（`java -jar app.jar`）

## 📋 チェックリスト

502エラーを解決するために、以下を確認してください：

- [ ] バックエンドサービスが「Running」状態になっている
- [ ] PostgreSQL サービスが「Running」状態になっている
- [ ] バックエンドのログにエラーが表示されていない
- [ ] 環境変数が正しく設定されている
- [ ] データベース接続情報が正しい
- [ ] `SERVER_PORT` 環境変数が設定されていない（または `$PORT` を使用）
- [ ] ヘルスチェックエンドポイントにアクセスできる
- [ ] バックエンドの公開URLが正しく生成されている

## 🔧 デバッグのヒント

### ログの確認方法

1. **ビルドログ**: Railway Dashboard → バックエンドサービス → Deployments → 最新のデプロイメント → Build Logs
2. **実行ログ**: Railway Dashboard → バックエンドサービス → Logs タブ

### 正常な起動時のログ

正常に起動している場合、ログには以下のようなメッセージが表示されます：

```
Started InternetBankingApplication in X.XXX seconds
Tomcat started on port(s): 8080 (http)
```

### エラー時のログ

エラーが発生している場合、ログには以下のようなメッセージが表示されます：

```
Application run failed
Error starting ApplicationContext
Connection refused
```

## 📚 参考資料

- `RAILWAY_TROUBLESHOOTING.md` - その他のトラブルシューティング
- `RAILWAY_DEPLOY_COMPLETE.md` - 完全なデプロイ手順
- [Railway Documentation](https://docs.railway.app/)

## 💡 ヒント

- 502エラーは通常、バックエンドの問題です
- まず、バックエンドサービスのログを確認してください
- 環境変数が正しく設定されているか確認してください
- PostgreSQL サービスが起動しているか確認してください
