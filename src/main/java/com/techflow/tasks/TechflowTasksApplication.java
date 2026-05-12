package com.techflow.tasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicacao TechFlow Tasks.
 *
 * <p>Sistema de gerenciamento de tarefas baseado em Kanban,
 * desenvolvido para a startup de logistica cliente da TechFlow Solutions.
 */
@SpringBootApplication
public class TechflowTasksApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechflowTasksApplication.class, args);
    }
}
