package com.mikhael.checkmate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Classe responsável pela criação e gerenciamento do banco de dados SQLite.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Nome do banco de dados
    private static final String NOME_BANCO = "Checkmate.db";

    // Versão do banco de dados
    private static final int VERSAO_BANCO = 1;

    // Construtor da classe AppBanco.
    public DatabaseHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    // Chamado ao criar o banco de dados pela primeira vez.
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Comando SQL para criar a tabela
        String SQL_CRIAR_TABELA = "CREATE TABLE " + DatabaseContract.Tabela.NOME_TABELA + " (" +
                DatabaseContract.Tabela._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // Coluna ID
                DatabaseContract.Tabela.Descricao + " TEXT NOT NULL, " +               // Coluna Descrição
                DatabaseContract.Tabela.Status + " INTEGER NOT NULL DEFAULT 0);";     // Coluna Status
        db.execSQL(SQL_CRIAR_TABELA); // Executa o comando SQL
    }

    // Chamado ao atualizar o banco de dados para uma nova versão.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Remove a tabela existente, se houver
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Tabela.NOME_TABELA);
        // Cria a tabela novamente
        onCreate(db);
    }
}