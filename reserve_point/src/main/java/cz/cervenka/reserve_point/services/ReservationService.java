package cz.cervenka.reserve_point.services;

import cz.cervenka.reserve_point.database.entities.*;
import cz.cervenka.reserve_point.database.repositories.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final ReservationModificationRequestRepository modificationRequestRepository;
    private final ReservationConfirmationRequestRepository confirmationRequestRepository;
    private final EmailService emailService;

    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public ReservationService(ReservationRepository reservationRepository,
                              CustomerRepository customerRepository,
                              UserRepository userRepository,
                              ServiceRepository serviceRepository,
                              ReservationModificationRequestRepository modificationRequestRepository,
                              EmailService emailService,
                              ReservationConfirmationRequestRepository confirmationRequestRepository) {
        this.reservationRepository = reservationRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.modificationRequestRepository = modificationRequestRepository;
        this.emailService = emailService;
        this.confirmationRequestRepository = confirmationRequestRepository;
    }

    public Optional<CustomerEntity> getAuthenticatedCustomer(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("User is not authenticated.");
            return Optional.empty();
        }

        String email = authentication.getName();
        System.out.println("Authenticated email: " + email);

        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            System.out.println("No user found with email: " + email);
            return Optional.empty();
        }

        Optional<CustomerEntity> customerOpt = customerRepository.findByUser(userOpt.get());
        if (customerOpt.isEmpty()) {
            System.out.println("No customer entity found for user: " + email);
        }
        return customerOpt;
    }

    public CustomerEntity saveCustomer(Authentication authentication, CustomerEntity customer) {
        return userRepository.findByEmail(authentication.getName())
                .flatMap(customerRepository::findByUser)
                .orElseGet(() -> {
                    customer.setUser(userRepository.findByEmail(authentication.getName()).orElseThrow());
                    return customerRepository.save(customer);
                });
    }

    @Transactional
    public ReservationEntity createReservation(CustomerEntity customer, Long serviceId, String notes) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found."));

        ReservationEntity reservation = new ReservationEntity();
        reservation.setCustomer(customer);
        reservation.setService(service);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setStatus(ReservationEntity.Status.PENDING);
        reservation.setNotes(notes);

        ReservationEntity savedReservation = reservationRepository.save(reservation);

        emailService.sendReservationCreationEmail(savedReservation, customer, service);

        return savedReservation;
    }

    public List<ReservationEntity> getFilteredUserReservations(CustomerEntity customer, String name, String surname, String date, String serviceType) {
        List<ReservationEntity> reservations = reservationRepository.findByCustomer(customer);

        LocalDate filterDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
        reservations = reservationRepository.findFilteredReservations(name, surname, filterDate, serviceType);

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

    @Transactional
    public void requestReservationConfirmation(Long reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));

        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        ReservationConfirmationRequestEntity reservationConfirmationRequest = new ReservationConfirmationRequestEntity();
        reservationConfirmationRequest.setReservation(reservation);
        confirmationRequestRepository.save(reservationConfirmationRequest);

        emailService.sendReservationConfirmationRequestEmail(reservation, customer, service);
    }

    @Transactional
    public void requestReservationModification(Long reservationId, String newNotes, ServiceEntity newService, LocalDateTime newOrderTime) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));

        ReservationModificationRequestEntity modificationRequest = new ReservationModificationRequestEntity();
        modificationRequest.setReservation(reservation);
        modificationRequest.setRequestedNotes(newNotes);
        modificationRequest.setRequestedService(newService);
        modificationRequest.setRequestedOrderTime(newOrderTime);
        modificationRequestRepository.save(modificationRequest);

        emailService.sendReservationModificationRequestEmail(reservation, reservation.getCustomer(), reservation.getService());
    }

    @Transactional
    public void requestReservationCancellation(Long reservationId, String reason) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));

        ReservationCancellationRequestEntity cancellationRequest = new ReservationCancellationRequestEntity();
        cancellationRequest.setReservation(reservation);
        cancellationRequest.setReason(reason);

        emailService.sendReservationCancellationRequestEmail(reservation, reservation.getCustomer(), reservation.getService(), reason);
    }
}