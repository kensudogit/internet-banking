@echo off
echo インターネットバンキングシステムを起動しています...

echo PostgreSQLデータベースを起動中...
docker-compose up -d postgres

echo データベースの起動を待機中...
timeout /t 10 /nobreak > nul

echo バックエンドを起動中...
docker-compose up -d backend

echo フロントエンドを起動中...
docker-compose up -d frontend

echo.
echo インターネットバンキングシステムが起動しました！
echo.
echo フロントエンド: http://localhost:3000
echo バックエンドAPI: http://localhost:8080/api
echo.
echo 停止するには: docker-compose down
echo.
pause
