package cz.cervenka.reserve_point.controllers;

import cz.cervenka.reserve_point.database.entities.ReservationEntity;
import cz.cervenka.reserve_point.database.repositories.ReservationRepository;
import cz.cervenka.reserve_point.services.AdminReservationService;
import cz.cervenka.reserve_point.config.EmailConfigService;
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
    private final EmailConfigService emailConfigService;
    private final ReservationRepository reservationRepository;

    public AdminReservationController(AdminReservationService reservationService, EmailConfigService emailConfigService, ReservationRepository reservationRepository) {
        this.reservationService = reservationService;
        this.emailConfigService = emailConfigService;
        this.reservationRepository = reservationRepository;
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

        // Handle null case for orderedTime
        String formattedOrderDate = (reservation.getOrderedTime() != null)
                ? reservation.getOrderedTime().format(reservationService.formatter)
                : null;
        model.addAttribute("formattedOrderDate", formattedOrderDate);

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


    @PostMapping("/approve")
    public String approveReservation(@RequestParam Long id) {
        Optional<ReservationEntity> reservationOpt = reservationService.getReservationById(id);

        if (reservationOpt.isPresent()) {
            ReservationEntity reservation = reservationOpt.get();
            reservation.setStatus(ReservationEntity.Status.valueOf("CONFIRMED"));
            reservationRepository.save(reservation);

            // Send approval email
            String emailContent = "<p>Dear " + reservation.getCustomer().getUser().getName() + ",</p>"
                    + "<p>Your reservation for <strong>" + reservation.getService().getName() + "</strong> has been approved!</p>";
            emailConfigService.sendEmail(reservation.getCustomer().getUser().getEmail(), "Reservation Approved", emailContent);
        }

        return "redirect:/admin/reservations";
    }

    @PostMapping("/reject")
    public String rejectReservation(@RequestParam Long id, @RequestParam String rejectionReason) {
        Optional<ReservationEntity> reservationOpt = reservationService.getReservationById(id);

        if (reservationOpt.isPresent()) {
            ReservationEntity reservation = reservationOpt.get();
            reservation.setStatus(ReservationEntity.Status.valueOf("CANCELED"));
            reservation.setNotes(rejectionReason);
            reservationRepository.save(reservation);

            // Send rejection email
            String emailContent = "<p>Dear " + reservation.getCustomer().getUser().getName() + ",</p>"
                    + "<p>Unfortunately, your reservation for <strong>" + reservation.getService().getName() + "</strong> was rejected.</p>"
                    + "<p>Reason: " + rejectionReason + "</p>";
            emailConfigService.sendEmail(reservation.getCustomer().getUser().getEmail(), "Reservation Rejected", emailContent);
        }

        return "redirect:/admin/reservations";
    }
}