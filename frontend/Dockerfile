# ビルドステージ
FROM node:18-alpine AS build
WORKDIR /app

# package.jsonとpackage-lock.jsonをコピー
COPY package*.json ./

# 依存関係をインストール
RUN npm cache clean --force && \
    echo "=== package.json確認 ===" && \
    cat package.json | grep -A 5 '"dependencies"' && \
    echo "=== npm install開始 ===" && \
    npm install --legacy-peer-deps || (echo "=== npm install失敗 ===" && exit 1) && \
    echo "=== インストール完了 ===" && \
    echo "=== node_modules確認 ===" && \
    ls -la node_modules | head -10 && \
    echo "=== react-scripts確認 ===" && \
    test -f node_modules/.bin/react-scripts && echo "react-scripts found" || (echo "react-scripts NOT FOUND" && ls -la node_modules/.bin/ | head -20 && exit 1) && \
    echo "=== npm list react-scripts ===" && \
    npm list react-scripts 2>&1 | head -5

# ソースコードをコピー（node_modulesは除外）
COPY . .

# アプリケーションをビルド（react-scriptsのパスを明示的に指定）
RUN echo "=== ビルド前確認 ===" && \
    which react-scripts || echo "react-scripts not in PATH" && \
    test -f node_modules/.bin/react-scripts && echo "react-scripts file exists" || echo "react-scripts file NOT EXISTS" && \
    echo "=== ビルド開始 ===" && \
    npm run build

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
CMD sh -c "serve -s build -l ${PORT:-3000}"
