package io.agora.api.example.examples.advanced;

import static io.agora.rtc2.Constants.RENDER_MODE_HIDDEN;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import io.agora.api.example.ecomm.R;
import io.agora.api.example.bean.MpOptions;
import io.agora.api.example.examples.advanced.device.CameraManager;
import io.agora.api.example.examples.advanced.device.FocusController;
import io.agora.api.example.examples.advanced.features.BeautyFeatureManager;
import io.agora.api.example.examples.advanced.features.FilterManager;
import io.agora.api.example.examples.advanced.features.RtcEngineManager;
import io.agora.api.example.examples.advanced.features.RtcEventHandler;
import io.agora.api.example.examples.advanced.ui.ControlPanelManager;
import io.agora.api.example.examples.advanced.ui.DialogManager;
import io.agora.api.example.examples.advanced.ui.LanguageManager;
import io.agora.api.example.examples.advanced.ui.UiStateManager;
import io.agora.api.example.examples.advanced.utils.PermissionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.agora.rtc2.Constants;
import io.agora.rtc2.video.BeautyOptions;
import io.agora.rtc2.video.ColorEnhanceOptions;
import io.agora.rtc2.video.FaceShapeAreaOptions;
import io.agora.rtc2.video.FaceShapeBeautyOptions;
import io.agora.rtc2.video.LowLightEnhanceOptions;
import io.agora.rtc2.video.SegmentationProperty;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoDenoiserOptions;
import io.agora.rtc2.video.VirtualBackgroundSource;

/**
 * EComm Video Process Extension - Refactored video processing extension module
 * Uses multiple utility classes to manage different functional modules
 */
