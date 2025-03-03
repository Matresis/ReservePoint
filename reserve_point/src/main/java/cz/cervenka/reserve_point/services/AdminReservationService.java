package cz.cervenka.reserve_point.services;

import cz.cervenka.reserve_point.database.entities.ReservationEntity;
import cz.cervenka.reserve_point.database.repositories.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class AdminReservationService {

    private final ReservationRepository reservationRepository;
    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public AdminReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<ReservationEntity> getFilteredReservations(String name, String surname, String date, String serviceType) {
        LocalDate filterDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
        List<ReservationEntity> reservations = reservationRepository.findFilteredReservations(name, surname, filterDate, serviceType);

        reservations.forEach(reservation ->
                reservation.setFormattedCreatedAt(reservation.getCreatedAt().format(formatter))
        );

        return reservations;
    }

    public Optional<ReservationEntity> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public ReservationEntity updateReservation(Long id, String status, String orderedTime, String notes) {
        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(id);
        if (reservationOpt.isEmpty()) return null;

        ReservationEntity reservation = reservationOpt.get();
        reservation.setStatus(ReservationEntity.Status.valueOf(status));

        if (orderedTime != null && !orderedTime.isEmpty()) {
            reservation.setOrderedTime(LocalDateTime.parse(orderedTime));
        }

        reservation.setNotes(notes);
        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            return;
        }
        reservationRepository.deleteById(id);
    }
}
