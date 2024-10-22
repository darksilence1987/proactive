package org.xhite.proactive.project.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.xhite.proactive.project.Project;
import org.xhite.proactive.user.AppUser;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByAssignedTo(AppUser user);

    List<Task> getTasksByProject(Project project);
}
