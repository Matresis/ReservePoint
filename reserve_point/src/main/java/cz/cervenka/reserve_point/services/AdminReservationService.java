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
    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    private final EmailService emailService;
    private final CustomerRepository customerRepository;
    private final ServiceRepository serviceRepository;

    public AdminReservationService(ReservationRepository reservationRepository, EmailService emailService, CustomerRepository customerRepository, ServiceRepository serviceRepository) {
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
        this.customerRepository = customerRepository;
        this.serviceRepository = serviceRepository;
    }

    @Transactional
    public void approveReservation(Long id, Long customerId, Long serviceId, String status, String orderedTime) {
        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));;

        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found."));

        reservation.setOrderedTime(LocalDateTime.parse(orderedTime));
        reservation.setStatus(ReservationEntity.Status.valueOf(status));

        emailService.sendReservationApprovalEmail(reservation, customer, service);
    }

    @Transactional
    public void rejectReservation(Long id, Long customerId, Long serviceId, String notes, String status) {
        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));;

        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found."));

        reservation.setStatus(ReservationEntity.Status.valueOf(status));
        reservation.setNotes(notes);

        emailService.sendReservationRejectionEmail(reservation, customer, service);
        deleteReservation(id);
    }

    @Transactional
    public void confirmReservation(Long id, Long customerId, Long serviceId) {
        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));;

        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found."));

        reservation.setStatus(ReservationEntity.Status.CONFIRMED);

        emailService.sendReservationConfirmationEmail(reservation, customer, service);
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

    private void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            return;
        }
        reservationRepository.deleteById(id);
    }

    public void deleteReservation(Long id, Long customerId, Long serviceId) {
        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));;

        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found."));

        if (!reservationRepository.existsById(id)) {
            return;
        }
        emailService.sendReservationCancellationEmail(reservation, customer, service);
        reservationRepository.deleteById(id);
    }

    public List<ReservationEntity> getFilteredReservations(String name, String surname, String date, String serviceType) {
        LocalDate filterDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
        List<ReservationEntity> reservations = reservationRepository.findFilteredReservations(name, surname, filterDate, serviceType);

        reservations.forEach(reservation ->
                reservation.setFormattedCreatedAt(reservation.getCreatedAt().format(formatter))
        );
        reservations.forEach(reservation ->
                reservation.setFormattedOrderTime(reservation.getOrderedTime().format(formatter))
        );
        return reservations;
    }
}