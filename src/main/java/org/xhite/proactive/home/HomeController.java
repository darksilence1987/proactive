package org.xhite.proactive.home;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String homePage(){
        return "redirect:projects";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }


}
