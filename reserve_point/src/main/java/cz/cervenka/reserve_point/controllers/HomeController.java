package cz.cervenka.reserve_point.controllers;

import cz.cervenka.reserve_point.database.entities.CustomerEntity;
import cz.cervenka.reserve_point.database.entities.ReservationEntity;
import cz.cervenka.reserve_point.services.ReservationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/")
public class HomeController {

    private final ReservationService reservationService;

    public HomeController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public String redirectToLogin() {
        return "loginForm";
    }

    @PostMapping("/logout")
    public String logout() {
        return "redirect:/loginForm";
    }

    @GetMapping("/home")
    public String showHome() {
        return "home";
    }

    @GetMapping("/reserveForm")
    public String showReservationForm(Model model, Authentication authentication) {
        Optional<CustomerEntity> customerOpt = reservationService.getAuthenticatedCustomer(authentication);

        if (customerOpt.isEmpty()) {
            model.addAttribute("customer", new CustomerEntity());
        } else {
            model.addAttribute("customer", customerOpt.get());
        }

        model.addAttribute("reservation", new ReservationEntity());
        return "reserveForm";
    }

    @PostMapping("/make-reservation")
    public String handleReservationSubmission(
            @ModelAttribute("customer") CustomerEntity customer,
            @RequestParam("serviceId") Long serviceId,
            @RequestParam(value = "notes", required = false) String notes,
            Authentication authentication,
            Model model) {

        CustomerEntity finalCustomer = reservationService.saveCustomer(authentication, customer);
        ReservationEntity reservation = reservationService.createReservation(finalCustomer, serviceId, notes);

        model.addAttribute("customer", finalCustomer);
        model.addAttribute("reservation", reservation);

        return "confirmation";
    }
}