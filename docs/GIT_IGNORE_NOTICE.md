# Git 配置重要提醒 / Git Configuration Important Notice

## 🔐 安全配置 / Security Configuration

在将此项目发布到git仓库之前，请确保以下配置：

### 1. 创建 .gitignore 文件
确保项目根目录下有 `.gitignore` 文件，并包含以下内容：

```gitignore
# Agora Configuration Files (Sensitive Information)
ecomm/src/main/res/values/config.xml
ecomm/src/main/res/values/config.xml.backup.*
ecomm/src/main/res/values/config.xml.tmp

# Configuration backups
*.backup.*
*.tmp

# Local configuration overrides
local.properties
local.gradle

# Keystore files
*.keystore
*.jks

# Log files
*.log
logs/

# Temporary files
*.tmp
*.temp

# IDE specific files
.vscode/
*.swp
*.swo
*~

# OS generated files
Thumbs.db
ehthumbs.db
Desktop.ini
```

### 2. 验证 config.xml 保护
运行以下命令确保 `config.xml` 不会被意外提交：

```bash
git status
# config.xml 不应出现在跟踪文件中
```

### 3. 如果 config.xml 已被跟踪
如果 `config.xml` 已经被git跟踪，请运行：

```bash
git rm --cached ecomm/src/main/res/values/config.xml
git commit -m "Remove config.xml from tracking (contains sensitive data)"
```

## 🚨 重要提醒 / Important Reminders

- **永远不要** 将包含真实App ID的 `config.xml` 提交到git
- **永远不要** 将包含真实证书或令牌的文件提交到git
- 只提交 `config_template.xml` 模板文件
- 使用自动化配置脚本或手动创建 `config.xml`

## 📚 多语言文档 / Multi-language Documentation

项目已提供四种语言的配置指南：
- [English](README_en-US.md)
- [简体中文](README_zh-CN.md)
- [日本語](README_ja-JP.md)
- [한국어](README_ko-KR.md)

## 🔧 自动化配置 / Automated Configuration

使用提供的脚本可以自动创建安全的配置：

```bash
# macOS/Linux
./scripts/auto_config.sh

# Windows
scripts\auto_config.bat
```

---

**安全第一，配置无忧！** / **Security First, Configuration Worry-free!**
