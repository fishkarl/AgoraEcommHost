package io.agora.api.example.examples.advanced.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Permission Management Utility Class
 * Responsible for permission checking and requesting
 */
public class PermissionHelper {
    private static final String TAG = "PermissionHelper";
    
    private final Context context;
    private Fragment fragment;
    private ActivityResultLauncher<String[]> permissionLauncher;
    private PermissionResultCallback callback;
    
    public interface PermissionResultCallback {
        void onPermissionsResult(boolean allPermissionsGranted, String[] permissions, int[] grantResults);
    }
    
    public PermissionHelper(Context context) {
        this.context = context;
    }
    
    /**
     * Initialize permission launcher
     */
    private void initializePermissionLauncher() {
        permissionLauncher = fragment.registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean allGranted = true;
                List<String> permissionList = new ArrayList<>(permissions.keySet());
                String[] permissionArray = permissionList.toArray(new String[0]);
                int[] grantResults = new int[permissionArray.length];
                
                for (int i = 0; i < permissionArray.length; i++) {
                    String permission = permissionArray[i];
                    boolean granted = permissions.get(permission) != null && permissions.get(permission);
                    grantResults[i] = granted ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
                    if (!granted) {
                        allGranted = false;
                    }
                }
                
                if (callback != null) {
                    callback.onPermissionsResult(allGranted, permissionArray, grantResults);
                }
            }
        );
    }
    
    /**
     * Get common permissions list
     */
    public static String[] getCommonPermissions() {
        List<String> permissionList = new ArrayList<>();
        permissionList.add(Manifest.permission.RECORD_AUDIO);
        permissionList.add(Manifest.permission.CAMERA);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
            permissionList.add(Manifest.permission.BLUETOOTH_CONNECT);
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        
        return permissionList.toArray(new String[0]);
    }
    
    /**
     * Check if permissions are granted
     */
    public static boolean checkPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check and request permissions
     */
    public void checkAndRequestPermissions() {
        checkAndRequestPermissions(getCommonPermissions());
    }
    
    /**
     * Check and request specified permissions
     */
    public void checkAndRequestPermissions(String[] permissions) {
        if (permissions == null || permissions.length == 0) {
            Log.w(TAG, "No permissions to request");
            return;
        }
        
        if (checkPermissions(fragment.requireContext(), permissions)) {
            // All permissions are granted
            int[] grantResults = new int[permissions.length];
            for (int i = 0; i < permissions.length; i++) {
                grantResults[i] = PackageManager.PERMISSION_GRANTED;
            }
            
            if (callback != null) {
                callback.onPermissionsResult(true, permissions, grantResults);
            }
        } else {
            // Request permissions
            permissionLauncher.launch(permissions);
        }
    }
    
    /**
     * Check single permission
     */
    public static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * Check camera permission
     */
    public static boolean checkCameraPermission(Context context) {
        return checkPermission(context, Manifest.permission.CAMERA);
    }
    
    /**
     * Check microphone permission
     */
    public static boolean checkMicrophonePermission(Context context) {
        return checkPermission(context, Manifest.permission.RECORD_AUDIO);
    }
    
    /**
     * Check storage permission
     */
    public static boolean checkStoragePermission(Context context) {
        return checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) &&
               checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    
    /**
     * Release resources
     */
    public void release() {
        fragment = null;
        permissionLauncher = null;
        callback = null;
    }
    
    /**
     * Check and request permission
     */
    public void checkOrRequestPermission(PermissionResultCallback callback) {
        checkOrRequestPermission(getCommonPermissions(), callback);
    }
    
    /**
     * Check and request specified permissions
     */
    public void checkOrRequestPermission(String[] permissions, PermissionResultCallback callback) {
        this.callback = callback;
        
        if (permissions != null && permissions.length > 0) {
            if (checkPermissions(context, permissions)) {
                int[] grantResults = new int[permissions.length];
                for (int i = 0; i < permissions.length; i++) {
                    grantResults[i] = PackageManager.PERMISSION_GRANTED;
                }
                callback.onPermissionsResult(true, permissions, grantResults);
            } else {
                // Need Fragment context to start permission request
                Log.w(TAG, "Permission request requires Fragment context");
            }
        }
    }
    
    /**
     * Set Fragment and permission launcher
     */
    public void setFragmentAndLauncher(Fragment fragment, ActivityResultLauncher<String[]> launcher) {
        this.fragment = fragment;
        this.permissionLauncher = launcher;
    }
} 