package io.agora.api.example.examples.advanced.device;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.video.AgoraFocalLengthInfo;

/**
 * Camera Manager Class
 * Responsible for camera switching, zoom control, focal length management and other functions
 */
public class CameraManager {
    private static final String TAG = "CameraManager";
    
    // Camera type enumeration
    public enum CameraType {
        FRONT,
        BACK
    }
    
    // Zoom range
    private float frontMinZoomFactor = 1.0f;
    private float frontMaxZoomFactor = 1.0f;
    private float backMinZoomFactor = 1.0f;
    private float backMaxZoomFactor = 1.0f;
    private float minZoomFactor = 0.5f;
    private float maxZoomFactor = 5.0f;
    private float currentZoomFactor = 1.0f;
    
    private CameraType currentCameraType = CameraType.BACK;
    private int cameraSwitchCount = 0;
    
    private RtcEngine engine;
    private Handler handler;
    
    public CameraManager(@NonNull Context context, RtcEngine engine) {
        this.engine = engine;
        this.handler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Initialize zoom range
     */
    public void initializeZoomRange() {
        if (engine == null) return;
        
        try {
            float maxZoom = engine.getCameraMaxZoomFactor();
            currentCameraType = CameraType.BACK;
            float minZoom = getCameraMinZoomFactor(currentCameraType);
            
            // Handle back camera ultra-wide mode preview restart
            handleBackCameraUltraWidePreview(minZoom);
            
            updateZoomRange(maxZoom, minZoom);
            currentZoomFactor = minZoomFactor;
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize zoom range: " + e.getMessage());
            setDefaultZoomRange();
        }
    }
    
    /**
     * Switch camera
     */
    public void switchCamera() {
        if (engine == null) return;
        
        engine.switchCamera();
        cameraSwitchCount++;
        
        // Delay updating zoom range to ensure camera switch is complete
        handler.postDelayed(() -> {
            updateZoomRangeForCameraSwitch();
        }, 500);
    }
    
    /**
     * Set zoom factor
     */
    public void setZoomFactor(float zoomFactor) {
        if (engine == null) return;
        
        // Ensure zoom factor is within valid range
        zoomFactor = Math.max(minZoomFactor, Math.min(maxZoomFactor, zoomFactor));
        currentZoomFactor = zoomFactor;
        
        try {
            engine.setCameraZoomFactor(zoomFactor);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set zoom factor: " + e.getMessage());
        }
    }
    
    /**
     * Get current zoom factor
     */
    public float getCurrentZoomFactor() {
        return currentZoomFactor;
    }
    
    /**
     * Get minimum zoom factor
     */
    public float getMinZoomFactor() {
        return minZoomFactor;
    }
    
    /**
     * Get maximum zoom factor
     */
    public float getMaxZoomFactor() {
        return maxZoomFactor;
    }
    
    /**
     * Get current camera type
     */
    public CameraType getCurrentCameraType() {
        return currentCameraType;
    }
    
    /**
     * Use Agora API to get camera's minimum zoom factor
     */
    private float getCameraMinZoomFactor(CameraType cameraType) {
        try {
            if (engine != null) {
                AgoraFocalLengthInfo[] focalLengthInfos = engine.queryCameraFocalLengthCapability();
                
                if (focalLengthInfos != null && focalLengthInfos.length > 0) {
                    boolean hasUltraWide = false;
                    for (AgoraFocalLengthInfo info : focalLengthInfos) {
                        if (info.cameraDirection == 1 && info.focalLengthType == 0) {
                            hasUltraWide = true;
                            break;
                        }
                    }
                    
                    if (cameraType == CameraType.FRONT) {
                        return 1.0f;
                    } else {
                        if (hasUltraWide) {
                            Log.d(TAG, "Back camera supports ultra-wide angle, min zoom factor: 0.5");
                            return 0.5f;
                        } else {
                            Log.d(TAG, "Back camera does not support ultra-wide angle, min zoom factor: 1.0");
                            return 1.0f;
                        }
                    }
                }
                
                float maxZoom = engine.getCameraMaxZoomFactor();
                if (cameraType == CameraType.FRONT) {
                    return 1.0f;
                } else {
                    if (maxZoom > 5.0f) {
                        return 0.5f;
                    } else {
                        return 1.0f;
                    }
                }
            }
            
            return (cameraType == CameraType.FRONT) ? 1.0f : 1.0f;
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting camera min zoom factor: " + e.getMessage());
            return (cameraType == CameraType.FRONT) ? 1.0f : 1.0f;
        }
    }
    
    /**
     * Update zoom range
     */
    private void updateZoomRange(float maxZoom, float minZoom) {
        if (maxZoom > 1.0f) {
            if (currentCameraType == CameraType.FRONT) {
                frontMinZoomFactor = minZoom;
                frontMaxZoomFactor = maxZoom;
                minZoomFactor = frontMinZoomFactor;
                maxZoomFactor = frontMaxZoomFactor;
                Log.d(TAG, "Front camera zoom range: " + minZoomFactor + " - " + maxZoomFactor);
            } else {
                backMinZoomFactor = minZoom;
                backMaxZoomFactor = maxZoom;
                minZoomFactor = backMinZoomFactor;
                maxZoomFactor = backMaxZoomFactor;
                Log.d(TAG, "Back camera zoom range: " + minZoomFactor + " - " + maxZoomFactor);
            }
        } else {
            if (currentCameraType == CameraType.FRONT) {
                frontMinZoomFactor = minZoom;
                frontMaxZoomFactor = 2.0f;
                minZoomFactor = frontMinZoomFactor;
                maxZoomFactor = frontMaxZoomFactor;
                Log.d(TAG, "Front camera does not support zoom, using default range: " + minZoomFactor + " - " + maxZoomFactor);
            } else {
                backMinZoomFactor = minZoom;
                backMaxZoomFactor = 5.0f;
                minZoomFactor = backMinZoomFactor;
                maxZoomFactor = backMaxZoomFactor;
                Log.d(TAG, "Back camera does not support zoom, using default range: " + minZoomFactor + " - " + maxZoomFactor);
            }
        }
        
        // Zoom range updated
    }
    
    /**
     * Update zoom range when switching camera
     */
    private void updateZoomRangeForCameraSwitch() {
        if (engine == null) return;
        
        try {
            CameraType newCameraType = (cameraSwitchCount % 2 == 1) ? CameraType.FRONT : CameraType.BACK;
            currentCameraType = newCameraType;
            
            float maxZoom = engine.getCameraMaxZoomFactor();
            float minZoom = getCameraMinZoomFactor(currentCameraType);
            
            handleBackCameraUltraWidePreview(minZoom);
            updateZoomRange(maxZoom, minZoom);
            
            // Adjust current zoom factor to new range
            if (currentZoomFactor < minZoomFactor) {
                currentZoomFactor = minZoomFactor;
            } else if (currentZoomFactor > maxZoomFactor) {
                currentZoomFactor = maxZoomFactor;
            }
            
            engine.setCameraZoomFactor(currentZoomFactor);
            Log.d(TAG, "Applied zoom factor after camera switch: " + currentZoomFactor);
            
            // Camera switched
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to update zoom range for camera switch: " + e.getMessage());
        }
    }
    
    /**
     * Handle back camera ultra-wide mode preview restart
     */
    private void handleBackCameraUltraWidePreview(float minZoomFactor) {
        if (currentCameraType == CameraType.BACK && minZoomFactor < 1.0f) {
            try {
                Log.d(TAG, "Back camera ultra-wide mode detected, restarting preview for min zoom: " + minZoomFactor);
                
                engine.stopPreview();
                Log.d(TAG, "Preview stopped for ultra-wide mode");
                
                handler.postDelayed(() -> {
                    try {
                        engine.startPreview();
                        Log.d(TAG, "Preview restarted for ultra-wide mode");
                    } catch (Exception e) {
                        Log.e(TAG, "Error restarting preview: " + e.getMessage());
                    }
                }, 500);
                
            } catch (Exception e) {
                Log.e(TAG, "Error handling ultra-wide preview restart: " + e.getMessage());
            }
        }
    }
    
    /**
     * Set default zoom range
     */
    private void setDefaultZoomRange() {
        if (currentCameraType == CameraType.FRONT) {
            frontMinZoomFactor = 0.5f;
            frontMaxZoomFactor = 2.0f;
            minZoomFactor = frontMinZoomFactor;
            maxZoomFactor = frontMaxZoomFactor;
        } else {
            backMinZoomFactor = 0.5f;
            backMaxZoomFactor = 5.0f;
            minZoomFactor = backMinZoomFactor;
            maxZoomFactor = backMaxZoomFactor;
        }
        Log.d(TAG, "Using default zoom range: " + minZoomFactor + " - " + maxZoomFactor);
    }
    
    /**
     * Update engine instance
     */
    public void updateEngine(RtcEngine newEngine) {
        this.engine = newEngine;
    }
    
    /**
     * Release resources
     */
    public void release() {
        handler.removeCallbacksAndMessages(null);
        engine = null;
    }
    
    /**
     * Get system zoom range
     */
    public void getSystemZoomRange() {
        if (engine == null) return;
        
        try {
            // Get current camera's zoom range
            float maxZoom = engine.getCameraMaxZoomFactor();
            
            // Default to back camera during initialization
            currentCameraType = CameraType.BACK;
            
            // Use Agora API to get minimum zoom factor
            float minZoom = getCameraMinZoomFactor(currentCameraType);
            
            // Handle back camera ultra-wide mode preview restart
            handleBackCameraUltraWidePreview(minZoom);
            
            if (maxZoom > 1.0f) {
                // If camera supports zoom, use camera-supported range
                if (currentCameraType == CameraType.FRONT) {
                    frontMinZoomFactor = minZoom;
                    frontMaxZoomFactor = maxZoom;
                    minZoomFactor = frontMinZoomFactor;
                    maxZoomFactor = frontMaxZoomFactor;
                    Log.d(TAG, "Front camera zoom range: " + minZoomFactor + " - " + maxZoomFactor);
                } else {
                    backMinZoomFactor = minZoom;
                    backMaxZoomFactor = maxZoom;
                    minZoomFactor = backMinZoomFactor;
                    maxZoomFactor = backMaxZoomFactor;
                    Log.d(TAG, "Back camera zoom range: " + minZoomFactor + " - " + maxZoomFactor);
                }
            } else {
                // If camera doesn't support zoom, use Camera2 API range
                if (currentCameraType == CameraType.FRONT) {
                    frontMinZoomFactor = minZoom;
                    frontMaxZoomFactor = 2.0f; // Front camera usually has smaller zoom range
                    minZoomFactor = frontMinZoomFactor;
                    maxZoomFactor = frontMaxZoomFactor;
                    Log.d(TAG, "Front camera does not support zoom, using Camera2 API range: " + minZoomFactor + " - " + maxZoomFactor);
                } else {
                    backMinZoomFactor = minZoom;
                    backMaxZoomFactor = 5.0f;
                    minZoomFactor = backMinZoomFactor;
                    maxZoomFactor = backMaxZoomFactor;
                    Log.d(TAG, "Back camera does not support zoom, using Camera2 API range: " + minZoomFactor + " - " + maxZoomFactor);
                }
            }
            
            // Reset current zoom factor to minimum
            currentZoomFactor = minZoomFactor;
            
        } catch (Exception e) {
            // Use default values when getting fails
            Log.e(TAG, "Failed to get camera zoom range: " + e.getMessage());
            if (currentCameraType == CameraType.FRONT) {
                frontMinZoomFactor = 0.5f;
                frontMaxZoomFactor = 2.0f;
                minZoomFactor = frontMinZoomFactor;
                maxZoomFactor = frontMaxZoomFactor;
            } else {
                backMinZoomFactor = 0.5f;
                backMaxZoomFactor = 5.0f;
                minZoomFactor = backMinZoomFactor;
                maxZoomFactor = backMaxZoomFactor;
            }
            Log.d(TAG, "Using default zoom range after error: " + minZoomFactor + " - " + maxZoomFactor);
        }
    }
    

    
    /**
     * Set current zoom factor
     */
    public void setCurrentZoomFactor(float zoomFactor) {
        this.currentZoomFactor = zoomFactor;
    }
    
    /**
     * Get current camera type string
     */
    public String getCurrentCameraTypeString() {
        return (currentCameraType == CameraType.FRONT) ? "Front Camera" : "Back Camera";
    }
} 