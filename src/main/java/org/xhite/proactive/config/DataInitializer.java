package org.xhite.proactive.config;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.xhite.proactive.project.Project;
import org.xhite.proactive.project.ProjectRepository;
import org.xhite.proactive.project.task.Task;
import org.xhite.proactive.project.task.TaskPriority;
import org.xhite.proactive.project.task.TaskRepository;
import org.xhite.proactive.project.task.TaskStatus;
import org.xhite.proactive.report.JasperReportManager;
import org.xhite.proactive.report.Report;
import org.xhite.proactive.report.ReportRepository;
import org.xhite.proactive.report.ReportType;
import org.xhite.proactive.user.AppUser;
import org.xhite.proactive.user.UserRepository;
import org.xhite.proactive.user.UserService;
import org.xhite.proactive.user.UserStatus;
import org.xhite.proactive.user.role.Role;
import org.xhite.proactive.user.role.RoleName;
import org.xhite.proactive.user.role.RoleRepository;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ReportRepository reportRepository;
    private final EntityManager entityManager;


    @PostConstruct
    public void initialize() throws JRException, FileNotFoundException {
        initializeRoles();
        initalizeTestUser();
        initializeAppUser();
        initializeProjectManager();
        initializeAdmin();
        initializeProjectsAndTasks();
    }



    private void initializeRoles(){
        Arrays.stream(RoleName.values())
                .filter(roleName -> !roleRepository.existsByRoleName(roleName))
                .forEach(roleName -> {
                    Role role = new Role();
                    role.setRoleName(roleName);
                    role.setDescription(roleName.name().replace("ROLE_", "").toLowerCase());
                    roleRepository.save(role);

                });
    }


    private void initalizeTestUser(){
        AppUser user = new AppUser();
        user.setUsername("xhite");
        user.setEmail("kaankara@gmail.com");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setStatus(UserStatus.ACTIVE);
        Role role = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
        role = roleRepository.findByRoleName(RoleName.ROLE_PROJECT_MANAGER)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
        role = roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
        userRepository.save(user);
    }

    private void initializeAppUser(){
        AppUser user = new AppUser();
        user.setUsername("user");
        user.setEmail("user@gmail.com");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setStatus(UserStatus.ACTIVE);
        Role role = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
        userRepository.save(user);
    }

    private void initializeProjectManager(){
        AppUser user = new AppUser();
        user.setUsername("pm");
        user.setEmail("manager@gmail.com");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setStatus(UserStatus.ACTIVE);
        Role role = roleRepository.findByRoleName(RoleName.ROLE_PROJECT_MANAGER)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
        userRepository.save(user);
    }

    private void initializeAdmin(){
        AppUser user = new AppUser();
        user.setUsername("admin");
        user.setEmail("admin@gmail.com");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setStatus(UserStatus.ACTIVE);
        Role role = roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
        userRepository.save(user);
    }
    private void initializeProjectsAndTasks() {
        AppUser xhite = userRepository.findByUsername("xhite")
                .orElseThrow(() -> new RuntimeException("Test user 'xhite' not found!"));
        AppUser user = userRepository.findByUsername("user")
                .orElseThrow(() -> new RuntimeException("Test user 'user' not found!"));

        // Proje 1
        Project project1 = Project.builder()
                .projectName("Proje X")
                .projectDescription("Örnek proje")
                .createdBy(xhite)
                .build();
        project1.getProjectMembers().add(xhite);

        // Proje 2
        Project project2 = Project.builder()
                .projectName("Proje Y")
                .projectDescription("Başka bir örnek proje")
                .createdBy(xhite)
                .build();
        project2.getProjectMembers().add(xhite);
        project2.getProjectMembers().add(user);

        // Görev 1 (Proje 1'e ait)
        Task task1 = Task.builder()
                .taskName("Görev 1")
                .taskDescription("Proje X için ilk görev")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.HIGH)
                .project(project1)
                .assignedTo(xhite)
                .build();

        // Görev 2 (Proje 2'ye ait)
        Task task2 = Task.builder()
                .taskName("Görev 2")
                .taskDescription("Proje Y için ilk görev")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .project(project2)
                .assignedTo(user)
                .build();

        projectRepository.saveAll(List.of(project1, project2));
        taskRepository.saveAll(List.of(task1, task2));
    }
}
