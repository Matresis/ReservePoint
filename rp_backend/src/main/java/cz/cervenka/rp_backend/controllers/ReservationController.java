package cz.cervenka.rp_backend.controllers;

import cz.cervenka.rp_backend.database.entities.ReservationEntity;
import cz.cervenka.rp_backend.database.repositories.ReservationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Controller
public class ReservationController {

    public ReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    private final ReservationRepository reservationRepository;

    @GetMapping("/reserveForm")
    public String showReservationForm(Model model) {
        model.addAttribute("reservation", new ReservationEntity());
        return "reserveForm";
    }

    @PostMapping("/reserveForm")
    public String handleReservationSubmission(@ModelAttribute ReservationEntity reservation) {
        reservation.setCreatedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
        return "confirmation";
    }

    @GetMapping("/admin/reservations")
    public String getAdminReservations(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String serviceType,
            Model model) {

        LocalDate filterDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;

        List<ReservationEntity> reservations;

        reservations = reservationRepository.findFilteredReservations(
                name != null && !name.isEmpty() ? name : null,
                surname != null && !surname.isEmpty() ? surname : null,
                filterDate,
                serviceType != null && !serviceType.isEmpty() ? serviceType : null
        );

        model.addAttribute("reservations", reservations);
        return "admin/reservations";
    }


    @GetMapping("/reservations")
    public String getUserReservations(
            Authentication authentication,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String serviceType,
            Model model) {

        String email = authentication.getName();

        LocalDate filterDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;

        List<ReservationEntity> reservations;

        if (name == null && surname == null && filterDate == null && serviceType == null) {
            reservations = reservationRepository.findByEmail(email);
        } else {
            reservations = reservationRepository.findFilteredReservations(name, surname, filterDate, serviceType);
        }

        model.addAttribute("reservations", reservations);
        return "reservations";
    }
}