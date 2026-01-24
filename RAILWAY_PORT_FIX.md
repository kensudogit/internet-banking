# Railway ポート設定の修正方法

このドキュメントでは、Railwayで502エラーが発生する原因の一つであるポート設定の問題を解決する方法を説明します。

## 🔴 問題の詳細

**エラーメッセージ:**
```
GET https://internet-banking-production-b084.up.railway.app/ 502 (Bad Gateway)
```

**原因:**
Railwayは自動的にポートを割り当て、`$PORT` 環境変数に設定します。しかし、`application.yml` で固定のポート（8080）を設定している場合、Railwayが割り当てたポートと一致せず、502エラーが発生します。

## ✅ 解決方法

### ステップ1: application.yml を修正

`backend/src/main/resources/application.yml` を以下のように修正：

**修正前:**
```yaml
server:
  port: 8080
```

**修正後:**
```yaml
server:
  port: ${PORT:8080}
```

これにより：
- Railway環境では、`$PORT` 環境変数を使用（Railwayが自動設定）
- ローカル開発環境では、デフォルトで8080を使用

### ステップ2: 環境変数の確認

Railway Dashboard で、バックエンドサービスの環境変数を確認：

1. Railway Dashboard → バックエンドサービス → 「**Variables**」タブ
2. `SERVER_PORT` 環境変数が設定されている場合は**削除**
3. Railwayが自動的に設定する `$PORT` 環境変数は表示されません（内部で使用されます）

### ステップ3: 変更をデプロイ

1. 変更をGitHubにプッシュ：
   ```bash
   git add backend/src/main/resources/application.yml
   git commit -m "Fix: Use PORT environment variable for Railway deployment"
   git push
   ```

2. Railwayで自動的に再デプロイが開始されます

3. または、Railway Dashboard で手動で再デプロイ：
   - バックエンドサービス → 「**Deployments**」タブ → 「**Redeploy**」

### ステップ4: ログを確認

再デプロイ後、ログを確認：

1. Railway Dashboard → バックエンドサービス → 「**Logs**」タブ
2. 以下のようなメッセージが表示されることを確認：
   ```
   Tomcat started on port(s): [PORT番号] (http)
   Started InternetBankingApplication in X.XXX seconds
   ```

## 🔍 確認方法

### 正常な起動の確認

ログに以下のメッセージが表示されれば、正常に起動しています：

```
Started InternetBankingApplication in X.XXX seconds
Tomcat started on port(s): [PORT番号] (http)
```

**注意**: `[PORT番号]` は、Railwayが自動的に割り当てたポート番号です（8080ではない可能性があります）。

### ヘルスチェックエンドポイントの確認

バックエンドの公開URLにアクセスして、応答があるか確認：

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

## 🚨 よくある間違い

### 間違い1: SERVER_PORT 環境変数を設定する

**間違い:**
```env
SERVER_PORT=8080
```

**問題:**
- Railwayが自動的に割り当てたポートと一致しない
- 502エラーが発生する

**正しい方法:**
- `SERVER_PORT` 環境変数を削除
- `application.yml` で `${PORT:8080}` を使用

### 間違い2: application.yml で固定ポートを設定する

**間違い:**
```yaml
server:
  port: 8080
```

**問題:**
- Railwayが割り当てたポートと一致しない
- 502エラーが発生する

**正しい方法:**
```yaml
server:
  port: ${PORT:8080}
```

## 📋 チェックリスト

ポート設定を修正したら、以下を確認してください：

- [ ] `application.yml` で `port: ${PORT:8080}` に設定されている
- [ ] `SERVER_PORT` 環境変数が削除されている（または設定されていない）
- [ ] 変更をGitHubにプッシュした
- [ ] Railwayで再デプロイが完了した
- [ ] ログに「Tomcat started on port(s): [PORT番号]」が表示されている
- [ ] ヘルスチェックエンドポイントにアクセスできる
- [ ] 502エラーが解消された

## 📚 参考資料

- `RAILWAY_502_ERROR_FIX.md` - 502エラーの詳細な解決方法
- `RAILWAY_DEPLOY_COMPLETE.md` - 完全なデプロイ手順
- [Railway Documentation - Ports](https://docs.railway.app/reference/variables#port)

## 💡 ヒント

- Railwayは自動的にポートを割り当てます
- `$PORT` 環境変数を使用することで、Railwayのポートを自動的に使用できます
- ローカル開発環境では、デフォルトで8080を使用します
- `SERVER_PORT` 環境変数は設定しないでください
