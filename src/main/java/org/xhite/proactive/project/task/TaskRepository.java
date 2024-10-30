package org.xhite.proactive.project.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.xhite.proactive.project.Project;
import org.xhite.proactive.user.AppUser;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.project = :project AND t.status != 'DELETED' AND t.status != 'DONE' ORDER BY t.createdAt DESC")
    List<Task> findActiveTasksByProject(Project project);

    @Query("SELECT t FROM Task t WHERE t.project = :project AND t.status = 'DONE' ORDER BY t.createdAt DESC")
    List<Task> findCompletedTasksByProject(Project project);

    @Query("SELECT t FROM Task t WHERE t.assignedTo = :user AND t.status != 'DELETED' AND t.status != 'DONE' ORDER BY t.createdAt DESC")
    List<Task> findActiveTasksByUser(AppUser user);

    @Query("SELECT t FROM Task t WHERE t.assignedTo = :user AND t.status = 'DONE' ORDER BY t.createdAt DESC")
    List<Task> findCompletedTasksByUser(AppUser user);

    long countByAssignedTo(AppUser user);
    long countByAssignedToAndStatus(AppUser user, TaskStatus status);
}
