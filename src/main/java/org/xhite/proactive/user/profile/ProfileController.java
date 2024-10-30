package org.xhite.proactive.user.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.xhite.proactive.project.ProjectService;
import org.xhite.proactive.user.UserService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;
    private final ProjectService projectService;

    @GetMapping("/profile/{username}")
    public String profile(@PathVariable String username, Model model) {
        model.addAttribute("user", userService.getUserByUsername(username));
        model.addAttribute("projects", projectService.getProjectsByUser(username)); // Fetch projects
        return "profile";

    }
}