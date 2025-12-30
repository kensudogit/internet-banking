# Railway デプロイ トラブルシューティングガイド

このドキュメントでは、Railway へのデプロイ時に発生する可能性のある問題とその解決方法を説明します。

## ログの確認方法

### PostgreSQL サービスのログ

1. Railway Dashboard で PostgreSQL サービスを選択
2. 「Logs」タブを開く
3. 以下のメッセージを確認：
   - `database system is ready to accept connections` - 正常に起動
   - `CREATE DATABASE` - データベース作成成功
   - エラーメッセージがないか確認

### バックエンドサービスのログ

1. Railway Dashboard でバックエンドサービスを選択
2. 「Logs」タブを開く
3. 以下のメッセージを確認：

#### 正常な起動ログの例

```
=== データベース接続ヘルスチェック開始 ===
データベース接続情報:
  - データベース製品: PostgreSQL
  - データベースバージョン: 17.7
  - ドライバー名: PostgreSQL JDBC Driver
  - URL: jdbc:postgresql://...
  - ユーザー名: postgres
✓ データベース接続テスト成功
  - 現在のデータベース: internet_banking
  - 既存テーブル数: 0
=== データベース接続ヘルスチェック完了 ===

データベーススキーマを初期化しています...
データベース接続が正常です。
データベーススキーマの初期化が完了しました。実行: 15, スキップ: 0
```

## よくある問題と解決方法

### 1. PostgreSQL が起動しない

**症状:**
- ログに `database system is ready to accept connections` が表示されない
- 接続エラーが発生する

**解決方法:**
1. PostgreSQL サービスのログを確認
2. サービスを再起動
3. Railway のリソース制限を確認（メモリ不足の可能性）

### 2. データベース接続エラー

**症状:**
- バックエンドログに接続エラーが表示される
- `データベース接続ヘルスチェック` が失敗する

**解決方法:**
1. 環境変数を確認：
   ```env
   SPRING_DATASOURCE_URL=jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]
   SPRING_DATASOURCE_USERNAME=[PGUSER]
   SPRING_DATASOURCE_PASSWORD=[PGPASSWORD]
   ```
2. PostgreSQL サービスの「Variables」タブで接続情報を確認
3. 接続URLの形式が正しいか確認（`jdbc:postgresql://host:port/database`）
4. PostgreSQL サービスが正常に起動しているか確認

### 3. データベーススキーマが作成されない

**症状:**
- テーブルが存在しない
- アプリケーションが起動するが、データベース操作でエラーが発生

**解決方法:**
1. 環境変数 `APP_DATABASE_AUTO_INIT=true` が設定されているか確認
2. バックエンドログで「データベーススキーマを初期化しています...」が表示されているか確認
3. ログにエラーメッセージがないか確認
4. 手動でスキーマを実行：
   - PostgreSQL サービスの「Connect」タブで接続情報を取得
   - ローカルから接続して `schema.sql` を実行

### 4. アプリケーションが起動しない

**症状:**
- デプロイが失敗する
- ログにエラーが表示される

**解決方法:**
1. ログを確認してエラーメッセージを特定
2. 環境変数が正しく設定されているか確認
3. Dockerfile が正しくビルドされているか確認
4. Java バージョンが正しいか確認（Java 21 が必要）

### 5. CORS エラー

**症状:**
- フロントエンドからバックエンドAPIにアクセスできない
- ブラウザのコンソールにCORSエラーが表示される

**解決方法:**
1. バックエンドの環境変数 `CORS_ALLOWED_ORIGINS` を確認
2. フロントエンドの公開URLが含まれているか確認
3. 複数のオリジンを許可する場合は、カンマ区切りで指定：
   ```env
   CORS_ALLOWED_ORIGINS=https://frontend-xxx.railway.app,https://another-domain.com
   ```

### 6. フロントエンドがバックエンドに接続できない

**症状:**
- APIリクエストが失敗する
- 404エラーが発生する

**解決方法:**
1. フロントエンドの環境変数 `REACT_APP_API_URL` を確認
2. バックエンドの公開URLが正しいか確認
3. バックエンドが正常に起動しているか確認
4. バックエンドのログでリクエストが届いているか確認

## ログレベルの調整

問題の特定が難しい場合は、ログレベルを調整してより詳細な情報を取得できます。

### バックエンドのログレベル設定

環境変数に以下を追加：

```env
LOGGING_LEVEL_COM_BANKING_INTERNETBANKING=DEBUG
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK=DEBUG
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_JDBC=DEBUG
```

### データベース接続の詳細ログ

```env
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_JDBC_DATASOURCE=DEBUG
LOGGING_LEVEL_ORG_HIBERNATE=DEBUG
```

## デバッグ手順

1. **PostgreSQL サービスの確認**
   - ログで正常に起動しているか確認
   - 接続情報を確認

2. **バックエンドサービスの確認**
   - ログで起動プロセスを確認
   - データベース接続ヘルスチェックの結果を確認
   - データベース初期化のログを確認

3. **フロントエンドサービスの確認**
   - ログでビルドが成功しているか確認
   - 環境変数が正しく設定されているか確認

4. **環境変数の確認**
   - すべての必須環境変数が設定されているか確認
   - 値が正しいか確認（特にURLやパスワード）

## サポート

問題が解決しない場合は、以下を確認してください：

1. [Railway Documentation](https://docs.railway.app/)
2. [Railway Discord](https://discord.gg/railway)
3. プロジェクトのログを確認
4. GitHub Issues で既存の問題を検索

