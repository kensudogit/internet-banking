# インターネットバンキング バックエンド

## 概要
Spring Boot + Doma2 + PostgreSQL を使用したインターネットバンキングシステムのバックエンド

## 技術スタック
- Java 17
- Spring Boot 3.2.0
- Spring Security
- Doma2 (データベースアクセス)
- PostgreSQL
- JWT認証

## 機能
- ユーザー認証・認可
- 口座管理
- 取引履歴管理
- 振込機能
- セキュリティ機能

## セットアップ

### 前提条件
- Java 17
- PostgreSQL
- Docker (推奨)

### ローカル開発

1. PostgreSQLを起動
```bash
docker run -d --name postgres -e POSTGRES_DB=internet_banking -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=password -p 5432:5432 postgres:15
```

2. データベーススキーマを作成
```bash
psql -h localhost -U postgres -d internet_banking -f src/main/resources/schema.sql
```

3. アプリケーションを起動
```bash
./gradlew bootRun
```

### Dockerを使用

```bash
docker-compose up -d
```

## API エンドポイント

### 認証
- `POST /api/auth/register` - ユーザー登録
- `POST /api/auth/login` - ログイン
- `POST /api/auth/logout` - ログアウト

### 口座
- `GET /api/accounts` - 全口座取得
- `GET /api/accounts/{id}` - 口座詳細取得
- `GET /api/accounts/user/{userId}` - ユーザーの口座一覧
- `POST /api/accounts/transfer` - 振込実行

### 取引履歴
- `GET /api/transactions` - 全取引履歴取得
- `GET /api/transactions/user/{userId}` - ユーザーの取引履歴
- `GET /api/transactions/account/{accountId}` - 口座の取引履歴

## 設定
`application.yml`でデータベース接続情報を設定してください。
