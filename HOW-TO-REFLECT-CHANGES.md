# 変更を反映する方法

## フロントエンドの変更を反映する

このプロジェクトでは、フロントエンドはDockerコンテナ内でビルド済みの静的ファイルを配信しています。
そのため、コードを変更した場合は**再ビルド**が必要です。

### 方法1: フロントエンドのみ再ビルド・再起動（推奨）

```batch
restart-frontend.bat
```

このスクリプトは以下を実行します：
1. フロントエンドコンテナを停止
2. フロントエンドコンテナを削除
3. フロントエンドを再ビルドして起動

### 方法2: 手動でコマンドを実行

```batch
docker-compose stop frontend
docker-compose rm -f frontend
docker-compose up -d --build frontend
```

### 方法3: 全体を再起動

```batch
restart.bat
```

## バックエンドの変更を反映する

バックエンドのコードを変更した場合：

```batch
restart-backend.bat
```

または手動で：

```batch
docker-compose restart backend
```

## ビルド状況の確認

フロントエンドのビルド状況を確認するには：

```batch
docker-compose logs -f frontend
```

バックエンドのログを確認するには：

```batch
docker-compose logs -f backend
```

## 開発モードで実行する場合（ホットリロード）

開発中に頻繁に変更を反映したい場合は、Dockerを使わずに直接実行することもできます：

### フロントエンドを開発モードで起動

```batch
cd frontend
npm install
npm start
```

この場合、コードを変更すると自動的にブラウザがリロードされます（ホットリロード）。

**注意**: この場合、バックエンドは別途起動する必要があります。

## トラブルシューティング

### 変更が反映されない場合

1. **ブラウザのキャッシュをクリア**
   - `Ctrl + Shift + R` (Windows/Linux)
   - `Cmd + Shift + R` (Mac)

2. **コンテナが正しく再起動されているか確認**
   ```batch
   docker ps
   ```
   `internet-banking-frontend` が表示されていることを確認

3. **ビルドエラーがないか確認**
   ```batch
   docker-compose logs frontend
   ```

4. **強制的に再ビルド**
   ```batch
   docker-compose build --no-cache frontend
   docker-compose up -d frontend
   ```

### ビルドに時間がかかる場合

初回ビルドや大きな変更後は、数分かかる場合があります。
ビルドが完了するまで待ってから、ブラウザで確認してください。

## よくある質問

### Q: 毎回再ビルドする必要がありますか？

A: はい。現在のDockerfileは本番モード（ビルド済みファイルを配信）のため、コード変更のたびに再ビルドが必要です。

### Q: 開発中にホットリロードを使いたい

A: `frontend`ディレクトリで直接`npm start`を実行してください。ただし、バックエンドは別途起動する必要があります。

### Q: 変更がすぐに反映されない

A: ブラウザのキャッシュをクリア（`Ctrl + Shift + R`）してみてください。
