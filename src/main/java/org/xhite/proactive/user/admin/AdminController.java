package org.xhite.proactive.user.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.xhite.proactive.user.UserService;
import org.xhite.proactive.user.UserStatus;


@Controller
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping("/admin/user-administration")
    public String userAdministration(Model model) {
        model.addAttribute("users", userService.getUsersByStatus(UserStatus.ACTIVE));
        model.addAttribute("pendingUsers", userService.getUsersByStatus(UserStatus.PENDING));
        return "user-administration";
    }

}
