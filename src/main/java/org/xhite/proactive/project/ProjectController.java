package org.xhite.proactive.project;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.xhite.proactive.project.task.Task;
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
}
