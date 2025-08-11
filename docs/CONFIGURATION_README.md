# Agora EComm Host Configuration Guide

This guide explains how to configure the Agora EComm Host project with your credentials securely.

## 🔐 Security First

**IMPORTANT**: Never commit sensitive information like App IDs, certificates, or tokens to git! The configuration system is designed to keep your credentials secure.

## 📁 Configuration Files

### 1. `config_template.xml` (Safe to commit)
- Template file with placeholder values
- Contains all configuration options
- Safe to share and commit to git

### 2. `config.xml` (NEVER commit!)
- Contains your actual credentials
- Automatically ignored by git
- Created from template when you run the configuration script

### 3. `ConfigManager.java`
- Java class that manages configuration
- Automatically loads values from config.xml
- Provides secure access to configuration

## 🚀 Quick Setup

### Option 1: Automated Setup (Recommended)

#### For macOS/Linux:
```bash
# Make script executable
chmod +x scripts/auto_config.sh

# Run configuration script
./scripts/auto_config.sh
```

#### For Windows:
```cmd
# Run configuration script
scripts\auto_config.bat
```

The script will:
1. ✅ Check requirements (ADB, Gradle)
2. 🔄 Backup existing configuration
3. 📝 Prompt for your credentials
4. ⚙️ Update configuration files
5. 🧹 Clean up old configuration
6. 🔨 Build and install the project

### Option 2: Manual Setup

1. **Copy template to config file:**
   ```bash
   cp ecomm/src/main/res/values/config_template.xml ecomm/src/main/res/values/config.xml
   ```

2. **Edit `config.xml` with your values:**
   ```xml
   <string name="agora_app_id" translatable="false">YOUR_ACTUAL_APP_ID</string>
   <string name="agora_app_certificate" translatable="false">YOUR_ACTUAL_CERTIFICATE</string>
   ```

3. **Build the project:**
   ```bash
   ./gradlew assembleDebug
   ```

## 📋 Required Configuration

### Essential (Required)
- **Agora App ID**: Your Agora application identifier
- **Agora App Certificate**: Your Agora application certificate

### Optional
- **Agora Token**: For secure token-based authentication
- **Token Server URL**: Your token server endpoint
- **Default Channel Name**: Default channel to join
- **API Server URL**: Your backend API server
- **API Key/Secret**: For API authentication

## 🔧 Configuration Options

### Feature Flags
```xml
<bool name="enable_beauty">true</bool>
<bool name="enable_filter">true</bool>
<bool name="enable_virtual_background">true</bool>
<bool name="enable_audio_processing">true</bool>
```

### Video/Audio Profiles
```xml
<string name="default_video_profile">720p</string>
<string name="default_audio_profile">music_standard</string>
```

### Debug Settings
```xml
<bool name="enable_debug_mode">false</bool>
<bool name="enable_logging">true</bool>
<string name="log_level">info</string>
```

## 🔄 Updating Configuration

### To update existing configuration:
1. Run the configuration script again
2. The script will backup your current config
3. Enter new values when prompted
4. Configuration will be updated automatically

### To reset configuration:
```java
ConfigManager configManager = new ConfigManager(context);
configManager.clearConfiguration();
```

## 🛡️ Security Features

### Automatic Git Ignoring
- `config.xml` is automatically added to `.gitignore`
- Backup files are ignored
- Template files are safe to commit

### Secure Logging
- Sensitive values are masked in logs
- Only shows first 8 characters for verification

### Configuration Validation
- Checks for required values
- Warns about missing credentials
- Provides configuration status summary

## 📱 Using Configuration in Code

### Basic Usage
```java
ConfigManager configManager = new ConfigManager(context);

// Get Agora App ID
String appId = configManager.getAgoraAppId();

// Check if configuration is complete
if (configManager.isConfigurationComplete()) {
    // Proceed with initialization
} else {
    // Show configuration error
}

// Get configuration summary
String summary = configManager.getConfigurationSummary();
```

### Feature Control
```java
// Check if beauty features are enabled
if (configManager.getBooleanConfig(ConfigManager.KEY_ENABLE_BEAUTY)) {
    // Initialize beauty features
}

// Check if filters are enabled
if (configManager.getBooleanConfig(ConfigManager.KEY_ENABLE_FILTER)) {
    // Initialize filter features
}
```

## 🚨 Troubleshooting

### Common Issues

#### 1. "Agora App ID not configured"
- Run the configuration script
- Check that `config.xml` exists and contains your App ID

#### 2. "Configuration file not found"
- Ensure `config_template.xml` exists
- Run the configuration script to create `config.xml`

#### 3. Build errors after configuration
- Clean and rebuild the project
- Check that all required values are set

### Debug Commands

#### Check Configuration Status
```java
ConfigManager configManager = new ConfigManager(context);
Log.d("Config", configManager.getConfigurationSummary());
```

#### Verify Git Ignoring
```bash
git status
# config.xml should not appear in tracked files
```

## 📚 Additional Resources

- [Agora Console](https://console.agora.io/) - Get your App ID and Certificate
- [Agora Documentation](https://docs.agora.io/en/) - API reference and guides
- [Token Server Guide](https://docs.agora.io/en/Video/token_server_android) - Secure token authentication

## 🤝 Contributing

When contributing to this project:
1. ✅ Only modify `config_template.xml` for new configuration options
2. ❌ Never add real credentials to any files
3. 🔒 Keep the security-first approach
4. 📝 Update this documentation for any configuration changes

## 📄 License

This configuration system is part of the Agora EComm Host project. Please refer to the main project license for usage terms.
