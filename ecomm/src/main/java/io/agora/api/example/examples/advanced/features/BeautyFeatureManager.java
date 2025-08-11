package io.agora.api.example.examples.advanced.features;

import android.content.Context;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;

import io.agora.rtc2.Constants;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.video.BeautyOptions;
import io.agora.rtc2.video.ColorEnhanceOptions;
import io.agora.rtc2.video.FaceShapeBeautyOptions;
import io.agora.rtc2.video.FaceShapeAreaOptions;
import io.agora.rtc2.video.LowLightEnhanceOptions;
import io.agora.rtc2.video.SegmentationProperty;
import io.agora.rtc2.video.VideoDenoiserOptions;
import io.agora.rtc2.video.VirtualBackgroundSource;
import io.agora.api.example.bean.MpOptions;

/**
 * 美颜功能管理器
 * 负责美颜、脸型美化、化妆等功能
 */
public class BeautyFeatureManager {
    private static final String TAG = "BeautyFeatureManager";
    
    private final Context context;
    private RtcEngine engine;
    private BeautyOptions beautyOptions;
    private FaceShapeBeautyOptions faceShapeBeautyOptions;
    private FaceShapeAreaOptions faceShapeAreaOptions;
    private MpOptions makeUpOptions;
    private double skinProtect = 1.0;
    private double strength = 0.5;
    
    public BeautyFeatureManager(@NonNull Context context, RtcEngine engine) {
        this.context = context;
        this.engine = engine;
        this.beautyOptions = new BeautyOptions();
        this.faceShapeBeautyOptions = new FaceShapeBeautyOptions();
        this.faceShapeAreaOptions = new FaceShapeAreaOptions();
        this.makeUpOptions = new MpOptions();
    }
    
    /**
     * 设置UI组件引用
     */
    public void setUIComponents(Switch beautySwitch, Switch shapeBeautySwitch, Switch makeUpSwitch,
                               SeekBar lightnessSeekBar, SeekBar rednessSeekBar, SeekBar sharpnessSeekBar,
                               SeekBar smoothnessSeekBar, SeekBar skinProtectSeekBar) {
        // 这个方法在新架构中不再需要，因为UI组件直接在主Fragment中管理
    }
    
    /**
     * 启用/禁用基础美颜
     */
    public void setBeautyEnabled(boolean enabled) {
        if (engine == null) return;
        
        try {
            engine.setBeautyEffectOptions(enabled, beautyOptions);
            Log.d(TAG, "Beauty effect " + (enabled ? "enabled" : "disabled"));
        } catch (Exception e) {
            Log.e(TAG, "Error setting beauty effect: " + e.getMessage());
        }
    }
    
    /**
     * 启用/禁用脸型美化
     */
    public void setFaceShapeBeautyEnabled(boolean enabled) {
        if (engine == null) return;
        
        try {
            engine.setFaceShapeBeautyOptions(enabled, faceShapeBeautyOptions);
            Log.d(TAG, "Face shape beauty " + (enabled ? "enabled" : "disabled"));
        } catch (Exception e) {
            Log.e(TAG, "Error setting face shape beauty: " + e.getMessage());
        }
    }
    
    /**
     * 启用/禁用面部化妆
     */
    public void setMakeUpEnabled(boolean enabled) {
        if (engine == null) return;
        
        try {
            makeUpOptions.enable_mu = enabled;
            engine.setExtensionProperty("agora_video_filters_clear_vision", "clear_vision", 
                "makeup_options", makeUpOptions.toJson(), 
                io.agora.rtc2.Constants.MediaSourceType.PRIMARY_CAMERA_SOURCE);
            Log.d(TAG, "Makeup " + (enabled ? "enabled" : "disabled"));
        } catch (Exception e) {
            Log.e(TAG, "Error setting makeup: " + e.getMessage());
        }
    }
    
