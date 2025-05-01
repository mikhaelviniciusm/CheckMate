package com.mikhael.checkmate;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

/**
 * Classe base para atividades que precisam de suporte a múltiplos idiomas.
 * Ajusta o contexto da aplicação para refletir o idioma selecionado pelo usuário.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        String language = preferences.getString(Constants.KEY_LANGUAGE, Constants.DEFAULT_LANGUAGE);
        super.attachBaseContext(applyLocale(newBase, language));
    }

    /**
     * Aplica o idioma especificado ao contexto.
     */
    protected Context applyLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }

    /**
     * Altera o idioma da atividade atual e recria a atividade para aplicar as mudanças.
     */
    protected void setLocale(String languageCode) {
        SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        String currentLanguage = preferences.getString(Constants.KEY_LANGUAGE, Constants.DEFAULT_LANGUAGE);

        if (!currentLanguage.equals(languageCode)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Constants.KEY_LANGUAGE, languageCode);
            editor.apply();

            applyLocale(this, languageCode);
            recreate();
        }
    }
}