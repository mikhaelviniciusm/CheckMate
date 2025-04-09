package com.mikhael.checkmate;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences("settings", MODE_PRIVATE);
        String language = preferences.getString("language", "pt");
        super.attachBaseContext(updateLocale(newBase, language));
    }

    private Context updateLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }
}