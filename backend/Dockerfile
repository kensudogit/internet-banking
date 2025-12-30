# ビルドステージ
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Gradleラッパーをコピー
COPY gradle ./gradle
COPY gradlew ./
COPY gradlew.bat ./

# build.gradle.ktsをコピー
COPY build.gradle.kts ./
COPY settings.gradle.kts* ./

# 依存関係をダウンロード（キャッシュを活用）
RUN ./gradlew dependencies --no-daemon || true

# ソースコードをコピー
COPY src ./src

# アプリケーションをビルド
RUN ./gradlew clean build -x test --no-daemon

# 実行ステージ
FROM eclipse-temurin:21-jre
WORKDIR /app

# ビルド済みのJARファイルをコピー
COPY --from=build /app/build/libs/backend-0.0.1-SNAPSHOT.jar app.jar

# JARファイルを実行
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
