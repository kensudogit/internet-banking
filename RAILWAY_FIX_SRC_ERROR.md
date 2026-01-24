# Railway "/src": not found エラーの解決方法

このドキュメントでは、Railwayでバックエンドをデプロイする際に発生する `"/src": not found` エラーの解決方法を詳しく説明します。

## 🔴 エラーの詳細

**エラーメッセージ:**
```
ERROR: failed to build: failed to solve: failed to compute cache key: failed to calculate checksum of ref ...: "/src": not found
```

**発生箇所:**
```
Dockerfile:18
COPY src ./src
```

## 🔍 原因の分析

このエラーは、RailwayでDockerビルドを実行する際に、ビルドコンテキストが正しく設定されていない場合に発生します。

### ビルドコンテキストとは？

Dockerビルドでは、**ビルドコンテキスト**（ビルドの基準となるディレクトリ）が重要です。Railwayでは、**Root Directory**の設定がビルドコンテキストを決定します。

- **Root Directory が `backend` の場合**: ビルドコンテキストは `backend` ディレクトリ → `COPY src ./src` は正しい
- **Root Directory が空欄の場合**: ビルドコンテキストはプロジェクトルート → `COPY src ./src` は `/src` を探してしまう（存在しない）

## ✅ 解決方法（ステップバイステップ）

### ステップ1: Railway Dashboard で設定を確認

1. [Railway Dashboard](https://railway.app/dashboard) にログイン
2. プロジェクトを選択
3. **バックエンドサービス**を選択
4. 「**Settings**」タブを開く
5. 画面右側のサイドバーで「**Source**」セクションを確認

**重要**: Settings タブには2つのセクションがあります：
- 左側: メインの設定（Networking、Variables など）
- 右側: サイドバー（Source、Deploy、Health など）

### ステップ2: Root Directory を設定

**現在の状態を確認:**
- Root Directory が空欄になっている
- または、間違った値が設定されている

**修正方法（詳細）:**

1. **Settings タブを開く**
   - バックエンドサービスを選択
   - 上部のタブから「**Settings**」をクリック

2. **右側のサイドバーを確認**
   - 画面右側に「**Source**」というセクションがあるはずです
   - もし表示されていない場合は、画面をスクロールするか、ブラウザの幅を広げてください

3. **Root Directory を設定**
   - 「**Source**」セクション内の「**Root Directory**」フィールドを探す
   - 空欄の場合は、`backend` と入力
   - 既に値が入っている場合は、`backend` に変更

4. **Dockerfile Path を確認**
   - 「**Dockerfile Path**」フィールドが `Dockerfile` になっているか確認
   - もし `backend/Dockerfile` になっている場合は、`Dockerfile` に変更

5. **設定を保存**
   - 「**Save**」ボタンまたは「**Update**」ボタンをクリック
   - 保存が成功したことを確認（緑色のチェックマークや成功メッセージが表示される）

**注意**: 設定を保存しても自動的に再デプロイが開始されない場合があります。その場合は、次のステップ3で手動で再デプロイを実行してください。

### ステップ3: 再デプロイ

設定を保存すると、自動的に再デプロイが開始される場合があります。開始されない場合は：

1. 「**Deployments**」タブを開く
2. 「**Redeploy**」ボタンをクリック

### ステップ4: ビルドログを確認

再デプロイ後、ビルドログを確認：

1. 「**Deployments**」タブを開く
2. 最新のデプロイメントをクリック
3. 「**Build Logs**」を確認

**成功の確認:**
- `COPY src ./src` のステップが成功している
- エラーが表示されていない

## 🎯 正しい設定の確認

### バックエンドサービスの設定

| 項目 | 正しい値 | 説明 |
|------|---------|------|
| **Root Directory** | `backend` | **必須**: これがないとエラーが発生します |
| **Dockerfile Path** | `Dockerfile` | Root Directory からの相対パス |
| **Start Command** | `java -jar app.jar` | アプリケーションの起動コマンド |

### 設定画面の見方

Railway Dashboard の Settings タブで、以下のように表示されるはずです：

```
Source
├─ Root Directory: backend
├─ Dockerfile Path: Dockerfile
└─ Start Command: java -jar app.jar
```

## 🚨 よくある間違い

### 間違い1: Root Directory を空欄にする

**間違い:**
- Root Directory: （空欄）
- Dockerfile Path: `backend/Dockerfile`

**問題:**
- ビルドコンテキストがプロジェクトルートになる
- `COPY src ./src` が `/src` を探してしまう

**正しい設定:**
- Root Directory: `backend`
- Dockerfile Path: `Dockerfile`

### 間違い2: Dockerfile Path を間違える

**間違い:**
- Root Directory: `backend`
- Dockerfile Path: `backend/Dockerfile`

**問題:**
- Root Directory が `backend` の場合、Dockerfile Path は `Dockerfile`（相対パス）である必要がある

**正しい設定:**
- Root Directory: `backend`
- Dockerfile Path: `Dockerfile`

## 🔧 トラブルシューティング

### 問題1: Root Directory の入力欄が見つからない

**解決方法:**

1. **Settings タブの右側のサイドバーを確認**
   - 画面右側に「**Source**」セクションがあるはずです
   - もし表示されていない場合：
     - ブラウザのウィンドウ幅を広げる
     - 画面をスクロールして右側を確認
     - ブラウザのズームレベルを100%に設定

2. **「**Source**」セクションを展開**
   - 「**Source**」セクションをクリックして展開
   - または、「**Edit**」ボタンがある場合はクリック

3. **RailwayのUIが更新されている場合**
   - RailwayのUIは定期的に更新されます
   - 最新のUIでは、「**Source**」セクションが「**Build & Deploy**」セクションに統合されている可能性があります
   - その場合は、「**Build & Deploy**」セクション内で「**Root Directory**」を探してください

4. **それでも見つからない場合**
   - Railway Dashboard の右上の「**?**」アイコンをクリックしてヘルプを確認
   - または、サービスを削除して再作成する（方法3を参照）

### 問題2: 設定を保存しても反映されない

**解決方法:**
1. ページをリロード
2. 設定が保存されているか確認
3. サービスを手動で再デプロイ

### 問題3: サービスを削除して再作成したい

**手順:**
1. バックエンドサービスを選択
2. 「**Settings**」タブを開く
3. 最下部の「**Delete Service**」をクリック
4. 確認ダイアログで「**Delete**」をクリック
5. 「**+ New**」→「**GitHub Repo**」を選択
6. 同じリポジトリを選択
7. **作成直後に** Settings タブで Root Directory を `backend` に設定
8. 環境変数を再設定
9. デプロイを開始

## 📋 チェックリスト

デプロイ前に以下を確認してください：

- [ ] Root Directory が `backend` に設定されている
- [ ] Dockerfile Path が `Dockerfile` に設定されている
- [ ] Start Command が `java -jar app.jar` に設定されている
- [ ] 環境変数が正しく設定されている
- [ ] 設定を保存した
- [ ] 再デプロイを実行した

## 📚 参考資料

- [Railway Documentation - Root Directory](https://docs.railway.app/develop/variables#root-directory)
- [Railway Documentation - Dockerfile](https://docs.railway.app/deploy/dockerfiles)
- `RAILWAY_DEPLOY_COMPLETE.md` - 完全なデプロイ手順
- `RAILWAY_TROUBLESHOOTING.md` - その他のトラブルシューティング

## 💡 ヒント

- Root Directory は**サービス作成時**に設定するのが最も確実です
- 設定を変更した後は、必ず再デプロイを実行してください
- ビルドログを確認して、エラーが解決されたか確認してください
