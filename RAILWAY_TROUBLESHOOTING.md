# Railway デプロイ トラブルシューティングガイド

このドキュメントでは、Railway へのデプロイ時に発生する可能性のある問題とその解決方法を説明します。

## ログの確認方法

### PostgreSQL サービスのログ

1. Railway Dashboard で PostgreSQL サービスを選択
2. 「Logs」タブを開く
3. 以下のメッセージを確認：
   - `database system is ready to accept connections` - 正常に起動 ✅
   - `CREATE DATABASE` - データベース作成成功 ✅
   - `PostgreSQL init process complete; ready for start up.` - 初期化完了 ✅
   - `checkpoint complete` - チェックポイント正常 ✅

**注意**: ログに `[err]` タグが付いている行が多数表示されますが、これらは OpenSSL の証明書生成時の出力であり、エラーではありません。実際のエラーは `ERROR` や `FATAL` というキーワードが含まれます。

詳細なログ解釈方法については [POSTGRESQL_LOG_GUIDE.md](./POSTGRESQL_LOG_GUIDE.md) を参照してください。

### バックエンドサービスのログ

1. Railway Dashboard でバックエンドサービスを選択
2. 「Logs」タブを開く
3. 以下のメッセージを確認：

#### 正常な起動ログの例

```
=== データベース接続ヘルスチェック開始 ===
接続試行 1/5
データベース接続情報:
  - データベース製品: PostgreSQL
  - データベースバージョン: 17.7
  - ドライバー名: PostgreSQL JDBC Driver
  - URL: jdbc:postgresql://***@***
  - ユーザー名: postgres
✓ データベース接続テスト成功
  - 現在のデータベース: internet_banking
  - 読み取り専用: false
  - 自動コミット: true
  - トランザクション分離レベル: 2
  - 既存テーブル数: 0
=== データベース接続ヘルスチェック完了 ===

データベーススキーマを初期化しています...
データベース接続を確認しています...
データベース接続が正常です。
データベーススキーマの初期化が完了しました。実行: 15, スキップ: 0

=== データベース起動検証開始 ===
1. データベース接続の確認...
   ✓ PostgreSQL バージョン: PostgreSQL 17.7...
2. 必須テーブルの存在確認...
   ✓ テーブル 'users' が存在します (行数: 0)
   ✓ テーブル 'accounts' が存在します (行数: 0)
   ✓ テーブル 'transactions' が存在します (行数: 0)
   ✓ テーブル 'fixed_deposits' が存在します (行数: 0)
   ✓ テーブル 'loans' が存在します (行数: 0)
   ✓ テーブル 'security_logs' が存在します (行数: 0)
3. データベース設定の確認...
   ✓ 現在のデータベース: internet_banking
   ✓ 文字エンコーディング: UTF8
   ✓ タイムゾーン: UTC
   ✓ 最大接続数: 100
4. 接続プールの状態確認...
   ✓ アクティブ接続: 2
   ✓ アイドル接続: 1
   ✓ 合計接続: 3/100
=== データベース起動検証完了: すべて正常 ===
```

#### 接続リトライのログ例

PostgreSQL の起動が遅い場合、自動的にリトライが実行されます：

```
=== データベース接続ヘルスチェック開始 ===
接続試行 1/5
接続試行 1 失敗: Connection refused
3秒後に再試行します...
接続試行 2/5
接続試行 2 失敗: Connection refused
3秒後に再試行します...
接続試行 3/5
データベース接続情報:
  ...
✓ データベース接続テスト成功
```

## よくある問題と解決方法

### 1. Dockerビルドエラー: "/src": not found

**症状:**
- ビルドログに `ERROR: failed to build: failed to solve: failed to compute cache key: "/src": not found` が表示される
- Dockerビルドが失敗する

**解決方法:**
1. Railway Dashboard でバックエンドサービスを選択
2. 「Settings」タブを開く
3. **「Root Directory」が `backend` に設定されているか確認**
4. 設定されていない場合は、`backend` に設定して保存
5. サービスを再デプロイ

**原因**: ルートディレクトリが設定されていないと、ビルドコンテキストがプロジェクトルートになり、`backend/src` ディレクトリが見つからなくなります。

### 2. Dockerfile が見つからないエラー

**症状:**
- ビルドログに `Dockerfile 'Dockerfile' does not exist` が表示される
- デプロイが開始できない

**解決方法（方法1: ルートディレクトリを使用する場合）:**
1. `backend/railway.json` ファイルが存在することを確認
2. Railway Dashboard でバックエンドサービスを選択
3. 「Settings」タブを開く
4. **「Root Directory」が `backend` に設定されているか確認**（重要）
5. 「Dockerfile Path」が `Dockerfile` に設定されているか確認
6. 設定を保存して再デプロイ

**解決方法（方法2: ルートディレクトリを設定しない場合）:**
1. Railway Dashboard でバックエンドサービスを選択
2. 「Settings」タブを開く
3. 「Root Directory」が空（または設定されていない）ことを確認
4. 「Dockerfile Path」が `backend/Dockerfile` に設定されているか確認
5. 設定を保存して再デプロイ

**原因**: 
- Railway はルートディレクトリ内で `railway.json` と `Dockerfile` を探します。
- ルートディレクトリが `backend` に設定されている場合、`backend/railway.json` と `backend/Dockerfile` が存在する必要があります。
- ルートディレクトリが設定されていない場合、プロジェクトルートの `railway.json` を使用し、Dockerfileのパスを `backend/Dockerfile` に設定する必要があります。

**推奨**: 方法1（ルートディレクトリを `backend` に設定）を使用することを推奨します。

### 3. PostgreSQL が起動しない

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
- 複数回の接続試行が失敗する

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
5. **接続リトライ機能**: アプリケーションは自動的に5回まで接続を試行します（各試行の間隔は3秒）
6. PostgreSQL サービスのログで `database system is ready to accept connections` が表示されているか確認
7. Railway のサービス起動順序を確認（PostgreSQL が先に起動している必要があります）

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

## ヘルスチェックエンドポイント

アプリケーション起動後、以下のエンドポイントでデータベースの状態を確認できます：

- **ヘルスチェック**: `https://[backend-domain]/api/actuator/health`
- **詳細情報**: `https://[backend-domain]/api/actuator/info`

ヘルスチェックエンドポイントのレスポンス例：

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "postgreSQLStatusChecker": {
      "status": "UP",
      "details": {
        "database": "internet_banking",
        "version": "PostgreSQL 17.7...",
        "active_connections": 2,
        "total_connections": 2,
        "max_connections": 100,
        "table_count": 6,
        "database_size": "8.5 MB"
      }
    }
  }
}
```

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

