@echo off
echo バックエンドを再起動しています...

echo バックエンドを再起動中...
docker-compose restart backend

echo.
echo バックエンドが再起動しました！
echo バックエンドAPI: http://localhost:8080/api
echo.
pause
