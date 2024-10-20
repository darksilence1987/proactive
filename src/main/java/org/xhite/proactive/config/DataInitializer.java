package org.xhite.proactive.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.xhite.proactive.user.role.Role;
import org.xhite.proactive.user.role.RoleName;
import org.xhite.proactive.user.role.RoleRepository;

import java.util.Arrays;

@Configuration
public class DataInitializer {
    @Autowired
    private RoleRepository roleRepository;

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
}
