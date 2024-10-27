package org.xhite.proactive.project.task;

import org.xhite.proactive.project.Project;

import java.util.List;

public interface TaskService {
    List<Task> getTasksByUser(String name);

    List<Task> getTasksByProject(Project project);

    Task getTaskById(Long id);
}
