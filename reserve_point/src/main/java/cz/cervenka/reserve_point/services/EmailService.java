package cz.cervenka.reserve_point.services;

import cz.cervenka.reserve_point.config.EmailConfig;
import cz.cervenka.reserve_point.database.entities.CustomerEntity;
import cz.cervenka.reserve_point.database.entities.ReservationEntity;
import cz.cervenka.reserve_point.database.entities.ServiceEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final EmailConfig emailConfig;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MMM.yyyy, HH:mm");

    public EmailService(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

    @Transactional
    public void sendReservationCreationEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String subject = "Reservation Received: " + service.getName();
        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "We have received your reservation request for <strong>" + service.getName() + "</strong>.",
                "We will notify you once your reservation is approved.",
                "View Reservations",
                "http://localhost:8080/reservations"
        );

        String adminEmailContent = emailConfig.generateStyledEmail(
                "New Reservation Request",
                "A new reservation request has been made by <strong>" + customer.getUser().getName() + " " + customer.getUser().getSurname() + "</strong>.",
                "Service: <strong>" + service.getName() + "</strong>",
                "Review Request",
                "http://localhost:8080/admin/reservations/" + reservation.getId()
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
        emailConfig.sendEmail("reservepointtp@gmail.com", "New Reservation Request", adminEmailContent);
    }

    @Transactional
    public void sendReservationApprovalEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String subject = "Reservation Approved: " + service.getName();
        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "<p>We are pleased to inform you that your reservation for <strong>" + service.getName() + "</strong> has been approved.</p>",
                "<p>We scheduled your reservation on: <strong>" + reservation.getOrderedTime() + "</strong></p><br>"
                        + "<p>If you have any questions or the scheduled time does not comply, please contact us.</p>",
                "View Reservation",
                "http://localhost:8080/reservations"
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }

    @Transactional
    public void sendReservationConfirmationEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String subject = "Reservation Confirmed: " + service.getName();
        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "<p>We are pleased to confirm that your reservation for <strong>" + service.getName() + "</strong> has been successfully scheduled.</p>",
                "<p>Your appointment is set for: <strong>" + reservation.getOrderedTime().format(DATE_FORMATTER) + "</strong></p><br>"
                        + "<p>If you have any questions or need to make changes, please contact us.</p><br>"
                        + "<p>We look forward to serving you!</p>",
                "View Reservation Details",
                "http://localhost:8080/reservations"
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }

    @Transactional
    public void sendReservationRejectionEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service, String notes) {
        String subject = "Reservation Rejected: " + service.getName();
        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "Unfortunately, your reservation for <strong>" + service.getName() + "</strong> made on: <strong>" + reservation.getCreatedAt().format(DATE_FORMATTER) + "</strong> has been rejected.",
                "The reason for rejection: <strong>" + notes + "</strong><br>"
                        + "<p>If you have any questions or you wish to recreate the reservation, please contact us or visit our website.</p>",
                "Make a New Reservation",
                "http://localhost:8080/"
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }

    @Transactional
    public void sendReservationCancellationEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service, String notes) {
        String subject = "Reservation Canceled: " + service.getName();
        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "We regret to inform you that your reservation for <strong>" + service.getName() + "</strong> scheduled on: <strong>" + reservation.getOrderedTime().format(DATE_FORMATTER) + "</strong> has been canceled.",
                "The reason for rejection: <strong>" + notes + "</strong><br>"
                        + "<p>If you wish to reschedule, please visit our website.</p>",
                "Make a New Reservation",
                "http://localhost:8080/"
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }

    @Transactional
    public void sendReservationModificationWishEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String subject = "Reservation Modification Request: " + service.getName();
        String adminEmailContent = emailConfig.generateStyledEmail(
                "New Reservation Modification Request",
                "A request for reservation modification has been made by <strong>" + customer.getUser().getName() + " " + customer.getUser().getSurname() + "</strong>.",
                "Service: <strong>" + service.getName() + "</strong>",
                "Review Request",
                "http://localhost:8080/admin/requests/" + reservation.getId()
        );

        emailConfig.sendEmail("reservepointtp@gmail.com", subject, adminEmailContent);
    }

    // TODO: modify the request viewing for specific reservation (when going from the email => highlight the request specific from that email)

    @Transactional
    public void sendReservationCancellationWishEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service, String reason) {
        String subject = "Reservation Cancellation Request: " + service.getName();
        String adminEmailContent = emailConfig.generateStyledEmail(
                "New Reservation Cancellation Request",
                "A request for reservation cancellation has been made by <strong>" + customer.getUser().getName() + " " + customer.getUser().getSurname() + "</strong>.",
                "Service: <strong>" + service.getName() + "</strong><br>"
                        + "Reason: <strong>" + reason + "</strong>",
                "Review Request",
                "http://localhost:8080/admin/requests/" + reservation.getId()
        );

        emailConfig.sendEmail("reservepointtp@gmail.com", subject, adminEmailContent);
    }

    @Transactional
    public void sendReservationModificationApprovalEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String subject = "Reservation Modification Approved: " + service.getName();
        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "<p>Your request to modify your reservation for <strong>" + service.getName() + "</strong> has been approved.</p>",
                "<p>New details:</p><br>"
                        + "<ul>"
                        + "<li><strong>Service:</strong> " + reservation.getService().getName() + "</li>"
                        + "<li><strong>Date & Time:</strong> " + reservation.getOrderedTime().format(DATE_FORMATTER) + "</li>"
                        + "<li><strong>Notes:</strong> " + reservation.getNotes() + "</li>"
                        + "</ul>",
                "View Reservation",
                "http://localhost:8080/reservations"
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }

    @Transactional
    public void sendReservationModificationRejectionEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String subject = "Reservation Modification Rejected: " + service.getName();
        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "<p>Unfortunately, your request to modify your reservation for <strong>" + service.getName() + "</strong> has been rejected.</p>",
                "<p>Unaffected details:</p><br>"
                        + "<ul>"
                        + "<li><strong>Service:</strong> " + reservation.getService().getName() + "</li>"
                        + "<li><strong>Date & Time:</strong> " + reservation.getOrderedTime().format(DATE_FORMATTER) + "</li>"
                        + "<li><strong>Notes:</strong> " + reservation.getNotes() + "</li>"
                        + "</ul><br>"
                        + "<p>If you have any questions, please contact us.</p>",
                "View Reservation",
                "http://localhost:8080/reservations"
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }

    @Transactional
    public void sendReservationCancellationApprovalEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service, String reason) {
        String subject = "Reservation Canceled: " + service.getName();
        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "Your reservation for: <strong>" + service.getName() + "</strong> scheduled on: <strong>" + reservation.getOrderedTime().format(DATE_FORMATTER) + "</strong> has been successfully canceled per your request.",
                "The reason for cancellation: <strong>" + reason + "</strong><br>"
                        + "<p>If you wish to reschedule, please visit our website.</p>",
                "Make a New Reservation",
                "http://localhost:8080/"
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }
}