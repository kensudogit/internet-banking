# PowerShell スクリプト使用ガイド

このディレクトリには、PowerShellでバックエンドを操作するためのスクリプトが含まれています。

## 利用可能なスクリプト

### 1. `compile.ps1` - コンパイル
Javaコードをコンパイルします。

```powershell
.\compile.ps1
```

### 2. `build.ps1` - ビルド
プロジェクト全体をビルドします（コンパイル + テスト + JAR作成）。

```powershell
.\build.ps1
```

### 3. `run.ps1` - 実行
Spring Bootアプリケーションを実行します。

```powershell
.\run.ps1
```

## PowerShellの実行ポリシー設定

PowerShellスクリプトを実行するには、実行ポリシーを設定する必要がある場合があります。

### 現在の実行ポリシーを確認
```powershell
Get-ExecutionPolicy
```

### 実行ポリシーを設定（管理者権限が必要な場合があります）

**オプション1: 現在のユーザーのみに適用**
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

**オプション2: スクリプトをバイパスして実行**
```powershell
powershell -ExecutionPolicy Bypass -File .\compile.ps1
```

**オプション3: スクリプトの実行を許可（推奨）**
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope Process
```

## トラブルシューティング

### エラー: "このシステムではスクリプトの実行が無効になっています"
実行ポリシーを変更してください（上記参照）。

### エラー: "gradlew が見つかりません"
プロジェクトのルートディレクトリ（`backend`フォルダ）でスクリプトを実行していることを確認してください。

### エラー: "アクセスが拒否されました"
Gradleラッパーのロックファイルの問題です。以下を試してください：

```powershell
# Gradleラッパーのロックファイルを削除（注意: 他のGradleプロセスが実行中でないことを確認）
Remove-Item "$env:USERPROFILE\.gradle\wrapper\dists\*\*\*.lck" -ErrorAction SilentlyContinue
```

## 注意事項

- これらのスクリプトは、Windows PowerShell 5.1以降で動作します
- PowerShell Core (pwsh) でも動作するはずです
- スクリプトは、実行されたディレクトリから`backend`ディレクトリに自動的に移動します
