@echo off
echo ========================================
echo ダミーデータをデータベースに投入します
echo ========================================
echo.

echo PostgreSQLコンテナの状態を確認中...
docker ps | findstr internet-banking-db >nul
if %ERRORLEVEL% NEQ 0 (
    echo エラー: PostgreSQLコンテナが起動していません。
    echo まず start.bat を実行してデータベースを起動してください。
    echo.
    pause
    exit /b 1
)

echo PostgreSQLコンテナに接続してSQLを実行中...
docker exec -i internet-banking-db psql -U postgres -d internet_banking < backend\src\main\resources\dummy-data.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo ダミーデータの投入が完了しました！
    echo ========================================
    echo.
    echo 以下のデータが追加されました:
    echo - ユーザーID 1 (testuser)
    echo - 4つの口座
    echo - 10件の取引履歴
    echo - 5件の投資情報
    echo.
    echo フロントエンドで http://localhost:3000 にアクセスして確認してください。
    echo.
) else (
    echo.
    echo ========================================
    echo エラーが発生しました
    echo ========================================
    echo.
    echo 考えられる原因:
    echo 1. データベースが起動していない
    echo 2. SQLファイルのパスが正しくない
    echo 3. データベース接続エラー
    echo.
    echo 解決方法:
    echo - start.bat を実行してデータベースを起動してください
    echo - または reset-and-init-db.bat を実行してデータベースをリセットしてください
    echo.
)

pause
