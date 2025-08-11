package io.agora.api.example.examples.advanced.device;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;

import io.agora.rtc2.RtcEngine;
import io.agora.api.example.examples.advanced.ui.UiStateManager;

/**
 * Focus Controller
 * Responsible for manual focus and auto focus functionality
 */
public class FocusController {
    private static final String TAG = "FocusController";
    
    private final Context context;
    private RtcEngine engine;
    private FrameLayout localVideoContainer;
    private boolean isManualFocusEnabled = false;
    private View.OnTouchListener screenTouchListener = null;
    private UiStateManager uiStateManager;
    
    public FocusController(@NonNull Context context, RtcEngine engine) {
        this.context = context;
        this.engine = engine;
    }
    
    /**
     * Enable manual focus mode
     */
    public void enableManualFocus() {
        if (engine == null) return;
        
        try {
            // Completely disable auto focus
            engine.setCameraAutoFocusFaceModeEnabled(false);
            
            // Add screen touch listener
            addScreenTouchListener();
            
            isManualFocusEnabled = true;
            
            // Manual focus mode enabled
            
            Log.d(TAG, "Manual focus enabled");
            
        } catch (Exception e) {
            Log.e(TAG, "Error enabling manual focus: " + e.getMessage());
        }
    }
    
    /**
     * Enable auto focus mode
     */
    public void enableAutoFocus() {
        if (engine == null) return;
        
        try {
            // Enable auto focus
            engine.setCameraAutoFocusFaceModeEnabled(true);
            
            // Remove screen touch listener
            removeScreenTouchListener();
            
            isManualFocusEnabled = false;
            
            // Auto focus mode enabled
            
            Log.d(TAG, "Auto focus enabled");
            
        } catch (Exception e) {
            Log.e(TAG, "Error enabling auto focus: " + e.getMessage());
        }
    }
    
    /**
     * Add screen touch listener
     */
    private void addScreenTouchListener() {
        if (localVideoContainer == null || screenTouchListener != null) return;
        
        screenTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isManualFocusEnabled || engine == null) return false;
                
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Get touch position relative to view coordinates
                    float x = event.getX();
                    float y = event.getY();
                    
                    // Get view dimensions
                    int viewWidth = v.getWidth();
                    int viewHeight = v.getHeight();
                    
                    // Convert coordinates to 0-1 range
                    float normalizedX = x / viewWidth;
                    float normalizedY = y / viewHeight;
                    
                    // Ensure coordinates are within valid range
                    normalizedX = Math.max(0.0f, Math.min(1.0f, normalizedX));
                    normalizedY = Math.max(0.0f, Math.min(1.0f, normalizedY));
                    
                    // Set focus position
                    engine.setCameraFocusPositionInPreview(normalizedX, normalizedY);
                    
                    // Ensure auto focus is completely disabled
                    engine.setCameraAutoFocusFaceModeEnabled(false);
                    
                    // Focus position updated
                    
                    Log.d(TAG, "Manual focus at: (" + normalizedX + ", " + normalizedY + ")");
                    return true;
                }
                return false;
            }
        };
        
        localVideoContainer.setOnTouchListener(screenTouchListener);
    }
    
    /**
     * Remove screen touch listener
     */
    private void removeScreenTouchListener() {
        if (localVideoContainer != null && screenTouchListener != null) {
            localVideoContainer.setOnTouchListener(null);
            screenTouchListener = null;
        }
    }
    
    /**
     * Check if manual focus is enabled
     */
    public boolean isManualFocusEnabled() {
        return isManualFocusEnabled;
    }
    
    /**
     * Update engine instance
     */
    public void updateEngine(RtcEngine newEngine) {
        this.engine = newEngine;
    }
    
    /**
     * Update video container
     */
    public void updateVideoContainer(FrameLayout newContainer) {
        this.localVideoContainer = newContainer;
    }
    
    /**
     * Release resources
     */
    public void release() {
        removeScreenTouchListener();
        engine = null;
        localVideoContainer = null;
        uiStateManager = null;
    }
    
    /**
     * Add screen touch listener
     */
    public void addScreenTouchListener(FrameLayout localVideoContainer, UiStateManager uiStateManager) {
        this.localVideoContainer = localVideoContainer;
        this.uiStateManager = uiStateManager;
        
        if (localVideoContainer == null || screenTouchListener != null) return;
        
        isManualFocusEnabled = true;
        screenTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isManualFocusEnabled || engine == null) return false;
                
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Get touch position relative to view coordinates
                    float x = event.getX();
                    float y = event.getY();
                    
                    // Get view dimensions
                    int viewWidth = v.getWidth();
                    int viewHeight = v.getHeight();
                    
                    // Convert coordinates to 0-1 range
                    float normalizedX = x / viewWidth;
                    float normalizedY = y / viewHeight;
                    
                    // Ensure coordinates are within valid range
                    normalizedX = Math.max(0.0f, Math.min(1.0f, normalizedX));
                    normalizedY = Math.max(0.0f, Math.min(1.0f, normalizedY));
                    
                    // Set focus position, completely manual control
                    engine.setCameraFocusPositionInPreview(normalizedX, normalizedY);
                    
                    // Ensure auto focus is completely disabled to prevent focus from being automatically switched
                    engine.setCameraAutoFocusFaceModeEnabled(false);
                    
                    // Show focus indicator
                    if (uiStateManager != null) {
                        uiStateManager.showFocusIndicator(x, y);
                    }
                    
                    Log.d(TAG, "Manual focus at: (" + normalizedX + ", " + normalizedY + ")");
                    return true;
                }
                return false;
            }
        };
        
        localVideoContainer.setOnTouchListener(screenTouchListener);
        Toast.makeText(context, "Manual focus enabled, tap anywhere on screen to focus", Toast.LENGTH_SHORT).show();
    }
    

} 