public class VideoProcessExtensionRefactored extends Fragment implements 
        View.OnClickListener, 
        CompoundButton.OnCheckedChangeListener, 
        SeekBar.OnSeekBarChangeListener, 
        AdapterView.OnItemSelectedListener,
        RtcEventHandler.EventCallback,
        DialogManager.DialogCallback {
    
    private static final String TAG = "VideoProcessExtensionRefactored";
    private static final int REQUEST_CODE_PICK_FILTER_FILE = 1001;

    // UI components
    private FrameLayout fl_local, fl_remote;
    private ScrollView controlPanel;
    private com.google.android.material.button.MaterialButton join;
    private com.google.android.material.floatingactionbutton.FloatingActionButton switchCamera, zoomControl, selectFilter, localCube, manualFocus;
    private Switch shapeBeauty, makeUp, beauty, virtualBackground, lightness2, colorful2, noiseReduce2;
    private com.google.android.material.card.MaterialCardView joinControlPanel;
    private LinearLayout rightControlPanel;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabShowControls;
    private com.google.android.material.textfield.TextInputEditText et_channel;
    private RadioGroup virtualBgType;
    
    // SeekBar controls
    private SeekBar seek_lightness, seek_redness, seek_sharpness, seek_videoEnhance, seek_smoothness, seek_strength, seek_skin;
    private SeekBar sbBrowStrength, sbLashStrength, sbShadowStrength, sbPupilStrength, sbBlushStrength, sbLipStrength;
    private SeekBar sbShapeBeautifyAreaIntensity, sbShapeBeautifyStyleIntensity;
    
    // Spinner controls
    private Spinner spinnerBrowStyle, spinnerLashStyle, spinnerShadowStyle, spinnerPupilStyle, spinnerBlushStyle, spinnerLipStyle;
    private Spinner spinnerBrowColor, spinnerLashColor, spinnerShadowColor, spinnerPupilColor, spinnerBlushColor, spinnerLipColor;
    private Spinner spinnerShapeBeautyArea, spinnerShapeBeautifyStyle;
    
    // Manager classes
    private RtcEngineManager engineManager;
    private RtcEventHandler eventHandler;
    private FilterManager filterManager;
    private BeautyFeatureManager beautyManager;
    private CameraManager cameraManager;
    private FocusController focusController;
    private UiStateManager uiStateManager;
    private DialogManager dialogManager;
    private LanguageManager languageManager;
    private ControlPanelManager controlPanelManager;
    private PermissionHelper permissionHelper;
    
    // State variables
    private boolean joined = false;
    private BeautyOptions beautyOptions = new BeautyOptions();
    private MpOptions makeUpOptions = new MpOptions();
    private FaceShapeBeautyOptions faceShapeBeautyOptions = new FaceShapeBeautyOptions();
    private FaceShapeAreaOptions faceShapeAreaOptions = new FaceShapeAreaOptions();
    private double skinProtect = 1.0;
    private double strength = 0.5;
    private VirtualBackgroundSource virtualBackgroundSource = new VirtualBackgroundSource();
    
    private Handler handler = new Handler(Looper.getMainLooper());
    
    // Permission related
    private ActivityResultLauncher<String[]> permissionLauncher;
    private String[] permissionArray;
    private PermissionHelper.PermissionResultCallback permissionResultCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize managers
        initializeManagers();
        
        // Load and apply language settings first, then create view
        languageManager.loadAndApplyLanguage();
        
        // Initialize permission launcher
        permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean allGranted = true;
                int[] grantResults = new int[permissionArray.length];
                
                for (int i = 0; i < permissionArray.length; i++) {
                    String permission = permissionArray[i];
                    boolean granted = permissions.get(permission) != null && permissions.get(permission);
                    grantResults[i] = granted ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
                    if (!granted) {
                        allGranted = false;
                    }
                }
                
                if (permissionResultCallback != null) {
                    permissionResultCallback.onPermissionsResult(allGranted, permissionArray, grantResults);
                }
            }
        );
        
        // Set Fragment and launcher for permission helper
        permissionHelper.setFragmentAndLauncher(this, permissionLauncher);
        
        return inflater.inflate(R.layout.fragment_video_enhancement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize UI components
        initViews(view);
        
        // Initialize RTC engine
        initializeEngine();
    }
    
    /**
     * Initialize all managers
     */
    private void initializeManagers() {
        Context context = requireContext();
        
        // Initialize various managers
        engineManager = new RtcEngineManager(context);
        filterManager = new FilterManager(context, null); // Set engine later
        beautyManager = new BeautyFeatureManager(context, null); // Set engine later
        cameraManager = new CameraManager(context, null); // Set engine later
        focusController = new FocusController(context, null); // Set engine later
        uiStateManager = new UiStateManager(context);
        languageManager = new LanguageManager(context);
        // ControlPanelManager will be initialized in onViewCreated
        permissionHelper = new PermissionHelper(context);
        
        // Initialize event handler
        eventHandler = new RtcEventHandler(context, null, this); // Set engine later
        
        // Initialize dialog manager
        dialogManager = new DialogManager(context, filterManager, this);
    }
    
    /**
     * Initialize RTC engine
     */
    private void initializeEngine() {
        engineManager.initializeEngine(eventHandler, new RtcEngineManager.EngineCallback() {
            @Override
            public void onEngineInitialized(io.agora.rtc2.RtcEngine engine) {
                // Update engine references in other managers
                filterManager = new FilterManager(requireContext(), engine);
                beautyManager = new BeautyFeatureManager(requireContext(), engine);
                cameraManager = new CameraManager(requireContext(), engine);
                focusController = new FocusController(requireContext(), engine);
                eventHandler = new RtcEventHandler(requireContext(), engine, VideoProcessExtensionRefactored.this);
                dialogManager = new DialogManager(requireContext(), filterManager, VideoProcessExtensionRefactored.this);
                
                // Initialize camera zoom range
                cameraManager.getSystemZoomRange();
                
                // Enable extensions and initialize properties - this is key!
                engine.enableExtension("agora_video_filters_clear_vision", "clear_vision", true);
                beautyManager.updateExtensionProperty();
                beautyManager.updateFaceShapeBeautyStyleOptions();
                
                Log.d(TAG, "Engine and extensions initialized successfully");
            }
            
            @Override
            public void onEngineError(String error) {
                Log.e(TAG, "Engine initialization error: " + error);
                Toast.makeText(requireContext(), "Engine initialization failed: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initViews(View view) {
        // Initialize all UI components
        fl_local = view.findViewById(R.id.fl_local);
        fl_remote = view.findViewById(R.id.fl_remote);
        controlPanel = view.findViewById(R.id.controlPanel);
        join = view.findViewById(R.id.btn_join);
        join.setOnClickListener(this);
        switchCamera = view.findViewById(R.id.btn_switch_camera);
        switchCamera.setOnClickListener(this);
        zoomControl = view.findViewById(R.id.btn_zoom_control);
        zoomControl.setOnClickListener(this);
        selectFilter = view.findViewById(R.id.btn_select_filter);
        selectFilter.setOnClickListener(this);
        localCube = view.findViewById(R.id.btn_local_cube);
        localCube.setOnClickListener(this);
        manualFocus = view.findViewById(R.id.btn_manual_focus);
        manualFocus.setOnClickListener(this);
        rightControlPanel = view.findViewById(R.id.right_control_panel);
        
        // Initialize control panel toggle button
        fabShowControls = view.findViewById(R.id.fab_show_controls);
        fabShowControls.setOnClickListener(this);
        
        // Initialize language selection button
        com.google.android.material.button.MaterialButton languageSelectionBtn = view.findViewById(R.id.btn_language_selection);
        languageSelectionBtn.setOnClickListener(this);
        et_channel = view.findViewById(R.id.et_channel);
        joinControlPanel = view.findViewById(R.id.ll_join);
        
        // Initialize Switch controls
        shapeBeauty = view.findViewById(R.id.switch_face_shape_beautify);
        shapeBeauty.setOnCheckedChangeListener(this);
        makeUp = view.findViewById(R.id.switch_face_makeup);
        makeUp.setOnCheckedChangeListener(this);
        beauty = view.findViewById(R.id.switch_face_beautify);
        beauty.setOnCheckedChangeListener(this);
        virtualBackground = view.findViewById(R.id.switch_virtual_background);
        virtualBackground.setOnCheckedChangeListener(this);
        lightness2 = view.findViewById(R.id.switch_lightness2);
        lightness2.setOnCheckedChangeListener(this);
        colorful2 = view.findViewById(R.id.switch_color2);
        colorful2.setOnCheckedChangeListener(this);
        noiseReduce2 = view.findViewById(R.id.switch_video_noise_reduce2);
        noiseReduce2.setOnCheckedChangeListener(this);
        
        // Initialize SeekBar controls
        seek_lightness = view.findViewById(R.id.lightening);
        seek_lightness.setOnSeekBarChangeListener(this);
        seek_redness = view.findViewById(R.id.redness);
        seek_redness.setOnSeekBarChangeListener(this);
        seek_sharpness = view.findViewById(R.id.sharpness);
        seek_sharpness.setOnSeekBarChangeListener(this);
        seek_videoEnhance = view.findViewById(R.id.sb_video_enhance);
        seek_videoEnhance.setOnSeekBarChangeListener(this);
        seek_smoothness = view.findViewById(R.id.smoothness);
        seek_smoothness.setOnSeekBarChangeListener(this);
        seek_strength = view.findViewById(R.id.strength);
        seek_strength.setOnSeekBarChangeListener(this);
        seek_skin = view.findViewById(R.id.skinProtect);
        seek_skin.setOnSeekBarChangeListener(this);
        
        // Initialize Makeup related SeekBars
        sbBrowStrength = view.findViewById(R.id.sb_brow_strength);
        sbBrowStrength.setOnSeekBarChangeListener(this);
        sbLashStrength = view.findViewById(R.id.sb_lash_strength);
        sbLashStrength.setOnSeekBarChangeListener(this);
        sbShadowStrength = view.findViewById(R.id.sb_shadow_strength);
        sbShadowStrength.setOnSeekBarChangeListener(this);
        sbPupilStrength = view.findViewById(R.id.sb_pupil_strength);
        sbPupilStrength.setOnSeekBarChangeListener(this);
        sbBlushStrength = view.findViewById(R.id.sb_blush_strength);
        sbBlushStrength.setOnSeekBarChangeListener(this);
        sbLipStrength = view.findViewById(R.id.sb_lip_strength);
        sbLipStrength.setOnSeekBarChangeListener(this);
        
        // Initialize Makeup related Spinners
        spinnerBrowStyle = view.findViewById(R.id.spinner_brow_style);
        spinnerBrowStyle.setOnItemSelectedListener(this);
        spinnerLashStyle = view.findViewById(R.id.spinner_lash_style);
        spinnerLashStyle.setOnItemSelectedListener(this);
        spinnerShadowStyle = view.findViewById(R.id.spinner_shadow_style);
        spinnerShadowStyle.setOnItemSelectedListener(this);
        spinnerPupilStyle = view.findViewById(R.id.spinner_pupil_style);
        spinnerPupilStyle.setOnItemSelectedListener(this);
        spinnerBlushStyle = view.findViewById(R.id.spinner_blush_style);
        spinnerBlushStyle.setOnItemSelectedListener(this);
        spinnerLipStyle = view.findViewById(R.id.spinner_lip_style);
        spinnerLipStyle.setOnItemSelectedListener(this);
        
        spinnerBrowColor = view.findViewById(R.id.spinner_brow_color);
        spinnerBrowColor.setOnItemSelectedListener(this);
        spinnerLashColor = view.findViewById(R.id.spinner_lash_color);
        spinnerLashColor.setOnItemSelectedListener(this);
        spinnerShadowColor = view.findViewById(R.id.spinner_shadow_color);
        spinnerShadowColor.setOnItemSelectedListener(this);
        spinnerPupilColor = view.findViewById(R.id.spinner_pupil_color);
        spinnerPupilColor.setOnItemSelectedListener(this);
        spinnerBlushColor = view.findViewById(R.id.spinner_blush_color);
        spinnerBlushColor.setOnItemSelectedListener(this);
        spinnerLipColor = view.findViewById(R.id.spinner_lip_color);
        spinnerLipColor.setOnItemSelectedListener(this);
        
        // Initialize Beauty Shape related controls
        sbShapeBeautifyAreaIntensity = view.findViewById(R.id.sb_shape_beautify_area_intensity);
        sbShapeBeautifyAreaIntensity.setOnSeekBarChangeListener(this);
        sbShapeBeautifyStyleIntensity = view.findViewById(R.id.sb_shape_beautify_style_intensity);
        sbShapeBeautifyStyleIntensity.setOnSeekBarChangeListener(this);
        spinnerShapeBeautyArea = view.findViewById(R.id.spinner_shape_beauty_area);
        spinnerShapeBeautyArea.setOnItemSelectedListener(this);
        spinnerShapeBeautifyStyle = view.findViewById(R.id.spinner_shape_beautify_style);
        spinnerShapeBeautifyStyle.setOnItemSelectedListener(this);
        
        // Initialize virtual background related controls
        virtualBgType = view.findViewById(R.id.virtual_bg_type);
        virtualBgType.setOnCheckedChangeListener((group, checkedId) -> {
            resetVirtualBackground();
        });
        
        // Set component references for UI state manager
        uiStateManager.setUiComponents(join, joinControlPanel, controlPanel, rightControlPanel, 
                                     fabShowControls, fl_local, fl_remote);
        
        Log.d(TAG, "Views initialized for refactored Video Process Extension");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (cameraManager != null) {
            cameraManager.getSystemZoomRange();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up screen click listeners
        if (focusController != null) {
            // Remove screen touch listeners
        }
        
        // Only clean up Engine when Activity is truly destroyed
        if (engineManager != null && !engineManager.isInitialized()) {
            Log.d(TAG, "Cleaning up RTC Engine in onDestroy");
            engineManager.destroyEngine();
        } else if (engineManager != null) {
            Log.d(TAG, "Preserving RTC Engine for language switch");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_CODE_PICK_FILTER_FILE && resultCode == android.app.Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                String filePath = filterManager.getPathFromUri(uri);
                
                if (filePath != null) {
                    if (engineManager != null && beauty != null && beauty.isChecked()) {
                        boolean success = filterManager.applyFilter(filePath, 0.5f);
                        if (success) {
                            Toast.makeText(requireContext(), "Filter applied successfully: " + filePath, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Filter application failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    }

    private void joinChannel(String channelId) {
        if (engineManager == null) {
            Log.e(TAG, "Engine manager is null, cannot join channel");
            return;
        }

        Log.d(TAG, "Joining channel: " + channelId);

        // Create local video view
        SurfaceView surfaceView = new SurfaceView(requireContext());
        if (fl_local != null && fl_local.getChildCount() > 0) {
            fl_local.removeAllViews();
        }
        if (fl_local != null) {
            fl_local.addView(surfaceView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        
        // Setup local video
        engineManager.setupLocalVideo(new VideoCanvas(surfaceView, RENDER_MODE_HIDDEN, 0));
        
        // Join channel
        engineManager.joinChannel(channelId, null);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_select_filter) {
            dialogManager.showFilterSelectionDialog();
        } else if (v.getId() == R.id.btn_local_cube) {
            dialogManager.showLocalCubeSelectionDialog();
        } else if (v.getId() == R.id.btn_zoom_control) {
            if (cameraManager != null) {
                dialogManager.showZoomControlDialog(
                    cameraManager.getCurrentZoomFactor(),
                    cameraManager.getMinZoomFactor(),
                    cameraManager.getMaxZoomFactor(),
                    cameraManager.getCurrentCameraTypeString()
                );
            }
        } else if (v.getId() == R.id.btn_switch_camera) {
            if (engineManager != null) {
                engineManager.switchCamera();
                // Delay updating zoom range to ensure camera switch is complete
                handler.postDelayed(() -> {
                    if (cameraManager != null) {
                        // Update zoom range when camera switches
                    }
                }, 500);
            }
        } else if (v.getId() == R.id.btn_manual_focus) {
            dialogManager.showManualFocusDialog();
        } else if (v.getId() == R.id.btn_language_selection) {
            dialogManager.showLanguageSelectionDialog(languageManager.getCurrentLanguage());
        } else if (v.getId() == R.id.fab_show_controls) {
            uiStateManager.toggleControlPanel();
        } else if (v.getId() == R.id.btn_join) {
            if (!joined) {
                String channelId = et_channel.getText().toString();
                if (channelId.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.enter_channel_id), Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check permissions
                permissionArray = PermissionHelper.getCommonPermissions();
                permissionResultCallback = new PermissionHelper.PermissionResultCallback() {
                    @Override
                    public void onPermissionsResult(boolean allPermissionsGranted, String[] permissions, int[] grantResults) {
                        if (allPermissionsGranted) {
                            // Permissions granted, join channel
                            uiStateManager.hideSoftKeyboard(et_channel);
                            join.setEnabled(false);
                            joinChannel(channelId);
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.camera_microphone_permission_required), Toast.LENGTH_LONG).show();
                        }
                    }
                };
                permissionHelper.checkOrRequestPermission(permissionResultCallback);
            } else {
                joined = false;
                if (engineManager != null) {
                    engineManager.leaveChannel();
                }
                uiStateManager.updateUiForChannelLeft();
            }
        }
    }

    // RtcEventHandler.EventCallback implementation
    @Override
    public void onJoinChannelSuccess(int uid) {
        engineManager.setMyUid(uid);
        engineManager.setJoined(true);
        joined = true;
        uiStateManager.updateUiForChannelJoined();
    }

    @Override
    public void onUserJoined(int uid) {
        // User join handling already completed in RtcEventHandler
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        // User offline handling already completed in RtcEventHandler
    }

    @Override
    public void onError(int err) {
        Log.w(TAG, String.format("onError code %d message %s", err, io.agora.rtc2.RtcEngine.getErrorDescription(err)));
    }

    @Override
    public FrameLayout getLocalVideoContainer() {
        return fl_local;
    }

    @Override
    public FrameLayout getRemoteVideoContainer() {
        return fl_remote;
    }

    // DialogManager.DialogCallback implementation
    @Override
    public void onManualFocusModeChanged(boolean isManual) {
        if (isManual) {
            focusController.addScreenTouchListener(fl_local, uiStateManager);
        } else {
            // Remove screen touch listeners
        }
    }

    @Override
    public void onLanguageChanged(String language) {
        languageManager.changeLanguage(language, this);
    }

    @Override
    public void onZoomChanged(float zoomFactor) {
        if (cameraManager != null) {
            cameraManager.setCurrentZoomFactor(zoomFactor);
        }
        if (engineManager != null) {
            engineManager.setCameraZoomFactor(zoomFactor);
        }
    }

    @Override
    public void onFilterSelected(String filterPath, float strength) {
        if (filterManager != null) {
            boolean success = filterManager.applyFilter(filterPath, strength);
            if (success) {
                Toast.makeText(requireContext(), "Filter applied successfully: " + filterPath + " (strength: " + String.format("%.1f", strength) + ")", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Filter application failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLocalCubeSelected(String cubeFile, float strength) {
        if (filterManager != null) {
            String filterPath = filterManager.copyAssetCubeToStorage(cubeFile);
            if (filterPath != null) {
                boolean success = filterManager.applyFilter(filterPath, strength);
                if (success) {
                    Toast.makeText(requireContext(), "Cube file selected successfully: " + cubeFile + " (strength: " + String.format("%.1f", strength) + ")", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Cube file application failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Cube file copy failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public io.agora.rtc2.RtcEngine getEngine() {
        return engineManager != null ? engineManager.getEngine() : null;
    }

    // Other interface implementations
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (beautyManager != null) {
            beautyManager.handleSpinnerSelection(parent, position, makeUpOptions, faceShapeAreaOptions, faceShapeBeautyOptions);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Handle Spinner unselected event
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (beautyManager != null) {
            beautyManager.handleSwitchChange(buttonView, isChecked, engineManager);
        }
        
        // Handle virtual background switch
        if (buttonView.getId() == io.agora.api.example.ecomm.R.id.switch_virtual_background) {
            resetVirtualBackground();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (beautyManager != null) {
            beautyManager.handleSeekBarChange(seekBar, progress, fromUser, 
                beautyOptions, makeUpOptions, faceShapeAreaOptions, faceShapeBeautyOptions,
                skinProtect, strength, engineManager, filterManager);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Start dragging progress bar
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Stop dragging progress bar
    }

    private void resetVirtualBackground() {
        if (beautyManager != null) {
            beautyManager.resetVirtualBackground(virtualBackground, virtualBgType, virtualBackgroundSource, engineManager);
        }
    }
}
