package cz.cervenka.reserve_point.services;

import cz.cervenka.reserve_point.database.entities.*;
import cz.cervenka.reserve_point.database.repositories.ReservationCancellationRequestRepository;
import cz.cervenka.reserve_point.database.repositories.ReservationModificationRequestRepository;
import cz.cervenka.reserve_point.database.repositories.ReservationRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class AdminRequestService {

    private final ReservationRepository reservationRepository;
    private final ReservationModificationRequestRepository modificationRequestRepository;
    private final ReservationCancellationRequestRepository cancellationRequestRepository;
    private final EmailService emailService;
    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public AdminRequestService(ReservationRepository reservationRepository, ReservationModificationRequestRepository modificationRequestRepository, ReservationCancellationRequestRepository cancellationRequestRepository, EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.modificationRequestRepository = modificationRequestRepository;
        this.cancellationRequestRepository = cancellationRequestRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void approveConfirmationRequest(Long id) {
        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        reservation.setStatus(ReservationEntity.Status.CONFIRMED);
        reservationRepository.save(reservation);

        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        emailService.sendReservationConfirmationEmail(reservation, customer, service);
    }

    @Transactional
    public void approveModificationRequest(Long id) {
        ReservationModificationRequestEntity request = modificationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        ReservationEntity reservation = request.getReservation();
        reservation.setNotes(request.getRequestedNotes());
        reservation.setService(request.getRequestedService());
        reservation.setOrderedTime(request.getRequestedOrderTime());

        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        emailService.sendReservationModificationApprovalEmail(reservation, customer, service);

        reservationRepository.save(reservation);
        modificationRequestRepository.delete(request);
    }

    @Transactional
    public void rejectModificationRequest(Long id) {
        ReservationModificationRequestEntity request = modificationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        ReservationEntity reservation = request.getReservation();
        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        emailService.sendReservationModificationRejectionEmail(reservation, customer, service);

        modificationRequestRepository.deleteById(id);
    }

    @Transactional
    public void approveCancellation(Long id) {
        ReservationCancellationRequestEntity request = cancellationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        ReservationEntity reservation = request.getReservation();
        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        String reason = reservation.getNotes();

        emailService.sendReservationCancellationApprovalEmail(reservation, customer, service, reason);

        reservationRepository.delete(reservation);
        cancellationRequestRepository.delete(request);
    }
}
