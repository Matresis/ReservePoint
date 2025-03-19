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
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MMM.yyyy, HH:mm");

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
                "Service: <strong>" + service.getName() + "</strong>",
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
                "<p>We are pleased to inform you that your reservation for <strong>" + service.getName() + "</strong> has been approved.</p>",
                "<p>We scheduled your reservation on: <strong>" + reservation.getOrderedTime() + "</strong></p>\n"
                        + "<p>If you have any questions or the scheduled time does not comply, please contact us.</p>",
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
                "<p>We are pleased to confirm that your reservation for <strong>" + service.getName() + "</strong> has been successfully scheduled.</p>",
                "<p>Your appointment is set for: <strong>" + reservation.getOrderedTime().format(DATE_FORMATTER) + "</strong></p>\n"
                        + "<p>If you have any questions or need to make changes, please contact us.</p>\n"
                        + "<p>We look forward to serving you!</p>",
                "View Reservation Details",
                "http://localhost:8080/reservations"
        );

        emailConfigService.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }

    @Transactional
    public void sendReservationRejectionEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service, String notes) {
        String subject = "Reservation Rejected: " + service.getName();
        String customerEmailContent = emailConfigService.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "Unfortunately, your reservation for <strong>" + service.getName() + "</strong> made on: <strong>" + reservation.getCreatedAt().format(DATE_FORMATTER) + "</strong> has been rejected.",
                "The reason for rejection: <strong>" + notes + "</strong>\n"
                        + "<p>If you have any questions or you wish to recreate the reservation, please contact us or visit our website.</p>",
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
                "We regret to inform you that your reservation for <strong>" + service.getName() + "</strong> scheduled on: <strong>" + reservation.getOrderedTime().format(DATE_FORMATTER) + "</strong> has been canceled.",
                "The reason for rejection: <strong>" + notes + "</strong>\n"
                        + "<p>If you wish to reschedule, please visit our website.</p>",
                "Make a New Reservation",
                "http://localhost:8080/"
        );

        emailConfigService.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }

    @Transactional
    public void sendReservationModificationWishEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String subject = "Reservation Modification Request: " + service.getName();
        String adminEmailContent = emailConfigService.generateStyledEmail(
                "New Reservation Modification Request",
                "A request for reservation modification has been made by <strong>" + customer.getUser().getName() + " " + customer.getUser().getSurname() + "</strong>.",
                "Service: <strong>" + service.getName() + "</strong>",
                "Review Request",
                "http://localhost:8080/admin/reservations/" + reservation.getId()
        );

        emailConfigService.sendEmail("reservepointtp@gmail.com", subject, adminEmailContent);
    }

    @Transactional
    public void sendReservationCancellationWishEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String subject = "Reservation Cancellation Request: " + service.getName();
        String adminEmailContent = emailConfigService.generateStyledEmail(
                "New Reservation Cancellation Request",
                "A request for reservation cancellation has been made by <strong>" + customer.getUser().getName() + " " + customer.getUser().getSurname() + "</strong>.",
                "Service: <strong>" + service.getName() + "</strong>",
                "Review Request",
                "http://localhost:8080/admin/reservations/" + reservation.getId()
        );

        emailConfigService.sendEmail("reservepointtp@gmail.com", subject, adminEmailContent);
    }
}