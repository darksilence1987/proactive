package org.xhite.proactive.project;

import java.util.List;

public interface ProjectService {
    List<Project> getProjectsByUser(String name);

    List<Project> getProjectsOwnedByUser(String name);

    Project getProjectById(Long id);

    List<Project> getAllProjects();

    void createProject(ProjectCreateDTO projectCreateDTO, String username);

    void addMemberToProject(Long id, String username, String owner);

    void removeMemberFromProject(Long projectId, String username, String username1);

    void deleteProject(Long id, String username);
}
