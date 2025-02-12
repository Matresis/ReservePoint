package cz.cervenka.rp_backend.controllers;

import cz.cervenka.rp_backend.database.entities.*;
import cz.cervenka.rp_backend.database.repositories.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    public ReservationController(
            ReservationRepository reservationRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository,
            ServiceRepository serviceRepository) {
        this.reservationRepository = reservationRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
    }

    @GetMapping("/make-reservation")
    public String showReservationForm(Model model, Authentication authentication) {
        Optional<CustomerEntity> customerOpt = getAuthenticatedCustomer(authentication);

        if (customerOpt.isEmpty()) {
            CustomerEntity newCustomer = new CustomerEntity();
            newCustomer.setUser(userRepository.findByEmail(authentication.getName()).orElseThrow());
            model.addAttribute("customer", newCustomer);
        } else {
            model.addAttribute("customer", customerOpt.get());
        }

        model.addAttribute("reservation", new ReservationEntity());
        model.addAttribute("services", serviceRepository.findAll());  // Load available services
        return "reserveForm";
    }

    @PostMapping("/make-reservation")
    public String handleReservationSubmission(@ModelAttribute("customer") CustomerEntity customer,
                                              @RequestParam("serviceId") Long serviceId,
                                              Authentication authentication,
                                              Model model) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(authentication.getName());
        if (userOpt.isEmpty()) return "redirect:/login";

        Optional<ServiceEntity> serviceOpt = serviceRepository.findById(serviceId);
        if (serviceOpt.isEmpty()) return "redirect:/make-reservation";

        Optional<CustomerEntity> customerOpt = customerRepository.findByUser(userOpt.get());

        CustomerEntity finalCustomer = customerOpt.orElseGet(() -> {
            customer.setUser(userOpt.get());
            return customerRepository.save(customer);
        });

        ReservationEntity reservation = new ReservationEntity();
        reservation.setCustomer(finalCustomer);
        reservation.setService(serviceOpt.get());
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setStatus(ReservationEntity.Status.PENDING);
        reservation.setReservationDate(LocalDate.now());

        reservationRepository.save(reservation);

        // Add attributes to the model
        model.addAttribute("customer", finalCustomer);
        model.addAttribute("reservation", reservation);

        return "confirmation";
    }


    // Admin view - list all reservations with optional filters
    @GetMapping("/admin/reservations")
    public String getAdminReservations(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String serviceType,
            Model model) {

        LocalDate filterDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
        List<ReservationEntity> reservations = reservationRepository.findFilteredReservations(
                name, surname, filterDate, serviceType
        );

        model.addAttribute("reservations", reservations);
        return "admin/reservations";
    }

    // User-specific reservations
    @GetMapping("/reservations")
    public String getUserReservations(Authentication authentication, Model model) {
        Optional<CustomerEntity> customerOpt = getAuthenticatedCustomer(authentication);
        if (customerOpt.isEmpty()) {
            return "redirect:/";
        }

        List<ReservationEntity> reservations = reservationRepository.findByCustomer(customerOpt.get());

        model.addAttribute("reservations", reservations);
        model.addAttribute("customer", customerOpt.get());
        return "reservations";
    }


    @GetMapping("/admin/reservations/{id}")
    public String viewReservation(@PathVariable Integer id, Model model) {
        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(id);
        if (reservationOpt.isEmpty()) {
            return "redirect:/admin/reservations";
        }

        model.addAttribute("reservation", reservationOpt.get());
        return "admin/reservation-detail";
    }


    @PostMapping("/admin/reservations/update")
    public String updateReservation(
            @RequestParam Integer id,
            @RequestParam String status,
            @RequestParam(required = false) String orderedTime) {

        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(id);
        if (reservationOpt.isEmpty()) return "redirect:/admin/reservations";

        ReservationEntity reservation = reservationOpt.get();
        reservation.setStatus(ReservationEntity.Status.valueOf(status));

        if (orderedTime != null && !orderedTime.isEmpty()) {
            reservation.setOrderedTime(LocalDateTime.parse(orderedTime));
        }

        reservationRepository.save(reservation);
        return "redirect:/admin/reservations/" + id;
    }

    @PostMapping("/admin/reservations/delete")
    public String deleteReservation(@RequestParam Integer id) {
        reservationRepository.deleteById(id);
        return "redirect:/admin/reservations";
    }



    // Helper method to get the logged-in customer
    private Optional<CustomerEntity> getAuthenticatedCustomer(Authentication authentication) {
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


}
