@echo off
echo ========================================
echo データベースをリセットしてダミーデータを投入します
echo ========================================
echo.
echo 警告: この操作は既存のデータベースを削除します
echo.
set /p confirm="続行しますか？ (y/N): "
if /i not "%confirm%"=="y" (
    echo キャンセルしました。
    pause
    exit /b
)

echo.
echo データベースコンテナを停止中...
docker-compose down

echo.
echo データベースボリュームを削除中...
for /f "tokens=*" %%i in ('docker volume ls -q ^| findstr postgres') do docker volume rm %%i 2>nul

echo.
echo データベースを再起動中...
docker-compose up -d postgres

echo.
echo データベースの起動を待機中（30秒）...
timeout /t 30 /nobreak > nul

echo.
echo ダミーデータを投入中...
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
    echo バックエンドとフロントエンドを起動してください:
    echo   start.bat
    echo.
) else (
    echo.
    echo エラーが発生しました。
    echo.
)

pause
