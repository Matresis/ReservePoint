package cz.cervenka.rp_backend.controllers;

import cz.cervenka.rp_backend.database.entities.ReservationEntity;
import cz.cervenka.rp_backend.database.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;


@Controller
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/reserve")
    public String showReservationForm(Model model) {
        model.addAttribute("reservation", new ReservationEntity());
        return "reserve";
    }

    @PostMapping("/reserve")
    public String handleReservationSubmission(@ModelAttribute ReservationEntity reservation) {
        reservationRepository.save(reservation);
        return "confirmation";
    }
}