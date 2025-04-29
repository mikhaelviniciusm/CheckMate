package com.mikhael.checkmate.database;

import android.provider.BaseColumns;

/**
 * Classe Contrato que define a estrutura do banco de dados.
 * Contém as constantes necessárias para a criação e manipulação das tabelas.
 */
public final class DatabaseContract {

    // Construtor privado para evitar instanciamento da classe
    private DatabaseContract() {
    }

    /**
     * Classe interna que define a tabela "Tarefas".
     * Implementa BaseColumns para incluir automaticamente a coluna "_ID".
     */
    public static class Tabela implements BaseColumns {
        // Nome da tabela
        public static final String NOME_TABELA = "Tarefas";

        // Nome da coluna que armazena a descrição da tarefa
        public static final String Descricao = "Descrição";

        // Nome da coluna que armazena o status da tarefa (ex.: concluído ou não)
        public static final String Status = "Status"; // 0 = pendente, 1 = concluída
    }
}