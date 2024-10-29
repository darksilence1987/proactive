package org.xhite.proactive.project.task;

import org.xhite.proactive.project.Project;

import java.util.List;

public interface TaskService {
    List<Task> getTasksByUser(String name);

    List<Task> getTasksByProject(Project project);

    Task getTaskById(Long id);

    void assignTask(Long projectId, Long taskId, String username, String username1);

    void createTask(Long id, TaskCreateDTO taskCreateDTO, String username);

    void completeTask(Long projectId, Long taskId, String username);
}
