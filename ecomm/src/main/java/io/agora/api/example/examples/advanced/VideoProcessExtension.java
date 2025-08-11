package io.agora.api.example.examples.advanced;

import static io.agora.rtc2.Constants.RENDER_MODE_HIDDEN;
import static io.agora.rtc2.video.VideoEncoderConfiguration.STANDARD_BITRATE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraAccessException;
import android.util.Range;
import io.agora.rtc2.video.AgoraFocalLengthInfo;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.BeautyOptions;
import io.agora.rtc2.video.ColorEnhanceOptions;
import io.agora.rtc2.video.FaceShapeAreaOptions;
import io.agora.rtc2.video.FaceShapeBeautyOptions;
import io.agora.rtc2.video.FilterEffectOptions;
import io.agora.rtc2.video.LowLightEnhanceOptions;
import io.agora.rtc2.video.SegmentationProperty;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoDenoiserOptions;
import io.agora.rtc2.video.VideoEncoderConfiguration;
import io.agora.rtc2.video.VirtualBackgroundSource;


/**
 * EComm Video Process Extension - 独立的视频处理扩展模块
 */
public class VideoProcessExtension extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {
    
    // 权限回调接口
    public interface PermissionResultCallback {
        void onPermissionsResult(boolean allPermissionsGranted, String[] permissions, int[] grantResults);
    }
    private static final String TAG = "ECommVideoProcessExtension";

    private FrameLayout fl_local, fl_remote;
    private ScrollView controlPanel;
    private com.google.android.material.button.MaterialButton join;
    private com.google.android.material.floatingactionbutton.FloatingActionButton switchCamera, zoomControl, selectFilter, localCube, manualFocus;
    private Switch shapeBeauty, makeUp, beauty, virtualBackground, lightness2, colorful2, noiseReduce2;
    private com.google.android.material.card.MaterialCardView joinControlPanel;
    private LinearLayout rightControlPanel;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabShowControls;
    private float currentZoomFactor = 1.0f;
    private float minZoomFactor = 0.5f;
    private float maxZoomFactor = 5.0f;
    private String selectedFilterPath = null;
    private static final int REQUEST_CODE_PICK_FILTER_FILE = 1001;
    private SeekBar seek_lightness, seek_redness, seek_sharpness, seek_videoEnhance, seek_smoothness, seek_strength, seek_skin;
    
    // Makeup
    private SeekBar sbBrowStrength, sbLashStrength, sbShadowStrength, sbPupilStrength, sbBlushStrength, sbLipStrength;
    private Spinner spinnerBrowStyle, spinnerLashStyle, spinnerShadowStyle, spinnerPupilStyle, spinnerBlushStyle, spinnerLipStyle;
    private Spinner spinnerBrowColor, spinnerLashColor, spinnerShadowColor, spinnerPupilColor, spinnerBlushColor, spinnerLipColor;
    
    // Beauty Shape
    private SeekBar sbShapeBeautifyAreaIntensity, sbShapeBeautifyStyleIntensity;
    private Spinner spinnerShapeBeautyArea, spinnerShapeBeautifyStyle;
    private com.google.android.material.textfield.TextInputEditText et_channel;
    private RadioGroup virtualBgType;
    private RtcEngine engine;
    private int myUid;
    private boolean joined = false;
    private BeautyOptions beautyOptions = new BeautyOptions();
    private FilterEffectOptions filterEffectOptions = new FilterEffectOptions();
    private MpOptions makeUpOptions = new MpOptions();
    private FaceShapeBeautyOptions faceShapeBeautyOptions = new FaceShapeBeautyOptions();
    private FaceShapeAreaOptions faceShapeAreaOptions = new FaceShapeAreaOptions();
    private double skinProtect = 1.0;
    private double strength = 0.5;
    private VirtualBackgroundSource virtualBackgroundSource = new VirtualBackgroundSource();
    
    private Handler handler = new Handler(Looper.getMainLooper());
    
    // 手动对焦相关
    private boolean isManualFocusEnabled = false;
    private View.OnTouchListener screenTouchListener = null;
    
    // 语言管理相关
    private String currentLanguage = "zh"; // 默认中文
    private static final String PREF_LANGUAGE = "pref_language";
    private static final String LANGUAGE_ZH = "zh";
    private static final String LANGUAGE_EN = "en";
    private static final String LANGUAGE_JA = "ja";
    private static final String LANGUAGE_KO = "ko";
    
    // 权限相关
    private ActivityResultLauncher<String[]> permissionLauncher;
    private String[] permissionArray;
    private PermissionResultCallback permissionResultCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 先加载和应用语言设置，再创建视图
        currentLanguage = loadLanguagePreference();
        applyLanguage(currentLanguage);
        
        // 初始化权限启动器
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
        
        return inflater.inflate(R.layout.fragment_video_enhancement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 初始化UI组件
        initViews(view);
        initEngine();
    }

    private void initViews(View view) {
        // 初始化所有UI组件
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
        
        // 初始化控制面板开关按钮
        fabShowControls = view.findViewById(R.id.fab_show_controls);
        fabShowControls.setOnClickListener(this);
        
        // 初始化语言选择按钮
        com.google.android.material.button.MaterialButton languageSelectionBtn = view.findViewById(R.id.btn_language_selection);
        languageSelectionBtn.setOnClickListener(this);
        et_channel = view.findViewById(R.id.et_channel);
        joinControlPanel = view.findViewById(R.id.ll_join);
        
        // 初始化Switch控件
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
        
        // 初始化SeekBar控件
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
        
        // 初始化Makeup相关的SeekBar
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
        
        // 初始化Makeup相关的Spinner
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
        
        // 初始化Beauty Shape相关的控件
        sbShapeBeautifyAreaIntensity = view.findViewById(R.id.sb_shape_beautify_area_intensity);
        sbShapeBeautifyAreaIntensity.setOnSeekBarChangeListener(this);
        sbShapeBeautifyStyleIntensity = view.findViewById(R.id.sb_shape_beautify_style_intensity);
        sbShapeBeautifyStyleIntensity.setOnSeekBarChangeListener(this);
        spinnerShapeBeautyArea = view.findViewById(R.id.spinner_shape_beauty_area);
        spinnerShapeBeautyArea.setOnItemSelectedListener(this);
        spinnerShapeBeautifyStyle = view.findViewById(R.id.spinner_shape_beautify_style);
        spinnerShapeBeautifyStyle.setOnItemSelectedListener(this);
        
        // 初始化虚拟背景相关控件
        virtualBgType = view.findViewById(R.id.virtual_bg_type);
        virtualBgType.setOnCheckedChangeListener((group, checkedId) -> {
            resetVirtualBackground();
        });
        
        Log.d(TAG, "Initializing views for EComm Video Process Extension");
    }

    private void initEngine() {
        try {
            // 首先尝试恢复已保存的Engine实例
            restoreEngine();
            
            // 如果Engine已存在且有效，直接使用
            if (engine != null && engineInitialized) {
                Log.d(TAG, "Using existing RTC Engine instance");
                return;
            }
            
            // 如果engine已经存在，先销毁它
            if (engine != null) {
                Log.d(TAG, "Destroying existing RTC Engine before reinitializing");
                engine.leaveChannel();
                RtcEngine.destroy();
                engine = null;
            }
            
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = requireContext().getApplicationContext();
            config.mAppId = getString(R.string.agora_app_id);
            config.mChannelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
            config.mEventHandler = iRtcEngineEventHandler;
            
            engine = RtcEngine.create(config);
            Log.d(TAG, "RTC Engine initialized successfully");
            
            // 保存Engine实例
            preserveEngine();
            
            // 启用视频滤镜扩展
            engine.enableExtension("agora_video_filters_clear_vision", "clear_vision", true);
            updateExtensionProperty();
            updateFaceShapeBeautyStyleOptions();
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize RTC Engine: " + e.getMessage());
        }
    }

