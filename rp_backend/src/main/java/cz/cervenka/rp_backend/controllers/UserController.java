package cz.cervenka.rp_backend.controllers;

import cz.cervenka.rp_backend.database.entities.UserEntity;
import cz.cervenka.rp_backend.database.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import java.time.LocalDateTime;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/registerForm")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserEntity());
        return "registerForm";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserEntity user) {
        // Check if username or email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "error"; // Display error if email exists
        }

        // Hash the password and set the created_at timestamp
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreated_at(LocalDateTime.now());

        // Check if this is the first user
        if (userRepository.count() == 0) {
            user.setRole("ADMIN");  // Assign ADMIN role to first user
        } else {
            user.setRole("USER");  // Assign USER role to user
        }

        // Save the user
        userRepository.save(user);

        // Redirect to login page form after successful registration
        return "loginForm";
    }

    @GetMapping("/loginForm")
    public String showLoginForm() {
        return "loginForm"; // Return the login view
    }

}