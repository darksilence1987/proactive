package org.xhite.proactive.user.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.xhite.proactive.user.UserService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;
    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        model.addAttribute("user", userService.getUserByUsername(principal.getName()));
        return "profile";
    }
}