    // 添加摄像头类型枚举
    private enum CameraType {
        FRONT,
        BACK
    }
    
    // 存储前后摄像头的缩放范围
    private float frontMinZoomFactor = 1.0f;
    private float frontMaxZoomFactor = 1.0f;
    private float backMinZoomFactor = 1.0f;
    private float backMaxZoomFactor = 1.0f;
    private CameraType currentCameraType = CameraType.BACK; // 默认后置摄像头
    private int cameraSwitchCount = 0; // 摄像头切换次数
    
    // 使用声网API获取摄像头的最小缩放系数
    private float getCameraMinZoomFactor(CameraType cameraType) {
        try {
            if (engine != null) {
                // 使用声网API查询摄像头焦距能力
                AgoraFocalLengthInfo[] focalLengthInfos = engine.queryCameraFocalLengthCapability();
                
                if (focalLengthInfos != null && focalLengthInfos.length > 0) {
                    // 检查是否有支持广角的摄像头
                    boolean hasUltraWide = false;
                    for (AgoraFocalLengthInfo info : focalLengthInfos) {
                        // 检查是否为后置摄像头且支持超广角
                        if (info.cameraDirection == 1 && // 1表示后置摄像头
                            info.focalLengthType == 0) { // 0表示超广角
                            hasUltraWide = true;
                            break;
                        }
                    }
                    
                    if (cameraType == CameraType.FRONT) {
                        // 前置摄像头通常最小缩放为1.0
                        return 1.0f;
                    } else {
                        // 后置摄像头：如果支持广角则最小缩放为0.5，否则为1.0
                        if (hasUltraWide) {
                            Log.d(TAG, "Back camera supports ultra-wide angle, min zoom factor: 0.5");
                            return 0.5f;
                        } else {
                            Log.d(TAG, "Back camera does not support ultra-wide angle, min zoom factor: 1.0");
                            return 1.0f;
                        }
                    }
                }
                
                // 如果无法获取焦距信息，使用传统方法推断
                float maxZoom = engine.getCameraMaxZoomFactor();
                
                if (cameraType == CameraType.FRONT) {
                    return 1.0f;
                } else {
                    // 后置摄像头根据最大缩放因子推断
                    if (maxZoom > 5.0f) {
                        return 0.5f; // 高倍变焦摄像头可能支持广角
                    } else {
                        return 1.0f;
                    }
                }
            }
            
            // 如果无法获取信息，使用默认值
            Log.w(TAG, "Unable to get camera zoom info, using default min zoom factor for " + cameraType);
            return (cameraType == CameraType.FRONT) ? 1.0f : 1.0f;
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting camera min zoom factor: " + e.getMessage());
            return (cameraType == CameraType.FRONT) ? 1.0f : 1.0f;
        }
    }
    
