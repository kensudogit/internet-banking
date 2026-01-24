# ビルドステージ
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Gradleラッパーをコピー
COPY gradle ./gradle
COPY gradlew* ./

# gradlewの改行コードをLFに変換（WindowsのCRLFをLinux用に変換）し、実行権限を付与
RUN tr -d '\r' < ./gradlew > ./gradlew.tmp && \
    mv ./gradlew.tmp ./gradlew && \
    chmod +x ./gradlew && \
    ls -la ./gradlew

# build.gradle.ktsをコピー
COPY build.gradle.kts ./
COPY settings.gradle.kts* ./

# 依存関係をダウンロード（キャッシュを活用）
RUN ./gradlew dependencies --no-daemon || true

# ソースコードをコピー
COPY src ./src

# アプリケーションをビルド（bootJarタスクを明示的に実行）
RUN ./gradlew clean bootJar -x test --no-daemon

# ビルド結果を確認（デバッグ用）
RUN echo "=== ビルド結果の確認 ===" && \
    ls -la /app/build/libs/ && \
    echo "=== JARファイルの検索 ===" && \
    find /app/build -name "*.jar" -type f

# 実行ステージ
FROM eclipse-temurin:21-jre
WORKDIR /app

# ビルド済みのJARファイルをコピー（ワイルドカードを使用して確実にコピー）
COPY --from=build /app/build/libs/*.jar app.jar

# JARファイルを実行
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
