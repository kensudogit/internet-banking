# GitHub リポジトリ確認とコミット手順

## 問題: Dockerfile が見つからない

GitHubから再インポートしても同じエラーが発生する場合、**GitHubリポジトリに`backend/Dockerfile`がコミットされていない**可能性が高いです。

## 確認手順

### 1. GitHubリポジトリで確認

1. GitHubで `https://github.com/kensudogit/internet-banking` を開く
2. `backend` ディレクトリをクリック
3. `Dockerfile` ファイルが表示されるか確認
   - **表示されない場合**: ファイルがコミットされていません
   - **表示される場合**: 別の問題の可能性があります

### 2. ローカルで確認

以下のコマンドで、GitHubにプッシュされているファイルを確認：

```bash
cd C:\devlop\internet-banking
git ls-files backend/Dockerfile
```

- **何も表示されない場合**: ファイルがGitに追跡されていません
- **`backend/Dockerfile` が表示される場合**: ファイルは追跡されていますが、プッシュされていない可能性があります

## 解決方法: Dockerfileをコミットする

### ステップ1: ファイルがGitに追跡されているか確認

```bash
cd C:\devlop\internet-banking
git status
```

`backend/Dockerfile` が表示される場合、まだコミットされていません。

### ステップ2: Dockerfileを追加してコミット

```bash
# Dockerfileを追加
git add backend/Dockerfile

# 他の必要なファイルも確認
git add backend/railway.json
git add backend/.dockerignore

# コミット
git commit -m "Add Dockerfile and Railway configuration for backend"

# GitHubにプッシュ
git push origin main
```

### ステップ3: Railwayで再デプロイ

1. Railway Dashboard でバックエンドサービスを選択
2. 「Deployments」タブを開く
3. 「Redeploy」ボタンをクリック
4. または、GitHubで新しいコミットをプッシュすると自動的にデプロイが開始されます

## その他の確認事項

### .gitignore の確認

プロジェクトルートに `.gitignore` ファイルを作成し、Dockerfileが除外されていないか確認：

```bash
# .gitignore に以下が含まれていないか確認
# Dockerfile  ← これがあると除外される
# *.dockerfile  ← これがあると除外される
```

### ブランチの確認

正しいブランチ（`main`）にコミットされているか確認：

```bash
git branch
# * main と表示されることを確認

git log --oneline -5
# 最新のコミットを確認
```

## 代替方法: Root Directoryを空にする

もし上記の方法で解決しない場合、以下の設定を試してください：

1. Railway Dashboard → 「Settings」タブ
2. 「Source」セクションで「Root Directory」を**空にする**（削除）
3. 「Settings」タブに戻る
4. 「Dockerfile Path」を `backend/Dockerfile` に変更
5. 保存して再デプロイ

この方法でも動作するはずです。

