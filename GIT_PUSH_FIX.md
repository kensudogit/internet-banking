# Git Push エラー対応ガイド

## 問題

`git push`を実行した際に以下のエラーが発生：

```
! [rejected]          main -> main (fetch first)
error: failed to push some refs to 'https://github.com/kensudogit/internet-banking.git'
hint: Updates were rejected because the remote contains work that you do not
hint: have locally.
```

## 原因

リモートリポジトリにローカルにない変更があるため、プッシュが拒否されています。

## 解決方法

### 方法1: リモートの変更を取得してマージ（推奨）

```bash
# 1. リモートの変更を取得
git fetch origin

# 2. リモートの変更をマージ
git pull origin main

# 3. コンフリクトがあれば解決
# （コンフリクトが発生した場合、ファイルを編集して解決）

# 4. 再度プッシュ
git push origin main
```

### 方法2: リベースを使用

```bash
# 1. リモートの変更を取得
git fetch origin

# 2. リベース
git rebase origin/main

# 3. コンフリクトがあれば解決
# （コンフリクトが発生した場合、ファイルを編集して解決）

# 4. 再度プッシュ（force pushが必要な場合）
git push origin main
```

### 方法3: 強制プッシュ（注意: リモートの変更が失われます）

**⚠️ 警告: この方法はリモートの変更を上書きします。他の人が作業している場合は使用しないでください。**

```bash
git push origin main --force
```

## ネットワーク接続エラーの場合

`Failed to connect to 127.0.0.1 port 9`というエラーが発生する場合：

### 1. プロキシ設定を確認

```bash
# プロキシ設定を確認
git config --get http.proxy
git config --get https.proxy

# プロキシ設定を無効化（必要に応じて）
git config --global --unset http.proxy
git config --global --unset https.proxy
```

### 2. システムのプロキシ設定を確認

Windowsの場合：
1. 「設定」→「ネットワークとインターネット」→「プロキシ」を開く
2. プロキシ設定を確認・無効化

### 3. ファイアウォール設定を確認

GitHubへの接続がブロックされていないか確認してください。

### 4. SSHを使用する（推奨）

HTTPS接続に問題がある場合、SSH接続を使用：

```bash
# リモートURLをSSHに変更
git remote set-url origin git@github.com:kensudogit/internet-banking.git

# 再度プル
git pull origin main
```

## ロックファイルエラーの場合

`.git/index.lock`や`.git/config.lock`が存在する場合：

```bash
# PowerShellで実行
Remove-Item -Path .git\index.lock -ErrorAction SilentlyContinue -Force
Remove-Item -Path .git\config.lock -ErrorAction SilentlyContinue -Force
```

## 現在の状況

- ローカルブランチ: `main`（1コミット先に進んでいる）
- リモートブランチ: `origin/main`
- ローカルの最新コミット: `80711f74 DB接続調整`
- リモートの最新コミット: `e3e2616e CORSを外す処理を実装`

## 推奨される手順

1. **ネットワーク接続を確認**
   - インターネット接続が正常か確認
   - プロキシ設定を確認

2. **ロックファイルを削除**
   ```powershell
   Remove-Item -Path .git\index.lock -ErrorAction SilentlyContinue -Force
   Remove-Item -Path .git\config.lock -ErrorAction SilentlyContinue -Force
   ```

3. **リモートの変更を取得**
   ```bash
   git fetch origin
   git pull origin main
   ```

4. **コンフリクトがあれば解決**
   - コンフリクトが発生したファイルを編集
   - `git add`でステージング
   - `git commit`でコミット

5. **再度プッシュ**
   ```bash
   git push origin main
   ```

## 参考資料

- [Git Documentation](https://git-scm.com/doc)
- [GitHub Help - Pushing to a remote](https://docs.github.com/en/get-started/using-git/pushing-commits-to-a-remote-repository)
