# Agora EComm Host 配置指南

本指南说明如何安全地配置 Agora EComm Host 项目。

## 🔐 安全第一

**重要提示**: 永远不要将敏感信息（如App ID、证书或令牌）提交到git！配置系统设计为保护您的凭据安全。

## 📁 配置文件

### 1. `config_template.xml` (可以提交)
- 包含占位符值的模板文件
- 包含所有配置选项
- 可以安全地分享和提交到git

### 2. `config.xml` (永远不要提交！)
- 包含您的实际凭据
- 自动被git忽略
- 运行配置脚本时从模板创建

### 3. `ConfigManager.java`
- 管理配置的Java类
- 自动从config.xml加载值
- 提供安全的配置访问

## 🚀 快速设置

### 选项1: 自动设置 (推荐)

#### macOS/Linux:
```bash
# 使脚本可执行
chmod +x scripts/auto_config.sh

# 运行配置脚本
./scripts/auto_config.sh
```

#### Windows:
```cmd
# 运行配置脚本
scripts\auto_config.bat
```

脚本将：
1. ✅ 检查要求 (ADB, Gradle)
2. 🔄 备份现有配置
3. 📝 提示输入您的凭据
4. ⚙️ 更新配置文件
5. 🧹 清理旧配置
6. 🔨 构建并安装项目

### 选项2: 手动设置

1. **复制模板到配置文件:**
   ```bash
   cp ecomm/src/main/res/values/config_template.xml ecomm/src/main/res/values/config.xml
   ```

2. **编辑 `config.xml` 填入您的值:**
   ```xml
   <string name="agora_app_id" translatable="false">您的实际App ID</string>
   <string name="agora_app_certificate" translatable="false">您的实际证书</string>
   ```

3. **构建项目:**
   ```bash
   ./gradlew assembleDebug
   ```

## 📋 必需配置

### 必需项
- **Agora App ID**: 您的Agora应用标识符
- **Agora App Certificate**: 您的Agora应用证书

### 可选项
- **Agora Token**: 用于安全令牌认证
- **Token Server URL**: 您的令牌服务器端点
- **Default Channel Name**: 默认加入的频道
- **API Server URL**: 您的后端API服务器
- **API Key/Secret**: 用于API认证

## 🔧 配置选项

### 功能开关
```xml
<bool name="enable_beauty">true</bool>
<bool name="enable_filter">true</bool>
<bool name="enable_virtual_background">true</bool>
<bool name="enable_audio_processing">true</bool>
```

### 视频/音频配置
```xml
<string name="default_video_profile">720p</string>
<string name="default_audio_profile">music_standard</string>
```

### 调试设置
```xml
<bool name="enable_debug_mode">false</bool>
<bool name="enable_logging">true</bool>
<string name="log_level">info</string>
```

## 🔄 更新配置

### 更新现有配置:
1. 再次运行配置脚本
2. 脚本将备份您当前的配置
3. 输入新值
4. 配置将自动更新

### 重置配置:
```java
ConfigManager configManager = new ConfigManager(context);
configManager.clearConfiguration();
```

## 🛡️ 安全特性

### 自动Git忽略
- `config.xml` 自动添加到 `.gitignore`
- 备份文件被忽略
- 模板文件可以安全提交

### 安全日志
- 敏感值在日志中被屏蔽
- 仅显示前8个字符用于验证

### 配置验证
- 检查必需值
- 警告缺失的凭据
- 提供配置状态摘要

## 📱 在代码中使用配置

### 基本用法
```java
ConfigManager configManager = new ConfigManager(context);

// 获取Agora App ID
String appId = configManager.getAgoraAppId();

// 检查配置是否完整
if (configManager.isConfigurationComplete()) {
    // 继续初始化
} else {
    // 显示配置错误
}

// 获取配置摘要
String summary = configManager.getConfigurationSummary();
```

### 功能控制
```java
// 检查美颜功能是否启用
if (configManager.getBooleanConfig(ConfigManager.KEY_ENABLE_BEAUTY)) {
    // 初始化美颜功能
}

// 检查滤镜功能是否启用
if (configManager.getBooleanConfig(ConfigManager.KEY_ENABLE_FILTER)) {
    // 初始化滤镜功能
}
```

## 🚨 故障排除

### 常见问题

#### 1. "Agora App ID未配置"
- 运行配置脚本
- 检查 `config.xml` 是否存在并包含您的App ID

#### 2. "配置文件未找到"
- 确保 `config_template.xml` 存在
- 运行配置脚本创建 `config.xml`

#### 3. 配置后构建错误
- 清理并重新构建项目
- 检查所有必需值是否已设置

### 调试命令

#### 检查配置状态
```java
ConfigManager configManager = new ConfigManager(context);
Log.d("Config", configManager.getConfigurationSummary());
```

#### 验证Git忽略
```bash
git status
# config.xml 不应出现在跟踪文件中
```

## 📚 其他资源

- [Agora Console](https://console.agora.io/) - 获取您的App ID和证书
- [Agora Documentation](https://docs.agora.io/en/) - API参考和指南
- [Token Server Guide](https://docs.agora.io/en/Video/token_server_android) - 安全令牌认证

## 🤝 贡献

为该项目做贡献时：
1. ✅ 只为新配置选项修改 `config_template.xml`
2. ❌ 永远不要在任何文件中添加真实凭据
3. 🔒 保持安全第一的方法
4. 📝 为任何配置更改更新此文档

## 📄 许可证

此配置系统是Agora EComm Host项目的一部分。请参考主项目许可证了解使用条款。
