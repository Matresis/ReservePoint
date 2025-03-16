package cz.cervenka.reserve_point.services;

import cz.cervenka.reserve_point.database.entities.*;
import cz.cervenka.reserve_point.database.repositories.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final EmailService emailService;

    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public ReservationService(ReservationRepository reservationRepository,
                              CustomerRepository customerRepository,
                              UserRepository userRepository,
                              ServiceRepository serviceRepository,
                              EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.emailService = emailService;
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
    public ReservationEntity createReservationAndSendEmail(CustomerEntity customer, Long serviceId, String notes) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found."));

        ReservationEntity reservation = new ReservationEntity();
        reservation.setCustomer(customer);
        reservation.setService(service);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setStatus(ReservationEntity.Status.PENDING);
        reservation.setNotes(notes);

        // Save reservation first
        ReservationEntity savedReservation = reservationRepository.save(reservation);

        // Prepare email content
        String userEmailContent = "<p>Dear " + customer.getUser().getName() + ",</p>"
                + "<p>Your reservation for <strong>" + service.getName() + "</strong> has been received.</p>"
                + "<p>We will notify you once it's approved.</p>";

        String adminEmailContent = "<p>New reservation request from <strong>"
                + customer.getUser().getName() + " " + customer.getUser().getSurname() + "</strong></p>"
                + "<p>Service Type: <strong>" + service.getName() + "</strong></p>"
                + "<p><a href='http://localhost:8080/admin/reservations/" + savedReservation.getId() + "'>Review Reservation</a></p>";

        // Send Emails
        emailService.sendEmail(customer.getUser().getEmail(), "Reservation Confirmation", userEmailContent);
        emailService.sendEmail("reservepointtp@gmail.com", "New Reservation Request", adminEmailContent);

        return savedReservation;
    }

    public List<ReservationEntity> getUserReservations(CustomerEntity customer) {
        List<ReservationEntity> reservations = reservationRepository.findByCustomer(customer);
        reservations.forEach(reservation ->
                reservation.setFormattedCreatedAt(reservation.getCreatedAt().format(formatter))
        );
        reservations.forEach(reservation ->
                reservation.setFormattedOrderTime(reservation.getOrderedTime().format(formatter))
        );
        return reservations;
    }

    public Optional<ReservationEntity> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }
}