    private void getSystemZoomRange() {
        if (engine != null) {
            try {
                // 获取当前摄像头的缩放范围
                float maxZoom = engine.getCameraMaxZoomFactor();
                
                // 初始化时默认为后置摄像头
                currentCameraType = CameraType.BACK;
                
                // 使用声网API获取最小缩放因子
                float minZoom = getCameraMinZoomFactor(currentCameraType);
                
                // 处理后置摄像头广角模式下的预览重启
                handleBackCameraUltraWidePreview(minZoom);
                
                if (maxZoom > 1.0f) {
                    // 如果相机支持缩放，使用相机支持的范围
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
                    // 如果相机不支持缩放，使用Camera2 API获取的范围
                    if (currentCameraType == CameraType.FRONT) {
                        frontMinZoomFactor = minZoom;
                        frontMaxZoomFactor = 2.0f; // 前置摄像头通常缩放范围较小
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
                
                // 重置当前缩放因子到最小值
                currentZoomFactor = minZoomFactor;
                
            } catch (Exception e) {
                // 获取失败时使用默认值
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
    }
    
    // 切换摄像头时更新缩放范围
    private void updateZoomRangeForCameraSwitch() {
        if (engine != null) {
            try {
                // 通过切换次数推断摄像头类型
                // 初始状态为后置摄像头，每次切换时改变状态
                cameraSwitchCount++;
                CameraType newCameraType = (cameraSwitchCount % 2 == 1) ? CameraType.FRONT : CameraType.BACK;
                
                // 更新摄像头类型
                currentCameraType = newCameraType;
                
                // 重新获取当前摄像头的缩放范围
                float maxZoom = engine.getCameraMaxZoomFactor();
                float minZoom = getCameraMinZoomFactor(currentCameraType);
                
                // 处理后置摄像头广角模式下的预览重启
                handleBackCameraUltraWidePreview(minZoom);
                
                if (maxZoom > 1.0f) {
                    if (currentCameraType == CameraType.FRONT) {
                        frontMinZoomFactor = minZoom;
                        frontMaxZoomFactor = maxZoom;
                        minZoomFactor = frontMinZoomFactor;
                        maxZoomFactor = frontMaxZoomFactor;
                        Log.d(TAG, "Switched to front camera, zoom range: " + minZoomFactor + " - " + maxZoomFactor);
                    } else {
                        backMinZoomFactor = minZoom;
                        backMaxZoomFactor = maxZoom;
                        minZoomFactor = backMinZoomFactor;
                        maxZoomFactor = backMaxZoomFactor;
                        Log.d(TAG, "Switched to back camera, zoom range: " + minZoomFactor + " - " + maxZoomFactor);
                    }
                } else {
                    // 如果相机不支持缩放，使用Camera2 API获取的范围
                    if (currentCameraType == CameraType.FRONT) {
                        frontMinZoomFactor = minZoom;
                        frontMaxZoomFactor = 2.0f;
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
                
                // 调整当前缩放因子到新范围
                if (currentZoomFactor < minZoomFactor) {
                    currentZoomFactor = minZoomFactor;
                } else if (currentZoomFactor > maxZoomFactor) {
                    currentZoomFactor = maxZoomFactor;
                }
                
                // 应用新的缩放因子
                engine.setCameraZoomFactor(currentZoomFactor);
                Log.d(TAG, "Applied zoom factor after camera switch: " + currentZoomFactor);
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to update zoom range for camera switch: " + e.getMessage());
            }
        }
    }
    
    // 处理后置摄像头广角模式下的预览重启
    private void handleBackCameraUltraWidePreview(float minZoomFactor) {
        if (currentCameraType == CameraType.BACK && minZoomFactor < 1.0f) {
            try {
                Log.d(TAG, "Back camera ultra-wide mode detected, restarting preview for min zoom: " + minZoomFactor);
                
                // 停止预览
                engine.stopPreview();
                Log.d(TAG, "Preview stopped for ultra-wide mode");
                
                // 延迟后重新开始预览
                handler.postDelayed(() -> {
                    try {
                        engine.startPreview();
                        Log.d(TAG, "Preview restarted for ultra-wide mode");
                    } catch (Exception e) {
                        Log.e(TAG, "Error restarting preview: " + e.getMessage());
                    }
                }, 500); // 500ms延迟
                
            } catch (Exception e) {
                Log.e(TAG, "Error handling ultra-wide preview restart: " + e.getMessage());
            }
        }
    }
    
    private void toggleControlPanel() {
        // 切换控制面板显示/隐藏
        if (controlPanel.getVisibility() == View.VISIBLE) {
            // 隐藏控制面板，显示右侧控制按钮
            controlPanel.setVisibility(View.INVISIBLE);
            rightControlPanel.setVisibility(View.VISIBLE);
            fabShowControls.setImageResource(R.drawable.ic_visibility_off);
        } else {
            // 显示控制面板，隐藏右侧控制按钮
            controlPanel.setVisibility(View.VISIBLE);
            rightControlPanel.setVisibility(View.GONE);
            fabShowControls.setImageResource(R.drawable.ic_visibility);
        }
    }
    
    // 获取常用权限列表
    private String[] getCommonPermission() {
        List<String> permissionList = new ArrayList<>();
        permissionList.add(Manifest.permission.RECORD_AUDIO);
        permissionList.add(Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
            permissionList.add(Manifest.permission.BLUETOOTH_CONNECT);
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        String[] permissionArray = new String[permissionList.size()];
        permissionList.toArray(permissionArray);
        return permissionArray;
    }
    
    // 检查权限是否已授予
    private boolean checkPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    
    // 权限检查和请求方法
    private void checkOrRequestPermisson(PermissionResultCallback callback) {
        checkOrRequestPermisson(getCommonPermission(), callback);
    }
    
    private void checkOrRequestPermisson(String[] permissions, PermissionResultCallback callback) {
        if (permissions != null && permissions.length > 0) {
            permissionArray = permissions;
            permissionResultCallback = callback;
            if (checkPermissions(requireContext(), permissionArray)) {
                int[] grantResults = new int[permissionArray.length];
                for (int i = 0; i < permissionArray.length; i++) {
                    grantResults[i] = PackageManager.PERMISSION_GRANTED;
                }
                permissionResultCallback.onPermissionsResult(true, permissionArray, grantResults);
            } else {
                permissionLauncher.launch(permissionArray);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getSystemZoomRange();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 清理屏幕点击监听器
        removeScreenTouchListener();
        
        // 只有在Activity真正销毁时才清理Engine
        if (engine != null && !engineInitialized) {
            Log.d(TAG, "Cleaning up RTC Engine in onDestroy");
            engine.leaveChannel();
            RtcEngine.destroy();
            engine = null;
            staticEngine = null;
            engineInitialized = false;
            staticJoined = false;
            staticMyUid = 0;
        } else if (engine != null) {
            Log.d(TAG, "Preserving RTC Engine for language switch");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_CODE_PICK_FILTER_FILE && resultCode == android.app.Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                String filePath = getPathFromUri(uri);
                
                if (filePath != null) {
                    selectedFilterPath = filePath;
                    Log.d(TAG, "Selected filter file: " + filePath);
                    
                    if (engine != null && beauty != null && beauty.isChecked()) {
                        filterEffectOptions.path = filePath;
                        int ret = engine.setFilterEffectOptions(true, filterEffectOptions);
                        if (ret == 0) {
                            Toast.makeText(requireContext(), "滤镜应用成功: " + filePath, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "滤镜应用失败，错误码: " + ret, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri uri) {
        try {
            String[] projection = {android.provider.MediaStore.MediaColumns.DATA};
            android.database.Cursor cursor = requireContext().getContentResolver().query(uri, projection, null, null, null);
            
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

    private String copyFileToAppDirectory(Uri uri) {
        try {
            java.io.File appDir = new java.io.File(requireContext().getExternalFilesDir(null), "filters");
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
            
            String fileName = "filter_" + System.currentTimeMillis() + ".cube";
            java.io.File destFile = new java.io.File(appDir, fileName);
            
            java.io.InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            java.io.FileOutputStream outputStream = new java.io.FileOutputStream(destFile);
            
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

    private void joinChannel(String channelId) {
        if (engine == null) {
            Log.e(TAG, "Engine is null, cannot join channel");
            return;
        }

        Log.d(TAG, "Joining channel: " + channelId);
        Log.d(TAG, "Current language: " + currentLanguage);
        Log.d(TAG, "Engine instance: " + engine.toString());

        // 验证engine状态
        try {
            // 尝试调用一个简单的API来验证engine是否正常工作
            engine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            Log.d(TAG, "Engine is responsive");
        } catch (Exception e) {
            Log.e(TAG, "Engine is not responsive: " + e.getMessage());
            return;
        }

        SurfaceView surfaceView = new SurfaceView(requireContext());
        if (fl_local != null && fl_local.getChildCount() > 0) {
            fl_local.removeAllViews();
        }
        if (fl_local != null) {
            fl_local.addView(surfaceView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        
        engine.setupLocalVideo(new VideoCanvas(surfaceView, RENDER_MODE_HIDDEN, 0));
        engine.setDefaultAudioRoutetoSpeakerphone(true);
        engine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        engine.enableVideo();
        
        // 设置1080p分辨率
        VideoEncoderConfiguration.VideoDimensions dimensions = new VideoEncoderConfiguration.VideoDimensions(1920, 1080);
        engine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                dimensions,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE
        ));

        ChannelMediaOptions option = new ChannelMediaOptions();
        option.autoSubscribeAudio = true;
        option.autoSubscribeVideo = true;
        option.publishMicrophoneTrack = true;
        option.publishCameraTrack = true;
        
        Log.d(TAG, "About to call engine.joinChannel with channelId: " + channelId);
        int res = engine.joinChannel(null, channelId, 0, option);
        Log.d(TAG, "joinChannel result: " + res);
        if (res != 0) {
            Log.e(TAG, "Failed to join channel: " + res + ", error description: " + RtcEngine.getErrorDescription(res));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_select_filter) {
            showFilterSelectionDialog();
        } else if (v.getId() == R.id.btn_local_cube) {
            showLocalCubeSelectionDialog();
        } else if (v.getId() == R.id.btn_zoom_control) {
            showZoomControlDialog();
        } else if (v.getId() == R.id.btn_switch_camera) {
            if (engine != null) {
                engine.switchCamera();
                // 延迟更新缩放范围，确保摄像头切换完成
                handler.postDelayed(() -> {
                    updateZoomRangeForCameraSwitch();
                }, 500);
            }
        } else if (v.getId() == R.id.btn_manual_focus) {
            showManualFocusDialog();
        } else if (v.getId() == R.id.btn_language_selection) {
            showLanguageSelectionDialog();
        } else if (v.getId() == R.id.fab_show_controls) {
            // 切换控制面板显示/隐藏
            toggleControlPanel();
        } else if (v.getId() == R.id.btn_join) {
            if (!joined) {
                String channelId = et_channel.getText().toString();
                if (channelId.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.enter_channel_id), Toast.LENGTH_SHORT).show();
                    return;
                }
                // 检查权限
                checkOrRequestPermisson(new PermissionResultCallback() {
                    @Override
                    public void onPermissionsResult(boolean allPermissionsGranted, String[] permissions, int[] grantResults) {
                        if (allPermissionsGranted) {
                            // 权限已授予，加入频道
                            hideSoftKeyboard(); // 隐藏软键盘
                            join.setEnabled(false);
                            joinChannel(channelId);
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.camera_microphone_permission_required), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                joined = false;
                engine.leaveChannel();
                join.setText("加入");
                join.setEnabled(true);
                controlPanel.setVisibility(View.INVISIBLE);
                joinControlPanel.setVisibility(View.VISIBLE);
                rightControlPanel.setVisibility(View.GONE);
            }
        }
    }

    private void showManualFocusDialog() {
        if (engine == null) return;
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.focus_mode_selection));
        
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        
        // 说明文字
        TextView instructionText = new TextView(requireContext());
        instructionText.setText(getString(R.string.focus_mode_description));
        instructionText.setTextColor(0xFF000000);
        instructionText.setTextSize(14);
        instructionText.setPadding(0, 0, 0, 20);
        layout.addView(instructionText);
        
        // 对焦模式选择
        RadioGroup focusModeGroup = new RadioGroup(requireContext());
        focusModeGroup.setOrientation(RadioGroup.VERTICAL);
        
        RadioButton autoFocus = new RadioButton(requireContext());
        autoFocus.setText(getString(R.string.auto_focus));
        autoFocus.setId(1);
        autoFocus.setChecked(true);
        
        RadioButton manualFocus = new RadioButton(requireContext());
        manualFocus.setText(getString(R.string.manual_focus_tap));
        manualFocus.setId(2);
        
        focusModeGroup.addView(autoFocus);
        focusModeGroup.addView(manualFocus);
        layout.addView(focusModeGroup);
        
        // 对焦模式切换监听
        focusModeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == 1) {
                // 自动对焦
                engine.setCameraAutoFocusFaceModeEnabled(true);
                
                // 移除屏幕点击监听
                removeScreenTouchListener();
            } else if (checkedId == 2) {
                // 手动对焦 - 完全关闭自动对焦
                engine.setCameraAutoFocusFaceModeEnabled(false);
                
                // 停止所有美颜功能并卸载扩展
                stopAllBeautyFeaturesAndUnloadExtension();
                
                // 添加屏幕点击监听
                addScreenTouchListener();
            }
        });
        
        builder.setView(layout);
        builder.setPositiveButton(getString(R.string.ok), null);
        builder.setNegativeButton(getString(R.string.reset), (dialog, which) -> {
            // 重置为自动对焦
            engine.setCameraAutoFocusFaceModeEnabled(true);
            removeScreenTouchListener();
            
            // 重新启用视频滤镜扩展
            try {
                engine.enableExtension("agora_video_filters_clear_vision", "clear_vision", true);
                Log.d(TAG, "Video filter extension re-enabled after resetting to auto focus");
            } catch (Exception e) {
                Log.e(TAG, "Error re-enabling extension: " + e.getMessage());
            }
        });
        
        builder.show();
    }
    
    private void addScreenTouchListener() {
        if (fl_local == null || screenTouchListener != null) return;
        
        isManualFocusEnabled = true;
        screenTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isManualFocusEnabled || engine == null) return false;
                
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 获取点击位置相对于视图的坐标
                    float x = event.getX();
                    float y = event.getY();
                    
                    // 获取视图的尺寸
                    int viewWidth = v.getWidth();
                    int viewHeight = v.getHeight();
                    
                    // 将坐标转换为0-1范围
                    float normalizedX = x / viewWidth;
                    float normalizedY = y / viewHeight;
                    
                    // 确保坐标在有效范围内
                    normalizedX = Math.max(0.0f, Math.min(1.0f, normalizedX));
                    normalizedY = Math.max(0.0f, Math.min(1.0f, normalizedY));
                    
                    // 设置对焦位置，完全手动控制
                    engine.setCameraFocusPositionInPreview(normalizedX, normalizedY);
                    
                    // 确保自动对焦完全禁用，防止焦点被自动切换
                    engine.setCameraAutoFocusFaceModeEnabled(false);
                    
                    // 显示对焦指示器（可选）
                    showFocusIndicator(x, y);
                    
                    Log.d(TAG, "Manual focus at: (" + normalizedX + ", " + normalizedY + ")");
                    return true;
                }
                return false;
            }
        };
        
        fl_local.setOnTouchListener(screenTouchListener);
        Toast.makeText(requireContext(), "手动对焦已启用，点击屏幕任意位置进行对焦", Toast.LENGTH_SHORT).show();
    }
    
    private void removeScreenTouchListener() {
        if (fl_local != null && screenTouchListener != null) {
            fl_local.setOnTouchListener(null);
            screenTouchListener = null;
        }
        isManualFocusEnabled = false;
    }
    
    // 停止所有美颜功能并卸载扩展
    private void stopAllBeautyFeaturesAndUnloadExtension() {
        if (engine == null) return;
        
        try {
            // 1. 停止所有美颜功能
            // 基础美颜
            if (beauty != null && beauty.isChecked()) {
                beauty.setChecked(false);
                engine.setBeautyEffectOptions(false, beautyOptions);
            }
            
            // 脸型美化
            if (shapeBeauty != null && shapeBeauty.isChecked()) {
                shapeBeauty.setChecked(false);
                engine.setFaceShapeBeautyOptions(false, faceShapeBeautyOptions);
            }
            
            // 面部化妆 - 使用setExtensionProperty来禁用
            if (makeUp != null && makeUp.isChecked()) {
                makeUp.setChecked(false);
                // 使用setExtensionProperty来禁用面部化妆
                engine.setExtensionProperty("agora-video-filter-extension", "enable_makeup", "false", "");
            }
            
            // 虚拟背景 - 需要添加SegmentationProperty参数
            if (virtualBackground != null && virtualBackground.isChecked()) {
                virtualBackground.setChecked(false);
                SegmentationProperty segmentationProperty = new SegmentationProperty();
                engine.enableVirtualBackground(false, virtualBackgroundSource, segmentationProperty);
            }
            
            // 暗光增强
            if (lightness2 != null && lightness2.isChecked()) {
                lightness2.setChecked(false);
                engine.setLowlightEnhanceOptions(false, new LowLightEnhanceOptions());
            }
            
            // 色彩增强
            if (colorful2 != null && colorful2.isChecked()) {
                colorful2.setChecked(false);
                engine.setColorEnhanceOptions(false, new ColorEnhanceOptions());
            }
            
            // 降噪
            if (noiseReduce2 != null && noiseReduce2.isChecked()) {
                noiseReduce2.setChecked(false);
                engine.setVideoDenoiserOptions(false, new VideoDenoiserOptions());
            }
            
            // 2. 彻底禁用视频滤镜扩展
            try {
                engine.enableExtension("agora_video_filters_clear_vision", "clear_vision", false);
                Log.d(TAG, "Video filter extension disabled successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error disabling extension: " + e.getMessage());
            }
            
            // 3. 重置所有相关变量
            selectedFilterPath = null;
            
            Log.d(TAG, "All beauty features stopped and extension disabled for manual focus mode");
            
        } catch (Exception e) {
            Log.e(TAG, "Error stopping beauty features: " + e.getMessage());
        }
    }
    
    private void hideSoftKeyboard() {
        if (et_channel != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(et_channel.getWindowToken(), 0);
            }
        }
    }
    
    private void showLanguageSelectionDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.language_selection));
        
        // 创建主布局
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        
        // 创建RadioGroup
        RadioGroup languageGroup = new RadioGroup(requireContext());
        languageGroup.setOrientation(RadioGroup.VERTICAL);
        
        // 创建语言选项按钮 - 根据当前语言环境设置文本
        RadioButton chineseBtn = new RadioButton(requireContext());
        chineseBtn.setText("中文");
        chineseBtn.setTextColor(0xFF000000);
        chineseBtn.setId(0);
        
        RadioButton englishBtn = new RadioButton(requireContext());
        englishBtn.setText("English");
        englishBtn.setTextColor(0xFF000000);
        englishBtn.setId(1);
        
        RadioButton japaneseBtn = new RadioButton(requireContext());
        japaneseBtn.setText("日本語");
        japaneseBtn.setTextColor(0xFF000000);
        japaneseBtn.setId(2);
        
        RadioButton koreanBtn = new RadioButton(requireContext());
        koreanBtn.setText("한국어");
        koreanBtn.setTextColor(0xFF000000);
        koreanBtn.setId(3);
        
        // 添加调试信息，显示每个按钮的文本
        Log.d(TAG, "Button 0 (ID 0) text: '" + chineseBtn.getText() + "'");
        Log.d(TAG, "Button 1 (ID 1) text: '" + englishBtn.getText() + "'");
        Log.d(TAG, "Button 2 (ID 2) text: '" + japaneseBtn.getText() + "'");
        Log.d(TAG, "Button 3 (ID 3) text: '" + koreanBtn.getText() + "'");
        
        // 添加按钮到RadioGroup
        languageGroup.addView(chineseBtn);
        languageGroup.addView(englishBtn);
        languageGroup.addView(japaneseBtn);
        languageGroup.addView(koreanBtn);
        
        // 根据当前语言设置正确的选中状态
        if (currentLanguage.equals(LANGUAGE_ZH)) {
            chineseBtn.setChecked(true);
        } else if (currentLanguage.equals(LANGUAGE_EN)) {
            englishBtn.setChecked(true);
        } else if (currentLanguage.equals(LANGUAGE_JA)) {
            japaneseBtn.setChecked(true);
        } else if (currentLanguage.equals(LANGUAGE_KO)) {
            koreanBtn.setChecked(true);
        }
        
        // 添加RadioGroup到主布局
        layout.addView(languageGroup);
        
        // 设置对话框内容
        builder.setView(layout);
        builder.setPositiveButton(getString(R.string.ok), null);
        
        // 创建对话框
        android.app.AlertDialog dialog = builder.create();
        
        // 设置选择监听器
        languageGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedLanguage = LANGUAGE_ZH; // 默认中文
            
            // 根据选中的RadioButton的ID来确定语言
            Log.d(TAG, "Selected checkedId: " + checkedId);
            
            // 使用ID映射，更可靠
            if (checkedId == 0) {
                selectedLanguage = LANGUAGE_ZH;      // ID 0 -> 中文
                Log.d(TAG, "Mapped to Chinese (ID 0)");
            } else if (checkedId == 1) {
                selectedLanguage = LANGUAGE_EN;      // ID 1 -> 英文
                Log.d(TAG, "Mapped to English (ID 1)");
            } else if (checkedId == 2) {
                selectedLanguage = LANGUAGE_JA;      // ID 2 -> 日文
                Log.d(TAG, "Mapped to Japanese (ID 2)");
            } else if (checkedId == 3) {
                selectedLanguage = LANGUAGE_KO;      // ID 3 -> 韩文
                Log.d(TAG, "Mapped to Korean (ID 3)");
            } else {
                Log.d(TAG, "Unknown checkedId: " + checkedId);
            }
            
            Log.d(TAG, "Selected language: " + selectedLanguage + ", Current language: " + currentLanguage);
            
            if (!selectedLanguage.equals(currentLanguage)) {
                // 在语言切换前保存Engine状态
                preserveEngine();
                
                currentLanguage = selectedLanguage;
                saveLanguagePreference(selectedLanguage);
                // 关闭对话框
                dialog.dismiss();
                // 重新创建Fragment以应用语言更改，但Engine会被保留
                recreateFragment();
            }
        });
        
        // 显示对话框
        dialog.show();
    }
    
    private void saveLanguagePreference(String language) {
        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("ECommPrefs", Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_LANGUAGE, language).apply();
        Log.d(TAG, "Language saved: " + language);
    }
    
    private String loadLanguagePreference() {
        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("ECommPrefs", Context.MODE_PRIVATE);
        String language = prefs.getString(PREF_LANGUAGE, LANGUAGE_ZH);
        Log.d(TAG, "Language loaded: " + language);
        return language;
    }
    
    private void restartActivity() {
        // 重启Activity以应用语言更改
        android.content.Intent intent = requireActivity().getIntent();
        // 添加标志以确保Activity重新创建
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        requireActivity().finish();
        startActivity(intent);
    }
    
    private void recreateFragment() {
        // 重新创建Fragment而不是重启整个Activity
        try {
            // 获取FragmentManager
            FragmentManager fragmentManager = getParentFragmentManager();
            
            // 创建新的Fragment实例
            VideoProcessExtension newFragment = new VideoProcessExtension();
            
            // 开始Fragment事务
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            
            // 移除当前Fragment
            transaction.remove(this);
            
            // 添加新的Fragment
            transaction.add(R.id.fragment_container, newFragment);
            
            // 提交事务
            transaction.commit();
            
            Log.d(TAG, "Fragment recreated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to recreate fragment: " + e.getMessage());
            // 如果Fragment重新创建失败，回退到Activity重启
            restartActivity();
        }
    }
    
    private void applyLanguageWithoutRecreation() {
        // 不重新创建Fragment，直接更新UI文本
        try {
            Log.d(TAG, "Applying language without recreation: " + currentLanguage);
            
            // 更新所有UI组件的文本
            updateUITexts();
            
            Log.d(TAG, "Language applied to UI successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to apply language to UI: " + e.getMessage());
            // 如果直接更新失败，回退到Fragment重新创建
            recreateFragment();
        }
    }
    
    private void updateUITexts() {
        // 更新所有UI组件的文本为当前语言
        if (join != null) {
            join.setText(R.string.join);
        }
        
        if (fabShowControls != null) {
            // 根据控制面板状态设置图标
            if (controlPanel != null && controlPanel.getVisibility() == View.VISIBLE) {
                fabShowControls.setImageResource(R.drawable.ic_visibility_off);
            } else {
                fabShowControls.setImageResource(R.drawable.ic_visibility);
            }
        }
        
        // 更新其他UI组件...
        // 这里可以根据需要添加更多UI组件的文本更新
    }
    
    // 静态变量来保存Engine实例和状态
    private static RtcEngine staticEngine = null;
    private static boolean engineInitialized = false;
    private static boolean staticJoined = false;
    private static int staticMyUid = 0;
    
    private void preserveEngine() {
        // 保存Engine实例和状态到静态变量
        if (engine != null) {
            staticEngine = engine;
            engineInitialized = true;
            staticJoined = joined;
            staticMyUid = myUid;
            Log.d(TAG, "Engine and state preserved to static variable - joined: " + joined + ", uid: " + myUid);
        }
    }
    
    private void restoreEngine() {
        // 从静态变量恢复Engine实例和状态
        if (staticEngine != null && engineInitialized) {
            engine = staticEngine;
            joined = staticJoined;
            myUid = staticMyUid;
            Log.d(TAG, "Engine and state restored from static variable - joined: " + joined + ", uid: " + myUid);
            
            // 同步UI状态
            syncUIWithEngineState();
        }
    }
    
    private void syncUIWithEngineState() {
        // 同步UI状态与Engine状态
        if (engine != null && joined) {
            Log.d(TAG, "Syncing UI: already joined channel with uid: " + myUid);
            
            handler.post(() -> {
                if (join != null) {
                    join.setEnabled(true);
                    join.setText("离开");
                    Log.d(TAG, "Updated join button text to '离开'");
                } else {
                    Log.w(TAG, "join button is null");
                }
                
                if (joinControlPanel != null) {
                    joinControlPanel.setVisibility(View.GONE);
                    Log.d(TAG, "Set joinControlPanel visibility to GONE");
                } else {
                    Log.w(TAG, "joinControlPanel is null");
                }
                
                // 如果控制面板隐藏，则显示右侧控制按钮面板
                if (controlPanel != null && controlPanel.getVisibility() == View.INVISIBLE && rightControlPanel != null) {
                    rightControlPanel.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Set rightControlPanel visibility to VISIBLE");
                }
            });
        } else {
            Log.d(TAG, "Syncing UI: not joined channel - engine: " + (engine != null) + ", joined: " + joined);
        }
    }
    
    private void applyLanguage(String languageCode) {
        try {
            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);
            
            Resources resources = requireContext().getResources();
            Configuration configuration = new Configuration(resources.getConfiguration());
            configuration.setLocale(locale);
            
            // 更新资源配置
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            
            // 更新Activity的配置
            requireActivity().getResources().updateConfiguration(configuration, 
                requireActivity().getResources().getDisplayMetrics());
            
            // 更新Application的配置
            requireActivity().getApplication().getResources().updateConfiguration(configuration, 
                requireActivity().getApplication().getResources().getDisplayMetrics());
                
            Log.d(TAG, "Language applied: " + languageCode);
        } catch (Exception e) {
            Log.e(TAG, "Error applying language: " + e.getMessage());
        }
    }
    
    private void showFocusIndicator(float x, float y) {
        // 创建对焦指示器（简单的视觉反馈）
        if (fl_local == null) return;
        
        // 移除之前的指示器
        View existingIndicator = fl_local.findViewWithTag("focus_indicator");
        if (existingIndicator != null) {
            fl_local.removeView(existingIndicator);
        }
        
        // 创建新的指示器
        View indicator = new View(requireContext());
        indicator.setTag("focus_indicator");
        indicator.setBackgroundResource(R.drawable.focus_rectangle);
        
        // 设置指示器位置和大小 - 放大3倍
        int baseIndicatorSize = 60;
        int indicatorSize = baseIndicatorSize * 3; // 放大3倍
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(indicatorSize, indicatorSize);
        params.leftMargin = (int) (x - indicatorSize / 2);
        params.topMargin = (int) (y - indicatorSize / 2);
        indicator.setLayoutParams(params);
        
        fl_local.addView(indicator);
        
        // 2秒后自动移除指示器
        handler.postDelayed(() -> {
            if (fl_local != null) {
                fl_local.removeView(indicator);
            }
        }, 2000);
    }

    private void showZoomControlDialog() {
        if (engine == null) return;
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.zoom_control_title));
        
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        
        // 显示当前摄像头类型
        TextView cameraTypeText = new TextView(requireContext());
        String cameraTypeStr = (currentCameraType == CameraType.FRONT) ? getString(R.string.front_camera) : getString(R.string.back_camera);
        cameraTypeText.setText(getString(R.string.current_camera) + ": " + cameraTypeStr);
        cameraTypeText.setTextColor(0xFF000000);
        cameraTypeText.setTextSize(14);
        layout.addView(cameraTypeText);
        
        // 显示缩放范围信息
        TextView zoomRangeText = new TextView(requireContext());
        zoomRangeText.setText(getString(R.string.zoom_range) + ": " + String.format("%.1fx - %.1fx", minZoomFactor, maxZoomFactor));
        zoomRangeText.setTextColor(0xFF000000);
        zoomRangeText.setTextSize(14);
        layout.addView(zoomRangeText);
        
        TextView currentZoomText = new TextView(requireContext());
        currentZoomText.setText(getString(R.string.current_zoom) + ": " + String.format("%.1fx", currentZoomFactor));
        currentZoomText.setTextColor(0xFF000000);
        currentZoomText.setTextSize(16);
        currentZoomText.setPadding(0, 10, 0, 10);
        layout.addView(currentZoomText);
        
        SeekBar zoomSeekBar = new SeekBar(requireContext());
        int maxProgress = (int) ((maxZoomFactor - minZoomFactor) * 10);
        int currentProgress = (int) ((currentZoomFactor - minZoomFactor) * 10);
        zoomSeekBar.setMax(maxProgress);
        zoomSeekBar.setProgress(currentProgress);
        
        zoomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentZoomFactor = minZoomFactor + (progress / 10.0f);
                    currentZoomText.setText("当前缩放: " + String.format("%.1fx", currentZoomFactor));
                    engine.setCameraZoomFactor(currentZoomFactor);
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        layout.addView(zoomSeekBar);
        
        builder.setView(layout);
        builder.setPositiveButton(getString(R.string.ok), null);
        builder.setNegativeButton(getString(R.string.reset), (dialog, which) -> {
            currentZoomFactor = 1.0f;
            engine.setCameraZoomFactor(currentZoomFactor);
        });
        
        builder.show();
    }

    private void showFilterSelectionDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.select_filter_file));
        
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        
        TextView currentFilterText = new TextView(requireContext());
        currentFilterText.setText(getString(R.string.current_filter_path) + ": " + (selectedFilterPath != null ? selectedFilterPath : getString(R.string.none)));
        currentFilterText.setTextColor(0xFF000000);
        currentFilterText.setTextSize(16);
        layout.addView(currentFilterText);
        
        String[] presetFilters = {
            getString(R.string.built_in_whiten_filter_desc),
            getString(R.string.built_in_smooth_filter_desc),
            getString(R.string.grayscale_filter_desc),
            getString(R.string.custom_filter_file_desc)
        };
        
        RadioGroup radioGroup = new RadioGroup(requireContext());
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        
        for (int i = 0; i < presetFilters.length; i++) {
            RadioButton radioButton = new RadioButton(requireContext());
            radioButton.setText(presetFilters[i]);
            radioButton.setId(i);
            radioGroup.addView(radioButton);
        }
        
        layout.addView(radioGroup);
        
        EditText customPathInput = new EditText(requireContext());
        customPathInput.setHint(getString(R.string.enter_custom_filter_path_hint));
        customPathInput.setText(Environment.getExternalStorageDirectory() + "/Android/data/io.agora.api.example.ecomm/files/");
        layout.addView(customPathInput);
        
        Button selectFileButton = new Button(requireContext());
        selectFileButton.setText(getString(R.string.select_file));
        selectFileButton.setTextSize(12);
        selectFileButton.setPadding(20, 10, 20, 10);
        selectFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"*/*"});
            intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.select_filter_file));
            try {
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_filter_file)), REQUEST_CODE_PICK_FILTER_FILE);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(requireContext(), getString(R.string.function_not_available), Toast.LENGTH_SHORT).show();
            }
        });
        layout.addView(selectFileButton);
        
        // 添加滤镜强度调整的SeekBar
        TextView strengthLabel = new TextView(requireContext());
        strengthLabel.setText(getString(R.string.filter_strength) + ": 0.5");
        strengthLabel.setTextColor(0xFF000000);
        strengthLabel.setTextSize(16);
        strengthLabel.setPadding(0, 20, 0, 10);
        layout.addView(strengthLabel);
        
        SeekBar strengthSeekBar = new SeekBar(requireContext());
        strengthSeekBar.setMax(10); // 0-10，对应0.0-1.0，步长0.1
        strengthSeekBar.setProgress(5); // 默认值0.5
        strengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float strength = progress / 10.0f; // 转换为0.0-1.0
                strengthLabel.setText(getString(R.string.filter_strength) + ": " + String.format("%.1f", strength));
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        layout.addView(strengthSeekBar);
        
        builder.setView(layout);
        
        builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            String filterPath = null;
            
            switch (checkedId) {
                case 0:
                    filterPath = "built_in_whiten_filter";
                    break;
                case 1:
                    filterPath = "built_in_smooth_filter";
                    break;
                case 2:
                    filterPath = Environment.getExternalStorageDirectory() + "/Android/data/io.agora.api.example.ecomm/files/grayscale.cube";
                    break;
                case 3:
                    filterPath = customPathInput.getText().toString();
                    break;
            }
            
            if (filterPath != null && !filterPath.isEmpty()) {
                selectedFilterPath = filterPath;
                
                if (!filterPath.startsWith("built_in_")) {
                    java.io.File file = new java.io.File(filterPath);
                    if (!file.exists()) {
                        Toast.makeText(requireContext(), getString(R.string.filter_file_not_exist) + ": " + filterPath, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                
                if (engine != null) {
                    // 获取strength值
                    float strength = strengthSeekBar.getProgress() / 10.0f;
                    
                    filterEffectOptions.path = filterPath;
                    filterEffectOptions.strength = strength;
                    int ret = engine.setFilterEffectOptions(true, filterEffectOptions);
                    if (ret == 0) {
                        Toast.makeText(requireContext(), getString(R.string.filter_apply_success) + ": " + filterPath + " (强度: " + String.format("%.1f", strength) + ")", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.filter_apply_failed) + ": " + ret, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.engine_not_initialized), Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        builder.setNegativeButton(getString(R.string.reset), null);
        builder.show();
    }

    private void showLocalCubeSelectionDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.select_local_cube_file));
        
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        
        TextView currentFilterText = new TextView(requireContext());
        currentFilterText.setText(getString(R.string.current_cube_path) + ": " + (selectedFilterPath != null ? selectedFilterPath : getString(R.string.no_cube_file)));
        currentFilterText.setTextColor(0xFF000000);
        currentFilterText.setTextSize(16);
        layout.addView(currentFilterText);
        
        String[] localCubeFiles = {
            "NaturalBoost - converted with Color.cube",
            "Long Beach Morning - converted with Color.cube",
            "MagicHour - converted with Color.cube",
            "CrispAutumn - converted with Color.cube",
            "SoftBlackAndWhite - converted with Color.cube",
            "Waves - converted with Color.cube",
            "OrangeAndBlue - converted with Color.cube",
            "Landscape - converted with Color.cube",
            "ColdChrome - converted with Color.cube",
            "BlueHour - converted with Color.cube"
        };
        
        RadioGroup radioGroup = new RadioGroup(requireContext());
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        
        for (int i = 0; i < localCubeFiles.length; i++) {
            RadioButton radioButton = new RadioButton(requireContext());
            radioButton.setText(localCubeFiles[i]);
            radioButton.setId(i);
            radioGroup.addView(radioButton);
        }
        
        layout.addView(radioGroup);
        
        // 添加cube强度调整的SeekBar
        TextView strengthLabel = new TextView(requireContext());
        strengthLabel.setText(getString(R.string.filter_strength) + ": 0.5");
        strengthLabel.setTextColor(0xFF000000);
        strengthLabel.setTextSize(16);
        strengthLabel.setPadding(0, 20, 0, 10);
        layout.addView(strengthLabel);
        
        SeekBar strengthSeekBar = new SeekBar(requireContext());
        strengthSeekBar.setMax(10); // 0-10，对应0.0-1.0，步长0.1
        strengthSeekBar.setProgress(5); // 默认值0.5
        strengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float strength = progress / 10.0f; // 转换为0.0-1.0
                strengthLabel.setText(getString(R.string.filter_strength) + ": " + String.format("%.1f", strength));
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        layout.addView(strengthSeekBar);
        
        builder.setView(layout);
        
        builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            if (checkedId >= 0 && checkedId < localCubeFiles.length) {
                String selectedCubeFile = localCubeFiles[checkedId];
                String filterPath = copyAssetCubeToStorage(selectedCubeFile);
                
                if (filterPath != null) {
                    selectedFilterPath = filterPath;
                    
                    if (engine != null) {
                        // 获取strength值
                        float strength = strengthSeekBar.getProgress() / 10.0f;
                        
                        filterEffectOptions.path = filterPath;
                        filterEffectOptions.strength = strength;
                        int ret = engine.setFilterEffectOptions(true, filterEffectOptions);
                        if (ret == 0) {
                            Toast.makeText(requireContext(), getString(R.string.cube_file_selected_desc) + ": " + selectedCubeFile + " (强度: " + String.format("%.1f", strength) + ")", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.filter_apply_failed) + ": " + ret, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.engine_not_initialized), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.function_not_available), Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.show();
    }

    private String copyAssetCubeToStorage(String assetFileName) {
        try {
            java.io.File appDir = new java.io.File(requireContext().getExternalFilesDir(null), "filters");
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
            
            java.io.File destFile = new java.io.File(appDir, assetFileName);
            
            if (destFile.exists()) {
                Log.d(TAG, "Local cube file already exists: " + destFile.getAbsolutePath());
                return destFile.getAbsolutePath();
            }
            
            java.io.InputStream inputStream = requireContext().getAssets().open("lut/" + assetFileName);
            java.io.FileOutputStream outputStream = new java.io.FileOutputStream(destFile);
            
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_shape_beauty_area:
                faceShapeAreaOptions.shapeArea = position - 1;
                updateFaceShapeBeautyAreaOptions();
                return;
            case R.id.spinner_shape_beautify_style:
                faceShapeBeautyOptions.shapeStyle = position;
                updateFaceShapeBeautyStyleOptions();
                return;
            case R.id.spinner_brow_style:
                makeUpOptions.browStyle = position;
                break;
            case R.id.spinner_lash_style:
                makeUpOptions.lashStyle = position;
                break;
            case R.id.spinner_shadow_style:
                makeUpOptions.shadowStyle = position;
                break;
            case R.id.spinner_pupil_style:
                makeUpOptions.pupilStyle = position;
                break;
            case R.id.spinner_blush_style:
                makeUpOptions.blushStyle = position;
                break;
            case R.id.spinner_lip_style:
                makeUpOptions.lipStyle = position;
                break;
            case R.id.spinner_brow_color:
                makeUpOptions.browColor = position;
                break;
            case R.id.spinner_lash_color:
                makeUpOptions.lashColor = position;
                break;
            case R.id.spinner_blush_color:
                makeUpOptions.blushColor = position;
                break;
            case R.id.spinner_lip_color:
                makeUpOptions.lipColor = position;
                break;
        }
        updateExtensionProperty();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // 处理Spinner未选择事件
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == shapeBeauty.getId()) {
            if (isChecked && !engine.isFeatureAvailableOnDevice(Constants.FEATURE_VIDEO_BEAUTY_EFFECT)) {
                buttonView.setChecked(false);
                Toast.makeText(requireContext(), "功能不可用", Toast.LENGTH_SHORT).show();
                return;
            }
            // 脸型美化可以单独开启，无需依赖面部美化
            updateFaceShapeBeautyStyleOptions();
        } else if (buttonView.getId() == makeUp.getId()) {
            if (isChecked && !engine.isFeatureAvailableOnDevice(Constants.FEATURE_VIDEO_BEAUTY_EFFECT)) {
                buttonView.setChecked(false);
                Toast.makeText(requireContext(), "功能不可用", Toast.LENGTH_SHORT).show();
                return;
            }
            makeUpOptions.enable_mu = isChecked;
            updateExtensionProperty();
        } else if (buttonView.getId() == beauty.getId()) {
            if (isChecked && !engine.isFeatureAvailableOnDevice(Constants.FEATURE_VIDEO_BEAUTY_EFFECT)) {
                buttonView.setChecked(false);
                Toast.makeText(requireContext(), "功能不可用", Toast.LENGTH_SHORT).show();
                return;
            }
            engine.setBeautyEffectOptions(isChecked, beautyOptions);
        } else if (buttonView.getId() == lightness2.getId()) {
            LowLightEnhanceOptions options = new LowLightEnhanceOptions();
            options.lowlightEnhanceLevel = LowLightEnhanceOptions.LOW_LIGHT_ENHANCE_LEVEL_FAST;
            options.lowlightEnhanceMode = LowLightEnhanceOptions.LOW_LIGHT_ENHANCE_AUTO;
            engine.setLowlightEnhanceOptions(isChecked, options);
        } else if (buttonView.getId() == colorful2.getId()) {
            setColorEnhance(isChecked);
        } else if (buttonView.getId() == virtualBackground.getId()) {
            if (isChecked && !engine.isFeatureAvailableOnDevice(Constants.FEATURE_VIDEO_VIRTUAL_BACKGROUND)) {
                buttonView.setChecked(false);
                Toast.makeText(requireContext(), "功能不可用", Toast.LENGTH_SHORT).show();
                return;
            }
            resetVirtualBackground();
        } else if (buttonView.getId() == noiseReduce2.getId()) {
            VideoDenoiserOptions options = new VideoDenoiserOptions();
            options.denoiserLevel = VideoDenoiserOptions.VIDEO_DENOISER_LEVEL_HIGH_QUALITY;
            options.denoiserMode = VideoDenoiserOptions.VIDEO_DENOISER_AUTO;
            engine.setVideoDenoiserOptions(isChecked, options);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float value = ((float) progress) / 10;
        if (seekBar.getId() == sbShapeBeautifyAreaIntensity.getId()) {
            faceShapeAreaOptions.shapeIntensity = progress;
            updateFaceShapeBeautyAreaOptions();
        } else if (seekBar.getId() == sbShapeBeautifyStyleIntensity.getId()) {
            faceShapeBeautyOptions.styleIntensity = progress;
            updateFaceShapeBeautyStyleOptions();
        } else if (seekBar.getId() == sbBrowStrength.getId()) {
            makeUpOptions.browStrength = value;
            updateExtensionProperty();
        } else if (seekBar.getId() == sbLashStrength.getId()) {
            makeUpOptions.lashStrength = value;
            updateExtensionProperty();
        } else if (seekBar.getId() == sbShadowStrength.getId()) {
            makeUpOptions.shadowStrength = value;
            updateExtensionProperty();
        } else if (seekBar.getId() == sbPupilStrength.getId()) {
            makeUpOptions.pupilStrength = value;
            updateExtensionProperty();
        } else if (seekBar.getId() == sbBlushStrength.getId()) {
            makeUpOptions.blushStrength = value;
            updateExtensionProperty();
        } else if (seekBar.getId() == sbLipStrength.getId()) {
            makeUpOptions.lipStrength = value;
            updateExtensionProperty();
        } else if (seekBar.getId() == seek_lightness.getId()) {
            beautyOptions.lighteningLevel = value;
            engine.setBeautyEffectOptions(beauty.isChecked(), beautyOptions);
        } else if (seekBar.getId() == seek_redness.getId()) {
            beautyOptions.rednessLevel = value;
            engine.setBeautyEffectOptions(beauty.isChecked(), beautyOptions);
        } else if (seekBar.getId() == seek_sharpness.getId()) {
            beautyOptions.sharpnessLevel = value;
            engine.setBeautyEffectOptions(beauty.isChecked(), beautyOptions);
        } else if (seekBar.getId() == seek_videoEnhance.getId()) {
            filterEffectOptions.strength = value;
            filterEffectOptions.path = getFilterPath();
            Log.d(TAG, "Filter path: " + filterEffectOptions.path);

            if (engine != null && beauty != null && beauty.isChecked()) {
                int ret = engine.setFilterEffectOptions(true, filterEffectOptions);
                Log.d(TAG, "onProgressChanged: ret=" + ret);

                if (ret != 0) {
                    Log.e(TAG, "Failed to set filter effect, error code: " + ret);
                    filterEffectOptions.path = "built_in_whiten_filter";
                    ret = engine.setFilterEffectOptions(true, filterEffectOptions);
                    Log.d(TAG, "Fallback to built-in filter, ret=" + ret);
                }
            }
        } else if (seekBar.getId() == seek_smoothness.getId()) {
            beautyOptions.smoothnessLevel = value;
            engine.setBeautyEffectOptions(beauty.isChecked(), beautyOptions);
        } else if (seekBar.getId() == seek_strength.getId()) {
            strength = value;
            setColorEnhance(colorful2.isChecked());
        } else if (seekBar.getId() == seek_skin.getId()) {
            skinProtect = value;
            setColorEnhance(colorful2.isChecked());
        }
    }

    private void updateFaceShapeBeautyAreaOptions() {
        if (engine != null) {
            engine.setFaceShapeAreaOptions(faceShapeAreaOptions);
        }
    }

    private void updateFaceShapeBeautyStyleOptions() {
        if (engine != null) {
            engine.setFaceShapeBeautyOptions(shapeBeauty.isChecked(), faceShapeBeautyOptions);
        }
    }

    private void updateExtensionProperty() {
        if (engine != null) {
            engine.setExtensionProperty("agora_video_filters_clear_vision", "clear_vision", "makeup_options", makeUpOptions.toJson(), Constants.MediaSourceType.PRIMARY_CAMERA_SOURCE);
        }
    }

    private void setColorEnhance(boolean isChecked) {
        ColorEnhanceOptions options = new ColorEnhanceOptions();
        options.strengthLevel = (float) strength;
        options.skinProtectLevel = (float) skinProtect;
        engine.setColorEnhanceOptions(isChecked, options);
        Log.d(TAG, "Color enhance: " + isChecked + ", strength: " + strength + ", skinProtect: " + skinProtect);
    }

    private void resetVirtualBackground() {
        if (virtualBackground.isChecked()) {
            int checkedId = virtualBgType.getCheckedRadioButtonId();
            VirtualBackgroundSource backgroundSource = new VirtualBackgroundSource();
            SegmentationProperty segproperty = new SegmentationProperty();
            if (checkedId == R.id.virtual_bg_image) {
                backgroundSource.backgroundSourceType = VirtualBackgroundSource.BACKGROUND_IMG;
                String imagePath = requireContext().getExternalCacheDir().getPath();
                String imageName = "agora-logo.png";
                backgroundSource.source = imagePath + "/" + imageName;
            } else if (checkedId == R.id.virtual_bg_color) {
                backgroundSource.backgroundSourceType = VirtualBackgroundSource.BACKGROUND_COLOR;
                backgroundSource.color = 0x0000EE;
            } else if (checkedId == R.id.virtual_bg_blur) {
                backgroundSource.backgroundSourceType = VirtualBackgroundSource.BACKGROUND_BLUR;
                backgroundSource.blurDegree = VirtualBackgroundSource.BLUR_DEGREE_MEDIUM;
            } else if (checkedId == R.id.virtual_bg_video) {
                backgroundSource.backgroundSourceType = VirtualBackgroundSource.BACKGROUND_VIDEO;
                backgroundSource.source = "https://agora-adc-artifacts.s3.cn-north-1.amazonaws.com.cn/resources/sample.mp4";
            }
            engine.enableVirtualBackground(true, backgroundSource, segproperty);
        } else {
            engine.enableVirtualBackground(false, null, null);
        }
    }

    private String getFilterPath() {
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
                java.io.File file = new java.io.File(path);
                if (file.exists()) {
                    Log.d(TAG, "Found filter file: " + path);
                    return path;
                }
            }
        }

        Log.d(TAG, "No custom filter found, using built-in filter");
        return "built_in_whiten_filter";
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // 开始拖动进度条
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // 停止拖动进度条
    }

    private final IRtcEngineEventHandler iRtcEngineEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onError(int err) {
            Log.w(TAG, String.format("onError code %d message %s", err, RtcEngine.getErrorDescription(err)));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            Log.i(TAG, String.format("onJoinChannelSuccess channel %s uid %d", channel, uid));
            myUid = uid;
            joined = true;
            
            handler.post(() -> {
                if (join != null) {
                    join.setEnabled(true);
                    join.setText("离开");
                    Log.d(TAG, "Updated join button text to '离开' in onJoinChannelSuccess");
                } else {
                    Log.w(TAG, "join button is null in onJoinChannelSuccess");
                }
                
                if (joinControlPanel != null) {
                    Log.d(TAG, "joinControlPanel current visibility: " + joinControlPanel.getVisibility());
                    joinControlPanel.setVisibility(View.GONE);
                    Log.d(TAG, "Set joinControlPanel visibility to GONE in onJoinChannelSuccess");
                    
                    // 强制刷新UI
                    joinControlPanel.invalidate();
                    joinControlPanel.requestLayout();
                    
                    // 延迟检查是否真的隐藏了
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
                    Log.w(TAG, "joinControlPanel is null in onJoinChannelSuccess");
                }
                
                // 如果控制面板隐藏，则显示右侧控制按钮面板
                if (controlPanel != null && controlPanel.getVisibility() == View.INVISIBLE && rightControlPanel != null) {
                    rightControlPanel.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Set rightControlPanel visibility to VISIBLE in onJoinChannelSuccess");
                }
            });
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            Log.i(TAG, "onUserJoined->" + uid);
            
            handler.post(() -> {
                if (fl_remote != null && fl_remote.getChildCount() > 0) {
                    fl_remote.removeAllViews();
                }
                
                Context context = getContext();
                if (context != null && fl_remote != null) {
                    SurfaceView surfaceView = new SurfaceView(context);
                    surfaceView.setZOrderMediaOverlay(true);
                    
                    int screenWidth = getResources().getDisplayMetrics().widthPixels;
                    int remoteWidth = screenWidth / 4;
                    int remoteHeight = (int) (remoteWidth * 16.0 / 9.0);
                    
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(remoteWidth, remoteHeight);
                    params.leftMargin = 16;
                    params.topMargin = 16;
                    fl_remote.addView(surfaceView, params);
                    
                    if (engine != null) {
                        engine.setupRemoteVideo(new VideoCanvas(surfaceView, RENDER_MODE_HIDDEN, uid));
                    }
                }
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            Log.i(TAG, String.format("user %d offline! reason:%d", uid, reason));
            handler.post(() -> {
                if (engine != null) {
                    engine.setupRemoteVideo(new VideoCanvas(null, RENDER_MODE_HIDDEN, uid));
                }
            });
        }
    };
} 