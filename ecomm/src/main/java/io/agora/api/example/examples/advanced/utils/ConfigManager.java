package io.agora.api.example.examples.advanced.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

/**
 * Configuration Manager
 * Manages sensitive configuration information and provides secure access to it
 */
public class ConfigManager {
    private static final String TAG = "ConfigManager";
    private static final String PREFS_NAME = "AgoraConfigPrefs";
    
    // Configuration keys
    public static final String KEY_AGORA_APP_ID = "agora_app_id";
    public static final String KEY_AGORA_APP_CERTIFICATE = "agora_app_certificate";
    public static final String KEY_AGORA_TOKEN = "agora_token";
    public static final String KEY_AGORA_TOKEN_SERVER_URL = "agora_token_server_url";
    public static final String KEY_DEFAULT_CHANNEL_NAME = "default_channel_name";
    public static final String KEY_DEFAULT_USER_ID = "default_user_id";
    public static final String KEY_DEFAULT_VIDEO_PROFILE = "default_video_profile";
    public static final String KEY_DEFAULT_AUDIO_PROFILE = "default_audio_profile";
    public static final String KEY_LOG_LEVEL = "log_level";
    public static final String KEY_ENABLE_BEAUTY = "enable_beauty";
    public static final String KEY_ENABLE_FILTER = "enable_filter";
    public static final String KEY_ENABLE_VIRTUAL_BACKGROUND = "enable_virtual_background";
    public static final String KEY_ENABLE_AUDIO_PROCESSING = "enable_audio_processing";
    public static final String KEY_API_SERVER_URL = "api_server_url";
    public static final String KEY_API_KEY = "api_key";
    public static final String KEY_API_SECRET = "api_secret";
    public static final String KEY_ANALYTICS_KEY = "analytics_key";
    public static final String KEY_ENABLE_ANALYTICS = "enable_analytics";
    public static final String KEY_ENABLE_DEBUG_MODE = "enable_debug_mode";
    public static final String KEY_ENABLE_LOGGING = "enable_logging";
    public static final String KEY_LOG_FILE_PATH = "log_file_path";
    
    private final Context context;
    private final SharedPreferences prefs;
    
    public ConfigManager(@NonNull Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        initializeConfig();
    }
    
    /**
     * Initialize configuration from resources and preferences
     */
    private void initializeConfig() {
        try {
            // Load default values from config.xml
            loadDefaultConfig();
            
            // Check if we need to migrate from old config
            migrateOldConfig();
            
            Log.d(TAG, "Configuration initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing configuration: " + e.getMessage());
        }
    }
    
    /**
     * Load default configuration from config.xml
     */
    private void loadDefaultConfig() {
        // Load string values
        setConfigIfEmpty(KEY_AGORA_APP_ID, getStringResource("agora_app_id"));
        setConfigIfEmpty(KEY_AGORA_APP_CERTIFICATE, getStringResource("agora_app_certificate"));
        setConfigIfEmpty(KEY_AGORA_TOKEN, getStringResource("agora_token"));
        setConfigIfEmpty(KEY_AGORA_TOKEN_SERVER_URL, getStringResource("agora_token_server_url"));
        setConfigIfEmpty(KEY_DEFAULT_CHANNEL_NAME, getStringResource("default_channel_name"));
        setConfigIfEmpty(KEY_DEFAULT_USER_ID, getStringResource("default_user_id"));
        setConfigIfEmpty(KEY_DEFAULT_VIDEO_PROFILE, getStringResource("default_video_profile"));
        setConfigIfEmpty(KEY_DEFAULT_AUDIO_PROFILE, getStringResource("default_audio_profile"));
        setConfigIfEmpty(KEY_LOG_LEVEL, getStringResource("log_level"));
        setConfigIfEmpty(KEY_API_SERVER_URL, getStringResource("api_server_url"));
        setConfigIfEmpty(KEY_API_KEY, getStringResource("api_key"));
        setConfigIfEmpty(KEY_API_SECRET, getStringResource("api_secret"));
        setConfigIfEmpty(KEY_ANALYTICS_KEY, getStringResource("analytics_key"));
        setConfigIfEmpty(KEY_LOG_FILE_PATH, getStringResource("log_file_path"));
        
        // Load boolean values
        setBooleanConfigIfEmpty(KEY_ENABLE_BEAUTY, getBooleanResource("enable_beauty"));
        setBooleanConfigIfEmpty(KEY_ENABLE_FILTER, getBooleanResource("enable_filter"));
        setBooleanConfigIfEmpty(KEY_ENABLE_VIRTUAL_BACKGROUND, getBooleanResource("enable_virtual_background"));
        setBooleanConfigIfEmpty(KEY_ENABLE_AUDIO_PROCESSING, getBooleanResource("enable_audio_processing"));
        setBooleanConfigIfEmpty(KEY_ENABLE_ANALYTICS, getBooleanResource("enable_analytics"));
        setBooleanConfigIfEmpty(KEY_ENABLE_DEBUG_MODE, getBooleanResource("enable_debug_mode"));
        setBooleanConfigIfEmpty(KEY_ENABLE_LOGGING, getBooleanResource("enable_logging"));
    }
    
    /**
     * Migrate configuration from old strings.xml if needed
     */
    private void migrateOldConfig() {
        try {
            // Check if we have old config in strings.xml
            String oldAppId = getStringResource("agora_app_id");
            if (oldAppId != null && !oldAppId.isEmpty() && !oldAppId.equals("YOUR_AGORA_APP_ID_HERE")) {
                // Migrate old config
                setConfig(KEY_AGORA_APP_ID, oldAppId);
                Log.d(TAG, "Migrated old Agora App ID configuration");
            }
        } catch (Exception e) {
            Log.w(TAG, "No old configuration to migrate");
        }
    }
    
