# Agora EComm Host

A powerful video processing application built with Agora RTC SDK, featuring beauty features, LUT filters, virtual backgrounds, and advanced video enhancement capabilities.

## 🌟 Project Features

### 🎥 Video Processing Capabilities
- **Real-time Beauty**: AI-driven facial enhancement and beautification
- **LUT Filters**: Support for 32-bit cube files, professional color grading and filter effects
- **Virtual Backgrounds**: Real-time background replacement and blur effects
- **Video Enhancement**: Low-light enhancement, color optimization, noise reduction

### 🎵 Audio Processing
- **Audio Beautification**: Professional-grade audio enhancement algorithms
- **Noise Reduction**: Intelligent environmental noise elimination
- **Echo Cancellation**: High-quality call experience
- **Audio Encoding**: Support for multiple audio formats

### 🌍 Multi-language Support
- Chinese (Simplified), English, Japanese, Korean
- Localized user interface
- Multi-language error messages and help information

### 🔧 Technical Architecture
- **Streamlined Design**: Single-module architecture focused on core video processing functionality
- **Configuration Management**: Secure credential management system
- **Automation Scripts**: Cross-platform configuration script support
- **Real-time Processing**: Low-latency video stream processing

## 📸 LUT Effect Showcase

### Original Effect
![Original Video Effect](../docs/imgs/Screenshot_20250811-170043_EComm%20Video%20Process%20Extension.jpg)

### After LUT Filter Application
![LUT Filter Effect](../docs/imgs/Screenshot_20250811-170112_EComm%20Video%20Process%20Extension.jpg)

> The above images demonstrate the before and after comparison of applying LUT filters, showing the significant improvement in video quality through color grading and filter effects.

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

## 📱 Main Features

### Video Processing
- **Beauty Features**: Whitening, smoothing, redness, sharpening
- **LUT System**: Support for 32-bit cube files, professional color grading and filter effects
- **Background Processing**: Virtual backgrounds, background blur, background replacement
- **Quality Enhancement**: Resolution upscaling, color optimization, contrast adjustment

### Real-time Communication
- **Channel Management**: Multi-channel support and switching
- **User Management**: User permissions and role control
- **Device Control**: Camera switching, microphone control
- **Network Optimization**: Adaptive bitrate and network quality monitoring

### Development Tools
- **Configuration Management**: Secure credential storage and access
- **Logging System**: Detailed debug and error logs
- **Performance Monitoring**: Real-time performance metrics and optimization suggestions
- **Error Handling**: Friendly error messages and solutions

## 🏗️ Project Structure

This project adopts a streamlined single-module architecture with all functionality integrated in the `ecomm/` module:

```
AgoraEcommHost/
├── ecomm/                        # Main video processing application module
│   ├── src/main/java/            # Java video processing code
│   ├── src/main/res/             # Module resources
│   ├── src/main/assets/          # LUT files and filter resources
│   └── build.gradle              # Module build configuration
├── scripts/                      # Automation scripts
│   ├── auto_config.sh           # macOS/Linux configuration script
│   └── auto_config.bat          # Windows configuration script
└── docs/                         # Project documentation
```

> **Note**: The project has been simplified from a multi-module architecture to a single-module architecture, removing the unnecessary app module to focus on core video processing functionality.

## 🔐 Security Features

### Credential Protection
- Sensitive configuration files automatically ignored by git
- Encrypted credential storage
- Secure logging (sensitive information automatically masked)

### Permission Management
- Runtime permission requests
- Principle of least privilege
- User privacy protection

## 📚 Documentation Resources

### Configuration Guides
- [配置指南 - 中文](README_CONFIGURATION_zh-CN.md)
- [Configuration Guide - English](README_CONFIGURATION_en-US.md)
- [設定ガイド - 日本語](README_CONFIGURATION_ja-JP.md)
- [설정 가이드 - 한국어](README_CONFIGURATION_ko-KR.md)

### Technical Documentation
- [Configuration Summary](CONFIGURATION_SUMMARY.md) - Technical configuration overview
- [Refactoring Summary](ecomm/REFACTORING_SUMMARY.md) - Development notes
- [Git Configuration Notice](GIT_IGNORE_NOTICE.md) - Security configuration guide

## 🤝 Contributing Guidelines

### Development Environment
1. Fork the project repository
2. Create a feature branch
3. Follow coding standards
4. Submit a Pull Request

### Coding Standards
- Use meaningful variable and function names
- Add appropriate comments and documentation
- Follow Android development best practices
- Ensure code passes all tests

### Security Principles
- Never commit files containing real credentials
- Use secure configuration management methods
- Follow the principle of least privilege
- Protect user privacy and data security

## 🚨 Important Notes

### Pre-use Preparation
- Ensure you have a valid Agora Console account
- Obtain necessary App ID and certificates
- Check device compatibility
- Understand relevant laws and regulations

### Performance Optimization
- Adjust video quality based on device performance
- Use beauty and LUT filter features reasonably
- Monitor network quality and bandwidth usage
- Regularly clean cache and temporary files

## 📄 License

This project is licensed under the specified open source license. Please read the license terms carefully before use.

## 🆘 Technical Support

### Configuration Issues
- Refer to multi-language configuration guides
- Check automated configuration scripts
- Verify Agora credential validity

### Technical Issues
- Check [Agora Official Documentation](https://docs.agora.io/en/)
- Review project logs and error messages
- Refer to frequently asked questions

### Community Support
- Submit issues and bug reports
- Participate in project discussions
- Share usage experiences and suggestions

---

**Agora EComm Host** - Making video communication smarter and more beautiful! 🎥✨