    /**
     * 更新美颜参数
     */
    public void updateBeautyOptions(float lightness, float redness, float sharpness, float smoothness) {
        if (engine == null) return;
        
        try {
            beautyOptions.lighteningLevel = lightness;
            beautyOptions.rednessLevel = redness;
            beautyOptions.sharpnessLevel = sharpness;
            beautyOptions.smoothnessLevel = smoothness;
            
                engine.setBeautyEffectOptions(true, beautyOptions);
            
            Log.d(TAG, "Beauty options updated: lightness=" + lightness + ", redness=" + redness + 
                ", sharpness=" + sharpness + ", smoothness=" + smoothness);
        } catch (Exception e) {
            Log.e(TAG, "Error updating beauty options: " + e.getMessage());
        }
    }
    
    /**
     * 更新脸型美化参数
     */
    public void updateFaceShapeBeautyOptions(int shapeStyle, int styleIntensity) {
        if (engine == null) return;
        
        try {
            faceShapeBeautyOptions.shapeStyle = shapeStyle;
            faceShapeBeautyOptions.styleIntensity = styleIntensity;
            engine.setFaceShapeBeautyOptions(true, faceShapeBeautyOptions);
            
            Log.d(TAG, "Face shape beauty options updated: style=" + shapeStyle + ", intensity=" + styleIntensity);
        } catch (Exception e) {
            Log.e(TAG, "Error updating face shape beauty options: " + e.getMessage());
        }
    }
    
    /**
     * 更新脸型美化区域参数
     */
    public void updateFaceShapeAreaOptions(int shapeArea, int areaIntensity) {
        if (engine == null) return;
        
        try {
            faceShapeAreaOptions.shapeArea = shapeArea;
            faceShapeAreaOptions.shapeIntensity = areaIntensity;
            engine.setFaceShapeAreaOptions(faceShapeAreaOptions);
            
            Log.d(TAG, "Face shape area options updated: area=" + shapeArea + ", intensity=" + areaIntensity);
        } catch (Exception e) {
            Log.e(TAG, "Error updating face shape area options: " + e.getMessage());
        }
    }
    
    /**
     * 更新化妆参数
     */
    public void updateMakeUpOptions(MpOptions options) {
        if (engine == null) return;
        
        try {
            this.makeUpOptions = options;
            
                engine.setExtensionProperty("agora_video_filters_clear_vision", "clear_vision", 
                    "makeup_options", makeUpOptions.toJson(), 
                    io.agora.rtc2.Constants.MediaSourceType.PRIMARY_CAMERA_SOURCE);
            
            Log.d(TAG, "Makeup options updated");
        } catch (Exception e) {
            Log.e(TAG, "Error updating makeup options: " + e.getMessage());
        }
    }
    
    /**
     * 停止所有美颜功能
     */
    public void stopAllBeautyFeatures() {
        if (engine == null) return;
        
        try {
            // 停止基础美颜
                engine.setBeautyEffectOptions(false, beautyOptions);
            
            // 停止脸型美化
                engine.setFaceShapeBeautyOptions(false, faceShapeBeautyOptions);
            
            // 停止面部化妆
                engine.setExtensionProperty("agora-video-filter-extension", "enable_makeup", "false", "");
            
            Log.d(TAG, "All beauty features stopped");
        } catch (Exception e) {
            Log.e(TAG, "Error stopping beauty features: " + e.getMessage());
        }
    }
    
    /**
     * 更新引擎实例
     */
    public void updateEngine(RtcEngine newEngine) {
        this.engine = newEngine;
    }
    
    /**
     * 释放资源
     */
    public void release() {
        engine = null;
        beautyOptions = null;
        faceShapeBeautyOptions = null;
        faceShapeAreaOptions = null;
        makeUpOptions = null;
    }
    
