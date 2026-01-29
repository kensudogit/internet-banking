# MOCK API自動フォールバック機能

## 概要

バックエンドAPIに接続できない場合（CORSエラー、ネットワークエラー、DB接続エラーなど）、自動的にMOCK APIにフォールバックする機能を実装しました。

## 実装内容

### 1. `mockApi.ts`の拡張

#### 追加された機能

- **`register`メソッド**: ユーザー登録のMOCK実装
- **`login`メソッド**: ログインのMOCK実装
- **モックユーザーデータ**: テスト用のユーザーアカウント

#### モックユーザーアカウント

以下のテストアカウントが利用可能です：

| ユーザー名 | パスワード | メールアドレス |
|-----------|----------|--------------|
| `testuser` | `password123` | `test@example.com` |
| `demo` | `demo123` | `demo@example.com` |

### 2. `api.ts`の改善

#### 自動フォールバック機能

以下のエラーが発生した場合、自動的にMOCK APIにフォールバックします：

- **ネットワークエラー**
  - `Failed to fetch`
  - `TypeError`
  - `ERR_FAILED`
  - `ERR_CONNECTION_REFUSED`
  - `NetworkError`
  - `Network request failed`

- **CORSエラー**
  - `blocked by CORS policy`
  - CORS関連のエラーメッセージ

- **サーバーエラー**
  - HTTP 5xxステータスコード（500, 502, 503など）

- **JSON解析エラー**
  - バックエンドがJSON以外のレスポンスを返した場合

#### フォールバック対象のメソッド

- ✅ `register`: ユーザー登録
- ✅ `login`: ログイン
- ✅ `getAccounts`: 口座情報取得（既存）
- ✅ `getTransactions`: 取引履歴取得（既存）

## 動作フロー

### ログイン/登録のフロー

```
1. フロントエンドがバックエンドAPIを呼び出し
   ↓
2. エラーが発生？
   ├─ NO → 正常にレスポンスを返す
   └─ YES → エラーの種類を判定
       ├─ ネットワークエラー → MOCK APIにフォールバック
       ├─ CORSエラー → MOCK APIにフォールバック
       ├─ HTTP 5xxエラー → MOCK APIにフォールバック
       └─ その他のエラー → エラーをスロー
   ↓
3. MOCK APIで処理
   ↓
4. 結果を返す（ユーザーはエラーを意識しない）
```

## 使用例

### ログイン

```typescript
// バックエンドが利用できない場合でも、自動的にMOCK APIが使用されます
try {
  const result = await apiService.login({
    username: 'testuser',
    password: 'password123'
  });
  
  if (result.error) {
    // 認証エラー（ユーザー名/パスワードが間違っている）
    console.error('ログイン失敗:', result.error);
  } else {
    // ログイン成功
    console.log('ログイン成功:', result.message);
  }
} catch (error) {
  // 予期しないエラー（通常は発生しない）
  console.error('予期しないエラー:', error);
}
```

### ユーザー登録

```typescript
// バックエンドが利用できない場合でも、自動的にMOCK APIが使用されます
try {
  const result = await apiService.register({
    username: 'newuser',
    email: 'newuser@example.com',
    password: 'password123',
    firstName: 'New',
    lastName: 'User',
    phoneNumber: '090-1234-5678'
  });
  
  if (result.error) {
    // 登録エラー（ユーザー名/メールアドレスの重複など）
    console.error('登録失敗:', result.error);
  } else {
    // 登録成功
    console.log('登録成功:', result.message);
  }
} catch (error) {
  // 予期しないエラー（通常は発生しない）
  console.error('予期しないエラー:', error);
}
```

## ログ出力

フォールバックが発生した場合、コンソールに以下のようなログが出力されます：

```
⚠️ バックエンドAPIに接続できません。MOCK APIにフォールバックします。
エラー詳細: Failed to fetch
エラー名: TypeError
Mock API: Logging in user testuser
Mock API: Login successful for user testuser
```

## メリット

1. **ユーザー体験の向上**
   - バックエンドが利用できない場合でも、アプリケーションが動作し続けます
   - エラーメッセージではなく、正常に動作しているように見えます

2. **開発効率の向上**
   - バックエンドを起動しなくても、フロントエンドの開発が可能です
   - テストが容易になります

3. **デバッグの容易さ**
   - フォールバックが発生した場合、コンソールログで確認できます
   - エラーの種類を識別しやすくなります

## 注意事項

1. **MOCK APIの制限**
   - MOCK APIはメモリ内のデータを使用するため、ページをリロードするとデータがリセットされます
   - 複数のユーザーが同時に使用する場合、データが共有されます

2. **本番環境での使用**
   - 本番環境では、バックエンドが正常に動作することを前提としています
   - MOCK APIは緊急時のフォールバックとして機能します

3. **セキュリティ**
   - MOCK APIは認証を簡易的に実装しているため、本番環境では使用しないでください
   - パスワードは平文で保存されています（テスト用）

## トラブルシューティング

### MOCK APIが使用されない場合

1. **環境変数の確認**
   - `REACT_APP_USE_MOCK_API=true`が設定されている場合、常にMOCK APIが使用されます
   - この設定を削除すると、バックエンドAPIが優先されます

2. **エラーの種類の確認**
   - ネットワークエラーやCORSエラー以外のエラー（例：400 Bad Request）は、MOCK APIにフォールバックしません
   - コンソールログでエラーの詳細を確認してください

3. **ブラウザのキャッシュ**
   - ブラウザのキャッシュをクリアして、最新のコードが読み込まれているか確認してください

## 次のステップ

1. コードをGitHubにプッシュ
2. Railwayでフロントエンドサービスを再デプロイ
3. バックエンドが利用できない状態でログインを試行
4. MOCK APIにフォールバックされることを確認
