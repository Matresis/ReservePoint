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
        return "registerPage"; // Redirects to the registration page
    }

    @GetMapping("/make-reservation")
    public String showReservationForm() {
        return "reserveForm"; // Name of the HTML file for making a reservation
    }

    @PostMapping("/logout")
    public String logout() {
        // Logic to handle session invalidation if needed
        return "login"; // Redirect to the login page after logout
    }

    @GetMapping("/home")
    public String showHome() {
        return "home";
    }
}