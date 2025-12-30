# インターネットバンキング フロントエンド

## 概要
React + TypeScript + Tailwind CSS を使用したインターネットバンキングシステムのフロントエンド

## 技術スタック
- React 18
- TypeScript
- Tailwind CSS
- React Router
- React Query
- Axios

## 機能
- ユーザー認証（ログイン・新規登録）
- ダッシュボード
- 口座情報表示
- 取引履歴表示
- 振込機能
- レスポンシブデザイン

## セットアップ

### 前提条件
- Node.js 16以上
- npm または yarn

### インストール

```bash
npm install
```

### 開発サーバー起動

```bash
npm start
```

アプリケーションは http://localhost:3000 で起動します。

### ビルド

```bash
npm run build
```

### テスト

```bash
npm test
```

## ページ構成

- `/` - ログインページ
- `/login` - ログインページ
- `/register` - 新規登録ページ
- `/dashboard` - ダッシュボード
- `/accounts` - 口座情報
- `/transactions` - 取引履歴
- `/transfer` - 振込

## 環境変数

`.env`ファイルで以下の環境変数を設定できます：

```
REACT_APP_API_URL=http://localhost:8080/api
```

## デザイン

Tailwind CSSを使用したモダンなデザインを採用しています。
- レスポンシブ対応
- アクセシビリティ対応
- ダークモード対応（将来実装予定）