    /**
     * 处理Spinner选择事件
     */
    public void handleSpinnerSelection(AdapterView<?> parent, int position, 
                                     MpOptions makeUpOptions, 
                                     FaceShapeAreaOptions faceShapeAreaOptions,
                                     FaceShapeBeautyOptions faceShapeBeautyOptions) {
        if (engine == null) return;
        
        try {
            switch (parent.getId()) {
                case io.agora.api.example.ecomm.R.id.spinner_shape_beauty_area:
                    this.faceShapeAreaOptions.shapeArea = position - 1;
                    engine.setFaceShapeAreaOptions(this.faceShapeAreaOptions);
                    break;
                case io.agora.api.example.ecomm.R.id.spinner_shape_beautify_style:
                    this.faceShapeBeautyOptions.shapeStyle = position;
                    engine.setFaceShapeBeautyOptions(true, this.faceShapeBeautyOptions);
                    break;
                case io.agora.api.example.ecomm.R.id.spinner_brow_style:
                    this.makeUpOptions.browStyle = position;
                    break;
                case io.agora.api.example.ecomm.R.id.spinner_lash_style:
                    this.makeUpOptions.lashStyle = position;
                    break;
                case io.agora.api.example.ecomm.R.id.spinner_shadow_style:
                    this.makeUpOptions.shadowStyle = position;
                    break;
                case io.agora.api.example.ecomm.R.id.spinner_pupil_style:
                    this.makeUpOptions.pupilStyle = position;
                    break;
                case io.agora.api.example.ecomm.R.id.spinner_blush_style:
                    this.makeUpOptions.blushStyle = position;
                    break;
                case io.agora.api.example.ecomm.R.id.spinner_lip_style:
                    this.makeUpOptions.lipStyle = position;
                    break;
                case io.agora.api.example.ecomm.R.id.spinner_brow_color:
                    this.makeUpOptions.browColor = position;
                    break;
                case io.agora.api.example.ecomm.R.id.spinner_lash_color:
                    this.makeUpOptions.lashColor = position;
                    break;
                case io.agora.api.example.ecomm.R.id.spinner_blush_color:
                    this.makeUpOptions.blushColor = position;
                    break;
                case io.agora.api.example.ecomm.R.id.spinner_lip_color:
                    this.makeUpOptions.lipColor = position;
                    break;
            }
            
            // 更新扩展属性
            updateExtensionProperty();
                
        } catch (Exception e) {
            Log.e(TAG, "Error handling spinner selection: " + e.getMessage());
        }
    }
    
