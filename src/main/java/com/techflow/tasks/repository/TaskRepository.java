package com.techflow.tasks.repository;

import com.techflow.tasks.model.Task;
import com.techflow.tasks.model.TaskPriority;
import com.techflow.tasks.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acesso a dados de tarefas via Spring Data JPA.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatusOrderByPriorityDescCreatedAtAsc(TaskStatus status);

    List<Task> findByPriorityOrderByCreatedAtAsc(TaskPriority priority);

    long countByStatus(TaskStatus status);
}
