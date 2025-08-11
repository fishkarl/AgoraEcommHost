package io.agora.api.example.examples.advanced.features;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.video.FilterEffectOptions;

/**
 * Filter Manager
 * Responsible for handling all filter-related functionality
 */
public class FilterManager {
    private static final String TAG = "FilterManager";
    
    private final Context context;
    private final RtcEngine engine;
    private String selectedFilterPath = null;
    private FilterEffectOptions filterEffectOptions = new FilterEffectOptions();
    
    public FilterManager(@NonNull Context context, @NonNull RtcEngine engine) {
        this.context = context;
        this.engine = engine;
    }
    
    /**
     * Get file path from URI
     */
    public String getPathFromUri(Uri uri) {
        try {
            String[] projection = {android.provider.MediaStore.MediaColumns.DATA};
            android.database.Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.MediaColumns.DATA);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();
                        return filePath;
                    }
                } finally {
                    cursor.close();
                }
            }
            
            return copyFileToAppDirectory(uri);
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting path from URI: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Copy file to app directory
     */
    private String copyFileToAppDirectory(Uri uri) {
        try {
            File appDir = new File(context.getExternalFilesDir(null), "filters");
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
            
            String fileName = "filter_" + System.currentTimeMillis() + ".cube";
            File destFile = new File(appDir, fileName);
            
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(destFile);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            
            inputStream.close();
            outputStream.close();
            
            Log.d(TAG, "File copied to: " + destFile.getAbsolutePath());
            return destFile.getAbsolutePath();
            
        } catch (Exception e) {
            Log.e(TAG, "Error copying file: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Copy resource file to storage
     */
    public String copyAssetCubeToStorage(String assetFileName) {
        try {
            File appDir = new File(context.getExternalFilesDir(null), "filters");
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
            
            File destFile = new File(appDir, assetFileName);
            
            if (destFile.exists()) {
                Log.d(TAG, "Local cube file already exists: " + destFile.getAbsolutePath());
                return destFile.getAbsolutePath();
            }
            
            InputStream inputStream = context.getAssets().open("lut/" + assetFileName);
            FileOutputStream outputStream = new FileOutputStream(destFile);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            
            inputStream.close();
            outputStream.close();
            
            Log.d(TAG, "Local cube file copied to: " + destFile.getAbsolutePath());
            return destFile.getAbsolutePath();
            
        } catch (Exception e) {
            Log.e(TAG, "Error copying asset cube file: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Apply filter
     */
    public boolean applyFilter(String filterPath, float strength) {
        if (engine == null) {
            Log.e(TAG, "Engine is null, cannot apply filter");
            return false;
        }
        
        selectedFilterPath = filterPath;
        filterEffectOptions.path = filterPath;
        filterEffectOptions.strength = strength;
        
        int ret = engine.setFilterEffectOptions(true, filterEffectOptions);
        if (ret == 0) {
            Log.d(TAG, "Filter applied successfully: " + filterPath + " (strength: " + strength + ")");
            return true;
        } else {
            Log.e(TAG, "Failed to apply filter, error code: " + ret);
            return false;
        }
    }
    
    /**
     * Get filter path
     */
    public String getFilterPath() {
        String[] possiblePaths = {
            "built_in_whiten_filter",
            "built_in_smooth_filter",
            Environment.getExternalStorageDirectory() + "/Android/data/io.agora.api.example.ecomm/files/grayscale.cube",
            Environment.getExternalStorageDirectory() + "/Android/data/io.agora.api.example.ecomm/files/filter.cube"
        };

        for (String path : possiblePaths) {
            if (path.startsWith("built_in_")) {
                Log.d(TAG, "Using built-in filter: " + path);
                return path;
            } else {
                File file = new File(path);
                if (file.exists()) {
                    Log.d(TAG, "Found filter file: " + path);
                    return path;
                }
            }
        }

        Log.d(TAG, "No custom filter found, using built-in filter");
        return "built_in_whiten_filter";
    }
    
    /**
     * Update filter strength
     */
    public void updateFilterStrength(float strength) {
        filterEffectOptions.strength = strength;
        filterEffectOptions.path = getFilterPath();
        
        if (engine != null) {
            int ret = engine.setFilterEffectOptions(true, filterEffectOptions);
            Log.d(TAG, "Filter strength updated: " + strength + ", ret=" + ret);

            if (ret != 0) {
                Log.e(TAG, "Failed to set filter effect, error code: " + ret);
                filterEffectOptions.path = "built_in_whiten_filter";
                ret = engine.setFilterEffectOptions(true, filterEffectOptions);
                Log.d(TAG, "Fallback to built-in filter, ret=" + ret);
            }
        }
    }
    
    /**
     * Disable filter
     */
    public void disableFilter() {
        if (engine != null) {
            engine.setFilterEffectOptions(false, filterEffectOptions);
            selectedFilterPath = null;
            Log.d(TAG, "Filter disabled");
        }
    }
    
    /**
     * Get the currently selected filter path
     */
    public String getSelectedFilterPath() {
        return selectedFilterPath;
    }
    
    /**
     * Set filter path
     */
    public void setSelectedFilterPath(String path) {
        this.selectedFilterPath = path;
    }
    
    /**
     * Get filter effect options
     */
    public FilterEffectOptions getFilterEffectOptions() {
        return filterEffectOptions;
    }
}
