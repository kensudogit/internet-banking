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
# Railwayの環境変数をビルド時に使用するため、ARGとして定義
# Railwayは自動的に環境変数をビルドプロセスに渡しますが、
# DockerfileでARGとして定義することで、ビルド時に確実に使用できます
ARG REACT_APP_API_URL
ARG REACT_APP_ENV
ARG REACT_APP_USE_MOCK_API

# 環境変数をENVとして設定（ビルド時に使用される）
ENV REACT_APP_API_URL=${REACT_APP_API_URL}
ENV REACT_APP_ENV=${REACT_APP_ENV}
ENV REACT_APP_USE_MOCK_API=${REACT_APP_USE_MOCK_API}

RUN echo "=== ビルド前確認 ===" && \
    which react-scripts || echo "react-scripts not in PATH" && \
    test -f node_modules/.bin/react-scripts && echo "react-scripts file exists" || echo "react-scripts file NOT EXISTS" && \
    echo "=== 環境変数確認 ===" && \
    echo "REACT_APP_API_URL=${REACT_APP_API_URL:-未設定}" && \
    echo "REACT_APP_ENV=${REACT_APP_ENV:-未設定}" && \
    echo "REACT_APP_USE_MOCK_API=${REACT_APP_USE_MOCK_API:-未設定}" && \
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
# Railwayが自動的に$PORT環境変数を設定するため、シェルで展開
# serveコマンドの-pオプション（ポート）を使用
CMD sh -c "serve -s build -p ${PORT:-3000}"
