package com.techflow.tasks.model;

/**
 * Estados possiveis de uma tarefa no fluxo Kanban.
 *
 * <p>Mapeia diretamente as colunas do quadro Kanban exigidas pelo cliente:
 * "A Fazer", "Em Progresso" e "Concluido".
 */
public enum TaskStatus {
    TODO("A Fazer"),
    IN_PROGRESS("Em Progresso"),
    DONE("Concluido");

    private final String label;

    TaskStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
