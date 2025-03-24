package cz.cervenka.reserve_point.controllers;

import cz.cervenka.reserve_point.database.entities.UserEntity;
import cz.cervenka.reserve_point.services.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/registerForm")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserEntity());
        return "auth/registerForm";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserEntity user,
                               @RequestParam String name,
                               @RequestParam String surname,
                               @RequestParam String password,
                               @RequestParam String email,
                               Model model) {
        try {
            if (name == null || name.isBlank()
                    || surname == null || surname.isBlank()
                    || password == null || password.isBlank()
                    || email == null || email.isBlank()) {
                model.addAttribute("errorMessage", "Fields cannot be left empty.");
            }

            authService.registerUser(user);
            return "redirect:/loginForm";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Fields cannot be left empty.");
            return "auth/registerForm";
        }
    }

    @GetMapping("/loginForm")
    public String showLoginForm() {
        return "auth/loginForm";
    }
}