    /**
     * Set configuration value if it's empty
     */
    private void setConfigIfEmpty(String key, String value) {
        if (value != null && !value.isEmpty() && !getConfig(key).isEmpty()) {
            setConfig(key, value);
        }
    }
    
    /**
     * Set boolean configuration value if it's empty
     */
    private void setBooleanConfigIfEmpty(String key, boolean value) {
        if (!prefs.contains(key)) {
            setBooleanConfig(key, value);
        }
    }
    
    /**
     * Get string resource safely
     */
    private String getStringResource(String name) {
        try {
            int resourceId = context.getResources().getIdentifier(name, "string", context.getPackageName());
            if (resourceId != 0) {
                return context.getString(resourceId);
            }
        } catch (Exception e) {
            Log.w(TAG, "Resource not found: " + name);
        }
        return "";
    }
    
    /**
     * Get boolean resource safely
     */
    private boolean getBooleanResource(String name) {
        try {
            int resourceId = context.getResources().getIdentifier(name, "bool", context.getPackageName());
            if (resourceId != 0) {
                return context.getResources().getBoolean(resourceId);
            }
        } catch (Exception e) {
            Log.w(TAG, "Boolean resource not found: " + name);
        }
        return false;
    }
    
    /**
     * Set configuration value
     */
    public void setConfig(String key, String value) {
        prefs.edit().putString(key, value).apply();
        Log.d(TAG, "Configuration updated: " + key + " = " + (key.contains("key") || key.contains("secret") || key.contains("token") || key.contains("certificate") ? "***" : value));
    }
    
    /**
     * Set boolean configuration value
     */
    public void setBooleanConfig(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
        Log.d(TAG, "Boolean configuration updated: " + key + " = " + value);
    }
    
    /**
     * Get configuration value
     */
    public String getConfig(String key) {
        return prefs.getString(key, "");
    }
    
    /**
     * Get boolean configuration value
     */
    public boolean getBooleanConfig(String key) {
        return prefs.getBoolean(key, false);
    }
    
    /**
     * Get Agora App ID
     */
    public String getAgoraAppId() {
        String appId = getConfig(KEY_AGORA_APP_ID);
        if (appId.isEmpty() || appId.equals("YOUR_AGORA_APP_ID_HERE")) {
            Log.w(TAG, "Agora App ID not configured!");
            return "";
        }
        return appId;
    }
    
    /**
     * Get Agora App Certificate
     */
    public String getAgoraAppCertificate() {
        String certificate = getConfig(KEY_AGORA_APP_CERTIFICATE);
        if (certificate.isEmpty() || certificate.equals("YOUR_AGORA_APP_CERTIFICATE_HERE")) {
            Log.w(TAG, "Agora App Certificate not configured!");
            return "";
        }
        return certificate;
    }
    
    /**
     * Get Agora Token
     */
    public String getAgoraToken() {
        String token = getConfig(KEY_AGORA_TOKEN);
        if (token.isEmpty() || token.equals("YOUR_AGORA_TOKEN_HERE")) {
            Log.w(TAG, "Agora Token not configured!");
            return "";
        }
        return token;
    }
    
    /**
     * Get Token Server URL
     */
    public String getTokenServerUrl() {
        return getConfig(KEY_AGORA_TOKEN_SERVER_URL);
    }
    
    /**
     * Get Default Channel Name
     */
    public String getDefaultChannelName() {
        return getConfig(KEY_DEFAULT_CHANNEL_NAME);
    }
    
    /**
     * Get Default User ID
     */
    public String getDefaultUserId() {
        return getConfig(KEY_DEFAULT_USER_ID);
    }
    
    /**
     * Check if configuration is complete
     */
    public boolean isConfigurationComplete() {
        String appId = getAgoraAppId();
        String certificate = getAgoraAppCertificate();
        
        return !appId.isEmpty() && !certificate.isEmpty();
    }
    
    /**
     * Get configuration summary (without sensitive data)
     */
    public String getConfigurationSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Configuration Status:\n");
        summary.append("- Agora App ID: ").append(getAgoraAppId().isEmpty() ? "NOT SET" : "SET").append("\n");
        summary.append("- Agora Certificate: ").append(getAgoraAppCertificate().isEmpty() ? "NOT SET" : "SET").append("\n");
        summary.append("- Token: ").append(getAgoraToken().isEmpty() ? "NOT SET" : "SET").append("\n");
        summary.append("- Default Channel: ").append(getDefaultChannelName()).append("\n");
        summary.append("- Features Enabled:\n");
        summary.append("  * Beauty: ").append(getBooleanConfig(KEY_ENABLE_BEAUTY)).append("\n");
        summary.append("  * Filter: ").append(getBooleanConfig(KEY_ENABLE_FILTER)).append("\n");
        summary.append("  * Virtual Background: ").append(getBooleanConfig(KEY_ENABLE_VIRTUAL_BACKGROUND)).append("\n");
        summary.append("  * Audio Processing: ").append(getBooleanConfig(KEY_ENABLE_AUDIO_PROCESSING)).append("\n");
        
        return summary.toString();
    }
    
    /**
     * Clear all configuration (for testing)
     */
    public void clearConfiguration() {
        prefs.edit().clear().apply();
        Log.d(TAG, "Configuration cleared");
    }
}
