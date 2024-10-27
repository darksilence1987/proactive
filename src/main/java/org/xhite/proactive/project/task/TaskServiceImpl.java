package org.xhite.proactive.project.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.xhite.proactive.project.Project;
import org.xhite.proactive.user.AppUser;
import org.xhite.proactive.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService{
    private final TaskRepository taskRepository;
    private final UserService userService;

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
}
