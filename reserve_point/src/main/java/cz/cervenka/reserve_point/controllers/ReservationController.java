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
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping()
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

    @GetMapping("/{id}")
    public String viewReservation(@PathVariable Long id, Model model) {
        Optional<ReservationEntity> reservationOpt = reservationService.getReservationById(id);
        if (reservationOpt.isEmpty()) {
            return "redirect:/reservations";
        }

        ReservationEntity reservation = reservationOpt.get();
        model.addAttribute("reservation", reservation);
        model.addAttribute("formattedCreatedAt", reservation.getCreatedAt().format(reservationService.formatter));
        model.addAttribute("formattedOrderDate", reservation.getOrderedTime().format(reservationService.formatter));

        return "reservation-detail";
    }
}
