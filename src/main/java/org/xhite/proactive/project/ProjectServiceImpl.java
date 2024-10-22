package org.xhite.proactive.project;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
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
}
