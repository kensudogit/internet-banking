# Railway テーブル不存在エラー 解決方法

## 🔴 エラー内容

```
Schema-validation: missing table [accounts]
```

このエラーは、データベースにテーブルが存在しないために発生しています。

## ✅ 解決手順

### ステップ1: Railway Dashboard で環境変数を設定

1. [Railway Dashboard](https://railway.app/dashboard) にログイン
2. プロジェクトを選択
3. **バックエンドサービス**を選択
4. 「**Variables**」タブを開く

### ステップ2: `SPRING_SQL_INIT_MODE` を設定

以下の環境変数を追加または確認してください：

- **Key**: `SPRING_SQL_INIT_MODE`
- **Value**: `always`

**重要**: この環境変数により、アプリケーション起動時に`schema.sql`が自動的に実行され、テーブルが作成されます。

### ステップ3: その他の必須環境変数を確認

以下の環境変数も設定されていることを確認してください：

| 変数名 | 値 |
|--------|-----|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://postgres.railway.internal:5432/railway?connectTimeout=10&socketTimeout=30` |
| `SPRING_DATASOURCE_USERNAME` | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | `xduqlioTiXIZrsMeMzdTJUEjILwtOKIY` |
| `JWT_SECRET` | （64文字以上のランダムな文字列） |
| `SPRING_SQL_INIT_MODE` | `always` ⭐ **重要** |
| `CORS_ALLOWED_ORIGINS` | `https://*.railway.app` |

### ステップ4: バックエンドサービスを再デプロイ（必須）

**重要**: 環境変数を設定した後、**必ず**再デプロイしてください。

1. バックエンドサービス → 「**Deployments**」タブ
2. 「**Redeploy**」ボタンをクリック
3. ビルドログと実行ログを確認

### ステップ5: ログを確認

再デプロイ後、ログを確認：

1. バックエンドサービス → 「**Logs**」タブ
2. 以下のメッセージが表示されることを確認：

**✅ 正常な起動:**
```
データベーススキーマを初期化しています...
データベーススキーマの初期化が完了しました。
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
Started InternetBankingApplication in X.XXX seconds
Tomcat started on port(s): [PORT番号] (http)
```

**❌ まだエラーが表示される場合:**
- `SPRING_SQL_INIT_MODE=always` が設定されているか確認
- `SPRING_DATASOURCE_URL` が正しく設定されているか確認
- 再デプロイを実行したか確認

## 🚨 よくある間違い

### 間違い1: `SPRING_SQL_INIT_MODE` が設定されていない

**問題:**
- デフォルト値が`never`のため、スキーマが初期化されない
- テーブルが存在しないため、`validate`モードでエラーが発生

**正しい方法:**
- `SPRING_SQL_INIT_MODE=always` を設定してください

### 間違い2: 環境変数を設定したが再デプロイしていない

**問題:**
- 環境変数を設定しただけでは、実行中のコンテナには反映されません
- 必ず再デプロイが必要です

**正しい方法:**
- 環境変数を設定した後、必ず「Redeploy」ボタンをクリック

### 間違い3: `SPRING_SQL_INIT_MODE` の値が間違っている

**問題:**
- `always` ではなく `never` や `embedded` が設定されている
- 大文字小文字が間違っている（`ALWAYS` ではなく `always`）

**正しい方法:**
- 値は必ず `always` （小文字）にしてください

## 📋 チェックリスト

テーブル不存在エラーを解決するために、以下を**順番に**確認してください：

1. [ ] Railway Dashboard でバックエンドサービスを選択
2. [ ] 「Variables」タブを開く
3. [ ] `SPRING_SQL_INIT_MODE` が `always` に設定されている
4. [ ] `SPRING_DATASOURCE_URL` が設定されている（`postgres.railway.internal` を使用）
5. [ ] `SPRING_DATASOURCE_USERNAME` が `postgres` に設定されている
6. [ ] `SPRING_DATASOURCE_PASSWORD` が正しく設定されている
7. [ ] `JWT_SECRET` が設定されている（64文字以上）
8. [ ] 環境変数を設定した後、「Redeploy」ボタンをクリック
9. [ ] ログに「データベーススキーマの初期化が完了しました」が表示されている
10. [ ] ログに「Started InternetBankingApplication」が表示されている
11. [ ] エラーが解消された

## 💡 補足説明

### `SPRING_SQL_INIT_MODE` の値について

- `always`: 常にスキーマを初期化（推奨：Railway環境）
- `never`: スキーマを初期化しない（デフォルト）
- `embedded`: 組み込みデータベースの場合のみ初期化

Railway環境では、PostgreSQLは別のコンテナで実行されているため、`always` を設定する必要があります。

### スキーマ初期化の流れ

1. アプリケーション起動
2. `SPRING_SQL_INIT_MODE=always` が設定されている場合
3. `schema.sql` が実行され、テーブルが作成される
4. `sample-data.sql` が実行され、サンプルデータが挿入される（オプション）
5. Hibernateが`validate`モードでテーブルの存在を確認
6. アプリケーションが正常に起動

## 📚 参考資料

- `RAILWAY_502_FIX_NOW.md` - 502エラー解決手順
- `RAILWAY_ENV_SETUP.md` - 環境変数設定の詳細ガイド
- `RAILWAY_DATABASE_CONNECTION_FIX.md` - データベース接続エラーの解決方法
