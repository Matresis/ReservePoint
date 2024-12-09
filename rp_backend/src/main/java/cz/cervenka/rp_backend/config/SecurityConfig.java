package cz.cervenka.rp_backend.config;

import cz.cervenka.rp_backend.filters.JwtAuthenticationFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import cz.cervenka.rp_backend.services.CustomUserDetailsService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication manager bean setup
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/style.css").permitAll()
                        .requestMatchers("/register", "/registerForm", "/loginForm", "/login", "/token").permitAll()
                        .requestMatchers("/home", "/make-reservation", "/reservations", "/reserveForm").hasAuthority("USER")
                        .requestMatchers("/admin/reservations", "/admin/home", "/admin/calendar").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/loginForm")                              // Custom login page at /loginForm
                        .loginProcessingUrl("/login")                         // Spring Security handles POST /login for authentication
                        .successHandler(customSuccessHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/loginForm")
                        .permitAll()
                )
                /*.sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Use stateless sessions
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class
                )*/;
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            // Get the user's role
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            // Redirect based on the role
            if ("ADMIN".equals(role)) {
                response.sendRedirect("/admin/home");  // Redirect admin to dashboard
            } else if ("USER".equals(role)) {
                response.sendRedirect("/home");            // Redirect regular users to home
            } else {
                response.sendRedirect("/loginForm?error=unknown_role");
            }
        };
    }
}
