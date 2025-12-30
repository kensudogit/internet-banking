# PostgreSQL ログ解釈ガイド

このドキュメントでは、Railway の PostgreSQL サービスのログを正しく解釈する方法を説明します。

## ログの基本構造

Railway の PostgreSQL ログは以下の形式で表示されます：

```
[タイムスタンプ] [レベル] [メッセージ]
```

- **タイムスタンプ**: ISO 8601形式（UTC）
- **レベル**: `[inf]` (情報), `[err]` (エラー), `[warn]` (警告)
- **メッセージ**: 実際のログ内容

## 重要な注意事項

### `[err]` タグについて

**重要**: ログに多数の `[err]` タグが表示されていても、これらは必ずしもエラーではありません。

PostgreSQL の Docker イメージでは、OpenSSL の証明書生成スクリプト（`init-ssl.sh`）の出力が `[err]` レベルでログに記録されます。これは、OpenSSL が標準エラー出力（stderr）に進捗情報を出力するためです。

**実際のエラー** は以下のキーワードが含まれます：
- `ERROR`
- `FATAL`
- `PANIC`
- `could not`
- `failed`
- `exception`

## 正常な起動シーケンス

PostgreSQL が正常に起動した場合、以下のメッセージが順番に表示されます：

### 1. 初期化フェーズ

```
[inf] selecting default "max_connections" ... 100
[inf] selecting default "shared_buffers" ... 128MB
[inf] The database cluster will be initialized with locale "en_US.utf8".
[inf] The default database encoding has accordingly been set to "UTF8".
[inf] creating configuration files ... ok
[inf] running bootstrap script ... ok
[inf] performing post-bootstrap initialization ... ok
[inf] syncing data to disk ... ok
```

✅ **意味**: データベースクラスターの初期化が正常に完了

### 2. データベース起動

```
[inf] waiting for server to start....
[inf] LOG:  starting PostgreSQL 17.7 ...
[inf] LOG:  listening on Unix socket "/var/run/postgresql/.s.PGSQL.5432"
[inf] LOG:  database system was shut down at ...
[inf] LOG:  database system is ready to accept connections
[inf] done
[inf] server started
```

✅ **意味**: PostgreSQL サーバーが正常に起動し、接続を受け付ける準備ができた

### 3. データベース作成

```
[inf] CREATE DATABASE
```

✅ **意味**: アプリケーション用のデータベースが作成された

### 4. SSL証明書生成（`[err]` タグが多数表示される）

```
[inf] /usr/local/bin/docker-entrypoint.sh: running /docker-entrypoint-initdb.d/init-ssl.sh
[err] ......+...+...++++++++++
[err] ++++++++++++++++++++++
[err] ... (多数の行) ...
[err] Certificate request self-signature ok
[err] subject=CN=localhost
```

⚠️ **注意**: これらの `[err]` タグは **エラーではありません**。OpenSSL の証明書生成プロセスの進捗表示です。

✅ **意味**: SSL証明書が正常に生成された

### 5. 初期化完了

```
[inf] PostgreSQL init process complete; ready for start up.
[inf] LOG:  starting PostgreSQL 17.7 ...
[inf] LOG:  listening on IPv4 address "0.0.0.0", port 5432
[inf] LOG:  listening on IPv6 address "::", port 5432
[inf] LOG:  database system is ready to accept connections
```

✅ **意味**: 初期化プロセスが完了し、PostgreSQL が本番モードで起動した

### 6. 定期的なチェックポイント

```
[err] LOG:  checkpoint starting: time
[err] LOG:  checkpoint complete: wrote 47 buffers (0.3%); ...
```

⚠️ **注意**: `[err]` タグが付いていますが、これは **正常な動作** です。PostgreSQL のログメッセージは標準エラー出力に出力されるため、Railway では `[err]` として表示されます。

✅ **意味**: 定期的なチェックポイントが正常に実行された

## エラーの識別方法

### 実際のエラーメッセージの例

以下のようなメッセージが表示された場合は、実際のエラーです：

```
[err] ERROR:  relation "users" does not exist
[err] FATAL:  password authentication failed for user "postgres"
[err] ERROR:  could not connect to server: Connection refused
[err] PANIC:  could not write to hash-join temporary file
```

### エラーの対処方法

1. **接続エラー**
   - 環境変数（`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`）を確認
   - PostgreSQL サービスが正常に起動しているか確認

2. **認証エラー**
   - データベースパスワードを確認
   - PostgreSQL サービスの環境変数を確認

3. **テーブル不存在エラー**
   - データベーススキーマが初期化されているか確認
   - `APP_DATABASE_AUTO_INIT=true` が設定されているか確認

## ログの確認チェックリスト

PostgreSQL が正常に動作していることを確認するには、以下をチェックしてください：

- [ ] `database system is ready to accept connections` が表示されている
- [ ] `CREATE DATABASE` が表示されている
- [ ] `PostgreSQL init process complete; ready for start up.` が表示されている
- [ ] `checkpoint complete` が定期的に表示されている
- [ ] `ERROR` や `FATAL` というキーワードが含まれるメッセージがない
- [ ] アプリケーション側のログで接続成功メッセージが表示されている

## よくある質問

### Q: ログに `[err]` がたくさん表示されているが、問題ないか？

**A**: はい、問題ありません。OpenSSL の証明書生成や PostgreSQL の通常のログメッセージは `[err]` レベルで表示されますが、これらはエラーではありません。実際のエラーは `ERROR` や `FATAL` というキーワードが含まれます。

### Q: `initdb: warning: enabling "trust" authentication` は問題ないか？

**A**: はい、問題ありません。これは初期化時の警告メッセージで、Railway 環境では正常な動作です。

### Q: チェックポイントメッセージが定期的に表示されるのは正常か？

**A**: はい、正常です。PostgreSQL は定期的にチェックポイントを実行して、データの整合性を保証します。

## 参考リンク

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Railway Documentation](https://docs.railway.app/)
- [PostgreSQL Docker Image](https://hub.docker.com/_/postgres)

