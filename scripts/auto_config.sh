#!/bin/bash

# Agora EComm Host Auto Configuration Script
# This script automatically configures the project with your Agora credentials

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration file paths
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CONFIG_TEMPLATE="$PROJECT_ROOT/ecomm/src/main/res/values/config_template.xml"
CONFIG_FILE="$PROJECT_ROOT/ecomm/src/main/res/values/config.xml"
STRINGS_FILE="$PROJECT_ROOT/ecomm/src/main/res/values/strings.xml"

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if required tools are installed
check_requirements() {
    print_status "Checking requirements..."
    
    if ! command -v adb &> /dev/null; then
        print_error "ADB is not installed or not in PATH"
        print_error "Please install Android SDK Platform Tools"
        exit 1
    fi
    
    if ! command -v gradle &> /dev/null; then
        print_warning "Gradle is not installed or not in PATH"
        print_warning "You may need to use ./gradlew instead"
    fi
    
    print_success "Requirements check passed"
}

# Function to backup existing configuration
backup_config() {
    if [ -f "$CONFIG_FILE" ]; then
        print_status "Backing up existing configuration..."
        cp "$CONFIG_FILE" "${CONFIG_FILE}.backup.$(date +%Y%m%d_%H%M%S)"
        print_success "Configuration backed up"
    fi
}

# Function to create configuration from template
create_config() {
    print_status "Creating configuration from template..."
    
    if [ ! -f "$CONFIG_TEMPLATE" ]; then
        print_error "Configuration template not found: $CONFIG_TEMPLATE"
        exit 1
    fi
    
    cp "$CONFIG_TEMPLATE" "$CONFIG_FILE"
    print_success "Configuration file created from template"
}

# Function to prompt for configuration values
prompt_config() {
    print_status "Please enter your Agora configuration values:"
    echo
    
    # Agora App ID
    read -p "Enter your Agora App ID: " AGORA_APP_ID
    if [ -z "$AGORA_APP_ID" ]; then
        print_error "Agora App ID is required"
        exit 1
    fi
    
    # Agora App Certificate
    read -p "Enter your Agora App Certificate: " AGORA_APP_CERTIFICATE
    if [ -z "$AGORA_APP_CERTIFICATE" ]; then
        print_error "Agora App Certificate is required"
        exit 1
    fi
    
    # Agora Token (optional)
    read -p "Enter your Agora Token (optional, press Enter to skip): " AGORA_TOKEN
    
    # Token Server URL
    read -p "Enter your Token Server URL (optional, press Enter to skip): " TOKEN_SERVER_URL
    
    # Default Channel Name
    read -p "Enter default channel name [test_channel]: " DEFAULT_CHANNEL
    DEFAULT_CHANNEL=${DEFAULT_CHANNEL:-test_channel}
    
    # API Server URL (optional)
    read -p "Enter your API Server URL (optional, press Enter to skip): " API_SERVER_URL
    
    # API Key (optional)
    read -p "Enter your API Key (optional, press Enter to skip): " API_KEY
    
    # API Secret (optional)
    read -p "Enter your API Secret (optional, press Enter to skip): " API_SECRET
}

