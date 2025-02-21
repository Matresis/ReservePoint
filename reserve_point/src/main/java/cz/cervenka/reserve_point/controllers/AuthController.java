package cz.cervenka.reserve_point.controllers;

import cz.cervenka.reserve_point.database.entities.UserEntity;
import cz.cervenka.reserve_point.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/registerForm")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserEntity());
        return "registerForm";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserEntity user) {
        try {
            authService.registerUser(user);
            return "redirect:/loginForm";
        } catch (IllegalArgumentException e) {
            return "error";
        }
    }

    @GetMapping("/loginForm")
    public String showLoginForm() {
        return "loginForm";
    }

    /*@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            Map<String, String> response = authService.authenticateUser(
                    loginRequest.get("email"),
                    loginRequest.get("password")
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }
    }*/
}