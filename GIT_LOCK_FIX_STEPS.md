# Gitロックファイル問題の解決手順

## 現在の状況

- ローカルブランチ: `main`（`80711f74 DB接続調整`）
- リモートブランチ: `origin/main`（`dfa91e1f 最新化`）
- 問題: `.git/index.lock`ファイルへのアクセスが拒否されている

## 解決手順

### 方法1: PowerShellスクリプトを使用（推奨）

1. **管理者権限でPowerShellを開く**
   - Windowsキーを押す
   - 「PowerShell」と入力
   - 「Windows PowerShell」を右クリック
   - 「管理者として実行」を選択

2. **スクリプトを実行**
   ```powershell
   cd C:\devlop\internet-banking
   .\fix-git-lock.ps1
   ```

3. **スクリプトの指示に従う**
   - Gitプロセスを終了するか選択
   - マージまたはリセットを選択

### 方法2: 手動で対応

#### ステップ1: すべてのGitプロセスを終了

1. **タスクマネージャーを開く**（Ctrl+Shift+Esc）
2. **「詳細」タブを開く**
3. **以下のプロセスを探して終了**:
   - `git.exe`
   - `git-credential-manager.exe`
   - `git-credential-manager-core.exe`
   - `git-askpass.exe`
   - その他のGit関連プロセス

#### ステップ2: ロックファイルを削除

**管理者権限でPowerShellを開いて実行**:

```powershell
cd C:\devlop\internet-banking

# すべてのロックファイルを削除
Get-ChildItem -Path .git -Filter *.lock -Recurse | Remove-Item -Force

# 確認
Get-ChildItem -Path .git -Filter *.lock -Recurse
```

**または、エクスプローラーで手動削除**:

1. エクスプローラーで`C:\devlop\internet-banking\.git`フォルダを開く
2. `index.lock`ファイルを探す
3. ファイルを右クリック → 「削除」
4. 削除できない場合は、ファイルのプロパティで「読み取り専用」のチェックを外す

#### ステップ3: リモートの状態にリセット

```bash
git reset --hard origin/main
```

#### ステップ4: プッシュ

```bash
git push origin main
```

### 方法3: 新しいクローンを作成（最終手段）

ロックファイルの問題が解決しない場合：

1. **現在のリポジトリをバックアップ**
   ```powershell
   Copy-Item -Path C:\devlop\internet-banking -Destination C:\devlop\internet-banking-backup -Recurse
   ```

2. **新しいクローンを作成**
   ```bash
   cd C:\devlop
   Remove-Item -Path internet-banking -Recurse -Force
   git clone https://github.com/kensudogit/internet-banking.git
   ```

3. **必要なファイルをコピー**
   - バックアップから必要なファイルをコピー

## トラブルシューティング

### ロックファイルが削除できない場合

1. **ファイルのプロパティを確認**
   - 読み取り専用になっていないか
   - 別のプロセスが使用していないか

2. **管理者権限で削除**
   - PowerShellを管理者として実行
   - エクスプローラーを管理者として実行

3. **再起動**
   - コンピューターを再起動してから再度試す

### ネットワーク接続エラーの場合

`Failed to connect to 127.0.0.1 port 9`というエラーが発生する場合：

1. **プロキシ設定を確認**
   - Windows設定 → ネットワークとインターネット → プロキシ
   - プロキシを無効化

2. **SSH接続を使用**
   ```bash
   git remote set-url origin git@github.com:kensudogit/internet-banking.git
   ```

## 推奨される手順

1. **管理者権限でPowerShellを開く**
2. **`fix-git-lock.ps1`スクリプトを実行**
3. **スクリプトの指示に従う**

## 注意事項

- **重要**: `git reset --hard`はローカルの変更を失います
- 重要な変更がある場合は、事前にバックアップを取ることを推奨します
- ロックファイルは通常、Git操作が正常に完了すると自動的に削除されます
