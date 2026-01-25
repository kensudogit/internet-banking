# ビルドステージ
FROM node:18-alpine AS build
WORKDIR /app

# package.jsonとpackage-lock.jsonをコピー
COPY package*.json ./

# 依存関係をインストール
RUN npm install

# node_modules/.binの実行権限を付与
RUN chmod -R +x node_modules/.bin

# ソースコードをコピー
COPY . .

# ビルド前にファイルの存在を確認（デバッグ用）
RUN echo "=== ビルド前のファイル確認 ===" && \
    ls -la src/pages/Login.tsx && \
    ls -la src/pages/Register.tsx && \
    ls -la src/App.tsx && \
    echo "=== ビルド開始 ==="

# アプリケーションをビルド
RUN npm run build

# ビルド後の確認（デバッグ用）
RUN echo "=== ビルド完了 ===" && \
    ls -la build/static/js/ | head -5

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
