package com.techflow.tasks.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {

    @Test
    @DisplayName("Tarefa nova entra na coluna TODO com prioridade MEDIUM por padrao")
    void newTaskHasDefaults() {
        Task task = new Task();
        assertEquals(TaskStatus.TODO, task.getStatus());
        assertEquals(TaskPriority.MEDIUM, task.getPriority());
        assertNotNull(task.getCreatedAt());
        assertNotNull(task.getUpdatedAt());
    }

    @Test
    @DisplayName("Construtor permite definir titulo, descricao, status e prioridade")
    void parameterizedConstructorWorks() {
        Task task = new Task("Levantar requisitos", "ETs", TaskStatus.IN_PROGRESS, TaskPriority.HIGH);
        assertEquals("Levantar requisitos", task.getTitle());
        assertEquals("ETs", task.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals(TaskPriority.HIGH, task.getPriority());
    }

    @Test
    @DisplayName("isOverdue retorna true quando o prazo passou e a tarefa nao foi concluida")
    void overdueWhenDueDatePastAndNotDone() {
        Task task = new Task("Atrasada", null, TaskStatus.IN_PROGRESS, TaskPriority.HIGH);
        task.setDueDate(LocalDate.now().minusDays(2));
        assertTrue(task.isOverdue());
    }

    @Test
    @DisplayName("isOverdue retorna false quando a tarefa esta concluida")
    void notOverdueWhenDone() {
        Task task = new Task("Concluida", null, TaskStatus.DONE, TaskPriority.HIGH);
        task.setDueDate(LocalDate.now().minusDays(10));
        assertFalse(task.isOverdue());
    }

    @Test
    @DisplayName("isOverdue retorna false quando nao ha prazo definido")
    void notOverdueWhenNoDueDate() {
        Task task = new Task("Sem prazo", null, TaskStatus.TODO, TaskPriority.LOW);
        assertFalse(task.isOverdue());
    }

    @Test
    @DisplayName("touch atualiza o updatedAt")
    void touchChangesUpdatedAt() throws InterruptedException {
        Task task = new Task("X", null, TaskStatus.TODO, TaskPriority.LOW);
        var before = task.getUpdatedAt();
        Thread.sleep(5);
        task.touch();
        assertTrue(task.getUpdatedAt().isAfter(before));
    }

    @Test
    @DisplayName("Enums TaskStatus e TaskPriority expoem labels legiveis")
    void enumLabelsArePresent() {
        assertEquals("A Fazer", TaskStatus.TODO.getLabel());
        assertEquals("Em Progresso", TaskStatus.IN_PROGRESS.getLabel());
        assertEquals("Concluido", TaskStatus.DONE.getLabel());
        assertEquals("Critica", TaskPriority.CRITICAL.getLabel());
    }
}
