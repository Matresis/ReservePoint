package cz.cervenka.reserve_point.controllers;

import cz.cervenka.reserve_point.database.entities.ReservationEntity;
import cz.cervenka.reserve_point.services.AdminReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final AdminReservationService reservationService;

    public AdminReservationController(AdminReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public String getAdminReservations(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String serviceType,
            Model model) {

        List<ReservationEntity> reservations = reservationService.getFilteredReservations(name, surname, date, serviceType);
        model.addAttribute("reservations", reservations);
        return "admin/reservations";
    }

    @GetMapping("/{id}")
    public String viewReservation(@PathVariable Long id, Model model) {
        Optional<ReservationEntity> reservationOpt = reservationService.getReservationById(id);
        if (reservationOpt.isEmpty()) {
            return "redirect:/admin/reservations";
        }

        ReservationEntity reservation = reservationOpt.get();
        model.addAttribute("reservation", reservation);
        model.addAttribute("formattedCreatedAt", reservation.getCreatedAt().format(reservationService.formatter));
        model.addAttribute("formattedOrderDate", reservation.getOrderedTime().format(reservationService.formatter));

        return "admin/reservation-detail";
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateReservation(
            @RequestParam Long id,
            @RequestParam String status,
            @RequestParam(required = false) String orderedTime,
            @RequestParam("notes") String notes) {

        ReservationEntity updatedReservation = reservationService.updateReservation(id, status, orderedTime, notes);
        if (updatedReservation == null) {
            return ResponseEntity.badRequest().body("Failed to update reservation.");
        }

        return ResponseEntity.ok().body(Map.of(
                "message", "Reservation updated successfully",
                "id", updatedReservation.getId(),
                "status", updatedReservation.getStatus(),
                "orderedTime", updatedReservation.getOrderedTime(),
                "notes", updatedReservation.getNotes()
        ));
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<?> deleteReservation(@RequestParam Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok().body(Map.of("message", "Reservation deleted successfully", "id", id));
    }


    @PostMapping("/calendar")
    public String showCalendar() {
        return "redirect:/admin/reservations/calendar";
    }
}