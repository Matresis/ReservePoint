package cz.cervenka.rp_backend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping("/")
    public String redirectToRegister() {
        return "login";
    }

    @GetMapping("/make-reservation")
    public String showReservationForm() {
        return "reserveForm";
    }

    @PostMapping("/logout")
    public String logout() {
        // Logic to handle session invalidation if needed
        return "login";
    }

    @GetMapping("/home")
    public String showHome() {
        return "home";
    }
}