# ビルドステージ
FROM node:18-alpine AS build
WORKDIR /app

# package.jsonとpackage-lock.jsonをコピー
COPY package*.json ./

# 依存関係をインストール
RUN npm install

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

# ポートを公開（Railwayが自動的に割り当てるポートを使用）
EXPOSE 3000

# 静的ファイルを配信（Railwayの$PORT環境変数を使用）
CMD sh -c "serve -s build -p ${PORT:-3000}"
