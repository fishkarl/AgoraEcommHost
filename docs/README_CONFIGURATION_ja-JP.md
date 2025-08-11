# Agora EComm Host 設定ガイド

このガイドでは、Agora EComm Host プロジェクトを安全に設定する方法について説明します。

## 🔐 セキュリティ第一

**重要**: App ID、証明書、トークンなどの機密情報をgitにコミットしないでください！設定システムは、あなたの認証情報を安全に保つように設計されています。

## 📁 設定ファイル

### 1. `config_template.xml` (コミット可能)
- プレースホルダー値を持つテンプレートファイル
- すべての設定オプションを含む
- 安全に共有し、gitにコミットできる

### 2. `config.xml` (絶対にコミットしない！)
- 実際の認証情報を含む
- gitによって自動的に無視される
- 設定スクリプトを実行したときにテンプレートから作成される

### 3. `ConfigManager.java`
- 設定を管理するJavaクラス
- config.xmlから値を自動的に読み込む
- 設定への安全なアクセスを提供

## 🚀 クイックセットアップ

### オプション1: 自動セットアップ (推奨)

#### macOS/Linux:
```bash
# スクリプトを実行可能にする
chmod +x scripts/auto_config.sh

# 設定スクリプトを実行
./scripts/auto_config.sh
```

#### Windows:
```cmd
# 設定スクリプトを実行
scripts\auto_config.bat
```

スクリプトは以下を実行します：
1. ✅ 要件をチェック (ADB, Gradle)
2. 🔄 既存の設定をバックアップ
3. 📝 認証情報の入力を求める
4. ⚙️ 設定ファイルを更新
5. 🧹 古い設定をクリーンアップ
6. 🔨 プロジェクトをビルドしてインストール

### オプション2: 手動セットアップ

1. **テンプレートを設定ファイルにコピー:**
   ```bash
   cp ecomm/src/main/res/values/config_template.xml ecomm/src/main/res/values/config.xml
   ```

2. **`config.xml`を編集して実際の値を入力:**
   ```xml
   <string name="agora_app_id" translatable="false">実際のApp ID</string>
   <string name="agora_app_certificate" translatable="false">実際の証明書</string>
   ```

3. **プロジェクトをビルド:**
   ```bash
   ./gradlew assembleDebug
   ```

## 📋 必要な設定

### 必須項目
- **Agora App ID**: Agoraアプリケーション識別子
- **Agora App Certificate**: Agoraアプリケーション証明書

### オプション項目
- **Agora Token**: セキュアなトークンベース認証用
- **Token Server URL**: トークンサーバーのエンドポイント
- **Default Channel Name**: デフォルトで参加するチャンネル
- **API Server URL**: バックエンドAPIサーバー
- **API Key/Secret**: API認証用

## 🔧 設定オプション

### 機能フラグ
```xml
<bool name="enable_beauty">true</bool>
<bool name="enable_filter">true</bool>
<bool name="enable_virtual_background">true</bool>
<bool name="enable_audio_processing">true</bool>
```

### ビデオ/オーディオプロファイル
```xml
<string name="default_video_profile">720p</string>
<string name="default_audio_profile">music_standard</string>
```

### デバッグ設定
```xml
<bool name="enable_debug_mode">false</bool>
<bool name="enable_logging">true</bool>
<string name="log_level">info</string>
```

## 🔄 設定の更新

### 既存の設定を更新するには:
1. 設定スクリプトを再度実行
2. スクリプトが現在の設定をバックアップ
3. 新しい値を入力
4. 設定が自動的に更新される

### 設定をリセットするには:
```java
ConfigManager configManager = new ConfigManager(context);
configManager.clearConfiguration();
```

## 🛡️ セキュリティ機能

### 自動Git無視
- `config.xml`は自動的に`.gitignore`に追加される
- バックアップファイルは無視される
- テンプレートファイルは安全にコミットできる

### セキュアログ
- 機密値はログでマスクされる
- 検証用に最初の8文字のみ表示

### 設定検証
- 必要な値をチェック
- 不足している認証情報について警告
- 設定状態のサマリーを提供

## 📱 コードで設定を使用

### 基本的な使用方法
```java
ConfigManager configManager = new ConfigManager(context);

// Agora App IDを取得
String appId = configManager.getAgoraAppId();

// 設定が完了しているかチェック
if (configManager.isConfigurationComplete()) {
    // 初期化を続行
} else {
    // 設定エラーを表示
}

// 設定サマリーを取得
String summary = configManager.getConfigurationSummary();
```

### 機能制御
```java
// 美顔機能が有効かチェック
if (configManager.getBooleanConfig(ConfigManager.KEY_ENABLE_BEAUTY)) {
    // 美顔機能を初期化
}

// フィルター機能が有効かチェック
if (configManager.getBooleanConfig(ConfigManager.KEY_ENABLE_FILTER)) {
    // フィルター機能を初期化
}
```

## 🚨 トラブルシューティング

### よくある問題

#### 1. "Agora App IDが設定されていません"
- 設定スクリプトを実行
- `config.xml`が存在し、App IDが含まれているかチェック

#### 2. "設定ファイルが見つかりません"
- `config_template.xml`が存在することを確認
- 設定スクリプトを実行して`config.xml`を作成

#### 3. 設定後のビルドエラー
- プロジェクトをクリーンして再ビルド
- すべての必要な値が設定されているかチェック

### デバッグコマンド

#### 設定状態をチェック
```java
ConfigManager configManager = new ConfigManager(context);
Log.d("Config", configManager.getConfigurationSummary());
```

#### Git無視を確認
```bash
git status
# config.xmlは追跡ファイルに表示されないはず
```

## 📚 その他のリソース

- [Agora Console](https://console.agora.io/) - App IDと証明書を取得
- [Agora Documentation](https://docs.agora.io/en/) - APIリファレンスとガイド
- [Token Server Guide](https://docs.agora.io/en/Video/token_server_android) - セキュアなトークン認証

## 🤝 貢献

このプロジェクトに貢献する際は：
1. ✅ 新しい設定オプションの場合のみ`config_template.xml`を修正
2. ❌ 実際の認証情報をファイルに追加しない
3. 🔒 セキュリティ第一のアプローチを維持
4. 📝 設定変更があればこのドキュメントを更新

## 📄 ライセンス

この設定システムはAgora EComm Hostプロジェクトの一部です。使用条件については、メインプロジェクトのライセンスを参照してください。
