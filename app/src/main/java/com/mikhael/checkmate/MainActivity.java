package com.mikhael.checkmate;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configura idioma, tema e cores dinâmicas
        configureLocaleAndTheme();

        // Ativa o modo Edge-to-Edge e define o layout
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Configura insets do sistema, AppBar e campo de entrada
        configureWindowInsets();
        configureTopAppBar();
        configureInputFieldAndButton();
    }

    /**
     * Configura o idioma, tema e aplica cores dinâmicas.
     */
    private void configureLocaleAndTheme() {
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);

        // Configura o idioma
        setLocale(preferences.getString("language", "en"));

        // Configura o tema (modo claro/escuro)
        AppCompatDelegate.setDefaultNightMode(
                preferences.getBoolean("dark_mode", false)
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        );

        // Aplica cores dinâmicas, se disponíveis
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
    }

    /**
     * Configura os insets do sistema para o layout principal.
     */
    private void configureWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Configura o AppBar superior e o comportamento de clique no menu.
     */
    private void configureTopAppBar() {
        androidx.appcompat.widget.Toolbar mainTopAppBar = findViewById(R.id.mainTopAppBar);

        // Abre a tela de configurações ao clicar no item do menu
        mainTopAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.settingsTopAppBar) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    /**
     * Configura o campo de entrada e o botão de ação.
     */
    private void configureInputFieldAndButton() {
        TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.inputField).getParent().getParent();
        TextInputEditText inputField = findViewById(R.id.inputField);
        ImageButton checkButton = findViewById(R.id.checkButton);

        // Configura o comportamento do campo de entrada ao ganhar/perder foco
        inputField.setOnFocusChangeListener((v, hasFocus) -> {
            textInputLayout.setHint(hasFocus ? null : getString(R.string.task_to_be_added));
            textInputLayout.setBoxStrokeColor(
                    ContextCompat.getColor(this, hasFocus ? R.color.md_theme_surfaceContainer : R.color.design_default_color_primary)
            );
        });

        // Configura o comportamento do botão com base no texto do campo
        inputField.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Não utilizado */ }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleInputFieldTextChange(s, inputField, checkButton);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) { /* Não utilizado */ }
        });

        // Configura a ação ao clicar no botão
        checkButton.setOnClickListener(v -> inputField.setText(""));
    }

    /**
     * Gerencia as animações e visibilidade do botão com base no texto do campo de entrada.
     */
    private void handleInputFieldTextChange(CharSequence s, TextInputEditText inputField, ImageButton checkButton) {
        int originalWidth = inputField.getWidth();
        int reducedWidth = originalWidth - checkButton.getWidth() - 16; // Ajuste com margem

        if (s.length() > 0 && checkButton.getVisibility() != android.view.View.VISIBLE) {
            animateButtonVisibility(checkButton, true);
            animateInputFieldWidth(inputField, originalWidth, reducedWidth);
        } else if (s.length() == 0 && checkButton.getVisibility() == android.view.View.VISIBLE) {
            animateButtonVisibility(checkButton, false);
            animateInputFieldWidth(inputField, inputField.getWidth(), originalWidth);
        }
    }

    /**
     * Anima a visibilidade do botão (mostrar/ocultar).
     */
    private void animateButtonVisibility(ImageButton button, boolean show) {
        if (show) {
            button.setAlpha(0f);
            button.setScaleX(0f);
            button.setScaleY(0f);
            button.setVisibility(android.view.View.VISIBLE);

            button.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator())
                    .start();
        } else {
            button.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .alpha(0f)
                    .setDuration(300)
                    .setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator())
                    .withEndAction(() -> {
                        button.setVisibility(android.view.View.GONE);
                        button.setScaleX(1f);
                        button.setScaleY(1f);
                    })
                    .start();
        }
    }

    /**
     * Anima a largura do campo de entrada.
     */
    private void animateInputFieldWidth(TextInputEditText inputField, int fromWidth, int toWidth) {
        ValueAnimator animator = ValueAnimator.ofInt(fromWidth, toWidth);
        animator.addUpdateListener(animation -> {
            inputField.getLayoutParams().width = (int) animation.getAnimatedValue();
            inputField.requestLayout();
        });
        animator.setDuration(300);
        animator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
        animator.start();
    }

    /**
     * Define o idioma da aplicação com base no código de idioma fornecido.
     */
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}