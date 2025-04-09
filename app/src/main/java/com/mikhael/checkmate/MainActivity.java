package com.mikhael.checkmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.color.DynamicColors;

import java.util.Locale;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get the shared preferences
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);

        // Get the saved language preference
        String language = preferences.getString("language", "en");

        // Set the locale to the saved language
        setLocale(language);

        super.onCreate(savedInstanceState);

        // Apply dynamic colors if available
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());

        // Check if dark mode is enabled on the device
        boolean isDarkModeEnabled = preferences.getBoolean("dark_mode", false);

        // Set the theme based on the saved preference
        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        androidx.appcompat.widget.Toolbar mainTopAppBar = findViewById(R.id.mainTopAppBar);

        // Call activity_settings.xml
        mainTopAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.settingsTopAppBar) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    // Method to set the locale based on the selected language
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}