package io.agora.api.example.examples.advanced.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Locale;

/**
 * Language Manager
 * Responsible for multi-language switching and localization functionality
 */
public class LanguageManager {
    private static final String TAG = "LanguageManager";
    
    // Language constants
    public static final String LANGUAGE_ZH = "zh";
    public static final String LANGUAGE_EN = "en";
    public static final String LANGUAGE_JA = "ja";
    public static final String LANGUAGE_KO = "ko";
    
    private static final String PREF_LANGUAGE = "pref_language";
    private final Context context;
    private String currentLanguage;
    
    public LanguageManager(@NonNull Context context) {
        this.context = context;
        this.currentLanguage = loadLanguagePreference();
    }
    
    /**
     * Get current language
     */
    public String getCurrentLanguage() {
        return currentLanguage;
    }
    
    /**
     * Set language
     */
    public void setLanguage(String languageCode) {
        if (languageCode == null || languageCode.equals(currentLanguage)) {
            return;
        }
        
        try {
            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);
            
            Resources resources = context.getResources();
            Configuration configuration = new Configuration(resources.getConfiguration());
            configuration.setLocale(locale);
            
            // Update resource configuration
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            
            // Update Application configuration
            context.getApplicationContext().getResources().updateConfiguration(configuration, 
                context.getApplicationContext().getResources().getDisplayMetrics());
            
            currentLanguage = languageCode;
            saveLanguagePreference(languageCode);
            
            // Language changed
            
            Log.d(TAG, "Language applied: " + languageCode);
            
        } catch (Exception e) {
            Log.e(TAG, "Error applying language: " + e.getMessage());
        }
    }
    
    /**
     * Save language preference
     */
    private void saveLanguagePreference(String language) {
        android.content.SharedPreferences prefs = context.getSharedPreferences("ECommPrefs", Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_LANGUAGE, language).apply();
        Log.d(TAG, "Language saved: " + language);
    }
    
    /**
     * Load language preference
     */
    private String loadLanguagePreference() {
        android.content.SharedPreferences prefs = context.getSharedPreferences("ECommPrefs", Context.MODE_PRIVATE);
        String language = prefs.getString(PREF_LANGUAGE, LANGUAGE_ZH);
        Log.d(TAG, "Language loaded: " + language);
        return language;
    }
    
    /**
     * Get supported languages list
     */
    public static String[] getSupportedLanguages() {
        return new String[]{LANGUAGE_ZH, LANGUAGE_EN, LANGUAGE_JA, LANGUAGE_KO};
    }
    
    /**
     * Get language display name
     */
    public static String getLanguageDisplayName(String languageCode) {
        switch (languageCode) {
            case LANGUAGE_ZH:
                return "中文";
            case LANGUAGE_EN:
                return "English";
            case LANGUAGE_JA:
                return "日本語";
            case LANGUAGE_KO:
                return "한국어";
            default:
                return languageCode;
        }
    }
    
    /**
     * Check if it's a valid language code
     */
    public static boolean isValidLanguageCode(String languageCode) {
        return LANGUAGE_ZH.equals(languageCode) || 
               LANGUAGE_EN.equals(languageCode) || 
               LANGUAGE_JA.equals(languageCode) || 
               LANGUAGE_KO.equals(languageCode);
    }
    
    /**
     * Update context
     */
    public void updateContext(Context newContext) {
        // Context is final, cannot reassign
    }
    
    /**
     * Release resources
     */
    public void release() {
        // Context is final, no need to release
    }
    
    /**
     * Load and apply language settings
     */
    public void loadAndApplyLanguage() {
        currentLanguage = loadLanguagePreference();
        applyLanguage(currentLanguage);
    }
    
    /**
     * Change language
     */
    public void changeLanguage(String language, Fragment fragment) {
        if (language == null || language.equals(currentLanguage)) {
            return;
        }
        
        try {
            // Save language settings
            currentLanguage = language;
            saveLanguagePreference(language);
            
            // Recreate Fragment to apply language changes
            recreateFragment(fragment);
            
        } catch (Exception e) {
            Log.e(TAG, "Error changing language: " + e.getMessage());
        }
    }
    
    /**
     * Apply language settings
     */
    private void applyLanguage(String languageCode) {
        try {
            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);
            
            Resources resources = context.getResources();
            Configuration configuration = new Configuration(resources.getConfiguration());
            configuration.setLocale(locale);
            
            // Update resource configuration
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            
            // Update Activity configuration
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).getResources().updateConfiguration(configuration, 
                    ((android.app.Activity) context).getResources().getDisplayMetrics());
            }
            
            // Update Application configuration
            context.getApplicationContext().getResources().updateConfiguration(configuration, 
                context.getApplicationContext().getResources().getDisplayMetrics());
                
            Log.d(TAG, "Language applied: " + languageCode);
        } catch (Exception e) {
            Log.e(TAG, "Error applying language: " + e.getMessage());
        }
    }
    
    /**
     * Recreate Fragment
     */
    private void recreateFragment(Fragment fragment) {
        try {
            // Get FragmentManager
            androidx.fragment.app.FragmentManager fragmentManager = fragment.getParentFragmentManager();
            
            // Create new Fragment instance
            Fragment newFragment = fragment.getClass().newInstance();
            
            // Start Fragment transaction
            androidx.fragment.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
            
            // Remove current Fragment
            transaction.remove(fragment);
            
            // Add new Fragment
            transaction.add(android.R.id.content, newFragment);
            
            // Commit transaction
            transaction.commit();
            
            Log.d(TAG, "Fragment recreated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to recreate fragment: " + e.getMessage());
        }
    }
} 