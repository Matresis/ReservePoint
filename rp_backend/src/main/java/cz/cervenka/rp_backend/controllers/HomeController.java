package cz.cervenka.rp_backend.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping("/")
    public String redirectToRegister() {
        return "loginForm";
    }

    @GetMapping("/make-reservation")
    public String showReservationForm() {
        return "reserveForm";
    }

    @PostMapping("/logout")
    public String logout() {
        return "loginForm";
    }

    @GetMapping("/home")
    public String showHome() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User authenticated: " + auth.isAuthenticated() + " with roles: " + auth.getAuthorities());
        return "home";
    }

    @GetMapping("/admin/home")
    public String showAdminHome() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User authenticated: " + auth.isAuthenticated() + " with roles: " + auth.getAuthorities());
        return "admin/home";
    }

}