    /**
     * 处理Switch开关事件
     */
    public void handleSwitchChange(CompoundButton buttonView, boolean isChecked, RtcEngineManager engineManager) {
        if (engine == null) return;
        
        try {
            if (buttonView.getId() == io.agora.api.example.ecomm.R.id.switch_face_shape_beautify) {
                if (isChecked && !engineManager.isFeatureAvailableOnDevice(Constants.FEATURE_VIDEO_BEAUTY_EFFECT)) {
                    buttonView.setChecked(false);
                    Toast.makeText(context, "功能不可用", Toast.LENGTH_SHORT).show();
                    return;
                }
                engine.setFaceShapeBeautyOptions(isChecked, faceShapeBeautyOptions);
                
            } else if (buttonView.getId() == io.agora.api.example.ecomm.R.id.switch_face_makeup) {
                if (isChecked && !engineManager.isFeatureAvailableOnDevice(Constants.FEATURE_VIDEO_BEAUTY_EFFECT)) {
                    buttonView.setChecked(false);
                    Toast.makeText(context, "功能不可用", Toast.LENGTH_SHORT).show();
                    return;
                }
                makeUpOptions.enable_mu = isChecked;
                updateExtensionProperty();
                    
            } else if (buttonView.getId() == io.agora.api.example.ecomm.R.id.switch_face_beautify) {
                if (isChecked && !engineManager.isFeatureAvailableOnDevice(Constants.FEATURE_VIDEO_BEAUTY_EFFECT)) {
                    buttonView.setChecked(false);
                    Toast.makeText(context, "功能不可用", Toast.LENGTH_SHORT).show();
                    return;
                }
                engine.setBeautyEffectOptions(isChecked, beautyOptions);
                
            } else if (buttonView.getId() == io.agora.api.example.ecomm.R.id.switch_lightness2) {
                LowLightEnhanceOptions options = new LowLightEnhanceOptions();
                options.lowlightEnhanceLevel = LowLightEnhanceOptions.LOW_LIGHT_ENHANCE_LEVEL_FAST;
                options.lowlightEnhanceMode = LowLightEnhanceOptions.LOW_LIGHT_ENHANCE_AUTO;
                engine.setLowlightEnhanceOptions(isChecked, options);
                
            } else if (buttonView.getId() == io.agora.api.example.ecomm.R.id.switch_color2) {
                setColorEnhance(isChecked);
                
            } else if (buttonView.getId() == io.agora.api.example.ecomm.R.id.switch_virtual_background) {
                if (isChecked && !engineManager.isFeatureAvailableOnDevice(Constants.FEATURE_VIDEO_VIRTUAL_BACKGROUND)) {
                    buttonView.setChecked(false);
                    Toast.makeText(context, "功能不可用", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 虚拟背景重置在外部处理，这里需要调用resetVirtualBackground
                
            } else if (buttonView.getId() == io.agora.api.example.ecomm.R.id.switch_video_noise_reduce2) {
                VideoDenoiserOptions options = new VideoDenoiserOptions();
                options.denoiserLevel = VideoDenoiserOptions.VIDEO_DENOISER_LEVEL_HIGH_QUALITY;
                options.denoiserMode = VideoDenoiserOptions.VIDEO_DENOISER_AUTO;
                engine.setVideoDenoiserOptions(isChecked, options);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling switch change: " + e.getMessage());
        }
    }
    
    /**
     * 处理SeekBar变化事件
     */
    public void handleSeekBarChange(SeekBar seekBar, int progress, boolean fromUser,
                                  BeautyOptions beautyOptions, MpOptions makeUpOptions,
                                  FaceShapeAreaOptions faceShapeAreaOptions,
                                  FaceShapeBeautyOptions faceShapeBeautyOptions,
                                  double skinProtect, double strength,
                                  RtcEngineManager engineManager, FilterManager filterManager) {
        if (engine == null || !fromUser) return;
        
        try {
            float value = ((float) progress) / 10;
            
            if (seekBar.getId() == io.agora.api.example.ecomm.R.id.sb_shape_beautify_area_intensity) {
                faceShapeAreaOptions.shapeIntensity = progress;
                engine.setFaceShapeAreaOptions(faceShapeAreaOptions);
                
            } else if (seekBar.getId() == io.agora.api.example.ecomm.R.id.sb_shape_beautify_style_intensity) {
                faceShapeBeautyOptions.styleIntensity = progress;
                engine.setFaceShapeBeautyOptions(true, faceShapeBeautyOptions);
                
            } else if (seekBar.getId() == io.agora.api.example.ecomm.R.id.sb_brow_strength) {
                makeUpOptions.browStrength = value;
                updateExtensionProperty();
                    
            } else if (seekBar.getId() == io.agora.api.example.ecomm.R.id.sb_lash_strength) {
                makeUpOptions.lashStrength = value;
                updateExtensionProperty();
                    
            } else if (seekBar.getId() == io.agora.api.example.ecomm.R.id.sb_shadow_strength) {
                makeUpOptions.shadowStrength = value;
                updateExtensionProperty();
                    
            } else if (seekBar.getId() == io.agora.api.example.ecomm.R.id.sb_pupil_strength) {
                makeUpOptions.pupilStrength = value;
                updateExtensionProperty();
                    
            } else if (seekBar.getId() == io.agora.api.example.ecomm.R.id.sb_blush_strength) {
                makeUpOptions.blushStrength = value;
                updateExtensionProperty();
                    
            } else if (seekBar.getId() == io.agora.api.example.ecomm.R.id.sb_lip_strength) {
                makeUpOptions.lipStrength = value;
                updateExtensionProperty();
                    
            } else if (seekBar.getId() == io.agora.api.example.ecomm.R.id.lightening) {
                beautyOptions.lighteningLevel = value;
                engine.setBeautyEffectOptions(true, beautyOptions);
                
            } else if (seekBar.getId() == io.agora.api.example.ecomm.R.id.redness) {
                beautyOptions.rednessLevel = value;
                engine.setBeautyEffectOptions(true, beautyOptions);
                
            } else if (seekBar.getId() == io.agora.api.example.ecomm.R.id.sharpness) {
                beautyOptions.sharpnessLevel = value;
                engine.setBeautyEffectOptions(true, beautyOptions);
                
            } else if (seekBar.getId() == io.agora.api.example.ecomm.R.id.sb_video_enhance) {
                if (filterManager != null) {
                    filterManager.updateFilterStrength(value);
                }
                
            } else if (seekBar.getId() == io.agora.api.example.ecomm.R.id.smoothness) {
                beautyOptions.smoothnessLevel = value;
                engine.setBeautyEffectOptions(true, beautyOptions);
                
            } else if (seekBar.getId() == io.agora.api.example.ecomm.R.id.strength) {
                this.strength = value;
                setColorEnhance(true);
                
            } else if (seekBar.getId() == io.agora.api.example.ecomm.R.id.skinProtect) {
                this.skinProtect = value;
                setColorEnhance(true);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling seekbar change: " + e.getMessage());
        }
    }
    
    /**
     * 重置虚拟背景
     */
    public void resetVirtualBackground(Switch virtualBackground, RadioGroup virtualBgType,
                                     VirtualBackgroundSource virtualBackgroundSource,
                                     RtcEngineManager engineManager) {
        if (engine == null) return;
        
        try {
            if (virtualBackground.isChecked()) {
                int checkedId = virtualBgType.getCheckedRadioButtonId();
                VirtualBackgroundSource backgroundSource = new VirtualBackgroundSource();
                SegmentationProperty segproperty = new SegmentationProperty();
                
                if (checkedId == io.agora.api.example.ecomm.R.id.virtual_bg_image) {
                    backgroundSource.backgroundSourceType = VirtualBackgroundSource.BACKGROUND_IMG;
                    String imagePath = context.getExternalCacheDir().getPath();
                    String imageName = "agora-logo.png";
                    backgroundSource.source = imagePath + "/" + imageName;
                } else if (checkedId == io.agora.api.example.ecomm.R.id.virtual_bg_color) {
                    backgroundSource.backgroundSourceType = VirtualBackgroundSource.BACKGROUND_COLOR;
                    backgroundSource.color = 0x0000EE;
                } else if (checkedId == io.agora.api.example.ecomm.R.id.virtual_bg_blur) {
                    backgroundSource.backgroundSourceType = VirtualBackgroundSource.BACKGROUND_BLUR;
                    backgroundSource.blurDegree = VirtualBackgroundSource.BLUR_DEGREE_MEDIUM;
                } else if (checkedId == io.agora.api.example.ecomm.R.id.virtual_bg_video) {
                    backgroundSource.backgroundSourceType = VirtualBackgroundSource.BACKGROUND_VIDEO;
                    backgroundSource.source = "https://agora-adc-artifacts.s3.cn-north-1.amazonaws.com.cn/resources/sample.mp4";
                }
                engine.enableVirtualBackground(true, backgroundSource, segproperty);
            } else {
                engine.enableVirtualBackground(false, null, null);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error resetting virtual background: " + e.getMessage());
        }
    }
    
    /**
     * 更新扩展属性
     */
    public void updateExtensionProperty() {
        if (engine != null) {
            engine.setExtensionProperty("agora_video_filters_clear_vision", "clear_vision", 
                "makeup_options", makeUpOptions.toJson(), 
                Constants.MediaSourceType.PRIMARY_CAMERA_SOURCE);
        }
    }
    
    /**
     * 更新脸型美化样式选项
     */
    public void updateFaceShapeBeautyStyleOptions() {
        if (engine != null) {
            engine.setFaceShapeBeautyOptions(false, faceShapeBeautyOptions);
        }
    }
    
    /**
     * 设置色彩增强
     */
    private void setColorEnhance(boolean isChecked) {
        if (engine == null) return;
        
        try {
            ColorEnhanceOptions options = new ColorEnhanceOptions();
            // 使用类成员变量中的值
            options.strengthLevel = (float) strength;
            options.skinProtectLevel = (float) skinProtect;
            engine.setColorEnhanceOptions(isChecked, options);
            Log.d(TAG, "Color enhance: " + isChecked + ", strength: " + strength + ", skinProtect: " + skinProtect);
        } catch (Exception e) {
            Log.e(TAG, "Error setting color enhance: " + e.getMessage());
        }
    }
} 