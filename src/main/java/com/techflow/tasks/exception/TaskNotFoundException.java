package com.techflow.tasks.exception;

/**
 * Lancada quando uma tarefa nao e encontrada pelo seu identificador.
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Tarefa com id=" + id + " nao foi encontrada.");
    }
}
