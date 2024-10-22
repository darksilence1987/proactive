package org.xhite.proactive.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.xhite.proactive.user.AppUser;
import org.xhite.proactive.user.UserRepository;
import org.xhite.proactive.user.UserStatus;
import org.xhite.proactive.user.role.Role;
import org.xhite.proactive.user.role.RoleName;
import org.xhite.proactive.user.role.RoleRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeRoles(){
        Arrays.stream(RoleName.values())
                .filter(roleName -> !roleRepository.existsByName(roleName))
                .forEach(roleName -> {
                    Role role = new Role();
                    role.setName(roleName);
                    role.setDescription(roleName.name().replace("ROLE_", "").toLowerCase());
                    roleRepository.save(role);

                });
    }

    @PostConstruct
    public void initalizeTestUser(){
        AppUser user = new AppUser();
        user.setUsername("xhite");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setStatus(UserStatus.ACTIVE);
        Role role = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
        role = roleRepository.findByName(RoleName.ROLE_PROJECT_MANAGER)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
        role = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
        userRepository.save(user);
    }
}
