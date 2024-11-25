package cz.cervenka.rp_backend.controllers;

import cz.cervenka.rp_backend.database.entities.UserEntity;
import cz.cervenka.rp_backend.database.repositories.UserRepository;
import cz.cervenka.rp_backend.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
        user.setCreated_at(LocalDateTime.now());

        if (userRepository.count() == 0) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }

        userRepository.save(user);
        return "loginForm"; // Redirect to loginForm after registration
    }


    @GetMapping("/loginForm")
    public String showLoginForm() {
        return "loginForm"; // Return the login view
    }


    @PostMapping("/api/login")
    public ResponseEntity<Map<String, String>> login(@ModelAttribute UserEntity loginRequest) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            // Generate JWT token
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            String token = jwtUtil.generateToken(authentication.getName(), role);

            // Return the token in a JSON response
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("role", role);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }


}