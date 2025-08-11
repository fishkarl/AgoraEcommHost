package io.agora.api.example.examples.advanced.features;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoEncoderConfiguration;

import static io.agora.rtc2.video.VideoEncoderConfiguration.STANDARD_BITRATE;

/**
 * RTC Engine Manager
 * Responsible for managing RTC engine initialization, configuration and state
 */
public class RtcEngineManager {
    private static final String TAG = "RtcEngineManager";
    
    private final Context context;
    private RtcEngine engine;
    private boolean isInitialized = false;
    private int myUid = 0;
    private boolean isJoined = false;
    
    // Static variables to save Engine instance and state
    private static RtcEngine staticEngine = null;
    private static boolean engineInitialized = false;
    private static boolean staticJoined = false;
    private static int staticMyUid = 0;
    
    public interface EngineCallback {
        void onEngineInitialized(RtcEngine engine);
        void onEngineError(String error);
    }
    
    public RtcEngineManager(@NonNull Context context) {
        this.context = context;
    }
    
    /**
     * Initialize RTC engine
     */
    public void initializeEngine(IRtcEngineEventHandler eventHandler, EngineCallback callback) {
        try {
            // First try to restore saved Engine instance
            restoreEngine();
            
            // If Engine exists and is valid, use it directly
            if (engine != null && engineInitialized) {
                Log.d(TAG, "Using existing RTC Engine instance");
                if (callback != null) {
                    callback.onEngineInitialized(engine);
                }
                return;
            }
            
            // If engine already exists, destroy it first
            if (engine != null) {
                Log.d(TAG, "Destroying existing RTC Engine before reinitializing");
                engine.leaveChannel();
                RtcEngine.destroy();
                engine = null;
            }
            
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = context.getApplicationContext();
            config.mAppId = context.getString(io.agora.api.example.ecomm.R.string.agora_app_id);
            config.mChannelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
            config.mEventHandler = eventHandler;
            
            engine = RtcEngine.create(config);
            Log.d(TAG, "RTC Engine initialized successfully");
            
            // Save Engine instance
            preserveEngine();
            
            // Enable video filter extension
            engine.enableExtension("agora_video_filters_clear_vision", "clear_vision", true);
            
            if (callback != null) {
                callback.onEngineInitialized(engine);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize RTC Engine: " + e.getMessage());
            if (callback != null) {
                callback.onEngineError("Failed to initialize RTC Engine: " + e.getMessage());
            }
        }
    }
    
    /**
     * Join channel
     */
    public void joinChannel(String channelId, String token) {
        if (engine == null) {
            Log.e(TAG, "Engine is null, cannot join channel");
            return;
        }

        Log.d(TAG, "Joining channel: " + channelId);

                    // Verify engine status
        try {
            // 尝试调用一个简单的API来验证engine是否正常工作
            engine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            Log.d(TAG, "Engine is responsive");
        } catch (Exception e) {
            Log.e(TAG, "Engine is not responsive: " + e.getMessage());
            return;
        }

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

        io.agora.rtc2.ChannelMediaOptions option = new io.agora.rtc2.ChannelMediaOptions();
        option.autoSubscribeAudio = true;
        option.autoSubscribeVideo = true;
        option.publishMicrophoneTrack = true;
        option.publishCameraTrack = true;
        
        Log.d(TAG, "About to call engine.joinChannel with channelId: " + channelId);
        int res = engine.joinChannel(token, channelId, 0, option);
        Log.d(TAG, "joinChannel result: " + res);
        if (res != 0) {
            Log.e(TAG, "Failed to join channel: " + res + ", error description: " + RtcEngine.getErrorDescription(res));
        }
    }
    
    /**
     * 离开频道
     */
    public void leaveChannel() {
        if (engine != null) {
            engine.leaveChannel();
            isJoined = false;
            Log.d(TAG, "Left channel");
        }
    }
    
    /**
     * 销毁引擎
     */
    public void destroyEngine() {
        if (engine != null) {
            engine.leaveChannel();
            RtcEngine.destroy();
            engine = null;
            staticEngine = null;
            engineInitialized = false;
            staticJoined = false;
            staticMyUid = 0;
            isInitialized = false;
            Log.d(TAG, "RTC Engine destroyed");
        }
    }
    
    /**
     * 保存Engine实例和状态到静态变量
     */
    private void preserveEngine() {
        if (engine != null) {
            staticEngine = engine;
            engineInitialized = true;
            staticJoined = isJoined;
            staticMyUid = myUid;
            Log.d(TAG, "Engine and state preserved to static variable - joined: " + isJoined + ", uid: " + myUid);
        }
    }
    
    /**
     * 从静态变量恢复Engine实例和状态
     */
    private void restoreEngine() {
        if (staticEngine != null && engineInitialized) {
            engine = staticEngine;
            isJoined = staticJoined;
            myUid = staticMyUid;
            Log.d(TAG, "Engine and state restored from static variable - joined: " + isJoined + ", uid: " + myUid);
        }
    }
    
    /**
     * 获取RTC引擎实例
     */
    public RtcEngine getEngine() {
        return engine;
    }
    
    /**
     * 检查引擎是否已初始化
     */
    public boolean isInitialized() {
        return isInitialized && engine != null;
    }
    
    /**
     * 设置用户ID
     */
    public void setMyUid(int uid) {
        this.myUid = uid;
        staticMyUid = uid;
    }
    
    /**
     * 获取用户ID
     */
    public int getMyUid() {
        return myUid;
    }
    
    /**
     * 设置加入状态
     */
    public void setJoined(boolean joined) {
        this.isJoined = joined;
        staticJoined = joined;
    }
    
    /**
     * 检查是否已加入频道
     */
    public boolean isJoined() {
        return isJoined;
    }
    
    /**
     * 切换摄像头
     */
    public void switchCamera() {
        if (engine != null) {
            engine.switchCamera();
        }
    }
    
    /**
     * 设置摄像头缩放因子
     */
    public void setCameraZoomFactor(float zoomFactor) {
        if (engine != null) {
            engine.setCameraZoomFactor(zoomFactor);
        }
    }
    
    /**
     * 获取摄像头最大缩放因子
     */
    public float getCameraMaxZoomFactor() {
        if (engine != null) {
            return engine.getCameraMaxZoomFactor();
        }
        return 1.0f;
    }
    
    /**
     * 设置摄像头自动对焦模式
     */
    public void setCameraAutoFocusFaceModeEnabled(boolean enabled) {
        if (engine != null) {
            engine.setCameraAutoFocusFaceModeEnabled(enabled);
        }
    }
    
    /**
     * 设置摄像头对焦位置
     */
    public void setCameraFocusPositionInPreview(float x, float y) {
        if (engine != null) {
            engine.setCameraFocusPositionInPreview(x, y);
        }
    }
    
    /**
     * 设置本地视频
     */
    public void setupLocalVideo(io.agora.rtc2.video.VideoCanvas videoCanvas) {
        if (engine != null) {
            engine.setupLocalVideo(videoCanvas);
        }
    }
    
    /**
     * 设置远程视频
     */
    public void setupRemoteVideo(io.agora.rtc2.video.VideoCanvas videoCanvas) {
        if (engine != null) {
            engine.setupRemoteVideo(videoCanvas);
        }
    }
    
    /**
     * 开始预览
     */
    public void startPreview() {
        if (engine != null) {
            engine.startPreview();
        }
    }
    
    /**
     * 停止预览
     */
    public void stopPreview() {
        if (engine != null) {
            engine.stopPreview();
        }
    }
    
    /**
     * 检查功能是否可用
     */
    public boolean isFeatureAvailableOnDevice(int feature) {
        if (engine != null) {
            return engine.isFeatureAvailableOnDevice(feature);
        }
        return false;
    }
}
