package org.xhite.proactive.project;

import jakarta.persistence.*;
import org.xhite.proactive.project.task.Task;
import org.xhite.proactive.user.AppUser;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String projectName;
    private String projectDescription;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> projectTasks = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    private AppUser createdBy;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "project_members",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private List<AppUser> projectMembers = new ArrayList<>();
}
