package cz.cervenka.reserve_point.services;

import cz.cervenka.reserve_point.config.EmailConfigService;
import cz.cervenka.reserve_point.database.entities.CustomerEntity;
import cz.cervenka.reserve_point.database.entities.ReservationEntity;
import cz.cervenka.reserve_point.database.entities.ServiceEntity;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final EmailConfigService emailConfigService;

    public EmailService(EmailConfigService emailConfigService) {
        this.emailConfigService = emailConfigService;
    }

    public void sendReservationCreationEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String customerEmailContent = "<p>Dear " + customer.getUser().getName() + ",</p>"
                + "<p>Your reservation for <strong>" + service.getName() + "</strong> has been received.</p>"
                + "<p>We will notify you once it's approved.</p>"
                + "<p><a href='http://localhost:8080/reservations>Review Reservation</a></p>";

        String adminEmailContent = "<p>New reservation request from <strong>"
                + customer.getUser().getName() + " " + customer.getUser().getSurname() + "</strong></p>"
                + "<p>Service Type: <strong>" + service.getName() + "</strong></p>"
                + "<p><a href='http://localhost:8080/admin/reservations/" + reservation.getId() + "'>Review Reservation</a></p>";

        emailConfigService.sendEmail(customer.getUser().getEmail(), "Reservation Confirmation", customerEmailContent);
        emailConfigService.sendEmail("reservepointtp@gmail.com", "New Reservation Request", adminEmailContent);
    }
}