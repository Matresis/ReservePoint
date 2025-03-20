package cz.cervenka.reserve_point.services;

import cz.cervenka.reserve_point.database.entities.*;
import cz.cervenka.reserve_point.database.repositories.ReservationCancellationRequestRepository;
import cz.cervenka.reserve_point.database.repositories.ReservationConfirmationRequestRepository;
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
    private final ReservationConfirmationRequestRepository confirmationRequestRepository;
    private final EmailService emailService;
    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public AdminRequestService(ReservationRepository reservationRepository,
                               ReservationModificationRequestRepository modificationRequestRepository,
                               ReservationCancellationRequestRepository cancellationRequestRepository,
                               ReservationConfirmationRequestRepository confirmationRequestRepository,
                               EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.modificationRequestRepository = modificationRequestRepository;
        this.cancellationRequestRepository = cancellationRequestRepository;
        this.confirmationRequestRepository = confirmationRequestRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void approveConfirmationRequest(Long id) {
        ReservationConfirmationRequestEntity request = confirmationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        ReservationEntity reservation = request.getReservation();
        reservation.setStatus(ReservationEntity.Status.CONFIRMED);
        reservationRepository.save(reservation);

        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        emailService.sendReservationConfirmationEmail(reservation, customer, service);
        confirmationRequestRepository.delete(request);
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
    public void rejectConfirmationRequest(Long id, String reason) {
        ReservationConfirmationRequestEntity request = confirmationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        ReservationEntity reservation = request.getReservation();
        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        reservation.setStatus(ReservationEntity.Status.APPROVED);
        reservationRepository.save(reservation);

        emailService.sendReservationCancellationEmail(reservation, customer, service, reason);

        confirmationRequestRepository.delete(request);
    }

    @Transactional
    public void rejectModificationRequest(Long id, String reason) {
        ReservationModificationRequestEntity request = modificationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        ReservationEntity reservation = request.getReservation();
        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        emailService.sendReservationModificationRejectionEmail(reservation, customer, service, reason);

        modificationRequestRepository.delete(request);
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
