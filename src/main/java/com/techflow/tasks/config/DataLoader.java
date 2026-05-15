package com.techflow.tasks.config;

import com.techflow.tasks.model.Task;
import com.techflow.tasks.model.TaskPriority;
import com.techflow.tasks.model.TaskStatus;
import com.techflow.tasks.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.List;

/**
 * Carrega dados iniciais para a demonstracao do sistema (ambiente "default" / "dev").
 *
 * <p>Os exemplos refletem o contexto da startup de logistica cliente
 * da TechFlow Solutions e cobrem todas as colunas do Kanban e prioridades.
 */
@Configuration
@Profile("!test")
public class DataLoader {

    @Bean
    public CommandLineRunner seedTasks(TaskRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            List<Task> seed = List.of(
                    build("Configurar dashboard operacional",
                            "Tela inicial com KPIs de entregas em tempo real.",
                            TaskStatus.TODO, TaskPriority.HIGH, "Equipe de UX",
                            LocalDate.now().plusDays(7)),
                    build("Integrar API de rastreamento",
                            "Consumir webhook da transportadora parceira.",
                            TaskStatus.TODO, TaskPriority.CRITICAL, "Backend",
                            LocalDate.now().plusDays(3)),
                    build("Documentar API publica",
                            "Gerar contrato OpenAPI 3.1 para os clientes B2B.",
                            TaskStatus.TODO, TaskPriority.MEDIUM, "Backend",
                            LocalDate.now().plusDays(14)),
                    build("Implementar CRUD de tarefas",
                            "CRUD completo no backend com validacoes.",
                            TaskStatus.IN_PROGRESS, TaskPriority.HIGH, "Backend",
                            LocalDate.now().plusDays(2)),
                    build("Criar quadro Kanban responsivo",
                            "Layout em colunas com drag-and-drop futuramente.",
                            TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM, "Frontend",
                            LocalDate.now().plusDays(5)),
                    build("Definir politica de SLA",
                            "Acordo de nivel de servico para entregas urgentes.",
                            TaskStatus.IN_PROGRESS, TaskPriority.LOW, "Produto",
                            LocalDate.now().plusDays(20)),
                    build("Levantar requisitos com cliente",
                            "Reuniao inicial e mapa de personas.",
                            TaskStatus.DONE, TaskPriority.MEDIUM, "Produto", null),
                    build("Configurar repositorio GitHub",
                            "Repositorio publico com Projects e protecao de branch.",
                            TaskStatus.DONE, TaskPriority.MEDIUM, "DevOps", null),
                    build("Pipeline de CI inicial",
                            "GitHub Actions executando build e testes a cada PR.",
                            TaskStatus.DONE, TaskPriority.HIGH, "DevOps", null),
                    build("Definir arquitetura inicial",
                            "Stack Java + Spring Boot + JPA escolhida.",
                            TaskStatus.DONE, TaskPriority.HIGH, "Arquiteto", null),
                    build("[Mudanca de escopo] Filtros por prioridade",
                            "Permitir filtrar o Kanban por prioridade da tarefa.",
                            TaskStatus.TODO, TaskPriority.HIGH, "Frontend",
                            LocalDate.now().plusDays(10)),
                    build("[Mudanca de escopo] Indicador de atraso",
                            "Destacar visualmente tarefas com prazo vencido.",
                            TaskStatus.IN_PROGRESS, TaskPriority.CRITICAL, "Frontend",
                            LocalDate.now().minusDays(1))
            );
            repository.saveAll(seed);
        };
    }

    private Task build(String title, String description, TaskStatus status,
                       TaskPriority priority, String assignee, LocalDate dueDate) {
        Task t = new Task(title, description, status, priority);
        t.setAssignee(assignee);
        t.setDueDate(dueDate);
        return t;
    }
}
