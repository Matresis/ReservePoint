package cz.cervenka.rp_backend.controllers;

import cz.cervenka.rp_backend.database.entities.UserEntity;
import cz.cervenka.rp_backend.database.repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserEntity());
        return "registerPage";
    }

    @PostMapping(value = "/register", consumes = "application/x-www-form-urlencoded")
    public String registerUser(@ModelAttribute UserEntity user) {
        // Check if the username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            // Redirect to an error page indicating the username is taken
            return "error";
        }

        // Set the current time for the created_at attribute
        user.setCreated_at(LocalDateTime.now());

        // Save the new user to the repository
        userRepository.save(user);

        // Redirect to the reservation page upon successful registration
        return "reserveForm";
    }


    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Return the login view
    }

    @PostMapping(value = "/login", consumes = "application/x-www-form-urlencoded")
    public String loginUser(@ModelAttribute UserEntity user) {
        // Find user by email
        Optional<UserEntity> foundUserOpt = userRepository.findByUsername(user.getUsername());

        // Check if the user exists
        if (foundUserOpt.isPresent()) {
            UserEntity foundUser = foundUserOpt.get(); // Get the user entity

            // Check if the password matches
            if (foundUser.getPassword().equals(user.getPassword())) {
                // Successful login, redirect to reserveForm.html
                return "home";
            }
        }
        // If login fails, redirect to the login page with an error message
        return "error";
    }
}