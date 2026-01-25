# PowerShell script for building the backend
# Usage: .\build.ps1

Write-Host "=== Backend Build Script ===" -ForegroundColor Green

# Change to backend directory
$backendDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $backendDir

Write-Host "Current directory: $(Get-Location)" -ForegroundColor Yellow

# Check if gradlew exists
if (Test-Path ".\gradlew.bat") {
    Write-Host "Found gradlew.bat, starting build..." -ForegroundColor Green
    .\gradlew.bat build --no-daemon
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Build successful!" -ForegroundColor Green
    } else {
        Write-Host "Build failed with exit code: $LASTEXITCODE" -ForegroundColor Red
        exit $LASTEXITCODE
    }
} elseif (Test-Path ".\gradlew") {
    Write-Host "Found gradlew, starting build..." -ForegroundColor Green
    bash .\gradlew build --no-daemon
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Build successful!" -ForegroundColor Green
    } else {
        Write-Host "Build failed with exit code: $LASTEXITCODE" -ForegroundColor Red
        exit $LASTEXITCODE
    }
} else {
    Write-Host "Error: gradlew or gradlew.bat not found!" -ForegroundColor Red
    exit 1
}
