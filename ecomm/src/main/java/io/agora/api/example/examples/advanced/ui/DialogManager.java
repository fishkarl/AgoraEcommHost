package io.agora.api.example.examples.advanced.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import io.agora.api.example.ecomm.R;
import io.agora.api.example.examples.advanced.features.FilterManager;
import io.agora.rtc2.RtcEngine;

/**
 * Dialog Manager
 * Responsible for creating and managing various dialogs
 */
public class DialogManager {
    private static final String TAG = "DialogManager";
    
    private final Context context;
    private final FilterManager filterManager;
    private final DialogCallback callback;
    
    public interface DialogCallback {
        void onManualFocusModeChanged(boolean isManual);
        void onLanguageChanged(String language);
        void onZoomChanged(float zoomFactor);
        void onFilterSelected(String filterPath, float strength);
        void onLocalCubeSelected(String cubeFile, float strength);
        RtcEngine getEngine();
    }
    
    public DialogManager(@NonNull Context context, @NonNull FilterManager filterManager, @NonNull DialogCallback callback) {
        this.context = context;
        this.filterManager = filterManager;
        this.callback = callback;
    }
    
    /**
     * Show manual focus dialog
     */
    public void showManualFocusDialog() {
        if (callback.getEngine() == null) return;
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.focus_mode_selection));
        
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        
        // Instruction text
        TextView instructionText = new TextView(context);
        instructionText.setText(context.getString(R.string.focus_mode_description));
        instructionText.setTextColor(0xFF000000);
        instructionText.setTextSize(14);
        instructionText.setPadding(0, 0, 0, 20);
        layout.addView(instructionText);
        
        // Focus mode selection
        RadioGroup focusModeGroup = new RadioGroup(context);
        focusModeGroup.setOrientation(RadioGroup.VERTICAL);
        
        RadioButton autoFocus = new RadioButton(context);
        autoFocus.setText(context.getString(R.string.auto_focus));
        autoFocus.setId(1);
        autoFocus.setChecked(true);
        
        RadioButton manualFocus = new RadioButton(context);
        manualFocus.setText(context.getString(R.string.manual_focus_tap));
        manualFocus.setId(2);
        
        focusModeGroup.addView(autoFocus);
        focusModeGroup.addView(manualFocus);
        layout.addView(focusModeGroup);
        
        // Focus mode switch listener
        focusModeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == 1) {
                // Auto focus
                callback.getEngine().setCameraAutoFocusFaceModeEnabled(true);
                if (callback != null) {
                    callback.onManualFocusModeChanged(false);
                }
            } else if (checkedId == 2) {
                // Manual focus
                callback.getEngine().setCameraAutoFocusFaceModeEnabled(false);
                if (callback != null) {
                    callback.onManualFocusModeChanged(true);
                }
            }
        });
        
        builder.setView(layout);
        builder.setPositiveButton(context.getString(R.string.ok), null);
        builder.setNegativeButton(context.getString(R.string.reset), (dialog, which) -> {
            // Reset to auto focus
            callback.getEngine().setCameraAutoFocusFaceModeEnabled(true);
            if (callback != null) {
                callback.onManualFocusModeChanged(false);
            }
        });
        
        builder.show();
    }
    
    /**
     * Show language selection dialog
     */
    public void showLanguageSelectionDialog(String currentLanguage) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.language_selection));
        
        // Create main layout
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        
        // Create RadioGroup
        RadioGroup languageGroup = new RadioGroup(context);
        languageGroup.setOrientation(RadioGroup.VERTICAL);
        
        // Create language option buttons
        RadioButton chineseBtn = new RadioButton(context);
        chineseBtn.setText("中文");
        chineseBtn.setTextColor(0xFF000000);
        chineseBtn.setId(0);
        
        RadioButton englishBtn = new RadioButton(context);
        englishBtn.setText("English");
        englishBtn.setTextColor(0xFF000000);
        englishBtn.setId(1);
        
        RadioButton japaneseBtn = new RadioButton(context);
        japaneseBtn.setText("日本語");
        japaneseBtn.setTextColor(0xFF000000);
        japaneseBtn.setId(2);
        
        RadioButton koreanBtn = new RadioButton(context);
        koreanBtn.setText("한국어");
        koreanBtn.setTextColor(0xFF000000);
        koreanBtn.setId(3);
        
        // Add buttons to RadioGroup
        languageGroup.addView(chineseBtn);
        languageGroup.addView(englishBtn);
        languageGroup.addView(japaneseBtn);
        languageGroup.addView(koreanBtn);
        
        // Set correct selected state based on current language
        if ("zh".equals(currentLanguage)) {
            chineseBtn.setChecked(true);
        } else if ("en".equals(currentLanguage)) {
            englishBtn.setChecked(true);
        } else if ("ja".equals(currentLanguage)) {
            japaneseBtn.setChecked(true);
        } else if ("ko".equals(currentLanguage)) {
            koreanBtn.setChecked(true);
        }
        
        // Add RadioGroup to main layout
        layout.addView(languageGroup);
        
        // Set dialog content
        builder.setView(layout);
        builder.setPositiveButton(context.getString(R.string.ok), null);
        
        // Create dialog
        android.app.AlertDialog dialog = builder.create();
        
        // Set selection listener
        languageGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedLanguage = "zh"; // Default Chinese
            
            if (checkedId == 0) {
                selectedLanguage = "zh";
            } else if (checkedId == 1) {
                selectedLanguage = "en";
            } else if (checkedId == 2) {
                selectedLanguage = "ja";
            } else if (checkedId == 3) {
                selectedLanguage = "ko";
            }
            
            if (!selectedLanguage.equals(currentLanguage)) {
                if (callback != null) {
                    callback.onLanguageChanged(selectedLanguage);
                }
                dialog.dismiss();
            }
        });
        
        // Show dialog
        dialog.show();
    }
    
    /**
     * Show zoom control dialog
     */
    public void showZoomControlDialog(float currentZoomFactor, float minZoomFactor, float maxZoomFactor, String cameraTypeStr) {
        if (callback.getEngine() == null) return;
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.zoom_control_title));
        
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        
        // Show current camera type
        TextView cameraTypeText = new TextView(context);
        cameraTypeText.setText(context.getString(R.string.current_camera) + ": " + cameraTypeStr);
        cameraTypeText.setTextColor(0xFF000000);
        cameraTypeText.setTextSize(14);
        layout.addView(cameraTypeText);
        
        // Show zoom range information
        TextView zoomRangeText = new TextView(context);
        zoomRangeText.setText(context.getString(R.string.zoom_range) + ": " + String.format("%.1fx - %.1fx", minZoomFactor, maxZoomFactor));
        zoomRangeText.setTextColor(0xFF000000);
        zoomRangeText.setTextSize(14);
        layout.addView(zoomRangeText);
        
        TextView currentZoomText = new TextView(context);
        currentZoomText.setText(context.getString(R.string.current_zoom) + ": " + String.format("%.1fx", currentZoomFactor));
        currentZoomText.setTextColor(0xFF000000);
        currentZoomText.setTextSize(16);
        currentZoomText.setPadding(0, 10, 0, 10);
        layout.addView(currentZoomText);
        
        SeekBar zoomSeekBar = new SeekBar(context);
        int maxProgress = (int) ((maxZoomFactor - minZoomFactor) * 10);
        int currentProgress = (int) ((currentZoomFactor - minZoomFactor) * 10);
        zoomSeekBar.setMax(maxProgress);
        zoomSeekBar.setProgress(currentProgress);
        
        zoomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float newZoomFactor = minZoomFactor + (progress / 10.0f);
                    currentZoomText.setText("Current Zoom: " + String.format("%.1fx", newZoomFactor));
                    if (callback != null) {
                        callback.onZoomChanged(newZoomFactor);
                    }
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        layout.addView(zoomSeekBar);
        
        builder.setView(layout);
        builder.setPositiveButton(context.getString(R.string.ok), null);
        builder.setNegativeButton(context.getString(R.string.reset), (dialog, which) -> {
            if (callback != null) {
                callback.onZoomChanged(1.0f);
            }
        });
        
        builder.show();
    }
    
    /**
     * Show filter selection dialog
     */
    public void showFilterSelectionDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.select_filter_file));
        
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        
        TextView currentFilterText = new TextView(context);
        currentFilterText.setText(context.getString(R.string.current_filter_path) + ": " + 
            (filterManager.getSelectedFilterPath() != null ? filterManager.getSelectedFilterPath() : context.getString(R.string.none)));
        currentFilterText.setTextColor(0xFF000000);
        currentFilterText.setTextSize(16);
        layout.addView(currentFilterText);
        
        String[] presetFilters = {
            context.getString(R.string.built_in_whiten_filter_desc),
            context.getString(R.string.built_in_smooth_filter_desc),
            context.getString(R.string.grayscale_filter_desc),
            context.getString(R.string.custom_filter_file_desc)
        };
        
        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        
        for (int i = 0; i < presetFilters.length; i++) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText(presetFilters[i]);
            radioButton.setId(i);
            radioGroup.addView(radioButton);
        }
        
        layout.addView(radioGroup);
        
        EditText customPathInput = new EditText(context);
        customPathInput.setHint(context.getString(R.string.enter_custom_filter_path_hint));
        customPathInput.setText(Environment.getExternalStorageDirectory() + "/Android/data/io.agora.api.example.ecomm/files/");
        layout.addView(customPathInput);
        
        Button selectFileButton = new Button(context);
        selectFileButton.setText(context.getString(R.string.select_file));
        selectFileButton.setTextSize(12);
        selectFileButton.setPadding(20, 10, 20, 10);
        selectFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"*/*"});
            intent.putExtra(Intent.EXTRA_TITLE, context.getString(R.string.select_filter_file));
            try {
                // This requires Activity's startActivityForResult, skip for now
                Toast.makeText(context, "File selection requires Activity support", Toast.LENGTH_SHORT).show();
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(context, context.getString(R.string.function_not_available), Toast.LENGTH_SHORT).show();
            }
        });
        layout.addView(selectFileButton);
        
        // Add filter strength adjustment SeekBar
        TextView strengthLabel = new TextView(context);
        strengthLabel.setText(context.getString(R.string.filter_strength) + ": 0.5");
        strengthLabel.setTextColor(0xFF000000);
        strengthLabel.setTextSize(16);
        strengthLabel.setPadding(0, 20, 0, 10);
        layout.addView(strengthLabel);
        
        SeekBar strengthSeekBar = new SeekBar(context);
        strengthSeekBar.setMax(10); // 0-10, corresponding to 0.0-1.0, step 0.1
        strengthSeekBar.setProgress(5); // Default value 0.5
        strengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float strength = progress / 10.0f; // Convert to 0.0-1.0
                strengthLabel.setText(context.getString(R.string.filter_strength) + ": " + String.format("%.1f", strength));
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        layout.addView(strengthSeekBar);
        
        builder.setView(layout);
        
        builder.setPositiveButton(context.getString(R.string.ok), (dialog, which) -> {
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
                if (!filterPath.startsWith("built_in_")) {
                    java.io.File file = new java.io.File(filterPath);
                    if (!file.exists()) {
                        Toast.makeText(context, context.getString(R.string.filter_file_not_exist) + ": " + filterPath, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                
                // Get strength value
                float strength = strengthSeekBar.getProgress() / 10.0f;
                
                if (callback != null) {
                    callback.onFilterSelected(filterPath, strength);
                }
            }
        });
        
        builder.setNegativeButton(context.getString(R.string.reset), null);
        builder.show();
    }
    
    /**
     * Show local Cube selection dialog
     */
    public void showLocalCubeSelectionDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.select_local_cube_file));
        
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        
        TextView currentFilterText = new TextView(context);
        currentFilterText.setText(context.getString(R.string.current_cube_path) + ": " + 
            (filterManager.getSelectedFilterPath() != null ? filterManager.getSelectedFilterPath() : context.getString(R.string.no_cube_file)));
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
        
        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        
        for (int i = 0; i < localCubeFiles.length; i++) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText(localCubeFiles[i]);
            radioButton.setId(i);
            radioGroup.addView(radioButton);
        }
        
        layout.addView(radioGroup);
        
        // Add cube strength adjustment SeekBar
        TextView strengthLabel = new TextView(context);
        strengthLabel.setText(context.getString(R.string.filter_strength) + ": 0.5");
        strengthLabel.setTextColor(0xFF000000);
        strengthLabel.setTextSize(16);
        strengthLabel.setPadding(0, 20, 0, 10);
        layout.addView(strengthLabel);
        
        SeekBar strengthSeekBar = new SeekBar(context);
        strengthSeekBar.setMax(10); // 0-10, corresponding to 0.0-1.0, step 0.1
        strengthSeekBar.setProgress(5); // Default value 0.5
        strengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float strength = progress / 10.0f; // Convert to 0.0-1.0
                strengthLabel.setText(context.getString(R.string.filter_strength) + ": " + String.format("%.1f", strength));
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        layout.addView(strengthSeekBar);
        
        builder.setView(layout);
        
        builder.setPositiveButton(context.getString(R.string.ok), (dialog, which) -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            if (checkedId >= 0 && checkedId < localCubeFiles.length) {
                String selectedCubeFile = localCubeFiles[checkedId];
                String filterPath = filterManager.copyAssetCubeToStorage(selectedCubeFile);
                
                if (filterPath != null) {
                    // Get strength value
                    float strength = strengthSeekBar.getProgress() / 10.0f;
                    
                    if (callback != null) {
                        callback.onLocalCubeSelected(selectedCubeFile, strength);
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.function_not_available), Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        builder.setNegativeButton(context.getString(R.string.cancel), null);
        builder.show();
    }
}
