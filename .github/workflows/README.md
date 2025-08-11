# GitHub Actions Workflows

本项目包含两个GitHub Actions workflow文件，用于自动化构建、测试和部署流程。

## 📋 Workflow 文件

### 1. `android.yml` - 完整版CI/CD Pipeline
包含完整的CI/CD流程，适用于生产环境：

- **构建阶段**: 构建Debug和Release APK
- **测试阶段**: 运行单元测试和集成测试
- **代码质量**: 运行Lint检查和Detekt静态分析
- **安全扫描**: 依赖安全检查
- **发布阶段**: 自动签名和发布到GitHub Releases

### 2. `android-simple.yml` - 简化版构建测试
适用于开发阶段的快速构建和测试：

- **构建**: Debug和Release APK构建
- **测试**: 单元测试和Lint检查
- **产物**: 上传APK文件作为构建产物

## 🚀 使用方法

### 自动触发
- 推送到 `main` 或 `develop` 分支时自动触发
- 创建Pull Request时自动触发
- 发布Release时自动触发（仅完整版）

### 手动触发
1. 进入GitHub仓库的Actions页面
2. 选择要运行的workflow
3. 点击"Run workflow"按钮
4. 选择分支和参数
5. 点击"Run workflow"开始执行

## ⚙️ 环境要求

- **JDK版本**: 17 (Temurin)
- **Android SDK**: API 35
- **构建工具**: 35.0.0
- **运行环境**: Ubuntu Latest

## 🔐 密钥配置

如需使用发布功能，请在GitHub仓库的Settings > Secrets and variables > Actions中添加以下密钥：

- `SIGNING_KEY`: 签名密钥的Base64编码
- `KEY_ALIAS`: 密钥别名
- `KEY_STORE_PASSWORD`: 密钥库密码
- `KEY_PASSWORD`: 密钥密码

## 📱 构建产物

### Debug APK
- 路径: `ecomm/build/outputs/apk/debug/ecomm-debug.apk`
- 用途: 开发测试和调试

### Release APK
- 路径: `ecomm/build/outputs/apk/release/ecomm-release.apk`
- 用途: 生产环境发布

### Release Bundle
- 路径: `ecomm/build/outputs/bundle/release/`
- 用途: Google Play Store发布

## 🧪 测试覆盖

- **单元测试**: `./gradlew :ecomm:testDebugUnitTest`
- **集成测试**: `./gradlew :ecomm:connectedDebugAndroidTest`
- **代码质量**: `./gradlew :ecomm:lintDebug`
- **静态分析**: `./gradlew :ecomm:detekt`

## 📊 缓存策略

- **Gradle缓存**: 自动缓存依赖包和构建产物
- **Android SDK**: 缓存SDK组件和构建工具
- **构建优化**: 并行构建和增量编译

## 🔍 故障排除

### 常见问题
1. **构建失败**: 检查依赖版本兼容性
2. **测试失败**: 查看测试报告和日志
3. **内存不足**: 调整GRADLE_OPTS中的内存设置
4. **权限问题**: 确保gradlew有执行权限

### 日志查看
- 在Actions页面查看详细的执行日志
- 下载构建产物和测试报告
- 检查缓存命中率和构建时间

## 📚 相关链接

- [GitHub Actions 官方文档](https://docs.github.com/en/actions)
- [Android Gradle Plugin 文档](https://developer.android.com/studio/build)
- [Agora RTC SDK 文档](https://docs.agora.io/en/)
