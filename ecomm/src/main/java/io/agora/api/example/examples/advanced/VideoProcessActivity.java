package io.agora.api.example.examples.advanced;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import io.agora.api.example.ecomm.R;

/**
 * EComm Video Process Activity - Used to load VideoProcessExtension Fragment
 */
public class VideoProcessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set full screen display
        setFullScreen();
        
        setContentView(R.layout.activity_video_process);
        
        // Hide ActionBar to achieve full screen
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        // Load VideoProcessExtension Fragment
        loadVideoProcessFragment();
    }
    
    private void setFullScreen() {
        // Hide status bar and navigation bar
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        // Set window layout parameters
        Window window = getWindow();
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        
        // Hide system UI
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }
    
    private void loadVideoProcessFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment existingFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        
        if (existingFragment == null) {
            // Use refactored Fragment
            VideoProcessExtensionRefactored videoProcessFragment = new VideoProcessExtensionRefactored();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, videoProcessFragment)
                    .commit();
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
} 