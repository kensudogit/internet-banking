# ビルドステージ
FROM node:18-alpine AS build
WORKDIR /app

# package.jsonとpackage-lock.jsonをコピー
COPY package*.json ./

# 依存関係をインストール
RUN npm ci

# ソースコードをコピー
COPY . .

# アプリケーションをビルド
RUN npm run build

# 本番ステージ
FROM node:18-alpine
WORKDIR /app

# serveパッケージをグローバルにインストール
RUN npm install -g serve

# ビルド済みのファイルをコピー
COPY --from=build /app/build ./build

# ポートを公開
EXPOSE 3000

# 静的ファイルを配信
CMD ["serve", "-s", "build", "-l", "3000"]
