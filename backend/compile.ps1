# PowerShell script for compiling the backend
# Usage: .\compile.ps1

Write-Host "=== Backend Compilation Script ===" -ForegroundColor Green

# Change to backend directory
$backendDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $backendDir

Write-Host "Current directory: $(Get-Location)" -ForegroundColor Yellow

# Check if gradlew exists
if (Test-Path ".\gradlew.bat") {
    Write-Host "Found gradlew.bat, starting compilation..." -ForegroundColor Green
    .\gradlew.bat compileJava --no-daemon
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Compilation successful!" -ForegroundColor Green
    } else {
        Write-Host "Compilation failed with exit code: $LASTEXITCODE" -ForegroundColor Red
        exit $LASTEXITCODE
    }
} elseif (Test-Path ".\gradlew") {
    Write-Host "Found gradlew, starting compilation..." -ForegroundColor Green
    bash .\gradlew compileJava --no-daemon
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Compilation successful!" -ForegroundColor Green
    } else {
        Write-Host "Compilation failed with exit code: $LASTEXITCODE" -ForegroundColor Red
        exit $LASTEXITCODE
    }
} else {
    Write-Host "Error: gradlew or gradlew.bat not found!" -ForegroundColor Red
    exit 1
}
