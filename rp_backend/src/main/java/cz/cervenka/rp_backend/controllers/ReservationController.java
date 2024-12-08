package cz.cervenka.rp_backend.controllers;

import cz.cervenka.rp_backend.database.entities.ReservationEntity;
import cz.cervenka.rp_backend.database.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/reserveForm")
    public String showReservationForm(Model model) {
        model.addAttribute("reservation", new ReservationEntity());
        return "reserveForm";
    }

    @PostMapping("/reserveForm")
    public String handleReservationSubmission(@ModelAttribute ReservationEntity reservation) {
        reservation.setCreated_at(LocalDate.now());
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

        // Check if any filters are provided; if not, fetch all reservations
        if (name == null && surname == null && filterDate == null && serviceType == null) {
            reservations = reservationRepository.findAll();
        } else {
            reservations = reservationRepository.findFilteredReservations(name, surname, filterDate, serviceType);
        }

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

        // Check if any filters are provided; if not, fetch all reservations
        if (name == null && surname == null && filterDate == null && serviceType == null) {
            reservations = reservationRepository.findByEmail(email);
        } else {
            reservations = reservationRepository.findFilteredReservations(name, surname, filterDate, serviceType);
        }

        model.addAttribute("reservations", reservations);
        return "reservations";
    }


    /*@GetMapping("/admin/reservations")
    public ResponseEntity<List<ReservationEntity>> getAllReservations(Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        if (role.equals("ADMIN")) {
            List<ReservationEntity> reservations = reservationRepository.findAll();
            System.out.println("Reservations: " + reservations);
            return ResponseEntity.ok(reservations);
        } else {
            throw new RuntimeException("Unauthorized access");
        }
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationEntity>> getUserReservations(Authentication authentication) {
        String email = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        if (role.equals("USER")) {
            // Return only the reservations matching the user's email
            List<ReservationEntity> reservations = reservationRepository.findByEmail(email);
            System.out.println("Reservations: " + reservations);
            return ResponseEntity.ok(reservations);
        } else {
            throw new RuntimeException("Unauthorized access");
        }
    }*/
}