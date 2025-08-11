@echo off
setlocal enabledelayedexpansion

REM Agora EComm Host Auto Configuration Script for Windows
REM This script automatically configures the project with your Agora credentials

echo ===========================================
echo Agora EComm Host Auto Configuration Script
echo ===========================================
echo.

REM Set project paths
set "PROJECT_ROOT=%~dp0.."
set "CONFIG_TEMPLATE=%PROJECT_ROOT%\ecomm\src\main\res\values\config_template.xml"
set "CONFIG_FILE=%PROJECT_ROOT%\ecomm\src\main\res\values\config.xml"
set "STRINGS_FILE=%PROJECT_ROOT%\ecomm\src\main\res\values\strings.xml"

REM Check requirements
echo [INFO] Checking requirements...

REM Check if ADB is available
adb version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] ADB is not installed or not in PATH
    echo [ERROR] Please install Android SDK Platform Tools
    pause
    exit /b 1
)

REM Check if Gradle is available
gradle --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [WARNING] Gradle is not installed or not in PATH
    echo [WARNING] You may need to use gradlew.bat instead
)

echo [SUCCESS] Requirements check passed
echo.

REM Backup existing configuration
if exist "%CONFIG_FILE%" (
    echo [INFO] Backing up existing configuration...
    set "BACKUP_FILE=%CONFIG_FILE%.backup.%date:~-4,4%%date:~-10,2%%date:~-7,2%_%time:~0,2%%time:~3,2%%time:~6,2%"
    set "BACKUP_FILE=%BACKUP_FILE: =0%"
    copy "%CONFIG_FILE%" "%BACKUP_FILE%" >nul
    echo [SUCCESS] Configuration backed up
    echo.
)

REM Create configuration from template
echo [INFO] Creating configuration from template...

if not exist "%CONFIG_TEMPLATE%" (
    echo [ERROR] Configuration template not found: %CONFIG_TEMPLATE%
    pause
    exit /b 1
)

copy "%CONFIG_TEMPLATE%" "%CONFIG_FILE%" >nul
echo [SUCCESS] Configuration file created from template
echo.

REM Prompt for configuration values
echo [INFO] Please enter your Agora configuration values:
echo.

REM Agora App ID
set /p "AGORA_APP_ID=Enter your Agora App ID: "
if "!AGORA_APP_ID!"=="" (
    echo [ERROR] Agora App ID is required
    pause
    exit /b 1
)

REM Agora App Certificate
set /p "AGORA_APP_CERTIFICATE=Enter your Agora App Certificate: "
if "!AGORA_APP_CERTIFICATE!"=="" (
    echo [ERROR] Agora App Certificate is required
    pause
    exit /b 1
)

REM Agora Token (optional)
set /p "AGORA_TOKEN=Enter your Agora Token (optional, press Enter to skip): "

REM Token Server URL
set /p "TOKEN_SERVER_URL=Enter your Token Server URL (optional, press Enter to skip): "

REM Default Channel Name
set /p "DEFAULT_CHANNEL=Enter default channel name [test_channel]: "
if "!DEFAULT_CHANNEL!"=="" set "DEFAULT_CHANNEL=test_channel"

REM API Server URL (optional)
set /p "API_SERVER_URL=Enter your API Server URL (optional, press Enter to skip): "

REM API Key (optional)
set /p "API_KEY=Enter your API Key (optional, press Enter to skip): "

REM API Secret (optional)
set /p "API_SECRET=Enter your API Secret (optional, press Enter to skip): "

echo.

REM Update configuration file
echo [INFO] Updating configuration file...

REM Create temporary file for sed-like replacement
set "TEMP_FILE=%CONFIG_FILE%.tmp"

REM Update Agora App ID
powershell -Command "(Get-Content '%CONFIG_FILE%') -replace 'YOUR_AGORA_APP_ID_HERE', '!AGORA_APP_ID!' | Set-Content '%TEMP_FILE%'"
move /y "%TEMP_FILE%" "%CONFIG_FILE%" >nul

REM Update Agora App Certificate
powershell -Command "(Get-Content '%CONFIG_FILE%') -replace 'YOUR_AGORA_APP_CERTIFICATE_HERE', '!AGORA_APP_CERTIFICATE!' | Set-Content '%TEMP_FILE%'"
move /y "%TEMP_FILE%" "%CONFIG_FILE%" >nul

