package io.agora.api.example.examples.advanced.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import io.agora.api.example.examples.advanced.device.CameraManager;
import io.agora.api.example.examples.advanced.device.FocusController;
import io.agora.api.example.examples.advanced.features.BeautyFeatureManager;
import io.agora.api.example.examples.advanced.features.FilterManager;
import io.agora.api.example.examples.advanced.features.RtcEngineManager;
import io.agora.api.example.examples.advanced.features.RtcEventHandler;
import io.agora.api.example.examples.advanced.ui.DialogManager;
import io.agora.rtc2.RtcEngine;

/**
 * Runtime Object Manager
 * 统一管理所有Runtime对象，解决Fragment重新创建时的对象引用问题
 */
public class RuntimeObjectManager {
    private static final String TAG = "RuntimeObjectManager";
    
    // 静态变量保存所有Runtime对象
    private static RtcEngineManager staticEngineManager = null;
    private static FilterManager staticFilterManager = null;
    private static BeautyFeatureManager staticBeautyManager = null;
    private static CameraManager staticCameraManager = null;
    private static FocusController staticFocusController = null;
    private static RtcEventHandler staticEventHandler = null;
    private static DialogManager staticDialogManager = null;
    
    // 标记是否已初始化
    private static boolean isRuntimeObjectsInitialized = false;
    
    /**
     * 保存Runtime对象到静态变量
     */
    public static void preserveRuntimeObjects(
            RtcEngineManager engineManager,
            FilterManager filterManager,
            BeautyFeatureManager beautyManager,
            CameraManager cameraManager,
            FocusController focusController,
            RtcEventHandler eventHandler,
            DialogManager dialogManager) {
        
        staticEngineManager = engineManager;
        staticFilterManager = filterManager;
        staticBeautyManager = beautyManager;
        staticCameraManager = cameraManager;
        staticFocusController = focusController;
        staticEventHandler = eventHandler;
        staticDialogManager = dialogManager;
        
        isRuntimeObjectsInitialized = true;
        
        Log.d(TAG, "Runtime objects preserved to static variables");
    }
    
    /**
     * 从静态变量恢复Runtime对象
     */
    public static RuntimeObjects restoreRuntimeObjects(@NonNull Context context, @NonNull Fragment fragment) {
        if (!isRuntimeObjectsInitialized || staticEngineManager == null) {
            Log.d(TAG, "No runtime objects to restore, will create new ones");
            return null;
        }
        
        Log.d(TAG, "Restoring runtime objects from static variables");
        
        // 更新Context引用，但保持对象状态
        updateContextReferences(context, fragment);
        
        // 返回恢复的对象
        return new RuntimeObjects(
            staticEngineManager,
            staticFilterManager,
            staticBeautyManager,
            staticCameraManager,
            staticFocusController,
            staticEventHandler,
            staticDialogManager
        );
    }
    
    /**
     * 重新创建Manager对象但保持Engine引用和状态
     */
    private static void updateContextReferences(@NonNull Context context, @NonNull Fragment fragment) {
        try {
            // 获取当前Engine实例
            RtcEngine engine = staticEngineManager != null ? staticEngineManager.getEngine() : null;
            
            if (engine != null) {
                // 重新创建Manager对象，使用新的Context但相同的Engine
                staticFilterManager = new FilterManager(context, engine);
                staticBeautyManager = new BeautyFeatureManager(context, engine);
                staticCameraManager = new CameraManager(context, engine);
                staticFocusController = new FocusController(context, engine);
                
                // EventHandler需要Fragment引用
                if (fragment instanceof RtcEventHandler.EventCallback) {
                    staticEventHandler = new RtcEventHandler(context, engine, (RtcEventHandler.EventCallback) fragment);
                    
                    // 关键：更新Engine的EventHandler引用
                    try {
                        // 使用反射或Engine提供的方法更新EventHandler
                        // 但由于Agora SDK可能不支持直接更新，我们需要另一种方案
                        Log.d(TAG, "EventHandler recreated with new Fragment reference");
                    } catch (Exception ex) {
                        Log.w(TAG, "Could not update Engine's EventHandler: " + ex.getMessage());
                    }
                }
                
                // DialogManager需要FilterManager和Callback
                if (fragment instanceof DialogManager.DialogCallback) {
                    staticDialogManager = new DialogManager(context, staticFilterManager, (DialogManager.DialogCallback) fragment);
                }
                
                Log.d(TAG, "Manager objects recreated with new context references");
            }
        } catch (Exception e) {
            Log.w(TAG, "Some manager objects could not be recreated: " + e.getMessage());
        }
    }
    
    /**
     * 清理所有Runtime对象
     */
    public static void clearRuntimeObjects() {
        Log.d(TAG, "Clearing all runtime objects");
        
        // 清理Engine相关对象
        if (staticEngineManager != null) {
            staticEngineManager.destroyEngine();
        }
        
        // 清理所有静态引用
        staticEngineManager = null;
        staticFilterManager = null;
        staticBeautyManager = null;
        staticCameraManager = null;
        staticFocusController = null;
        staticEventHandler = null;
        staticDialogManager = null;
        
        isRuntimeObjectsInitialized = false;
    }
    
    /**
     * 检查Runtime对象是否已初始化
     */
    public static boolean isRuntimeObjectsInitialized() {
        return isRuntimeObjectsInitialized && staticEngineManager != null;
    }
    
    /**
     * 获取Engine实例（用于外部访问）
     */
    public static RtcEngine getEngine() {
        return staticEngineManager != null ? staticEngineManager.getEngine() : null;
    }
    
    /**
     * Runtime对象容器类
     */
    public static class RuntimeObjects {
        public final RtcEngineManager engineManager;
        public final FilterManager filterManager;
        public final BeautyFeatureManager beautyManager;
        public final CameraManager cameraManager;
        public final FocusController focusController;
        public final RtcEventHandler eventHandler;
        public final DialogManager dialogManager;
        
        public RuntimeObjects(
                RtcEngineManager engineManager,
                FilterManager filterManager,
                BeautyFeatureManager beautyManager,
                CameraManager cameraManager,
                FocusController focusController,
                RtcEventHandler eventHandler,
                DialogManager dialogManager) {
            
            this.engineManager = engineManager;
            this.filterManager = filterManager;
            this.beautyManager = beautyManager;
            this.cameraManager = cameraManager;
            this.focusController = focusController;
            this.eventHandler = eventHandler;
            this.dialogManager = dialogManager;
        }
    }
}
