package org.xhite.proactive.project;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.xhite.proactive.user.AppUser;
import org.xhite.proactive.user.UserService;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;
    private final UserService userService;


    @Override
    public List<Project> getProjectsByUser(String name) {
        AppUser user = userService.getUserByUsername(name);
        if(user == null) return new ArrayList<>();
        Long userId = user.getId();

        return projectRepository.findProjectsByMemberId(userId);
    }

    @Override
    public List<Project> getProjectsOwnedByUser(String name) {
        AppUser user = userService.getUserByUsername(name);
        if(user == null) return new ArrayList<>();
        return projectRepository.findProjectsByCreatedBy(user);
    }

    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findProjectById(id);
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Project createProject(ProjectCreateDTO projectCreateDTO, String username) {
        AppUser owner = userService.getUserByUsername(username);

        Project project = new Project();
        project.setProjectName(projectCreateDTO.getProjectName());
        project.setProjectDescription(projectCreateDTO.getProjectDescription());
        project.setCreatedBy(owner);
        project.getProjectMembers().add(owner);

        return projectRepository.save(project);
    }

    public void addMemberToProject(Long projectId, String username, String ownerUsername) {
        Project project = getProjectById(projectId);

        // Verify that the requester is the project owner
        if (!project.getCreatedBy().getUsername().equals(ownerUsername)) {
            throw new AccessDeniedException("Only project owner can add members");
        }

        AppUser newMember = userService.getUserByUsername(username);

        if (!project.getProjectMembers().contains(newMember)) {
            project.getProjectMembers().add(newMember);
            projectRepository.save(project);
        }
    }

    @Override
    public void removeMemberFromProject(Long projectId, String username, String username1) {
        Project project = getProjectById(projectId);

        // Verify that the requester is the project owner
        if (!project.getCreatedBy().getUsername().equals(username1)) {
            throw new AccessDeniedException("Only project owner can remove members");
        }

        AppUser member = userService.getUserByUsername(username);

        if (project.getProjectMembers().contains(member)) {
            project.getProjectMembers().remove(member);
            projectRepository.save(project);
        }
    }
}
