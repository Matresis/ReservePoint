package cz.cervenka.reserve_point.services;

import cz.cervenka.reserve_point.database.entities.CustomerEntity;
import cz.cervenka.reserve_point.database.entities.ReservationEntity;
import cz.cervenka.reserve_point.database.entities.ServiceEntity;
import cz.cervenka.reserve_point.database.repositories.CustomerRepository;
import cz.cervenka.reserve_point.database.repositories.ReservationRepository;
import cz.cervenka.reserve_point.database.repositories.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class AdminReservationService {

    private final ReservationRepository reservationRepository;
    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final EmailService emailService;

    public AdminReservationService(ReservationRepository reservationRepository, EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
    }

    @Transactional
    public ReservationEntity updateReservation(Long id, String status, String orderedTime, String notes) {
        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));

        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        ReservationEntity.Status previousStatus = reservation.getStatus();
        reservation.setStatus(ReservationEntity.Status.valueOf(status));

        if (orderedTime != null && !orderedTime.isEmpty()) {
            reservation.setOrderedTime(LocalDateTime.parse(orderedTime));
        }

        reservation.setNotes(notes);
        reservationRepository.save(reservation);

        if (previousStatus == ReservationEntity.Status.PENDING && reservation.getStatus() == ReservationEntity.Status.APPROVED) {
            emailService.sendReservationApprovalEmail(reservation, customer, service);
        }
        else if (reservation.getStatus() == ReservationEntity.Status.CONFIRMED) {
            emailService.sendReservationConfirmationEmail(reservation, customer, service);
        }
        else if (reservation.getStatus() == ReservationEntity.Status.CANCELED) {
            emailService.sendReservationRejectionEmail(reservation, customer, service, notes);
            reservationRepository.deleteById(id);
        }

        return reservation;
    }

    @Transactional
    public void deleteReservation(Long id, String notes) {
        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));

        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        reservation.setNotes(notes);

        emailService.sendReservationCancellationEmail(reservation, customer, service, notes);
        reservationRepository.deleteById(id);
    }

    @Transactional
    public ReservationEntity updateReservation(Long id, String orderedTime, String notes) {
        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(id);
        if (reservationOpt.isEmpty()) return null;

        ReservationEntity reservation = reservationOpt.get();

        if (orderedTime != null && !orderedTime.isEmpty()) {
            reservation.setOrderedTime(LocalDateTime.parse(orderedTime));
        }

        reservation.setNotes(notes);
        return reservationRepository.save(reservation);
    }

    public List<ReservationEntity> getFilteredReservations(String name, String surname, String date, String serviceType) {
        LocalDate filterDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
        List<ReservationEntity> reservations = reservationRepository.findFilteredReservations(name, surname, filterDate, serviceType);

        reservations.forEach(reservation -> {
            reservation.setFormattedCreatedAt(reservation.getCreatedAt().format(formatter));

            if (reservation.getOrderedTime() != null) {
                reservation.setFormattedOrderTime(reservation.getOrderedTime().format(formatter));
            } else {
                reservation.setFormattedOrderTime("N/A");
            }
        });

        return reservations;
    }
}