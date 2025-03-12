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
                              ServiceRepository serviceRepository, EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.emailService = emailService;
    }

    public Optional<CustomerEntity> getAuthenticatedCustomer(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Optional<UserEntity> userOpt = userRepository.findByEmail(authentication.getName());
        return userOpt.flatMap(customerRepository::findByUser);
    }

    public CustomerEntity saveCustomer(Authentication authentication, CustomerEntity customer) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(authentication.getName());
        if (userOpt.isEmpty()) {
            throw new IllegalStateException("User not found.");
        }

        return customerRepository.findByUser(userOpt.get()).orElseGet(() -> {
            customer.setUser(userOpt.get());
            return customerRepository.save(customer);
        });
    }

    public ReservationEntity createReservation(CustomerEntity customer, Long serviceId, String notes) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found."));

        ReservationEntity reservation = new ReservationEntity();
        reservation.setCustomer(customer);
        reservation.setService(service);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setStatus(ReservationEntity.Status.PENDING);
        reservation.setNotes(notes);

        return reservationRepository.save(reservation);
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

    @Transactional
    public ReservationEntity createReservation(ReservationEntity reservation) {
        ReservationEntity savedReservation = reservationRepository.save(reservation);

        // Email content
        String userEmailContent = "<p>Dear " + reservation.getCustomer().getUser().getName() + ",</p>"
                + "<p>Your reservation for <strong>" + reservation.getService().getName() + "</strong> has been received.</p>"
                + "<p>We will notify you once it's approved.</p>";

        String adminEmailContent = "<p>New reservation request from <strong>"
                + reservation.getCustomer().getUser().getName() + " " + reservation.getCustomer().getUser().getSurname() + "</strong></p>"
                + "<p>Service Type: <strong>" + reservation.getService().getName() + "</strong></p>"
                + "<p><a href='http://yourwebsite.com/admin/reservations/" + savedReservation.getId() + "'>Review Reservation</a></p>";

        // Send Emails
        emailService.sendEmail(reservation.getCustomer().getUser().getEmail(), "Reservation Confirmation", userEmailContent);
        emailService.sendEmail("admin@yourwebsite.com", "New Reservation Request", adminEmailContent);

        return savedReservation;
    }
}