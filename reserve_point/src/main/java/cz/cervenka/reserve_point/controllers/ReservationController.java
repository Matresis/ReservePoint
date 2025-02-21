package cz.cervenka.reserve_point.controllers;

import cz.cervenka.reserve_point.database.entities.*;
import cz.cervenka.reserve_point.services.ReservationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
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

    @GetMapping("/reservations")
    public String getUserReservations(Authentication authentication, Model model) {
        Optional<CustomerEntity> customerOpt = reservationService.getAuthenticatedCustomer(authentication);
        if (customerOpt.isEmpty()) {
            return "redirect:/";
        }

        List<ReservationEntity> reservations = reservationService.getUserReservations(customerOpt.get());

        model.addAttribute("reservations", reservations);
        model.addAttribute("customer", customerOpt.get());
        return "reservations";
    }
}
