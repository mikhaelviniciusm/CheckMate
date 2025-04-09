package com.mikhael.checkmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.Locale;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get the shared preferences
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);

        // Get the saved language preference
        String language = preferences.getString("language", "pt");

        // Set the locale to the saved language
        setLocale(language);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        androidx.appcompat.widget.Toolbar settingsTopAppBar = findViewById(R.id.arrowBack);
        settingsTopAppBar.setNavigationOnClickListener(v -> {
            finish();
        });

        MaterialSwitch themeSwitch = findViewById(R.id.themeSwitch);

        // Check if dark mode is enabled on the device
        boolean isDarkModeEnabled = preferences.getBoolean("dark_mode", false);
        themeSwitch.setChecked(isDarkModeEnabled);

        // Set the theme based on the saved preference
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
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

    // Method to handle the language selection
    public void onLanguageClick(View view) {
        String[] languages = {"Português", "English"};
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        String currentLanguage = preferences.getString("language", "pt");
        int checkedItem = currentLanguage.equals("pt") ? 0 : 1;

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.select_language))
                .setSingleChoiceItems(languages, checkedItem, (dialog, which) -> {
                    SharedPreferences.Editor editor = preferences.edit();
                    String selectedLanguage = which == 0 ? "pt" : "en";
                    editor.putString("language", selectedLanguage);
                    editor.apply();

                    // Restart the app to apply the new language
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void onClearTasksClick(View view) {
        // Lógica para tratar o clique no item "Clear all tasks"
    }
}