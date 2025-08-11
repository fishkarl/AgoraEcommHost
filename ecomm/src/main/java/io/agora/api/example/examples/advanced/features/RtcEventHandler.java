package io.agora.api.example.examples.advanced.features;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.video.VideoCanvas;

import static io.agora.rtc2.Constants.RENDER_MODE_HIDDEN;

/**
 * RTC Engine Event Handler
 * Responsible for handling all RTC engine callback events
 */
public class RtcEventHandler extends IRtcEngineEventHandler {
    private static final String TAG = "RtcEventHandler";
    
    private final Handler handler;
    private final Context context;
    private final RtcEngine engine;
    private final EventCallback callback;
    
    public interface EventCallback {
        void onJoinChannelSuccess(int uid);
        void onUserJoined(int uid);
        void onUserOffline(int uid, int reason);
        void onError(int err);
        FrameLayout getLocalVideoContainer();
        FrameLayout getRemoteVideoContainer();
    }
    
    public RtcEventHandler(@NonNull Context context, @NonNull RtcEngine engine, @NonNull EventCallback callback) {
        this.context = context;
        this.engine = engine;
        this.callback = callback;
        this.handler = new Handler(Looper.getMainLooper());
    }
    
    @Override
    public void onError(int err) {
        Log.w(TAG, String.format("onError code %d message %s", err, RtcEngine.getErrorDescription(err)));
        if (callback != null) {
            callback.onError(err);
        }
    }
    
    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.i(TAG, String.format("onJoinChannelSuccess channel %s uid %d", channel, uid));
        
        if (callback != null) {
            callback.onJoinChannelSuccess(uid);
        }
    }
    
    @Override
    public void onUserJoined(int uid, int elapsed) {
        super.onUserJoined(uid, elapsed);
        Log.i(TAG, "onUserJoined->" + uid);
        
        handler.post(() -> {
            if (callback == null) return;
            
            FrameLayout remoteContainer = callback.getRemoteVideoContainer();
            if (remoteContainer == null) return;
            
            // Clear previous remote video view
            if (remoteContainer.getChildCount() > 0) {
                remoteContainer.removeAllViews();
            }
            
            // Create new remote video view
            SurfaceView surfaceView = new SurfaceView(context);
            surfaceView.setZOrderMediaOverlay(true);
            
            // Calculate remote video window size
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int remoteWidth = screenWidth / 4;
            int remoteHeight = (int) (remoteWidth * 16.0 / 9.0);
            
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(remoteWidth, remoteHeight);
            params.leftMargin = 16;
            params.topMargin = 16;
            remoteContainer.addView(surfaceView, params);
            
            // Setup remote video
            if (engine != null) {
                engine.setupRemoteVideo(new VideoCanvas(surfaceView, RENDER_MODE_HIDDEN, uid));
            }
            
            if (callback != null) {
                callback.onUserJoined(uid);
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
            
            if (callback != null) {
                callback.onUserOffline(uid, reason);
            }
        });
    }
}
