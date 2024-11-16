package cz.cervenka.rp_backend.controllers;

import cz.cervenka.rp_backend.database.entities.ReservationEntity;
import cz.cervenka.rp_backend.database.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

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

    @GetMapping("/reservations")
    public String showReservations(Model model) {
        List<ReservationEntity> reservations = reservationRepository.findAll();
        model.addAttribute("reservations", reservations);
        return "reservations";
    }
}