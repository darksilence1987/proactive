package org.xhite.proactive.project;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.xhite.proactive.project.task.Task;
import org.xhite.proactive.project.task.TaskCreateDTO;
import org.xhite.proactive.project.task.TaskService;
import org.xhite.proactive.user.AppUser;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final TaskService taskService;

    @GetMapping("/projects")
    public String getProjectsPage(Model model, @AuthenticationPrincipal UserDetails principal) {
        List<Project> projectsOwned = projectService.getProjectsOwnedByUser(principal.getUsername());
        List<Project> projects = projectService.getProjectsByUser(principal.getUsername());

        Set<Project> allProjects = new HashSet<>();
        allProjects.addAll(projectsOwned);
        allProjects.addAll(projects);

        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            model.addAttribute("allProjects", projectService.getAllProjects());
        } else {
            model.addAttribute("allProjects", allProjects);
        }
        return "projects";

    }

    @GetMapping("/projects/{id}")
    public String projectDetail(@PathVariable Long id, Model model) {
        Project project = projectService.getProjectById(id);
        List<Task> tasks = taskService.getTasksByProject(project);
        List<AppUser> members = project.getProjectMembers();

        model.addAttribute("project", project);
        model.addAttribute("tasks", tasks);
        model.addAttribute("members", members);
        return "project-detail";
    }

    @PreAuthorize("hasRole('ROLE_PROJECT_MANAGER')")
    @GetMapping("/projects/create")
    public String showCreateProjectPage() {
        return "create-project";
    }

    @PreAuthorize("hasRole('ROLE_PROJECT_MANAGER')")
    @PostMapping("/projects/create")
    public String createProject(@ModelAttribute ProjectCreateDTO projectCreateDTO,
                                @AuthenticationPrincipal UserDetails principal) {
        projectService.createProject(projectCreateDTO, principal.getUsername());
        return "redirect:/projects";
    }

    @PreAuthorize("hasRole('ROLE_PROJECT_MANAGER')")
    @PostMapping("/projects/{id}/members")
    public String addProjectMember(@PathVariable Long id,
                                   @RequestParam String username,
                                   @AuthenticationPrincipal UserDetails principal) {
        projectService.addMemberToProject(id, username, principal.getUsername());
        return "redirect:/projects/" + id;
    }

    @PreAuthorize("hasRole('ROLE_PROJECT_MANAGER')")
    @PostMapping("/projects/{projectId}/members/{username}/remove")
    public String removeProjectMember(@PathVariable Long projectId,
                                      @PathVariable String username,
                                      @AuthenticationPrincipal UserDetails principal) {
        projectService.removeMemberFromProject(projectId, username, principal.getUsername());
        return "redirect:/projects/" + projectId;
    }

    @PreAuthorize("hasRole('ROLE_PROJECT_MANAGER')")
    @GetMapping("/projects/{id}/tasks/create")
    public String showCreateTaskForm(@PathVariable Long id, Model model) {
        model.addAttribute("projectId", id);
        return "create-task";
    }

    @PreAuthorize("hasRole('ROLE_PROJECT_MANAGER')")
    @PostMapping("/projects/{id}/tasks/create")
    public String createTask(@PathVariable Long id,
                             @ModelAttribute TaskCreateDTO taskCreateDTO,
                             @AuthenticationPrincipal UserDetails principal) {
        taskService.createTask(id, taskCreateDTO, principal.getUsername());
        return "redirect:/projects/" + id;
    }

    @PreAuthorize("hasRole('ROLE_PROJECT_MANAGER')")
    @PostMapping("/projects/{projectId}/tasks/{taskId}/assign")
    public String assignTask(@PathVariable Long projectId,
                             @PathVariable Long taskId,
                             @RequestParam String username,
                             @AuthenticationPrincipal UserDetails principal) {
        taskService.assignTask(projectId, taskId, username, principal.getUsername());
        return "redirect:/projects/" + projectId;
    }

    @PostMapping("/projects/{projectId}/tasks/{taskId}/complete")
    public String completeTask(@PathVariable Long projectId,
                               @PathVariable Long taskId,
                               @AuthenticationPrincipal UserDetails principal) {
        taskService.completeTask(projectId, taskId, principal.getUsername());
        return "redirect:/projects/" + projectId;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        model.addAttribute("error", "Bu işlemi yapmaya yetkiniz yok");
        return "error";
    }

}
