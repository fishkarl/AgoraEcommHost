package io.agora.api.example.examples.advanced.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * UI State Manager
 * Responsible for managing UI state changes in VideoProcessExtension
 */
public class UiStateManager {
    private static final String TAG = "UiStateManager";
    
    private final Handler handler;
    private final Context context;
    
    // UI component references
    private MaterialButton joinButton;
    private MaterialCardView joinControlPanel;
    private ScrollView controlPanel;
    private LinearLayout rightControlPanel;
    private FloatingActionButton fabShowControls;
    private FrameLayout localVideoContainer;
    private FrameLayout remoteVideoContainer;
    
    public UiStateManager(@NonNull Context context) {
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Set UI component references
     */
    public void setUiComponents(MaterialButton joinButton, 
                               MaterialCardView joinControlPanel,
                               ScrollView controlPanel,
                               LinearLayout rightControlPanel,
                               FloatingActionButton fabShowControls,
                               FrameLayout localVideoContainer,
                               FrameLayout remoteVideoContainer) {
        this.joinButton = joinButton;
        this.joinControlPanel = joinControlPanel;
        this.controlPanel = controlPanel;
        this.rightControlPanel = rightControlPanel;
        this.fabShowControls = fabShowControls;
        this.localVideoContainer = localVideoContainer;
        this.remoteVideoContainer = remoteVideoContainer;
        
        Log.d(TAG, "UI components set - joinControlPanel: " + (joinControlPanel != null ? "not null" : "null"));
        Log.d(TAG, "UI components set - joinButton: " + (joinButton != null ? "not null" : "null"));
    }
    
    /**
     * Update UI state after successfully joining channel
     */
    public void updateUiForChannelJoined() {
        Log.d(TAG, "updateUiForChannelJoined called - checking UI components...");
        Log.d(TAG, "joinButton: " + (joinButton != null ? "not null" : "null"));
        Log.d(TAG, "joinControlPanel: " + (joinControlPanel != null ? "not null" : "null"));
        Log.d(TAG, "controlPanel: " + (controlPanel != null ? "not null" : "null"));
        Log.d(TAG, "rightControlPanel: " + (rightControlPanel != null ? "not null" : "null"));
        
        handler.post(() -> {
            if (joinButton != null) {
                joinButton.setEnabled(true);
                joinButton.setText("Leave");
                Log.d(TAG, "Updated join button text to 'Leave'");
            } else {
                Log.w(TAG, "join button is null");
            }
            
            if (joinControlPanel != null) {
                Log.d(TAG, "joinControlPanel current visibility: " + joinControlPanel.getVisibility());
                Log.d(TAG, "joinControlPanel id: " + joinControlPanel.getId());
                Log.d(TAG, "joinControlPanel parent: " + (joinControlPanel.getParent() != null ? "has parent" : "no parent"));
                
                joinControlPanel.setVisibility(View.GONE);
                Log.d(TAG, "Set joinControlPanel visibility to GONE");
                
                // Force refresh UI
                joinControlPanel.invalidate();
                joinControlPanel.requestLayout();
                
                // Delay check if really hidden
                handler.postDelayed(() -> {
                    if (joinControlPanel != null) {
                        Log.d(TAG, "joinControlPanel visibility after delay: " + joinControlPanel.getVisibility());
                        if (joinControlPanel.getVisibility() != View.GONE) {
                            Log.w(TAG, "joinControlPanel is still visible, trying to hide again");
                            joinControlPanel.setVisibility(View.GONE);
                            joinControlPanel.invalidate();
                        }
                    }
                }, 100);
            } else {
                Log.w(TAG, "joinControlPanel is null when trying to hide it!");
            }
            
            // If control panel is hidden, show right control button panel
            if (controlPanel != null && controlPanel.getVisibility() == View.INVISIBLE && rightControlPanel != null) {
                rightControlPanel.setVisibility(View.VISIBLE);
                Log.d(TAG, "Set rightControlPanel visibility to VISIBLE");
            }
        });
    }
    
    /**
     * Update UI state after leaving channel
     */
    public void updateUiForChannelLeft() {
        handler.post(() -> {
            if (joinButton != null) {
                joinButton.setText("Join");
                joinButton.setEnabled(true);
            }
            
            if (joinControlPanel != null) {
                joinControlPanel.setVisibility(View.VISIBLE);
            }
            
            if (controlPanel != null) {
                controlPanel.setVisibility(View.INVISIBLE);
            }
            
            if (rightControlPanel != null) {
                rightControlPanel.setVisibility(View.GONE);
            }
        });
    }
    
    /**
     * Toggle control panel show/hide
     */
    public void toggleControlPanel() {
        if (controlPanel == null || rightControlPanel == null || fabShowControls == null) {
            Log.w(TAG, "UI components not initialized");
            return;
        }
        
        if (controlPanel.getVisibility() == View.VISIBLE) {
            // Hide control panel, show right control buttons
            controlPanel.setVisibility(View.INVISIBLE);
            rightControlPanel.setVisibility(View.VISIBLE);
            fabShowControls.setImageResource(io.agora.api.example.ecomm.R.drawable.ic_visibility_off);
        } else {
            // Show control panel, hide right control buttons
            controlPanel.setVisibility(View.VISIBLE);
            rightControlPanel.setVisibility(View.GONE);
            fabShowControls.setImageResource(io.agora.api.example.ecomm.R.drawable.ic_visibility);
        }
    }
    
    /**
     * Show focus indicator
     */
    public void showFocusIndicator(float x, float y) {
        if (localVideoContainer == null) return;
        
        // Remove previous indicator
        View existingIndicator = localVideoContainer.findViewWithTag("focus_indicator");
        if (existingIndicator != null) {
            localVideoContainer.removeView(existingIndicator);
        }
        
        // Create new indicator
        View indicator = new View(context);
        indicator.setTag("focus_indicator");
        indicator.setBackgroundResource(io.agora.api.example.ecomm.R.drawable.focus_rectangle);
        
        // Set indicator position and size - magnify 3x
        int baseIndicatorSize = 60;
        int indicatorSize = baseIndicatorSize * 3; // Magnify 3x
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(indicatorSize, indicatorSize);
        params.leftMargin = (int) (x - indicatorSize / 2);
        params.topMargin = (int) (y - indicatorSize / 2);
        indicator.setLayoutParams(params);
        
        localVideoContainer.addView(indicator);
        
        // Auto remove indicator after 2 seconds
        handler.postDelayed(() -> {
            if (localVideoContainer != null) {
                localVideoContainer.removeView(indicator);
            }
        }, 2000);
    }
    
    /**
     * Hide soft keyboard
     */
    public void hideSoftKeyboard(android.widget.EditText editText) {
        if (editText == null) return;
        
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) 
            context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
    
    /**
     * Get local video container
     */
    public FrameLayout getLocalVideoContainer() {
        return localVideoContainer;
    }
    
    /**
     * Get remote video container
     */
    public FrameLayout getRemoteVideoContainer() {
        return remoteVideoContainer;
    }
    
    /**
     * Clear remote video view
     */
    public void clearRemoteVideoView() {
        if (remoteVideoContainer != null && remoteVideoContainer.getChildCount() > 0) {
            remoteVideoContainer.removeAllViews();
        }
    }
    
    /**
     * Clear local video view
     */
    public void clearLocalVideoView() {
        if (localVideoContainer != null && localVideoContainer.getChildCount() > 0) {
            localVideoContainer.removeAllViews();
        }
    }
}
