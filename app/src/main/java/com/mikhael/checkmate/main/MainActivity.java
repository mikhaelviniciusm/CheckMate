package com.mikhael.checkmate.main;

import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mikhael.checkmate.BaseActivity;
import com.mikhael.checkmate.R;
import com.mikhael.checkmate.settings.SettingsActivity;
import com.mikhael.checkmate.database.DatabaseContract;
import com.mikhael.checkmate.database.DatabaseHelper;

import java.util.Locale;

public class MainActivity extends BaseActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configurações iniciais de idioma, tema e layout
        configureLocaleAndTheme();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.md_theme_surfaceContainer));

        // Configurações de layout e banco de dados
        configureWindowInsets();
        configureTopAppBar();
        configureInputFieldAndButton();

        // Inicializa o banco de dados e carrega as tarefas
        databaseHelper = new DatabaseHelper(this);
        loadTasksFromDatabase();
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

        // Gerencia o foco no campo de entrada
        inputField.setOnFocusChangeListener((v, hasFocus) -> {
            textInputLayout.setHint(hasFocus ? null : getString(R.string.task_to_be_added));
            textInputLayout.setBoxStrokeColor(
                    ContextCompat.getColor(this, hasFocus ? R.color.md_theme_surfaceContainer : R.color.design_default_color_primary)
            );
        });

        // Observa mudanças no texto do campo de entrada
        inputField.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleInputFieldTextChange(s, inputField, checkButton);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });

        // Adiciona a tarefa ao banco de dados ao clicar no botão
        checkButton.setOnClickListener(v -> {
            String taskDescription = inputField.getText().toString().trim();
            if (!taskDescription.isEmpty()) {
                addTaskToDatabase(taskDescription);
                inputField.setText("");
            }
        });
    }

    /**
     * Adiciona uma tarefa ao banco de dados e atualiza a lista de tarefas.
     */
    private void addTaskToDatabase(String description) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Tabela.Descricao, description);
        values.put(DatabaseContract.Tabela.Status, 0); // Status pendente
        db.insert(DatabaseContract.Tabela.NOME_TABELA, null, values);
        loadTasksFromDatabase();
    }

    /**
     * Carrega as tarefas do banco de dados e atualiza a interface do usuário.
     */
    private void loadTasksFromDatabase() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseContract.Tabela.NOME_TABELA,
                null,
                null,
                null,
                null,
                null,
                null
        );

        LinearLayout pendingTasksContainer = findViewById(R.id.pendingTasksContainer);
        LinearLayout completedTasksContainer = findViewById(R.id.completedTasksContainer);
        TextView pendingTasksLabel = findViewById(R.id.pendingTasksLabel);
        TextView completedTasksLabel = findViewById(R.id.completedTasksLabel);

        // Limpa os contêineres antes de recarregar as tarefas
        pendingTasksContainer.removeAllViews();
        completedTasksContainer.removeAllViews();

        // Itera sobre as tarefas no banco de dados
        boolean isFirstPending = true;
        boolean isFirstCompleted = true;

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.Tabela._ID));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Tabela.Descricao));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.Tabela.Status));

            CheckBox taskCheckBox = new CheckBox(this);
            taskCheckBox.setText(description);
            taskCheckBox.setChecked(status == 1);
            taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateTaskStatus(id, isChecked));

            // Configura o menu de opções ao pressionar longamente
            taskCheckBox.setOnLongClickListener(v -> {
                showTaskOptionsMenu(id, description, taskCheckBox);
                return true;
            });

            // Adiciona a tarefa ao contêiner apropriado
            if (status == 0) {
                if (!isFirstPending) {
                    pendingTasksContainer.addView(createDivider());
                }
                isFirstPending = false;
                pendingTasksContainer.addView(taskCheckBox);
            } else {
                if (!isFirstCompleted) {
                    completedTasksContainer.addView(createDivider());
                }
                isFirstCompleted = false;
                taskCheckBox.setPaintFlags(taskCheckBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                completedTasksContainer.addView(taskCheckBox);
            }
        }

        // Atualiza a visibilidade dos rótulos
        pendingTasksLabel.setVisibility(pendingTasksContainer.getChildCount() > 0 ? View.VISIBLE : View.GONE);
        completedTasksLabel.setVisibility(completedTasksContainer.getChildCount() > 0 ? View.VISIBLE : View.GONE);

        cursor.close();
    }

    /**
     * Cria uma View que atua como um divisor entre os itens.
     */
    private View createDivider() {
        View divider = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1 // Altura do divisor
        );
        params.setMargins(0, 24, 0, 24); // Margens superior e inferior
        divider.setLayoutParams(params);
        divider.setBackgroundColor(ContextCompat.getColor(this, R.color.md_theme_outline)); // Cor do divisor
        return divider;
    }

    /**
     * Exibe o menu de opções para editar ou excluir uma tarefa.
     */
    private void showTaskOptionsMenu(int taskId, String currentDescription, CheckBox taskCheckBox) {
        android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(this, taskCheckBox);
        popupMenu.getMenu().add("Editar");
        popupMenu.getMenu().add("Excluir");

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Editar")) {
                showEditTaskDialog(taskId, currentDescription);
            } else if (item.getTitle().equals("Excluir")) {
                deleteTaskFromDatabase(taskId);
            }
            return true;
        });

        popupMenu.show();
    }

    /**
     * Exibe um diálogo para editar a descrição de uma tarefa.
     */
    private void showEditTaskDialog(int taskId, String currentDescription) {
        com.google.android.material.dialog.MaterialAlertDialogBuilder builder =
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(this);
        builder.setTitle("Editar Tarefa");

        final TextInputEditText input = new TextInputEditText(this);
        input.setText(currentDescription);
        input.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(32, 16, 32, 16); // Padding em dp
        container.addView(input);

        builder.setView(container);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String newDescription = input.getText().toString().trim();
            if (!newDescription.isEmpty()) {
                updateTaskDescription(taskId, newDescription);
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * Atualiza a descrição de uma tarefa no banco de dados.
     */
    private void updateTaskDescription(int taskId, String newDescription) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Tabela.Descricao, newDescription);
        db.update(DatabaseContract.Tabela.NOME_TABELA, values, DatabaseContract.Tabela._ID + " = ?", new String[]{String.valueOf(taskId)});
        loadTasksFromDatabase();
    }

    /**
     * Exclui uma tarefa do banco de dados.
     */
    private void deleteTaskFromDatabase(int taskId) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(DatabaseContract.Tabela.NOME_TABELA, DatabaseContract.Tabela._ID + " = ?", new String[]{String.valueOf(taskId)});
        loadTasksFromDatabase();
    }

    /**
     * Atualiza o status de uma tarefa no banco de dados.
     */
    private void updateTaskStatus(int taskId, boolean isCompleted) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Tabela.Status, isCompleted ? 1 : 0);
        db.update(DatabaseContract.Tabela.NOME_TABELA, values, DatabaseContract.Tabela._ID + " = ?", new String[]{String.valueOf(taskId)});
        loadTasksFromDatabase();
    }

    /**
     * Gerencia as animações e visibilidade do botão com base no texto do campo de entrada.
     */
    private void handleInputFieldTextChange(CharSequence s, TextInputEditText inputField, ImageButton checkButton) {
        int originalWidth = inputField.getWidth();
        int reducedWidth = originalWidth - checkButton.getWidth() - 16; // Ajuste com margem

        if (s.length() > 0 && checkButton.getVisibility() != View.VISIBLE) {
            animateButtonVisibility(checkButton, true);
            animateInputFieldWidth(inputField, originalWidth, reducedWidth);
        } else if (s.length() == 0 && checkButton.getVisibility() == View.VISIBLE) {
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
            button.setVisibility(View.VISIBLE);

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
                        button.setVisibility(View.GONE);
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