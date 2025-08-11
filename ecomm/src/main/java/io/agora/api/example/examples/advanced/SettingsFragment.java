package io.agora.api.example.examples.advanced;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.slider.Slider;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.chip.Chip;

import io.agora.api.example.ecomm.R;

/**
 * EComm Settings Fragment - Material Design style settings interface
 */
public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, 
                                                          Slider.OnChangeListener, 
                                                          AdapterView.OnItemSelectedListener {
    
    private static final String TAG = "ECommSettingsFragment";
    
    // Callback interface for communication with main Fragment
    public interface SettingsCallback {
        void onFaceShapeBeautifyChanged(boolean enabled, int area, float areaIntensity, int style, float styleIntensity);
        void onFaceMakeupChanged(boolean enabled, int browStyle, int browColor, float browStrength,
                                int lashStyle, int lashColor, float lashStrength,
                                int shadowStyle, float shadowStrength,
                                int pupilStyle, float pupilStrength,
                                int blushStyle, int blushColor, float blushStrength,
                                int lipStyle, int lipColor, float lipStrength);
        void onFaceBeautifyChanged(boolean enabled, float lightening, float redness, float sharpness, float smoothness);
        void onVideoEnhanceChanged(boolean enabled, float strength);
        void onLightnessEnhanceChanged(boolean enabled);
        void onColorEnhanceChanged(boolean enabled, float strength, float skinProtect);
        void onNoiseReduceChanged(boolean enabled);
        void onVirtualBackgroundChanged(boolean enabled, int type);
    }
    
    private SettingsCallback callback;
    
    // Face shape beautification
    private SwitchMaterial switchFaceShapeBeautify;
    private Slider sliderShapeBeautifyAreaIntensity;
    private Slider sliderShapeBeautifyStyleIntensity;
    
    // Face makeup
    private SwitchMaterial switchFaceMakeup;
    private Slider sliderBrowStrength;
    private Slider sliderLashStrength;
    private Slider sliderShadowStrength;
    private Slider sliderPupilStrength;
    private Slider sliderBlushStrength;
    private Slider sliderLipStrength;
    
    // Face beautification
    private SwitchMaterial switchFaceBeautify;
    private Slider sliderLightening;
    private Slider sliderRedness;
    private Slider sliderSharpness;
    private Slider sliderSmoothness;
    
    // Video enhancement
    private Slider sliderVideoEnhance;
    private SwitchMaterial switchLightnessEnhance;
    private SwitchMaterial switchColorEnhance;
    private Slider sliderColorStrength;
    private Slider sliderSkinProtect;
    private SwitchMaterial switchNoiseReduce;
    
    // Virtual background
    private SwitchMaterial switchVirtualBackground;
    private ChipGroup chipGroupVirtualBgType;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupListeners();
    }
    
    private void initViews(View view) {
        // Face shape beautification
        switchFaceShapeBeautify = view.findViewById(R.id.switch_face_shape_beautify);
        sliderShapeBeautifyAreaIntensity = view.findViewById(R.id.slider_shape_beautify_area_intensity);
        sliderShapeBeautifyStyleIntensity = view.findViewById(R.id.slider_shape_beautify_style_intensity);
        
        // Face makeup
        switchFaceMakeup = view.findViewById(R.id.switch_face_makeup);
        sliderBrowStrength = view.findViewById(R.id.slider_brow_strength);
        sliderLashStrength = view.findViewById(R.id.slider_lash_strength);
        sliderShadowStrength = view.findViewById(R.id.slider_shadow_strength);
        sliderPupilStrength = view.findViewById(R.id.slider_pupil_strength);
        sliderBlushStrength = view.findViewById(R.id.slider_blush_strength);
        sliderLipStrength = view.findViewById(R.id.slider_lip_strength);
        
        // Face beautification
        switchFaceBeautify = view.findViewById(R.id.switch_face_beautify);
        sliderLightening = view.findViewById(R.id.slider_lightening);
        sliderRedness = view.findViewById(R.id.slider_redness);
        sliderSharpness = view.findViewById(R.id.slider_sharpness);
        sliderSmoothness = view.findViewById(R.id.slider_smoothness);
        
        // Video enhancement
        sliderVideoEnhance = view.findViewById(R.id.slider_video_enhance);
        switchLightnessEnhance = view.findViewById(R.id.switch_lightness_enhance);
        switchColorEnhance = view.findViewById(R.id.switch_color_enhance);
        sliderColorStrength = view.findViewById(R.id.slider_color_strength);
        sliderSkinProtect = view.findViewById(R.id.slider_skin_protect);
        switchNoiseReduce = view.findViewById(R.id.switch_noise_reduce);
        
        // Virtual background
        switchVirtualBackground = view.findViewById(R.id.switch_virtual_background);
        chipGroupVirtualBgType = view.findViewById(R.id.chip_group_virtual_bg_type);
    }
    
    private void setupListeners() {
        // Set switch listeners
        switchFaceShapeBeautify.setOnCheckedChangeListener(this);
        switchFaceMakeup.setOnCheckedChangeListener(this);
        switchFaceBeautify.setOnCheckedChangeListener(this);
        switchLightnessEnhance.setOnCheckedChangeListener(this);
        switchColorEnhance.setOnCheckedChangeListener(this);
        switchNoiseReduce.setOnCheckedChangeListener(this);
        switchVirtualBackground.setOnCheckedChangeListener(this);
        
        // Set slider listeners
        sliderShapeBeautifyAreaIntensity.addOnChangeListener(this);
        sliderShapeBeautifyStyleIntensity.addOnChangeListener(this);
        sliderBrowStrength.addOnChangeListener(this);
        sliderLashStrength.addOnChangeListener(this);
        sliderShadowStrength.addOnChangeListener(this);
        sliderPupilStrength.addOnChangeListener(this);
        sliderBlushStrength.addOnChangeListener(this);
        sliderLipStrength.addOnChangeListener(this);
        sliderLightening.addOnChangeListener(this);
        sliderRedness.addOnChangeListener(this);
        sliderSharpness.addOnChangeListener(this);
        sliderSmoothness.addOnChangeListener(this);
        sliderVideoEnhance.addOnChangeListener(this);
        sliderColorStrength.addOnChangeListener(this);
        sliderSkinProtect.addOnChangeListener(this);
    }
    
    public void setCallback(SettingsCallback callback) {
        this.callback = callback;
    }
    
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (callback == null) return;
        
        int id = buttonView.getId();
        if (id == R.id.switch_face_shape_beautify) {
            callback.onFaceShapeBeautifyChanged(isChecked, 0, sliderShapeBeautifyAreaIntensity.getValue(), 0, sliderShapeBeautifyStyleIntensity.getValue());
        } else if (id == R.id.switch_face_makeup) {
            callback.onFaceMakeupChanged(isChecked, 0, 0, sliderBrowStrength.getValue(), 0, 0, sliderLashStrength.getValue(), 0, sliderShadowStrength.getValue(), 0, sliderPupilStrength.getValue(), 0, 0, sliderBlushStrength.getValue(), 0, 0, sliderLipStrength.getValue());
        } else if (id == R.id.switch_face_beautify) {
            callback.onFaceBeautifyChanged(isChecked, sliderLightening.getValue(), sliderRedness.getValue(), sliderSharpness.getValue(), sliderSmoothness.getValue());
        } else if (id == R.id.switch_lightness_enhance) {
            callback.onLightnessEnhanceChanged(isChecked);
        } else if (id == R.id.switch_color_enhance) {
            callback.onColorEnhanceChanged(isChecked, sliderColorStrength.getValue(), sliderSkinProtect.getValue());
        } else if (id == R.id.switch_noise_reduce) {
            callback.onNoiseReduceChanged(isChecked);
        } else if (id == R.id.switch_virtual_background) {
            int selectedChipId = chipGroupVirtualBgType.getCheckedChipId();
            int bgType = 0; // Default image background
            if (selectedChipId == R.id.chip_bg_color) bgType = 1;
            else if (selectedChipId == R.id.chip_bg_blur) bgType = 2;
            else if (selectedChipId == R.id.chip_bg_video) bgType = 3;
            callback.onVirtualBackgroundChanged(isChecked, bgType);
        }
    }
    
    @Override
    public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
        if (callback == null || !fromUser) return;
        
        // Trigger corresponding callback when slider value changes
        onCheckedChanged(switchFaceShapeBeautify, switchFaceShapeBeautify.isChecked());
    }
    
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Handle spinner selection changes
    }
    
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Handle when nothing is selected
    }
} 