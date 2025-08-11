# Agora EComm Host 설정 가이드

이 가이드는 Agora EComm Host 프로젝트를 안전하게 설정하는 방법을 설명합니다.

## 🔐 보안 우선

**중요**: App ID, 인증서 또는 토큰과 같은 민감한 정보를 git에 커밋하지 마세요! 설정 시스템은 귀하의 자격 증명을 안전하게 보호하도록 설계되었습니다.

## 📁 설정 파일

### 1. `config_template.xml` (커밋 가능)
- 플레이스홀더 값이 있는 템플릿 파일
- 모든 설정 옵션 포함
- 안전하게 공유하고 git에 커밋 가능

### 2. `config.xml` (절대 커밋하지 마세요!)
- 실제 자격 증명 포함
- git에서 자동으로 무시됨
- 설정 스크립트를 실행할 때 템플릿에서 생성됨

### 3. `ConfigManager.java`
- 설정을 관리하는 Java 클래스
- config.xml에서 값을 자동으로 로드
- 설정에 대한 안전한 액세스 제공

## 🚀 빠른 설정

### 옵션 1: 자동 설정 (권장)

#### macOS/Linux:
```bash
# 스크립트를 실행 가능하게 만들기
chmod +x scripts/auto_config.sh

# 설정 스크립트 실행
./scripts/auto_config.sh
```

#### Windows:
```cmd
# 설정 스크립트 실행
scripts\auto_config.bat
```

스크립트는 다음을 수행합니다:
1. ✅ 요구사항 확인 (ADB, Gradle)
2. 🔄 기존 설정 백업
3. 📝 자격 증명 입력 요청
4. ⚙️ 설정 파일 업데이트
5. 🧹 이전 설정 정리
6. 🔨 프로젝트 빌드 및 설치

### 옵션 2: 수동 설정

1. **템플릿을 설정 파일에 복사:**
   ```bash
   cp ecomm/src/main/res/values/config_template.xml ecomm/src/main/res/values/config.xml
   ```

2. **`config.xml`을 편집하여 실제 값 입력:**
   ```xml
   <string name="agora_app_id" translatable="false">실제_APP_ID</string>
   <string name="agora_app_certificate" translatable="false">실제_인증서</string>
   ```

3. **프로젝트 빌드:**
   ```bash
   ./gradlew assembleDebug
   ```

## 📋 필수 설정

### 필수 항목
- **Agora App ID**: Agora 애플리케이션 식별자
- **Agora App Certificate**: Agora 애플리케이션 인증서

### 선택 항목
- **Agora Token**: 보안 토큰 기반 인증용
- **Token Server URL**: 토큰 서버 엔드포인트
- **Default Channel Name**: 기본으로 참여할 채널
- **API Server URL**: 백엔드 API 서버
- **API Key/Secret**: API 인증용

## 🔧 설정 옵션

### 기능 플래그
```xml
<bool name="enable_beauty">true</bool>
<bool name="enable_filter">true</bool>
<bool name="enable_virtual_background">true</bool>
<bool name="enable_audio_processing">true</bool>
```

### 비디오/오디오 프로필
```xml
<string name="default_video_profile">720p</string>
<string name="default_audio_profile">music_standard</string>
```

### 디버그 설정
```xml
<bool name="enable_debug_mode">false</bool>
<bool name="enable_logging">true</bool>
<string name="log_level">info</string>
```

## 🔄 설정 업데이트

### 기존 설정을 업데이트하려면:
1. 설정 스크립트를 다시 실행
2. 스크립트가 현재 설정을 백업
3. 새 값 입력
4. 설정이 자동으로 업데이트됨

### 설정을 재설정하려면:
```java
ConfigManager configManager = new ConfigManager(context);
configManager.clearConfiguration();
```

## 🛡️ 보안 기능

### 자동 Git 무시
- `config.xml`이 자동으로 `.gitignore`에 추가됨
- 백업 파일이 무시됨
- 템플릿 파일이 안전하게 커밋됨

### 보안 로깅
- 민감한 값이 로그에서 마스킹됨
- 검증을 위해 처음 8자만 표시

### 설정 검증
- 필수 값 확인
- 누락된 자격 증명에 대한 경고
- 설정 상태 요약 제공

## 📱 코드에서 설정 사용

### 기본 사용법
```java
ConfigManager configManager = new ConfigManager(context);

// Agora App ID 가져오기
String appId = configManager.getAgoraAppId();

// 설정이 완료되었는지 확인
if (configManager.isConfigurationComplete()) {
    // 초기화 계속
} else {
    // 설정 오류 표시
}

// 설정 요약 가져오기
String summary = configManager.getConfigurationSummary();
```

### 기능 제어
```java
// 뷰티 기능이 활성화되었는지 확인
if (configManager.getBooleanConfig(ConfigManager.KEY_ENABLE_BEAUTY)) {
    // 뷰티 기능 초기화
}

// 필터 기능이 활성화되었는지 확인
if (configManager.getBooleanConfig(ConfigManager.KEY_ENABLE_FILTER)) {
    // 필터 기능 초기화
}
```

## 🚨 문제 해결

### 일반적인 문제

#### 1. "Agora App ID가 설정되지 않음"
- 설정 스크립트 실행
- `config.xml`이 존재하고 App ID가 포함되어 있는지 확인

#### 2. "설정 파일을 찾을 수 없음"
- `config_template.xml`이 존재하는지 확인
- 설정 스크립트를 실행하여 `config.xml` 생성

#### 3. 설정 후 빌드 오류
- 프로젝트 정리 및 재빌드
- 모든 필수 값이 설정되었는지 확인

### 디버그 명령

#### 설정 상태 확인
```java
ConfigManager configManager = new ConfigManager(context);
Log.d("Config", configManager.getConfigurationSummary());
```

#### Git 무시 확인
```bash
git status
# config.xml이 추적 파일에 나타나지 않아야 함
```

## 📚 추가 리소스

- [Agora Console](https://console.agora.io/) - App ID 및 인증서 가져오기
- [Agora Documentation](https://docs.agora.io/en/) - API 참조 및 가이드
- [Token Server Guide](https://docs.agora.io/en/Video/token_server_android) - 보안 토큰 인증

## 🤝 기여

이 프로젝트에 기여할 때:
1. ✅ 새로운 설정 옵션의 경우에만 `config_template.xml` 수정
2. ❌ 실제 자격 증명을 파일에 추가하지 마세요
3. 🔒 보안 우선 접근 방식 유지
4. 📝 설정 변경 시 이 문서 업데이트

## 📄 라이선스

이 설정 시스템은 Agora EComm Host 프로젝트의 일부입니다. 사용 조건은 메인 프로젝트 라이선스를 참조하세요.
