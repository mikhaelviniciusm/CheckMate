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
        // Obtém as preferências compartilhadas
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);

        // Define o idioma com base na preferência salva
        setLocale(preferences.getString("language", "pt"));

        super.onCreate(savedInstanceState);

        // Configura o layout e habilita o comportamento Edge-to-Edge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Ajusta o padding para evitar sobreposição com as barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configura a barra de navegação superior
        setupToolbar();

        // Configura o switch de tema
        setupThemeSwitch(preferences);
    }

    // Configura a barra de navegação superior
    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar settingsTopAppBar = findViewById(R.id.arrowBack);
        settingsTopAppBar.setNavigationOnClickListener(v -> finish());
    }

    // Configura o switch de tema para alternar entre modo claro e escuro
    private void setupThemeSwitch(SharedPreferences preferences) {
        MaterialSwitch themeSwitch = findViewById(R.id.themeSwitch);
        themeSwitch.setChecked(preferences.getBoolean("dark_mode", false));

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Salva a preferência de tema
            preferences.edit().putBoolean("dark_mode", isChecked).apply();

            // Aplica o tema com base na preferência
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            );
        });
    }

    // Define o idioma com base no código de idioma selecionado
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    // Trata a seleção de idioma
    public void onLanguageClick(View view) {
        String[] languages = {"Português", "English"};
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        int checkedItem = preferences.getString("language", "pt").equals("pt") ? 0 : 1;

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.select_language))
                .setSingleChoiceItems(languages, checkedItem, (dialog, which) -> {
                    // Salva a nova preferência de idioma
                    String selectedLanguage = which == 0 ? "pt" : "en";
                    preferences.edit().putString("language", selectedLanguage).apply();

                    // Reinicia o app para aplicar o novo idioma
                    restartApp();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Reinicia o aplicativo para aplicar alterações de idioma
    private void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // Trata o clique no item "Clear all tasks" (implementação futura)
    public void onClearTasksClick(View view) {
        // TODO: Implementar funcionalidade para limpar todas as tarefas
    }
}