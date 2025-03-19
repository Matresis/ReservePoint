package cz.cervenka.reserve_point.controllers;

import cz.cervenka.reserve_point.database.entities.*;
import cz.cervenka.reserve_point.database.repositories.ReservationRepository;
import cz.cervenka.reserve_point.database.repositories.ServiceRepository;
import cz.cervenka.reserve_point.services.ReservationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;
    private final ServiceRepository serviceRepository;
    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public ReservationController(ReservationService reservationService, ReservationRepository reservationRepository, ServiceRepository serviceRepository) {
        this.reservationService = reservationService;
        this.reservationRepository = reservationRepository;
        this.serviceRepository = serviceRepository;
    }

    @GetMapping()
    public String getUserReservations(Authentication authentication, Model model) {
        Optional<CustomerEntity> customerOpt = reservationService.getAuthenticatedCustomer(authentication);
        if (customerOpt.isEmpty()) {
            return "redirect:/home";
        }

        List<ReservationEntity> reservations = reservationService.getUserReservations(customerOpt.get());

        model.addAttribute("reservations", reservations);
        model.addAttribute("customer", customerOpt.get());
        return "reservations";
    }

    @GetMapping("/{id}")
    public String viewReservation(@PathVariable Long id, Model model) {
        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(id);
        if (reservationOpt.isEmpty()) {
            return "redirect:/reservations";
        }

        ReservationEntity reservation = reservationOpt.get();

        reservation.setFormattedCreatedAt(
                reservation.getCreatedAt() != null ? reservation.getCreatedAt().format(formatter) : "N/A"
        );
        reservation.setFormattedOrderTime(
                reservation.getOrderedTime() != null ? reservation.getOrderedTime().format(formatter) : "N/A"
        );

        model.addAttribute("reservation", reservation);
        model.addAttribute("formattedCreatedAt", reservation.getFormattedCreatedAt());
        model.addAttribute("formattedOrderTime", reservation.getFormattedOrderTime());
        model.addAttribute("services", serviceRepository.findAll());
        return "reservation-detail";
    }

    @PostMapping("/{id}/request-modification")
    public String requestModification(@PathVariable Long id,
                                      @RequestParam String newNotes,
                                      @RequestParam String newService,
                                      @RequestParam String newOrderTime) {
        reservationService.requestReservationModification(id, newNotes, newService, newOrderTime);
        return "redirect:/reservations/" + id;
    }

    @PostMapping("/{id}/request-cancellation")
    public String requestCancellation(@PathVariable Long id) {
        reservationService.requestReservationCancellation(id);
        return "redirect:/reservations/" + id;
    }
}
