# Railway Dockerfile エラー クイック修正ガイド

## エラー: `Dockerfile 'Dockerfile' does not exist`

このエラーが発生した場合、以下の手順を順番に確認してください。

## 即座に試すべき解決方法

### 方法1: 設定の再確認と保存

1. Railway Dashboard でバックエンドサービスを選択
2. 「Settings」タブを開く
3. 右側のサイドバーで「**Source**」をクリック
4. **「Root Directory」が `backend` になっているか確認**
   - なっていない場合: `backend` と入力して保存
5. 「Settings」タブに戻る
6. **「Dockerfile Path」が `Dockerfile` になっているか確認**
   - なっていない場合: `Dockerfile` と入力（`backend/Dockerfile` ではない）
7. すべての設定を保存
8. サービスを再デプロイ

### 方法2: リポジトリの再接続

1. 「Settings」タブ → 「Source」セクション
2. 「Disconnect」ボタンをクリック
3. 再度「Edit」をクリックしてリポジトリを再接続
4. ブランチが `main` に設定されていることを確認
5. 再デプロイ

### 方法3: ルートディレクトリを設定しない方法

もし上記の方法で解決しない場合：

1. 「Settings」タブ → 「Source」セクション
2. **「Root Directory」を空にする**（削除）
3. 「Settings」タブに戻る
4. **「Dockerfile Path」を `backend/Dockerfile` に変更**
5. 保存して再デプロイ

## 確認チェックリスト

- [ ] GitHub リポジトリに `backend/Dockerfile` が存在する
- [ ] `backend/Dockerfile` がコミットされている（`.gitignore` で除外されていない）
- [ ] 正しいブランチ（`main`）にコミットされている
- [ ] Railway の「Root Directory」が `backend` に設定されている（または空）
- [ ] Railway の「Dockerfile Path」が正しく設定されている
  - Root Directory が `backend` の場合: `Dockerfile`
  - Root Directory が空の場合: `backend/Dockerfile`
- [ ] 設定を保存している
- [ ] サービスを再デプロイしている

## よくある間違い

❌ **間違い**: Root Directory が `backend` なのに、Dockerfile Path を `backend/Dockerfile` に設定している
✅ **正しい**: Root Directory が `backend` の場合、Dockerfile Path は `Dockerfile`

❌ **間違い**: Root Directory が空なのに、Dockerfile Path を `Dockerfile` に設定している
✅ **正しい**: Root Directory が空の場合、Dockerfile Path は `backend/Dockerfile`

## それでも解決しない場合

### 最も可能性が高い原因: GitHubリポジトリにDockerfileがコミットされていない

**確認方法:**
1. GitHubで `https://github.com/kensudogit/internet-banking` を開く
2. `backend` ディレクトリをクリック
3. `Dockerfile` ファイルが表示されるか確認

**解決方法:**
```bash
cd C:\devlop\internet-banking
git add backend/Dockerfile
git add backend/railway.json
git add backend/.dockerignore
git commit -m "Add Dockerfile and Railway configuration"
git push origin main
```

詳細は [GITHUB_COMMIT_CHECK.md](./GITHUB_COMMIT_CHECK.md) を参照してください。

### その他の対処方法

1. Railway のログを確認して、実際のエラーメッセージを確認
2. 新しいサービスを作成して、最初から設定し直す
3. Root Directoryを空にして、Dockerfile Pathを `backend/Dockerfile` に設定する方法を試す

