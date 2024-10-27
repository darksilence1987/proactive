package org.xhite.proactive.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AppUserDTO {
    private Long id;
    private String username;
    private String email;
    private String password;
    private UserStatus status;
    private List<String> roles;

}
