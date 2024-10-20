package org.xhite.proactive.project.task;


import jakarta.persistence.*;
import org.xhite.proactive.project.Project;
import org.xhite.proactive.user.AppUser;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String taskName;
    private String taskDescription;
    private TaskStatus status;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser assignedTo;
}
