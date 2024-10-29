package org.xhite.proactive.project.task;

import org.xhite.proactive.project.Project;
import org.xhite.proactive.project.task.dto.TaskCreateDTO;
import org.xhite.proactive.project.task.dto.TaskUpdateDTO;

import java.util.List;

public interface TaskService {
    List<Task> getActiveTasksByUser(String name);

    List<Task> getActiveTasksByProject(Project project);

    Task getTaskById(Long id);

    void assignTask(Long projectId, Long taskId, String username, String username1);

    void createTask(Long id, TaskCreateDTO taskCreateDTO, String username);

    void completeTask(Long projectId, Long taskId, String username);

    void updateTask(Long projectId, Long taskId, TaskUpdateDTO taskUpdateDTO, String username);

    List<Task> getCompletedTasksByUser(String username);

    List<Task> getCompletedTasksByProject(Project project);
}
