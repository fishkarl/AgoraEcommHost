package io.agora.api.example.examples.advanced.ui;

import android.view.View;
import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Control Panel Manager
 * Responsible for control panel show/hide and state management
 */
public class ControlPanelManager {
    private static final String TAG = "ControlPanelManager";
    
    private ScrollView controlPanel;
    private LinearLayout rightControlPanel;
    private FloatingActionButton fabShowControls;
    private ControlPanelCallback callback;
    
    public interface ControlPanelCallback {
        void onControlPanelVisibilityChanged(boolean isVisible);
    }
    
    public ControlPanelManager(ScrollView controlPanel, LinearLayout rightControlPanel, 
                             FloatingActionButton fabShowControls, ControlPanelCallback callback) {
        this.controlPanel = controlPanel;
        this.rightControlPanel = rightControlPanel;
        this.fabShowControls = fabShowControls;
        this.callback = callback;
    }
    
    /**
     * Toggle control panel show/hide
     */
    public void toggleControlPanel() {
        if (controlPanel == null || rightControlPanel == null || fabShowControls == null) {
            Log.w(TAG, "Control panel components are null");
            return;
        }
        
        if (controlPanel.getVisibility() == View.VISIBLE) {
            // Hide control panel, show right control buttons
            hideControlPanel();
        } else {
            // Show control panel, hide right control buttons
            showControlPanel();
        }
    }
    
    /**
     * Show control panel
     */
    public void showControlPanel() {
        if (controlPanel != null) {
            controlPanel.setVisibility(View.VISIBLE);
        }
        if (rightControlPanel != null) {
            rightControlPanel.setVisibility(View.GONE);
        }
        if (fabShowControls != null) {
            fabShowControls.setImageResource(io.agora.api.example.ecomm.R.drawable.ic_visibility_off);
        }
        
        if (callback != null) {
            callback.onControlPanelVisibilityChanged(true);
        }
        
        Log.d(TAG, "Control panel shown");
    }
    
    /**
     * Hide control panel
     */
    public void hideControlPanel() {
        if (controlPanel != null) {
            controlPanel.setVisibility(View.INVISIBLE);
        }
        if (rightControlPanel != null) {
            rightControlPanel.setVisibility(View.VISIBLE);
        }
        if (fabShowControls != null) {
            fabShowControls.setImageResource(io.agora.api.example.ecomm.R.drawable.ic_visibility);
        }
        
        if (callback != null) {
            callback.onControlPanelVisibilityChanged(false);
        }
        
        Log.d(TAG, "Control panel hidden");
    }
    
    /**
     * Check if control panel is visible
     */
    public boolean isControlPanelVisible() {
        return controlPanel != null && controlPanel.getVisibility() == View.VISIBLE;
    }
    
    /**
     * Check if right control panel is visible
     */
    public boolean isRightControlPanelVisible() {
        return rightControlPanel != null && rightControlPanel.getVisibility() == View.VISIBLE;
    }
    
    /**
     * Set control panel visibility
     */
    public void setControlPanelVisibility(boolean visible) {
        if (visible) {
            showControlPanel();
        } else {
            hideControlPanel();
        }
    }
    
    /**
     * Update control panel state (for after language switch)
     */
    public void updateControlPanelState() {
        if (fabShowControls != null) {
            if (isControlPanelVisible()) {
                fabShowControls.setImageResource(io.agora.api.example.ecomm.R.drawable.ic_visibility_off);
            } else {
                fabShowControls.setImageResource(io.agora.api.example.ecomm.R.drawable.ic_visibility);
            }
        }
    }
    
    /**
     * Release resources
     */
    public void release() {
        controlPanel = null;
        rightControlPanel = null;
        fabShowControls = null;
        callback = null;
    }
} 