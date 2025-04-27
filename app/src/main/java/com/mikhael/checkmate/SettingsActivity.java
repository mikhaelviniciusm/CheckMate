package com.mikhael.checkmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.Locale;

/**
 * Activity de configurações do aplicativo.
 * Permite alterar idioma, tema e limpar todas as tarefas do banco de dados.
 */
public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Obtém as preferências do usuário
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);

        // Define o idioma com base nas preferências
        setLocale(preferences.getString("language", "pt"));

        super.onCreate(savedInstanceState);

        // Habilita o modo Edge-to-Edge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Configura os insets da janela para ajustar o layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configura a toolbar e o switch de tema
        setupToolbar();
        setupThemeSwitch(preferences);
    }

    /**
     * Configura a toolbar com botão de navegação para voltar.
     */
    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar settingsTopAppBar = findViewById(R.id.arrowBack);
        settingsTopAppBar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * Configura o switch de tema (modo claro/escuro).
     *
     * @param preferences Preferências do usuário.
     */
    private void setupThemeSwitch(SharedPreferences preferences) {
        MaterialSwitch themeSwitch = findViewById(R.id.themeSwitch);
        themeSwitch.setChecked(preferences.getBoolean("dark_mode", false));

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Salva a preferência de tema
            preferences.edit().putBoolean("dark_mode", isChecked).apply();

            // Aplica o tema selecionado
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            );
        });
    }

    /**
     * Define o idioma do aplicativo.
     *
     * @param languageCode Código do idioma (ex.: "pt" ou "en").
     */
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    /**
     * Exibe o diálogo para seleção de idioma.
     *
     * @param view View que acionou o evento.
     */
    public void onLanguageClick(View view) {
        String[] languages = {"Português", "English"};
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        int checkedItem = preferences.getString("language", "pt").equals("pt") ? 0 : 1;

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.select_language))
                .setSingleChoiceItems(languages, checkedItem, (dialog, which) -> {
                    // Salva o idioma selecionado
                    String selectedLanguage = which == 0 ? "pt" : "en";
                    preferences.edit().putString("language", selectedLanguage).apply();

                    // Reinicia o aplicativo para aplicar o idioma
                    restartApp();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Reinicia o aplicativo para aplicar alterações.
     */
    private void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Exibe o diálogo para confirmação de exclusão de todas as tarefas.
     *
     * @param view View que acionou o evento.
     */
    public void onClearTasksClick(View view) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.clear_all_tasks))
                .setMessage(getString(R.string.clear_all_tasks_confirmation))
                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                    // Limpa todas as tarefas do banco de dados
                    clearAllTasksFromDatabase();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Remove todas as tarefas do banco de dados.
     */
    private void clearAllTasksFromDatabase() {
        SQLiteDatabase db = new DatabaseHelper(this).getWritableDatabase();
        db.delete(DatabaseContract.Tabela.NOME_TABELA, null, null);
        db.close();

        // Reinicia a MainActivity após limpar as tarefas
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}