REM Update Agora Token if provided
if not "!AGORA_TOKEN!"=="" (
    powershell -Command "(Get-Content '%CONFIG_FILE%') -replace 'YOUR_AGORA_TOKEN_HERE', '!AGORA_TOKEN!' | Set-Content '%TEMP_FILE%'"
    move /y "%TEMP_FILE%" "%CONFIG_FILE%" >nul
)

REM Update Token Server URL if provided
if not "!TOKEN_SERVER_URL!"=="" (
    powershell -Command "(Get-Content '%CONFIG_FILE%') -replace 'https://your-token-server.com/token', '!TOKEN_SERVER_URL!' | Set-Content '%TEMP_FILE%'"
    move /y "%TEMP_FILE%" "%CONFIG_FILE%" >nul
)

REM Update Default Channel Name
powershell -Command "(Get-Content '%CONFIG_FILE%') -replace 'test_channel', '!DEFAULT_CHANNEL!' | Set-Content '%TEMP_FILE%'"
move /y "%TEMP_FILE%" "%CONFIG_FILE%" >nul

REM Update API Server URL if provided
if not "!API_SERVER_URL!"=="" (
    powershell -Command "(Get-Content '%CONFIG_FILE%') -replace 'https://your-api-server.com', '!API_SERVER_URL!' | Set-Content '%TEMP_FILE%'"
    move /y "%TEMP_FILE%" "%CONFIG_FILE%" >nul
)

REM Update API Key if provided
if not "!API_KEY!"=="" (
    powershell -Command "(Get-Content '%CONFIG_FILE%') -replace 'YOUR_API_KEY_HERE', '!API_KEY!' | Set-Content '%TEMP_FILE%'"
    move /y "%TEMP_FILE%" "%CONFIG_FILE%" >nul
)

REM Update API Secret if provided
if not "!API_SECRET!"=="" (
    powershell -Command "(Get-Content '%CONFIG_FILE%') -replace 'YOUR_API_SECRET_HERE', '!API_SECRET!' | Set-Content '%TEMP_FILE%'"
    move /y "%TEMP_FILE%" "%CONFIG_FILE%" >nul
)

echo [SUCCESS] Configuration file updated
echo.

REM Clean up old configuration from strings.xml
echo [INFO] Cleaning up old configuration from strings.xml...

if exist "%STRINGS_FILE%" (
    REM Remove old agora_app_id from strings.xml
    powershell -Command "(Get-Content '%STRINGS_FILE%') | Where-Object { $_ -notmatch 'agora_app_id.*translatable=\"false\"' } | Set-Content '%TEMP_FILE%'"
    move /y "%TEMP_FILE%" "%STRINGS_FILE%" >nul
    
    echo [SUCCESS] Old configuration cleaned up from strings.xml
    echo.
)

REM Build and install the project
echo [INFO] Building and installing the project...

cd /d "%PROJECT_ROOT%"

REM Clean build
echo [INFO] Cleaning build...
if exist "gradlew.bat" (
    call gradlew.bat clean
) else (
    gradle clean
)

REM Build project
echo [INFO] Building project...
if exist "gradlew.bat" (
    call gradlew.bat assembleDebug
) else (
    gradle assembleDebug
)

echo [SUCCESS] Project built successfully
echo.

REM Check if device is connected
adb devices | findstr "device$" >nul
if %errorlevel% equ 0 (
    echo [INFO] Installing APK to device...
    if exist "gradlew.bat" (
        call gradlew.bat installDebug
    ) else (
        gradle installDebug
    )
    echo [SUCCESS] APK installed to device
) else (
    echo [WARNING] No device connected. APK built but not installed
)

echo.

REM Show configuration summary
echo [SUCCESS] Configuration completed successfully!
echo.
echo Configuration Summary:
echo ======================
echo Agora App ID: !AGORA_APP_ID!
echo Agora App Certificate: !AGORA_APP_CERTIFICATE:~0,8!...
if not "!AGORA_TOKEN!"=="" (
    echo Agora Token: !AGORA_TOKEN:~0,8!...
)
if not "!TOKEN_SERVER_URL!"=="" (
    echo Token Server URL: !TOKEN_SERVER_URL!
)
echo Default Channel: !DEFAULT_CHANNEL!
if not "!API_SERVER_URL!"=="" (
    echo API Server URL: !API_SERVER_URL!
)
echo.
echo Next steps:
echo 1. The configuration has been saved to: %CONFIG_FILE%
echo 2. This file is now ignored by git for security
echo 3. You can run the app and it will use your configuration
echo 4. To update configuration, run this script again
echo.

pause
