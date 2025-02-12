package cz.cervenka.rp_backend.controllers;

import cz.cervenka.rp_backend.database.entities.UserEntity;
import cz.cervenka.rp_backend.database.repositories.UserRepository;
import cz.cervenka.rp_backend.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/registerForm")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserEntity());
        return "registerForm";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserEntity user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "error"; // Email already exists
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        if (userRepository.count() == 0) {
            user.setRole(UserEntity.Role.ADMIN);
        } else {
            user.setRole(UserEntity.Role.USER);
        }

        userRepository.save(user);
        return "loginForm";
    }


    @GetMapping("/loginForm")
    public String showLoginForm() {
        return "loginForm";
    }


    /*@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserEntity request) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // If authentication is successful, generate JWT token
            String token = JwtUtil.generateToken(authentication.getName());

            // Return the token in a JSON response
            return ResponseEntity.ok(Map.of("token", token, "username", authentication.getName()));

        } catch (AuthenticationException e) {
            // If authentication fails, return error response
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }
    }*/
}