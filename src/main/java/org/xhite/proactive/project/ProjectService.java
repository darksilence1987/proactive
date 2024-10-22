package org.xhite.proactive.project;

import java.util.List;

public interface ProjectService {
    List<Project> getProjectsByUser(String name);

    List<Project> getProjectsOwnedByUser(String name);

    Project getProjectById(Long id);

    List<Project> getAllProjects();
}
