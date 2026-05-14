package com.techflow.tasks.controller;

import com.techflow.tasks.model.Task;
import com.techflow.tasks.model.TaskPriority;
import com.techflow.tasks.model.TaskStatus;
import com.techflow.tasks.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador web (Thymeleaf) que entrega o quadro Kanban interativo.
 */
@Controller
public class TaskWebController {

    private final TaskService service;

    public TaskWebController(TaskService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/board";
    }

    @GetMapping("/board")
    public String board(Model model) {
        model.addAttribute("board", service.kanbanBoard());
        model.addAttribute("counts", service.countsByStatus());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        return "board";
    }

    @GetMapping("/tasks/new")
    public String newForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("editing", false);
        return "form";
    }

    @PostMapping("/tasks")
    public String create(@Valid @ModelAttribute("task") Task task,
                         BindingResult result,
                         Model model,
                         RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("editing", false);
            return "form";
        }
        Task saved = service.create(task);
        flash.addFlashAttribute("flashSuccess", "Tarefa #" + saved.getId() + " criada com sucesso.");
        return "redirect:/board";
    }

    @GetMapping("/tasks/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("task", service.findById(id));
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("editing", true);
        return "form";
    }

    @PostMapping("/tasks/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("task") Task task,
                         BindingResult result,
                         Model model,
                         RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("editing", true);
            return "form";
        }
        service.update(id, task);
        flash.addFlashAttribute("flashSuccess", "Tarefa #" + id + " atualizada.");
        return "redirect:/board";
    }

    @PostMapping("/tasks/{id}/move")
    public String move(@PathVariable Long id,
                       @RequestParam TaskStatus to,
                       RedirectAttributes flash) {
        service.moveTo(id, to);
        flash.addFlashAttribute("flashSuccess", "Tarefa #" + id + " movida para " + to.getLabel() + ".");
        return "redirect:/board";
    }

    @PostMapping("/tasks/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        service.delete(id);
        flash.addFlashAttribute("flashSuccess", "Tarefa #" + id + " removida.");
        return "redirect:/board";
    }
}
