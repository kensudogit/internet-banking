@echo off
echo フロントエンドを再ビルド・再起動しています...

echo フロントエンドコンテナを停止中...
docker-compose stop frontend

echo フロントエンドコンテナを削除中...
docker-compose rm -f frontend

echo フロントエンドを再ビルド・再起動中...
docker-compose up -d --build frontend

echo.
echo フロントエンドが再起動しました！
echo フロントエンド: http://localhost:3000
echo.
echo 注意: ビルドには数分かかる場合があります。
echo ビルド状況を確認するには: docker-compose logs -f frontend
echo.
pause
