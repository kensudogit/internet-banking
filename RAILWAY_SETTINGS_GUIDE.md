# Railway Settings 設定ガイド

このドキュメントでは、Railway Dashboard でバックエンドサービスの設定を正しく行う方法を詳しく説明します。

## 📍 Settings タブの場所

1. Railway Dashboard にログイン
2. プロジェクトを選択
3. **バックエンドサービス**を選択
4. 上部のタブから「**Settings**」をクリック

## 🎯 設定画面の構成

Settings タブは2つのセクションに分かれています：

### 左側: メイン設定エリア
- **Networking**: ドメイン設定
- **Variables**: 環境変数
- **Deploy**: デプロイ設定
- **Health**: ヘルスチェック設定

### 右側: サイドバー
- **Source**: ソースコードとビルド設定（**重要**）
- **Deploy**: デプロイ設定
- **Health**: ヘルスチェック設定

## 🔧 Source セクションの設定

### 場所の確認

1. Settings タブを開く
2. **画面右側のサイドバー**を確認
3. 「**Source**」セクションを探す

### 設定項目

| 項目 | 値 | 説明 |
|------|-----|------|
| **Root Directory** | `backend` | **必須**: ビルドコンテキストのルートディレクトリ |
| **Dockerfile Path** | `Dockerfile` | Root Directory からの相対パス |
| **Start Command** | `java -jar app.jar` | アプリケーションの起動コマンド |

### 設定手順（詳細）

#### 1. Root Directory の設定

**手順:**
1. 「**Source**」セクションをクリックして展開
2. 「**Root Directory**」フィールドを探す
3. 空欄の場合は `backend` と入力
4. 既に値が入っている場合は、`backend` に変更

**確認方法:**
- 入力欄に `backend` と表示されていることを確認
- 入力後に「**Save**」または「**Update**」ボタンをクリック

#### 2. Dockerfile Path の設定

**手順:**
1. 「**Dockerfile Path**」フィールドを確認
2. `Dockerfile` になっているか確認
3. もし `backend/Dockerfile` になっている場合は、`Dockerfile` に変更

**注意:**
- Root Directory が `backend` の場合、Dockerfile Path は `Dockerfile`（相対パス）である必要があります
- `backend/Dockerfile` と設定すると、Railwayは `backend/backend/Dockerfile` を探してしまいます

#### 3. Start Command の設定

**手順:**
1. 「**Start Command**」フィールドを確認
2. `java -jar app.jar` になっているか確認
3. 必要に応じて変更

## ✅ 設定の確認

### 正しい設定の例

```
Source
├─ Root Directory: backend
├─ Dockerfile Path: Dockerfile
└─ Start Command: java -jar app.jar
```

### 間違った設定の例

**例1: Root Directory が空欄**
```
Source
├─ Root Directory: （空欄）
├─ Dockerfile Path: backend/Dockerfile
└─ Start Command: java -jar app.jar
```
**問題**: ビルドコンテキストがプロジェクトルートになり、`src` ディレクトリが見つからない

**例2: Dockerfile Path が間違っている**
```
Source
├─ Root Directory: backend
├─ Dockerfile Path: backend/Dockerfile  ← 間違い
└─ Start Command: java -jar app.jar
```
**問題**: Dockerfile が見つからない

## 🔄 設定変更後の手順

### 1. 設定を保存

1. 設定を変更した後、「**Save**」または「**Update**」ボタンをクリック
2. 保存が成功したことを確認（緑色のチェックマークや成功メッセージ）

### 2. 再デプロイ

設定を保存した後、以下のいずれかの方法で再デプロイを実行：

**方法1: 自動再デプロイ**
- 設定を保存すると、自動的に再デプロイが開始される場合があります
- 「**Deployments**」タブで進行状況を確認

**方法2: 手動再デプロイ**
1. 「**Deployments**」タブを開く
2. 「**Redeploy**」ボタンをクリック
3. ビルドログを確認

### 3. ビルドログの確認

再デプロイ後、ビルドログを確認：

1. 「**Deployments**」タブを開く
2. 最新のデプロイメントをクリック
3. 「**Build Logs**」を確認

**成功の確認:**
- `COPY src ./src` のステップが成功している
- エラーが表示されていない
- ビルドが完了している

## 🚨 トラブルシューティング

### 問題1: Source セクションが見つからない

**解決方法:**
1. ブラウザのウィンドウ幅を広げる
2. 画面をスクロールして右側を確認
3. ブラウザのズームレベルを100%に設定
4. 別のブラウザで試す

### 問題2: 設定を保存できない

**解決方法:**
1. ページをリロード
2. 再度設定を試す
3. ブラウザのコンソールでエラーを確認
4. 別のブラウザで試す

### 問題3: 設定を保存しても反映されない

**解決方法:**
1. ページをリロード
2. 設定が保存されているか確認
3. 手動で再デプロイを実行
4. ビルドログでエラーを確認

### 問題4: RailwayのUIが異なる

RailwayのUIは定期的に更新されます。最新のUIでは：

- 「**Source**」セクションが「**Build & Deploy**」セクションに統合されている可能性があります
- その場合は、「**Build & Deploy**」セクション内で「**Root Directory**」を探してください

## 📋 チェックリスト

設定を完了したら、以下を確認してください：

- [ ] Root Directory が `backend` に設定されている
- [ ] Dockerfile Path が `Dockerfile` に設定されている
- [ ] Start Command が `java -jar app.jar` に設定されている
- [ ] 設定を保存した
- [ ] 再デプロイを実行した
- [ ] ビルドログでエラーがないことを確認した

## 📚 参考資料

- `RAILWAY_FIX_SRC_ERROR.md` - `/src`: not found エラーの解決方法
- `RAILWAY_DEPLOY_COMPLETE.md` - 完全なデプロイ手順
- [Railway Documentation](https://docs.railway.app/)
