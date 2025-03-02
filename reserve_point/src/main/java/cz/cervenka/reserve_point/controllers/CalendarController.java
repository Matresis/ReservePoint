package cz.cervenka.reserve_point.controllers;

import cz.cervenka.reserve_point.database.entities.ReservationEntity;
import cz.cervenka.reserve_point.database.repositories.ReservationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/reservations/calendar")
public class CalendarController {

    private final ReservationRepository reservationRepository;

    public CalendarController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // Display the calendar page
    @GetMapping
    public String showCalendarPage() {
        return "admin/calendar";
    }

    // Fetch reservations as events for the weekly calendar
    @GetMapping("/events")
    @ResponseBody
    public List<Map<String, Object>> getCalendarEvents() {
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getOrderedTime() != null) // Ensure it has a valid date
                .map(reservation -> {
                    Map<String, Object> event = new HashMap<>();
                    event.put("title", reservation.getService().getName() + " - " + reservation.getCustomer().getUser().getName());
                    event.put("start", reservation.getOrderedTime().toString()); // Convert LocalDateTime to string
                    event.put("id", reservation.getId());
                    return event;
                }).collect(Collectors.toList());
    }

    @PostMapping("/add-event")
    public String addReservationToCalendar(@RequestParam("id") Long reservationId, Model model) {
        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(reservationId);

        if (reservationOpt.isPresent()) {
            ReservationEntity reservation = reservationOpt.get();

            // If orderedTime is missing, return an error message
            if (reservation.getOrderedTime() == null) {
                model.addAttribute("errorMessage", "Order time must be entered before adding to the calendar.");
                model.addAttribute("reservation", reservation);
                return "admin/reservation-detail"; // Return the same page with error
            }

            // Otherwise, allow saving (if additional logic is needed)
            reservationRepository.save(reservation);
        }

        return "redirect:/admin/reservations/calendar";
    }

}