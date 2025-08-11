package io.agora.api.example.examples.advanced.utils;

import android.content.Context;
import android.util.Log;

/**
 * Configuration Usage Example
 * This class demonstrates how to use ConfigManager in your code
 */
public class ConfigUsageExample {
    private static final String TAG = "ConfigUsageExample";
    
    private final ConfigManager configManager;
    
    public ConfigUsageExample(Context context) {
        this.configManager = new ConfigManager(context);
    }
    
    /**
     * Example: Initialize Agora RTC Engine with configuration
     */
    public void initializeAgoraEngine() {
        // Check if configuration is complete
        if (!configManager.isConfigurationComplete()) {
            Log.e(TAG, "Configuration incomplete. Please run the configuration script first.");
            Log.d(TAG, configManager.getConfigurationSummary());
            return;
        }
        
        // Get Agora App ID
        String appId = configManager.getAgoraAppId();
        Log.d(TAG, "Using Agora App ID: " + appId);
        
        // Get Agora App Certificate
        String certificate = configManager.getAgoraAppCertificate();
        Log.d(TAG, "Using Agora Certificate: " + certificate.substring(0, 8) + "...");
        
        // Get default channel name
        String defaultChannel = configManager.getDefaultChannelName();
        Log.d(TAG, "Default channel: " + defaultChannel);
        
        // Initialize your RTC engine here...
        // RtcEngineConfig config = new RtcEngineConfig();
        // config.mAppId = appId;
        // ...
    }
    
    /**
     * Example: Check feature availability
     */
    public void checkFeatureAvailability() {
        Log.d(TAG, "Feature Availability:");
        Log.d(TAG, "- Beauty: " + configManager.getBooleanConfig(ConfigManager.KEY_ENABLE_BEAUTY));
        Log.d(TAG, "- Filter: " + configManager.getBooleanConfig(ConfigManager.KEY_ENABLE_FILTER));
        Log.d(TAG, "- Virtual Background: " + configManager.getBooleanConfig(ConfigManager.KEY_ENABLE_VIRTUAL_BACKGROUND));
        Log.d(TAG, "- Audio Processing: " + configManager.getBooleanConfig(ConfigManager.KEY_ENABLE_AUDIO_PROCESSING));
    }
    
    /**
     * Example: Initialize features based on configuration
     */
    public void initializeFeatures() {
        // Initialize beauty features if enabled
        if (configManager.getBooleanConfig(ConfigManager.KEY_ENABLE_BEAUTY)) {
            Log.d(TAG, "Initializing beauty features...");
            // initializeBeautyFeatures();
        }
        
        // Initialize filter features if enabled
        if (configManager.getBooleanConfig(ConfigManager.KEY_ENABLE_FILTER)) {
            Log.d(TAG, "Initializing filter features...");
            // initializeFilterFeatures();
        }
        
        // Initialize virtual background if enabled
        if (configManager.getBooleanConfig(ConfigManager.KEY_ENABLE_VIRTUAL_BACKGROUND)) {
            Log.d(TAG, "Initializing virtual background...");
            // initializeVirtualBackground();
        }
        
        // Initialize audio processing if enabled
        if (configManager.getBooleanConfig(ConfigManager.KEY_ENABLE_AUDIO_PROCESSING)) {
            Log.d(TAG, "Initializing audio processing...");
            // initializeAudioProcessing();
        }
    }
    
    /**
     * Example: Get API configuration
     */
    public void getApiConfiguration() {
        String apiServerUrl = configManager.getConfig(ConfigManager.KEY_API_SERVER_URL);
        String apiKey = configManager.getConfig(ConfigManager.KEY_API_KEY);
        String apiSecret = configManager.getConfig(ConfigManager.KEY_API_SECRET);
        
        if (!apiServerUrl.isEmpty() && !apiKey.isEmpty() && !apiSecret.isEmpty()) {
            Log.d(TAG, "API configuration found:");
            Log.d(TAG, "- Server: " + apiServerUrl);
            Log.d(TAG, "- Key: " + apiKey.substring(0, 8) + "...");
            Log.d(TAG, "- Secret: " + apiSecret.substring(0, 8) + "...");
        } else {
            Log.d(TAG, "API configuration not complete");
        }
    }
    
    /**
     * Example: Get debug configuration
     */
    public void getDebugConfiguration() {
        boolean debugMode = configManager.getBooleanConfig(ConfigManager.KEY_ENABLE_DEBUG_MODE);
        boolean logging = configManager.getBooleanConfig(ConfigManager.KEY_ENABLE_LOGGING);
        String logLevel = configManager.getConfig(ConfigManager.KEY_LOG_LEVEL);
        String logPath = configManager.getConfig(ConfigManager.KEY_LOG_FILE_PATH);
        
        Log.d(TAG, "Debug Configuration:");
        Log.d(TAG, "- Debug Mode: " + debugMode);
        Log.d(TAG, "- Logging: " + logging);
        Log.d(TAG, "- Log Level: " + logLevel);
        Log.d(TAG, "- Log Path: " + logPath);
    }
    
    /**
     * Example: Show complete configuration summary
     */
    public void showConfigurationSummary() {
        String summary = configManager.getConfigurationSummary();
        Log.d(TAG, "Configuration Summary:\n" + summary);
    }
    
    /**
     * Example: Update configuration at runtime
     */
    public void updateConfiguration() {
        // Update a configuration value
        configManager.setConfig("custom_setting", "new_value");
        
        // Update a boolean configuration
        configManager.setBooleanConfig("custom_flag", true);
        
        Log.d(TAG, "Configuration updated");
    }
    
    /**
     * Example: Clear configuration (for testing)
     */
    public void clearConfiguration() {
        configManager.clearConfiguration();
        Log.d(TAG, "Configuration cleared");
    }
}
