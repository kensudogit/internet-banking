# Gitロックファイル問題を解決するスクリプト

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Gitロックファイル問題を解決します" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# 現在のディレクトリを確認
$repoPath = "C:\devlop\internet-banking"
if (-not (Test-Path $repoPath)) {
    Write-Host "エラー: リポジトリが見つかりません: $repoPath" -ForegroundColor Red
    exit 1
}

Set-Location $repoPath

# ステップ1: Gitプロセスを確認
Write-Host "1. Gitプロセスを確認中..." -ForegroundColor Yellow
$gitProcesses = Get-Process | Where-Object {$_.ProcessName -like "*git*" -or $_.Path -like "*git*"}
if ($gitProcesses) {
    Write-Host "   警告: Gitプロセスが実行中です:" -ForegroundColor Yellow
    $gitProcesses | ForEach-Object { Write-Host "   - $($_.ProcessName) (PID: $($_.Id))" -ForegroundColor Yellow }
    $response = Read-Host "   これらのプロセスを終了しますか？ (y/n)"
    if ($response -eq "y" -or $response -eq "Y") {
        $gitProcesses | Stop-Process -Force -ErrorAction SilentlyContinue
        Write-Host "   Gitプロセスを終了しました" -ForegroundColor Green
        Start-Sleep -Seconds 2
    }
} else {
    Write-Host "   Gitプロセスは実行されていません" -ForegroundColor Green
}

# ステップ2: ロックファイルを削除
Write-Host ""
Write-Host "2. ロックファイルを削除中..." -ForegroundColor Yellow
$lockFiles = Get-ChildItem -Path .git -Filter *.lock -Recurse -ErrorAction SilentlyContinue
if ($lockFiles) {
    Write-Host "   見つかったロックファイル:" -ForegroundColor Yellow
    $lockFiles | ForEach-Object { Write-Host "   - $($_.FullName)" -ForegroundColor Yellow }
    
    # ロックファイルを削除
    $lockFiles | Remove-Item -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 1
    
    # 削除を確認
    $remainingLocks = Get-ChildItem -Path .git -Filter *.lock -Recurse -ErrorAction SilentlyContinue
    if ($remainingLocks) {
        Write-Host "   警告: 一部のロックファイルが削除できませんでした" -ForegroundColor Red
        $remainingLocks | ForEach-Object { Write-Host "   - $($_.FullName)" -ForegroundColor Red }
    } else {
        Write-Host "   すべてのロックファイルを削除しました" -ForegroundColor Green
    }
} else {
    Write-Host "   ロックファイルは見つかりませんでした" -ForegroundColor Green
}

# ステップ3: Gitの状態を確認
Write-Host ""
Write-Host "3. Gitの状態を確認中..." -ForegroundColor Yellow
try {
    $status = git status 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   Gitの状態:" -ForegroundColor Green
        $status | ForEach-Object { Write-Host "   $_" -ForegroundColor Gray }
    } else {
        Write-Host "   エラー: Gitの状態を取得できませんでした" -ForegroundColor Red
        Write-Host "   $status" -ForegroundColor Red
    }
} catch {
    Write-Host "   エラー: $($_.Exception.Message)" -ForegroundColor Red
}

# ステップ4: リモートの変更をマージ
Write-Host ""
Write-Host "4. リモートの変更をマージしますか？" -ForegroundColor Yellow
Write-Host "   オプション:" -ForegroundColor Yellow
Write-Host "   1. リモートの変更をマージ (git merge origin/main)" -ForegroundColor Cyan
Write-Host "   2. リモートの状態にリセット (git reset --hard origin/main)" -ForegroundColor Cyan
Write-Host "   3. スキップ" -ForegroundColor Cyan
$choice = Read-Host "   選択 (1/2/3)"

if ($choice -eq "1") {
    Write-Host ""
    Write-Host "   リモートの変更をマージ中..." -ForegroundColor Yellow
    # ロックファイルを削除してからマージ
    $lockFile = ".git\index.lock"
    if (Test-Path $lockFile) { Remove-Item $lockFile -Force -ErrorAction SilentlyContinue }
    git merge origin/main --no-ff -m "Merge remote-tracking branch 'origin/main'"
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   マージが完了しました" -ForegroundColor Green
    } else {
        Write-Host "   マージ中にエラーが発生しました" -ForegroundColor Red
    }
} elseif ($choice -eq "2") {
    Write-Host ""
    Write-Host "   リモートの状態にリセット中..." -ForegroundColor Yellow
    Write-Host "   警告: ローカルの変更は失われます" -ForegroundColor Red
    $confirm = Read-Host "   続行しますか？ (yes/no)"
    if ($confirm -eq "yes") {
        # ロックファイルを削除してからリセット
        $lockFile = ".git\index.lock"
        if (Test-Path $lockFile) { Remove-Item $lockFile -Force -ErrorAction SilentlyContinue }
        git reset --hard origin/main
        if ($LASTEXITCODE -eq 0) {
            Write-Host "   リセットが完了しました" -ForegroundColor Green
        } else {
            Write-Host "   リセット中にエラーが発生しました" -ForegroundColor Red
        }
    } else {
        Write-Host "   リセットをキャンセルしました" -ForegroundColor Yellow
    }
} else {
    Write-Host "   スキップしました" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "完了" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Cyan
