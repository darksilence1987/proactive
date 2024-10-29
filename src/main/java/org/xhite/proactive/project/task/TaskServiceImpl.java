package org.xhite.proactive.project.task;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.xhite.proactive.project.Project;
import org.xhite.proactive.project.ProjectService;
import org.xhite.proactive.project.task.dto.TaskCreateDTO;
import org.xhite.proactive.project.task.dto.TaskUpdateDTO;
import org.xhite.proactive.user.AppUser;
import org.xhite.proactive.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService{
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final ProjectService projectService;

    @Override
    public List<Task> getTasksByUser(String name) {
        AppUser user = userService.getUserByUsername(name);
        return taskRepository.findAllByAssignedTo(user);
    }

    @Override
    public List<Task> getTasksByProject(Project project) {
        return taskRepository.getTasksByProject(project);
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public void assignTask(Long projectId, Long taskId, String username, String managerUsername) {
        Project project = projectService.getProjectById(projectId);
        if (!project.getCreatedBy().getUsername().equals(managerUsername)) {
            throw new AccessDeniedException("Only project manager can assign tasks");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        AppUser assignee = userService.getUserByUsername(username);
        task.setAssignedTo(assignee);
        task.setStatus(TaskStatus.IN_PROGRESS);

        taskRepository.save(task);
    }

    @Override
    public void createTask(Long projectId, TaskCreateDTO dto, String creatorUsername) {
        Project project = projectService.getProjectById(projectId);
        if (!project.getCreatedBy().getUsername().equals(creatorUsername)) {
            throw new AccessDeniedException("Only project manager can create tasks");
        }

        Task task = Task.builder()
                .taskName(dto.getTaskName())
                .taskDescription(dto.getTaskDescription())
                .priority(dto.getPriority())
                .status(TaskStatus.TODO)
                .project(project)
                .build();

        taskRepository.save(task);
    }

    @Override
    public void updateTask(Long projectId, Long taskId, TaskUpdateDTO dto, String username) {
        Project project = projectService.getProjectById(projectId);
        if (!project.getCreatedBy().getUsername().equals(username)) {
            throw new AccessDeniedException("Only project manager can update tasks");
        }

        Task task = getTaskById(taskId);
        if (task == null || !task.getProject().getId().equals(projectId)) {
            throw new RuntimeException("Task not found");
        }

        task.setTaskName(dto.getTaskName());
        task.setTaskDescription(dto.getTaskDescription());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());

        taskRepository.save(task);
    }

    @Override
    public void completeTask(Long projectId, Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.getAssignedTo() != null &&
                !task.getAssignedTo().getUsername().equals(username)) {
            throw new AccessDeniedException("Only assigned user can complete the task");
        }

        task.setStatus(TaskStatus.DONE);
        taskRepository.save(task);
    }
}
