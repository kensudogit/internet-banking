@echo off
echo インターネットバンキングシステムを再起動しています...

echo バックエンドを再起動中...
docker-compose restart backend

echo フロントエンドを再ビルド・再起動中...
echo （フロントエンドの変更を反映するには再ビルドが必要です）
docker-compose stop frontend
docker-compose rm -f frontend
docker-compose up -d --build frontend

echo.
echo インターネットバンキングシステムが再起動しました！
echo.
echo フロントエンド: http://localhost:3000
echo バックエンドAPI: http://localhost:8080/api
echo.
echo 注意: フロントエンドのビルドには数分かかる場合があります。
echo ビルド状況を確認するには: docker-compose logs -f frontend
echo.
pause
