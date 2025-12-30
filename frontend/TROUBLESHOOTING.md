# トラブルシューティングガイド

## よくある問題と解決方法

### 1. 404エラー（APIエンドポイントが見つからない）

**症状:**
- `api/accounts/user/1:1 Failed to load resource: the server responded with a status of 404`
- `api/transactions/user/1:1 Failed to load resource: the server responded with a status of 404`

**原因:**
- Vercelのルーティング設定が正しくない
- APIファイルのパスが間違っている
- ビルドが正しく行われていない

**解決方法:**
1. `vercel.json`の設定を確認
2. APIファイルが正しい場所にあることを確認
3. Vercelに再デプロイ
4. ブラウザのキャッシュをクリア

### 2. manifest.jsonの読み込みエラー

**症状:**
- `manifest.json:1 Failed to load resource: the server responded with a status of 401`
- `Manifest: Line: 1, column: 1, Syntax error`

**原因:**
- `public/manifest.json`ファイルが存在しない
- ファイルの形式が正しくない

**解決方法:**
1. `public/manifest.json`ファイルが存在することを確認
2. JSONの構文が正しいことを確認
3. ファイルのパーミッションを確認

### 3. favicon.icoの読み込みエラー

**症状:**
- `favicon.ico`ファイルが見つからない

**原因:**
- `public/favicon.ico`ファイルが存在しない

**解決方法:**
1. `public/favicon.ico`ファイルを配置
2. 適切なサイズ（16x16, 32x32, 48x48）のアイコンファイルを使用

## デバッグ手順

### 1. ブラウザの開発者ツールで確認
1. F12キーを押して開発者ツールを開く
2. Consoleタブでエラーメッセージを確認
3. NetworkタブでAPIリクエストの状況を確認

### 2. Vercelのログを確認
1. Vercelダッシュボードにログイン
2. プロジェクトのFunctionsタブでログを確認
3. エラーの詳細を確認

### 3. ローカルでのテスト
1. `npm run dev`でローカルサーバーを起動
2. `http://localhost:3000`でアクセス
3. APIエンドポイントを直接テスト

## 予防策

### 1. 定期的なチェック
- ビルド後の動作確認
- APIエンドポイントの動作確認
- ファイルの存在確認

### 2. エラーハンドリングの強化
- フロントエンドでのエラー表示
- APIレスポンスの適切な処理
- ユーザーフレンドリーなエラーメッセージ

### 3. ログの活用
- サーバーサイドでのログ出力
- クライアントサイドでのログ出力
- エラーの追跡と分析

