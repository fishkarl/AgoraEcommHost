# Agora EComm Host

基于Agora RTC SDK构建的强大视频处理应用，具备美颜功能、LUT滤镜、虚拟背景和高级视频增强功能。

## 🌟 项目特色

- **实时美颜**: AI驱动的面部美化和修饰
- **LUT滤镜**: 支持32位cube文件，专业级色彩分级和滤镜效果
- **虚拟背景**: 实时背景替换和模糊效果
- **视频增强**: 低光增强、色彩优化、降噪处理
- **多语言支持**: 简体中文、英文、日文、韩文
- **精简架构**: 专注于视频处理核心功能的单一模块设计

## 🏗️ 项目架构

本项目采用精简的单模块架构，所有功能都集成在 `ecomm/` 模块中：

- **ecomm/**: 主要的视频处理应用模块
  - 包含完整的Android应用代码
  - 集成Agora RTC SDK功能
  - 实现美颜、滤镜、虚拟背景等核心特性
  - 支持多语言界面和配置管理

## 🚀 快速开始

### 系统要求
- Android Studio Arctic Fox 或更高版本
- Android SDK API 21+
- Agora Console 账户
- 支持OpenGL ES 3.0的设备

### 安装步骤
1. 克隆项目仓库
2. 运行配置脚本：
   ```bash
   # macOS/Linux
   chmod +x scripts/auto_config.sh
   ./scripts/auto_config.sh
   
   # Windows
   scripts\auto_config.bat
   ```
3. 按照提示配置Agora凭据
4. 在Android Studio中打开项目
5. 构建并运行ecomm模块

## 📚 文档

### 多语言文档
- [中文文档](docs/README_zh-CN.md)
- [English Documentation](docs/README_en-US.md)
- [日本語ドキュメント](docs/README_ja-JP.md)
- [한국어 문서](docs/README_ko-KR.md)

### 配置指南
- [配置指南 - 中文](docs/README_CONFIGURATION_zh-CN.md)
- [Configuration Guide - English](docs/README_CONFIGURATION_en-US.md)
- [設定ガイド - 日本語](docs/README_CONFIGURATION_ja-JP.md)
- [설정 가이드 - 한국어](docs/README_CONFIGURATION_ko-KR.md)

### 技术文档
- [配置总结](docs/CONFIGURATION_SUMMARY.md) - 技术配置概览
- [重构总结](ecomm/REFACTORING_SUMMARY.md) - 开发笔记和架构说明
- [Git配置提醒](docs/GIT_IGNORE_NOTICE.md) - 安全配置指南
- [发布检查清单](docs/RELEASE_CHECKLIST.md) - 发布前检查

## 🔗 相关链接

- [Agora官方文档](https://docs.agora.io/en/)
- [Agora Console](https://console.agora.io/)
- [Agora开发者社区](https://docs.agora.io/en/)

## 🤝 贡献指南

1. Fork项目仓库
2. 创建功能分支
3. 遵循代码规范
4. 提交Pull Request

## 📄 许可证

本项目采用指定的开源许可证。使用前请仔细阅读许可证条款。

---

**Agora EComm Host** - 让视频通信更智能、更美丽！🎥✨
