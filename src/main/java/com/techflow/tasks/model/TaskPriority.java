package com.techflow.tasks.model;

/**
 * Prioridade de uma tarefa.
 *
 * <p>Adicionada na <b>mudanca de escopo</b> apos solicitacao do cliente
 * para visualizar tarefas criticas da operacao de logistica.
 */
public enum TaskPriority {
    LOW("Baixa"),
    MEDIUM("Media"),
    HIGH("Alta"),
    CRITICAL("Critica");

    private final String label;

    TaskPriority(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
