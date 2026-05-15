package com.techflow.tasks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techflow.tasks.model.Task;
import com.techflow.tasks.model.TaskPriority;
import com.techflow.tasks.model.TaskStatus;
import com.techflow.tasks.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TaskRepository repository;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/tasks cria tarefa e retorna 201 com Location")
    void createTask() throws Exception {
        Task t = new Task("Nova", "desc", TaskStatus.TODO, TaskPriority.HIGH);

        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(t)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Nova"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    @DisplayName("POST /api/tasks com titulo em branco retorna 400")
    void createTaskWithBlankTitle() throws Exception {
        Task t = new Task("", null, TaskStatus.TODO, TaskPriority.LOW);

        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(t)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/tasks/{id} retorna tarefa existente")
    void getById() throws Exception {
        Task saved = repository.save(new Task("Visivel", null, TaskStatus.TODO, TaskPriority.LOW));

        mvc.perform(get("/api/tasks/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Visivel"));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} com id inexistente retorna 404")
    void getByIdNotFound() throws Exception {
        mvc.perform(get("/api/tasks/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id}/status?to=DONE move a tarefa")
    void moveTask() throws Exception {
        Task saved = repository.save(new Task("Mover", null, TaskStatus.TODO, TaskPriority.LOW));

        mvc.perform(patch("/api/tasks/" + saved.getId() + "/status").param("to", "DONE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} retorna 204 e remove o registro")
    void deleteTask() throws Exception {
        Task saved = repository.save(new Task("Excluir", null, TaskStatus.TODO, TaskPriority.LOW));

        mvc.perform(delete("/api/tasks/" + saved.getId()))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/tasks/" + saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/tasks lista todas as tarefas em JSON")
    void listAll() throws Exception {
        repository.save(new Task("a", null, TaskStatus.TODO, TaskPriority.LOW));
        repository.save(new Task("b", null, TaskStatus.DONE, TaskPriority.HIGH));

        mvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
