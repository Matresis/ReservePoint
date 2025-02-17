package cz.cervenka.reserve_point.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping("/")
    public String redirectToLogin() {
        return "loginForm";
    }

    /*@GetMapping("/make-reservation")
    public String showReservationForm() {
        return "reserveForm";
    }*/

    @PostMapping("/logout")
    public String logout() {
        return "loginForm";
    }

    @PostMapping("/admin/logout")
    public String logoutAdmin() {
        return "redirect:/loginForm";
    }

    @GetMapping("/home")
    public String showHome() {
        return "home";
    }

    @GetMapping("/admin/home")
    public String showAdminHome() {
        return "admin/home";
    }
}