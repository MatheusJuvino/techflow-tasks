package com.techflow.tasks.service;

import com.techflow.tasks.exception.TaskNotFoundException;
import com.techflow.tasks.model.Task;
import com.techflow.tasks.model.TaskPriority;
import com.techflow.tasks.model.TaskStatus;
import com.techflow.tasks.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Camada de servico responsavel pelas regras de negocio do CRUD de tarefas
 * e pela montagem da visao Kanban.
 */
@Service
@Transactional
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> findAll() {
        return repository.findAll();
    }

    public Task findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task create(Task task) {
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }
        if (task.getPriority() == null) {
            task.setPriority(TaskPriority.MEDIUM);
        }
        task.setId(null);
        return repository.save(task);
    }

    public Task update(Long id, Task changes) {
        Task existing = findById(id);
        existing.setTitle(changes.getTitle());
        existing.setDescription(changes.getDescription());
        if (changes.getStatus() != null) {
            existing.setStatus(changes.getStatus());
        }
        if (changes.getPriority() != null) {
            existing.setPriority(changes.getPriority());
        }
        existing.setAssignee(changes.getAssignee());
        existing.setDueDate(changes.getDueDate());
        existing.touch();
        return repository.save(existing);
    }

    public Task moveTo(Long id, TaskStatus newStatus) {
        Task existing = findById(id);
        existing.setStatus(newStatus);
        existing.touch();
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        repository.deleteById(id);
    }

    /**
     * Monta uma visao agregada do Kanban: para cada coluna, a lista de tarefas
     * ordenadas por prioridade (decrescente) e data de criacao (crescente).
     */
    @Transactional(readOnly = true)
    public Map<TaskStatus, List<Task>> kanbanBoard() {
        Map<TaskStatus, List<Task>> board = new EnumMap<>(TaskStatus.class);
        for (TaskStatus status : TaskStatus.values()) {
            board.put(status, repository.findByStatusOrderByPriorityDescCreatedAtAsc(status));
        }
        return board;
    }

    @Transactional(readOnly = true)
    public Map<TaskStatus, Long> countsByStatus() {
        Map<TaskStatus, Long> counts = new EnumMap<>(TaskStatus.class);
        for (TaskStatus status : TaskStatus.values()) {
            counts.put(status, repository.countByStatus(status));
        }
        return counts;
    }
}
