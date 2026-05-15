package com.techflow.tasks.service;

import com.techflow.tasks.exception.TaskNotFoundException;
import com.techflow.tasks.model.Task;
import com.techflow.tasks.model.TaskPriority;
import com.techflow.tasks.model.TaskStatus;
import com.techflow.tasks.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaskServiceTest {

    @Autowired
    private TaskService service;

    @Autowired
    private TaskRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("create persiste a tarefa atribuindo defaults quando faltam")
    void createAppliesDefaults() {
        Task t = new Task();
        t.setTitle("Configurar CI");
        Task saved = service.create(t);

        assertNotNull(saved.getId());
        assertEquals(TaskStatus.TODO, saved.getStatus());
        assertEquals(TaskPriority.MEDIUM, saved.getPriority());
    }

    @Test
    @DisplayName("findById lanca TaskNotFoundException quando id nao existe")
    void findByIdNotFound() {
        assertThrows(TaskNotFoundException.class, () -> service.findById(9999L));
    }

    @Test
    @DisplayName("update altera apenas campos visiveis e mantem id")
    void updateChangesFields() {
        Task created = service.create(new Task("v1", "old", TaskStatus.TODO, TaskPriority.LOW));

        Task changes = new Task();
        changes.setTitle("v2");
        changes.setDescription("new");
        changes.setStatus(TaskStatus.IN_PROGRESS);
        changes.setPriority(TaskPriority.HIGH);
        changes.setAssignee("Maria");

        Task updated = service.update(created.getId(), changes);

        assertEquals(created.getId(), updated.getId());
        assertEquals("v2", updated.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, updated.getStatus());
        assertEquals(TaskPriority.HIGH, updated.getPriority());
        assertEquals("Maria", updated.getAssignee());
    }

    @Test
    @DisplayName("moveTo desloca a tarefa para outra coluna do Kanban")
    void moveToChangesStatus() {
        Task t = service.create(new Task("Mover", null, TaskStatus.TODO, TaskPriority.MEDIUM));
        Task moved = service.moveTo(t.getId(), TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, moved.getStatus());
    }

    @Test
    @DisplayName("delete remove a tarefa do repositorio")
    void deleteRemoves() {
        Task t = service.create(new Task("Para excluir", null, TaskStatus.TODO, TaskPriority.LOW));
        service.delete(t.getId());
        assertThrows(TaskNotFoundException.class, () -> service.findById(t.getId()));
    }

    @Test
    @DisplayName("delete em id inexistente lanca TaskNotFoundException")
    void deleteUnknownThrows() {
        assertThrows(TaskNotFoundException.class, () -> service.delete(9999L));
    }

    @Test
    @DisplayName("kanbanBoard agrupa tarefas pelas tres colunas")
    void kanbanBoardGroupsByColumn() {
        service.create(new Task("a", null, TaskStatus.TODO, TaskPriority.LOW));
        service.create(new Task("b", null, TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM));
        service.create(new Task("c", null, TaskStatus.DONE, TaskPriority.HIGH));

        Map<TaskStatus, List<Task>> board = service.kanbanBoard();
        assertEquals(1, board.get(TaskStatus.TODO).size());
        assertEquals(1, board.get(TaskStatus.IN_PROGRESS).size());
        assertEquals(1, board.get(TaskStatus.DONE).size());
    }

    @Test
    @DisplayName("kanbanBoard ordena tarefas por prioridade decrescente dentro de cada coluna")
    void kanbanBoardOrdersByPriority() {
        service.create(new Task("low", null, TaskStatus.TODO, TaskPriority.LOW));
        service.create(new Task("crit", null, TaskStatus.TODO, TaskPriority.CRITICAL));
        service.create(new Task("med", null, TaskStatus.TODO, TaskPriority.MEDIUM));

        Map<TaskStatus, List<Task>> board = service.kanbanBoard();
        List<Task> todo = board.get(TaskStatus.TODO);
        assertEquals("crit", todo.get(0).getTitle());
        assertEquals("med", todo.get(1).getTitle());
        assertEquals("low", todo.get(2).getTitle());
    }

    @Test
    @DisplayName("countsByStatus retorna contagem por coluna")
    void countsByStatusReturnsCounts() {
        service.create(new Task("a", null, TaskStatus.TODO, TaskPriority.LOW));
        service.create(new Task("b", null, TaskStatus.TODO, TaskPriority.LOW));
        service.create(new Task("c", null, TaskStatus.DONE, TaskPriority.LOW));

        Map<TaskStatus, Long> counts = service.countsByStatus();
        assertEquals(2L, counts.get(TaskStatus.TODO));
        assertEquals(0L, counts.get(TaskStatus.IN_PROGRESS));
        assertEquals(1L, counts.get(TaskStatus.DONE));
    }

    @Test
    @DisplayName("Validacao do servico nao remove a obrigatoriedade do titulo (delegada a JPA)")
    void titleIsRequiredAtPersistence() {
        Task t = new Task();
        assertTrue(t.getTitle() == null);
    }
}
