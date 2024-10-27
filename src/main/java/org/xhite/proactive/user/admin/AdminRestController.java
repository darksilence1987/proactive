package org.xhite.proactive.user.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.xhite.proactive.user.AppUser;
import org.xhite.proactive.user.AppUserDTO;
import org.xhite.proactive.user.UserService;
import org.xhite.proactive.user.UserStatus;
import org.xhite.proactive.user.role.RoleName;
import org.xhite.proactive.user.role.RoleRepository;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @GetMapping("/get-user/{id}")
    public ResponseEntity<AppUserDTO> getUser(@PathVariable Long id) {
        AppUser appUser = userService.getUserById(id);
        return ResponseEntity.ok(AppUserDTO.builder()
                .id(appUser.getId())
                .username(appUser.getUsername())
                .email(appUser.getEmail())
                .password(appUser.getPassword())
                .status(appUser.getStatus())
                .roles(appUser.getRoles().stream()
                        .map(role -> role.getRoleName().name())
                        .collect(toList()))
                .build());
    }

    @PostMapping("/save-user")
    public ResponseEntity<Boolean> saveUser(@RequestBody AppUserDTO appUserDTO) {
        AppUser appUser = AppUser.builder()
                .username(appUserDTO.getUsername())
                .password(passwordEncoder.encode(appUserDTO.getPassword()))
                .email(appUserDTO.getEmail())
                .status(UserStatus.PENDING)
                .roles(appUserDTO.getRoles().stream()
                        .map(roleName ->roleRepository.findByRoleName(RoleName.valueOf(roleName)).orElseThrow())
                        .collect(toSet()))
                .build();
        userService.save(appUser);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/update-user/{id}")
    public ResponseEntity<Boolean> updateUser(@PathVariable Long id, @RequestBody AppUserDTO appUserDTO) {
        AppUser appUser = userService.getUserById(id);
        appUser.setPassword(passwordEncoder.encode(appUserDTO.getPassword()));
        appUser.setEmail(appUserDTO.getEmail());
        appUser.setRoles(appUserDTO.getRoles().stream()
                .map(roleName -> roleRepository.findByRoleName(RoleName.valueOf(roleName)).orElseThrow())
                .collect(toSet()));
        userService.save(appUser);
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(true);
    }

    @PutMapping("/reject-user/{id}")
    public ResponseEntity<Boolean> rejectUser(@PathVariable Long id) {
        userService.changeUserStatus(id, UserStatus.DELETED);
        return ResponseEntity.ok(true);
    }

    @PutMapping("/approve-user/{id}")
    public ResponseEntity<Boolean> approveUser(@PathVariable Long id) {
        userService.changeUserStatus(id, UserStatus.ACTIVE);
        return ResponseEntity.ok(true);
    }

}
