package org.xhite.proactive.user.role;


import lombok.Getter;

@Getter
public enum RoleName {
    ROLE_USER("user"),
    ROLE_PROJECT_MANAGER("project manager"),
    ROLE_ADMIN("admin");

    private final String description;

    RoleName(String description) {
        this.description = description;
    }

}