# Function to update configuration file
update_config() {
    print_status "Updating configuration file..."
    
    # Update Agora App ID
    sed -i.bak "s/YOUR_AGORA_APP_ID_HERE/$AGORA_APP_ID/g" "$CONFIG_FILE"
    
    # Update Agora App Certificate
    sed -i.bak "s/YOUR_AGORA_APP_CERTIFICATE_HERE/$AGORA_APP_CERTIFICATE/g" "$CONFIG_FILE"
    
    # Update Agora Token if provided
    if [ ! -z "$AGORA_TOKEN" ]; then
        sed -i.bak "s/YOUR_AGORA_TOKEN_HERE/$AGORA_TOKEN/g" "$CONFIG_FILE"
    fi
    
    # Update Token Server URL if provided
    if [ ! -z "$TOKEN_SERVER_URL" ]; then
        sed -i.bak "s|https://your-token-server.com/token|$TOKEN_SERVER_URL|g" "$CONFIG_FILE"
    fi
    
    # Update Default Channel Name
    sed -i.bak "s/test_channel/$DEFAULT_CHANNEL/g" "$CONFIG_FILE"
    
    # Update API Server URL if provided
    if [ ! -z "$API_SERVER_URL" ]; then
        sed -i.bak "s|https://your-api-server.com|$API_SERVER_URL|g" "$CONFIG_FILE"
    fi
    
    # Update API Key if provided
    if [ ! -z "$API_KEY" ]; then
        sed -i.bak "s/YOUR_API_KEY_HERE/$API_KEY/g" "$CONFIG_FILE"
    fi
    
    # Update API Secret if provided
    if [ ! -z "$API_SECRET" ]; then
        sed -i.bak "s/YOUR_API_SECRET_HERE/$API_SECRET/g" "$CONFIG_FILE"
    fi
    
    # Remove backup files
    rm -f "$CONFIG_FILE.bak"
    
    print_success "Configuration file updated"
}

# Function to remove old configuration from strings.xml
cleanup_strings() {
    print_status "Cleaning up old configuration from strings.xml..."
    
    if [ -f "$STRINGS_FILE" ]; then
        # Remove old agora_app_id from strings.xml
        sed -i.bak '/agora_app_id.*translatable="false"/d' "$STRINGS_FILE"
        
        # Remove backup file
        rm -f "$STRINGS_FILE.bak"
        
        print_success "Old configuration cleaned up from strings.xml"
    fi
}

# Function to build and install the project
build_and_install() {
    print_status "Building and installing the project..."
    
    cd "$PROJECT_ROOT"
    
    # Clean build
    print_status "Cleaning build..."
    if command -v gradle &> /dev/null; then
        gradle clean
    else
        ./gradlew clean
    fi
    
    # Build project
    print_status "Building project..."
    if command -v gradle &> /dev/null; then
        gradle assembleDebug
    else
        ./gradlew assembleDebug
    fi
    
    print_success "Project built successfully"
    
    # Check if device is connected
    if adb devices | grep -q "device$"; then
        print_status "Installing APK to device..."
        if command -v gradle &> /dev/null; then
            gradle installDebug
        else
            ./gradlew installDebug
        fi
        print_success "APK installed to device"
    else
        print_warning "No device connected. APK built but not installed"
    fi
}

# Function to show configuration summary
show_summary() {
    print_success "Configuration completed successfully!"
    echo
    echo "Configuration Summary:"
    echo "======================"
    echo "Agora App ID: $AGORA_APP_ID"
    echo "Agora App Certificate: ${AGORA_APP_CERTIFICATE:0:8}..."
    if [ ! -z "$AGORA_TOKEN" ]; then
        echo "Agora Token: ${AGORA_TOKEN:0:8}..."
    fi
    if [ ! -z "$TOKEN_SERVER_URL" ]; then
        echo "Token Server URL: $TOKEN_SERVER_URL"
    fi
    echo "Default Channel: $DEFAULT_CHANNEL"
    if [ ! -z "$API_SERVER_URL" ]; then
        echo "API Server URL: $API_SERVER_URL"
    fi
    echo
    echo "Next steps:"
    echo "1. The configuration has been saved to: $CONFIG_FILE"
    echo "2. This file is now ignored by git for security"
    echo "3. You can run the app and it will use your configuration"
    echo "4. To update configuration, run this script again"
}

# Main execution
main() {
    echo "==========================================="
    echo "Agora EComm Host Auto Configuration Script"
    echo "==========================================="
    echo
    
    # Check requirements
    check_requirements
    
    # Backup existing config
    backup_config
    
    # Create config from template
    create_config
    
    # Prompt for configuration values
    prompt_config
    
    # Update configuration file
    update_config
    
    # Clean up old configuration
    cleanup_strings
    
    # Build and install
    build_and_install
    
    # Show summary
    show_summary
}

# Run main function
main "$@"
