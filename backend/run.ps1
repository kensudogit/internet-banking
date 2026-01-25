# PowerShell script for running the backend
# Usage: .\run.ps1

Write-Host "=== Backend Run Script ===" -ForegroundColor Green

# Change to backend directory
$backendDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $backendDir

Write-Host "Current directory: $(Get-Location)" -ForegroundColor Yellow

# Check if gradlew exists
if (Test-Path ".\gradlew.bat") {
    Write-Host "Found gradlew.bat, starting application..." -ForegroundColor Green
    .\gradlew.bat bootRun --no-daemon
} elseif (Test-Path ".\gradlew") {
    Write-Host "Found gradlew, starting application..." -ForegroundColor Green
    bash .\gradlew bootRun --no-daemon
} else {
    Write-Host "Error: gradlew or gradlew.bat not found!" -ForegroundColor Red
    exit 1
}
