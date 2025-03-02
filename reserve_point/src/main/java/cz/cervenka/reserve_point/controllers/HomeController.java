package cz.cervenka.reserve_point.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String redirectToLogin() {
        return "loginForm";
    }

    @PostMapping("/logout")
    public String logout() {
        return "redirect:/loginForm";
    }

    @GetMapping("/home")
    public String showHome() {
        return "home";
    }
}