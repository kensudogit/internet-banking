# Railway テーブル不存在エラー 最終解決方法

## 🔴 エラー内容

```
Schema-validation: missing table [accounts]
```

このエラーは、データベースにテーブルが存在しないために発生しています。

## ✅ 解決手順（最重要）

### ステップ1: Railway Dashboard で環境変数を設定

1. [Railway Dashboard](https://railway.app/dashboard) にログイン
2. プロジェクトを選択
3. **バックエンドサービス**を選択
4. 「**Variables**」タブを開く

### ステップ2: `SPRING_SQL_INIT_MODE` を設定（必須）

以下の環境変数を**必ず**設定してください：

- **Key**: `SPRING_SQL_INIT_MODE`
- **Value**: `always` ⭐ **重要：必ず小文字の `always` にしてください**

**重要**: 
- この環境変数により、Spring Bootの標準SQL初期化機能が実行され、`schema.sql`が自動的に実行されます
- Spring Bootの標準機能は、Hibernateのバリデーションより**先に**実行されるため、テーブルが作成されてからバリデーションが実行されます

### ステップ3: その他の必須環境変数を確認

以下の環境変数も設定されていることを確認してください：

| 変数名 | 値 | 必須 |
|--------|-----|------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://postgres.railway.internal:5432/railway?connectTimeout=10&socketTimeout=30` | ✅ |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | ✅ |
| `SPRING_DATASOURCE_PASSWORD` | `xduqlioTiXIZrsMeMzdTJUEjILwtOKIY` | ✅ |
| `JWT_SECRET` | （64文字以上のランダムな文字列） | ✅ |
| `SPRING_SQL_INIT_MODE` | `always` ⭐ | ✅ |
| `CORS_ALLOWED_ORIGINS` | `https://*.railway.app` | 推奨 |

### ステップ4: バックエンドサービスを再デプロイ（必須）

**重要**: 環境変数を設定した後、**必ず**再デプロイしてください。

1. バックエンドサービス → 「**Deployments**」タブ
2. 「**Redeploy**」ボタンをクリック
3. ビルドログと実行ログを確認

### ステップ5: ログを確認

再デプロイ後、ログを確認：

1. バックエンドサービス → 「**Logs**」タブ
2. 以下のメッセージが表示されることを確認：

**✅ 正常な起動（Spring Bootの標準SQL初期化機能が実行された場合）:**
```
Initializing Spring embedded WebApplicationContext
Processing PersistenceUnitInfo [name: default]
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
Started InternetBankingApplication in X.XXX seconds
Tomcat started on port(s): [PORT番号] (http)
```

**✅ 正常な起動（DatabaseInitializerが実行された場合）:**
```
データベース初期化を開始します（autoInit=true）
データベース接続を確認しています...
データベース接続が正常です。
データベーススキーマを初期化しています...
データベーススキーマの初期化が完了しました。
Started InternetBankingApplication in X.XXX seconds
```

**❌ まだエラーが表示される場合:**
- `SPRING_SQL_INIT_MODE=always` が設定されているか確認（値は必ず小文字の `always`）
- `SPRING_DATASOURCE_URL` が正しく設定されているか確認
- 再デプロイを実行したか確認
- ログに「データベース自動初期化は無効です」が表示されていないか確認

## 🚨 よくある間違い

### 間違い1: `SPRING_SQL_INIT_MODE` が設定されていない

**問題:**
- デフォルト値が`never`のため、スキーマが初期化されない
- テーブルが存在しないため、`validate`モードでエラーが発生

**正しい方法:**
- `SPRING_SQL_INIT_MODE=always` を設定してください（必ず小文字）

### 間違い2: `SPRING_SQL_INIT_MODE` の値が間違っている

**問題:**
- `ALWAYS`（大文字）や `Always`（大文字小文字混在）が設定されている
- `always` ではなく `never` や `embedded` が設定されている

**正しい方法:**
- 値は必ず `always` （小文字）にしてください

### 間違い3: 環境変数を設定したが再デプロイしていない

**問題:**
- 環境変数を設定しただけでは、実行中のコンテナには反映されません
- 必ず再デプロイが必要です

**正しい方法:**
- 環境変数を設定した後、必ず「Redeploy」ボタンをクリック

## 💡 技術的な説明

### Spring Bootの標準SQL初期化機能

`spring.sql.init.mode=always` を設定すると、Spring Bootは以下の順序で処理を実行します：

1. **データソースの初期化**
2. **SQLスキーマの実行** (`schema.sql`)
3. **SQLデータの実行** (`sample-data.sql`) - オプション
4. **Hibernateのバリデーション** (`ddl-auto: validate`)

この順序により、テーブルが作成されてからバリデーションが実行されるため、エラーが発生しません。

### DatabaseInitializerの役割

`DatabaseInitializer`は、Spring Bootの標準SQL初期化機能が実行されなかった場合のフォールバックとして機能します。`app.database.auto-init=true` が設定されている場合のみ実行されます。

## 📋 チェックリスト

テーブル不存在エラーを解決するために、以下を**順番に**確認してください：

1. [ ] Railway Dashboard でバックエンドサービスを選択
2. [ ] 「Variables」タブを開く
3. [ ] `SPRING_SQL_INIT_MODE` が `always` に設定されている（必ず小文字）
4. [ ] `SPRING_DATASOURCE_URL` が設定されている（`postgres.railway.internal` を使用）
5. [ ] `SPRING_DATASOURCE_USERNAME` が `postgres` に設定されている
6. [ ] `SPRING_DATASOURCE_PASSWORD` が正しく設定されている
7. [ ] `JWT_SECRET` が設定されている（64文字以上）
8. [ ] 環境変数を設定した後、「Redeploy」ボタンをクリック
9. [ ] ログに「Started InternetBankingApplication」が表示されている
10. [ ] エラーが解消された

## 📚 参考資料

- `RAILWAY_MISSING_TABLE_FIX.md` - テーブル不存在エラーの詳細な解決方法
- `RAILWAY_502_FIX_NOW.md` - 502エラー解決手順
- `RAILWAY_ENV_SETUP.md` - 環境変数設定の詳細ガイド
