# Agora EComm Host

A powerful video processing application built with Agora RTC SDK, featuring beauty features, LUT filters, virtual backgrounds, and advanced video enhancement capabilities.

## 🌟 Project Features

- **Real-time Beauty**: AI-driven facial enhancement and beautification
- **LUT Filters**: Support for 32-bit cube files, professional color grading and filter effects
- **Virtual Backgrounds**: Real-time background replacement and blur effects
- **Video Enhancement**: Low-light enhancement, color optimization, noise reduction
- **Multi-language Support**: Chinese (Simplified), English, Japanese, Korean
- **Streamlined Architecture**: Single-module design focused on core video processing functionality

## 📸 LUT Effect Showcase

### Original Effect
![Original Video Effect](docs/imgs/Screenshot_20250811-170043_EComm%20Video%20Process%20Extension.jpg)

### After LUT Filter Application
![LUT Filter Effect](docs/imgs/Screenshot_20250811-170112_EComm%20Video%20Process%20Extension.jpg)

> The above images demonstrate the before and after comparison of applying LUT filters, showing the significant improvement in video quality through color grading and filter effects.

## 🏗️ Project Architecture

This project adopts a streamlined single-module architecture with all functionality integrated in the `ecomm/` module:

- **ecomm/**: Main video processing application module
  - Contains complete Android application code
  - Integrates Agora RTC SDK functionality
  - Implements beauty, filter, virtual background and other core features
  - Supports multi-language interface and configuration management

## 🚀 Quick Start

### System Requirements
- Android Studio Arctic Fox or later
- Android SDK API 21+
- Agora Console account
- Device supporting OpenGL ES 3.0

### Installation Steps
1. Clone the project repository
2. Run the configuration script:
   ```bash
   # macOS/Linux
   chmod +x scripts/auto_config.sh
   ./scripts/auto_config.sh
   
   # Windows
   scripts\auto_config.bat
   ```
3. Follow the prompts to configure your Agora credentials
4. Open the project in Android Studio
5. Build and run the ecomm module

## 📚 Documentation

### Multi-language Documentation
- [中文文档](docs/README_zh-CN.md)
- [English Documentation](docs/README_en-US.md)
- [日本語ドキュメント](docs/README_ja-JP.md)
- [한국어 문서](docs/README_ko-KR.md)

### Configuration Guides
- [配置指南 - 中文](docs/README_CONFIGURATION_zh-CN.md)
- [Configuration Guide - English](docs/README_CONFIGURATION_en-US.md)
- [設定ガイド - 日本語](docs/README_CONFIGURATION_ja-JP.md)
- [설정 가이드 - 한국어](docs/README_CONFIGURATION_ko-KR.md)

### Technical Documentation
- [Configuration Summary](docs/CONFIGURATION_SUMMARY.md) - Technical configuration overview
- [Refactoring Summary](ecomm/REFACTORING_SUMMARY.md) - Development notes and architecture details
- [Git Configuration Notice](docs/GIT_IGNORE_NOTICE.md) - Security configuration guide
- [Release Checklist](docs/RELEASE_CHECKLIST.md) - Pre-release checklist

## 🔗 Related Links

- [Agora Official Documentation](https://docs.agora.io/en/)
- [Agora Console](https://console.agora.io/)
- [Agora Developer Community](https://docs.agora.io/en/)

## 🤝 Contributing

1. Fork the project repository
2. Create a feature branch
3. Follow coding standards
4. Submit a Pull Request

## 📄 License

This project is licensed under the specified open source license. Please read the license terms carefully before use.

---

**Agora EComm Host** - Making video communication smarter and more beautiful! 🎥✨
