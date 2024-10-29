package org.xhite.proactive.project.task;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.xhite.proactive.project.task.comment.Comment;
import org.xhite.proactive.project.task.comment.CommentDTO;
import org.xhite.proactive.project.task.comment.CommentService;
import org.xhite.proactive.project.task.dto.TaskUpdateDTO;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final CommentService commentService;

    @GetMapping("/my-tasks")
    public String getMyTasksPage(Model model, @AuthenticationPrincipal UserDetails principal) {
        var activeTasks = taskService.getActiveTasksByUser(principal.getUsername());
        var completedTasks = taskService.getCompletedTasksByUser(principal.getUsername());
        model.addAttribute("activeTasks", activeTasks);
        model.addAttribute("completedTasks", completedTasks);
        return "tasks";

    }

    @GetMapping("/projects/{projectId}/tasks/{taskId}")
    public String getTaskDetail(@PathVariable Long projectId,
                                @PathVariable Long taskId,
                                Model model) {
        Task task = taskService.getTaskById(taskId);
        List<Comment> comments = commentService.getTaskComments(taskId);

        model.addAttribute("task", task);
        model.addAttribute("projectId", projectId);
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", new CommentDTO());
        return "task-detail";
    }

    @PreAuthorize("hasRole('ROLE_PROJECT_MANAGER')")
    @PostMapping("/projects/{projectId}/tasks/{taskId}/update")
    public String updateTask(@PathVariable Long projectId,
                             @PathVariable Long taskId,
                             @ModelAttribute TaskUpdateDTO taskUpdateDTO,
                             @AuthenticationPrincipal UserDetails principal) {
        taskService.updateTask(projectId, taskId, taskUpdateDTO, principal.getUsername());
        return "redirect:/projects/" + projectId + "/tasks/" + taskId;
    }

    @PostMapping("/projects/{projectId}/tasks/{taskId}/comments")
    public String addComment(@PathVariable Long projectId,
                             @PathVariable Long taskId,
                             @ModelAttribute("newComment") CommentDTO commentDTO,
                             @AuthenticationPrincipal UserDetails principal) {
        commentService.addComment(taskId, commentDTO, principal.getUsername());
        return "redirect:/projects/" + projectId + "/tasks/" + taskId;
    }

    @PostMapping("/projects/{projectId}/tasks/{taskId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long projectId,
                                @PathVariable Long taskId,
                                @PathVariable Long commentId,
                                @AuthenticationPrincipal UserDetails principal) {
        commentService.deleteComment(commentId, principal.getUsername());
        return "redirect:/projects/" + projectId + "/tasks/" + taskId;
    }
}
