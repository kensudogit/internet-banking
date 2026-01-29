# Git マージ手動対応ガイド

## 現在の状況

- ローカルブランチ: `main`（`80711f74 DB接続調整`）
- リモートブランチ: `origin/main`（`dfa91e1f 最新化`）
- ブランチが分岐: ローカルに1コミット、リモートに13コミット

## 問題

Gitのロックファイル（`.git/*.lock`）が残っており、マージ操作が失敗しています。

## 解決方法

### ステップ1: すべてのGitプロセスを終了

1. **タスクマネージャーを開く**（Ctrl+Shift+Esc）
2. **Git関連のプロセスを確認**
   - `git.exe`
   - `git-credential-manager.exe`
   - `git-credential-manager-core.exe`
   - その他のGit関連プロセス
3. **すべてのGitプロセスを終了**

### ステップ2: ロックファイルを削除

PowerShellで以下を実行：

```powershell
cd C:\devlop\internet-banking

# すべてのロックファイルを削除
Get-ChildItem -Path .git -Filter *.lock -Recurse | Remove-Item -Force -ErrorAction SilentlyContinue

# 確認
Get-ChildItem -Path .git -Filter *.lock -Recurse
```

### ステップ3: リモートの変更をマージ

```bash
# リモートの変更をマージ
git merge origin/main --no-ff -m "Merge remote-tracking branch 'origin/main'"
```

### ステップ4: コンフリクトがあれば解決

コンフリクトが発生した場合：

1. **コンフリクトファイルを確認**
   ```bash
   git status
   ```

2. **コンフリクトファイルを編集**
   - `<<<<<<< HEAD`から`=======`まで: ローカルの変更
   - `=======`から`>>>>>>> origin/main`まで: リモートの変更
   - 必要な変更を残して、マーカーを削除

3. **変更をステージング**
   ```bash
   git add <コンフリクトファイル>
   ```

4. **マージを完了**
   ```bash
   git commit -m "Merge remote-tracking branch 'origin/main'"
   ```

### ステップ5: プッシュ

```bash
git push origin main
```

## 代替方法: リセットしてからマージ

ロックファイルの問題が解決しない場合：

### 方法A: ローカルの変更を保持してマージ

```bash
# 現在の変更を一時的に保存
git stash

# リモートの最新を取得
git fetch origin

# リモートの状態にリセット
git reset --hard origin/main

# 保存した変更を適用
git stash pop

# 変更をコミット
git add .
git commit -m "DB接続調整"

# プッシュ
git push origin main
```

### 方法B: リモートの変更を優先

```bash
# リモートの最新を取得
git fetch origin

# リモートの状態にリセット（ローカルの変更は失われます）
git reset --hard origin/main

# プッシュ
git push origin main
```

## トラブルシューティング

### ロックファイルが削除できない場合

1. **ファイルのプロパティを確認**
   - 読み取り専用になっていないか確認
   - 別のプロセスが使用していないか確認

2. **管理者権限でPowerShellを実行**
   - PowerShellを右クリック → 「管理者として実行」

3. **手動で削除**
   - エクスプローラーで`.git`フォルダを開く
   - `*.lock`ファイルを手動で削除

### マージが失敗する場合

1. **Gitの状態を確認**
   ```bash
   git status
   git log --oneline --graph --all -10
   ```

2. **マージを中止**
   ```bash
   git merge --abort
   ```

3. **再度マージを試行**

## 推奨される手順

1. **すべてのGitプロセスを終了**
2. **ロックファイルを削除**
3. **`git merge origin/main`を実行**
4. **コンフリクトがあれば解決**
5. **`git push origin main`を実行**

## 注意事項

- **重要**: リセット操作（`git reset --hard`）はローカルの変更を失います
- マージ前に重要な変更がある場合は、バックアップを取ることを推奨します
- コンフリクトが発生した場合は、慎重に解決してください
