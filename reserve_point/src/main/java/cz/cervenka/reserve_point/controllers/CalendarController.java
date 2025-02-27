package cz.cervenka.reserve_point.controllers;

import cz.cervenka.reserve_point.database.entities.ReservationEntity;
import cz.cervenka.reserve_point.database.repositories.ReservationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/reservations/calendar")
public class CalendarController {

    private final ReservationRepository reservationRepository;

    public CalendarController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @GetMapping
    public String showCalendarPage() {
        return "admin/calendar";
    }

    @GetMapping("/events")
    @ResponseBody
    public List<Map<String, Object>> getCalendarEvents() {
        return reservationRepository.findAll().stream().map(reservation -> {
            Map<String, Object> event = new HashMap<>();
            event.put("title", reservation.getService().getName() + " - " + reservation.getCustomer().getUser().getName());
            event.put("start", reservation.getOrderedTime().toString());
            return event;
        }).collect(Collectors.toList());
    }
}