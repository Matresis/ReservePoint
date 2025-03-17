package cz.cervenka.reserve_point.services;

import cz.cervenka.reserve_point.config.EmailConfigService;
import cz.cervenka.reserve_point.database.entities.CustomerEntity;
import cz.cervenka.reserve_point.database.entities.ReservationEntity;
import cz.cervenka.reserve_point.database.entities.ServiceEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final EmailConfigService emailConfigService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public EmailService(EmailConfigService emailConfigService) {
        this.emailConfigService = emailConfigService;
    }

    @Transactional
    public void sendReservationCreationEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String subject = "Reservation Received: " + service.getName();
        String customerEmailContent = emailConfigService.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "We have received your reservation request for <strong>" + service.getName() + "</strong>.",
                "We will notify you once your reservation is approved.",
                "View Reservations",
                "http://localhost:8080/reservations"
        );

        String adminEmailContent = emailConfigService.generateStyledEmail(
                "New Reservation Request",
                "A new reservation request has been made by <strong>" + customer.getUser().getName() + " " + customer.getUser().getSurname() + "</strong>.",
                "Service Type: <strong>" + service.getName() + "</strong>",
                "Review Request",
                "http://localhost:8080/admin/reservations/" + reservation.getId()
        );

        emailConfigService.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
        emailConfigService.sendEmail("reservepointtp@gmail.com", "New Reservation Request", adminEmailContent);
    }

    @Transactional
    public void sendReservationApprovalEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String subject = "Reservation Approved: " + service.getName();
        String customerEmailContent = emailConfigService.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "Your reservation for <strong>" + service.getName() + "</strong> has been approved.",
                "Scheduled Date: <strong>" + reservation.getOrderedTime().format(DATE_FORMATTER) + "</strong>",
                "View Reservation",
                "http://localhost:8080/reservations"
        );

        emailConfigService.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }

    @Transactional
    public void sendReservationConfirmationEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String subject = "Reservation Confirmed: " + service.getName();
        String customerEmailContent = emailConfigService.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "Your reservation for <strong>" + service.getName() + "</strong> has been successfully scheduled.",
                "Appointment Date: <strong>" + reservation.getOrderedTime().format(DATE_FORMATTER) + "</strong>",
                "View Reservation Details",
                "http://localhost:8080/reservations"
        );

        emailConfigService.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }

    @Transactional
    public void sendReservationRejectionEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String subject = "Reservation Rejected: " + service.getName();
        String customerEmailContent = emailConfigService.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "Unfortunately, your reservation for <strong>" + service.getName() + "</strong> has been rejected.",
                "Reason: <strong>" + reservation.getNotes() + "</strong>",
                "Make a New Reservation",
                "http://localhost:8080/"
        );

        emailConfigService.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }

    @Transactional
    public void sendReservationCancellationEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service, String notes) {
        String subject = "Reservation Canceled: " + service.getName();
        String customerEmailContent = emailConfigService.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "We regret to inform you that your reservation for <strong>" + service.getName() + "</strong> has been canceled.",
                "Reason: <strong>" + notes + "</strong>",
                "Make a New Reservation",
                "http://localhost:8080/"
        );

        emailConfigService.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }
}
