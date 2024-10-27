package org.xhite.proactive.project.task;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    @GetMapping("/my-tasks")
    public String getMyTasksPage(Model model, @AuthenticationPrincipal UserDetails principal) {
        var tasks = taskService.getTasksByUser(principal.getUsername());
        model.addAttribute("tasks", tasks);
        return "tasks";

    }

    @GetMapping("/tasks/{id}")
    public String taskDetails(@PathVariable Long id, Model model) {
        var task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        return "task-detail";
    }
}
