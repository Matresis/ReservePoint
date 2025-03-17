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
        String customerEmailContent = emailConfigService.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "Your reservation for <strong>" + service.getName() + "</strong> has been received.",
                "We will notify you once it's approved.",
                "Review Reservation",
                "http://localhost:8080/reservations"
        );

        String adminEmailContent = emailConfigService.generateStyledEmail(
                "New reservation request",
                "A new reservation request has been received from <strong>" + customer.getUser().getName() + " " + customer.getUser().getSurname() + "</strong>.",
                "Service Type: <strong>" + service.getName() + "</strong>",
                "Review Reservation",
                "http://localhost:8080/admin/reservations/" + reservation.getId()
        );

        emailConfigService.sendEmail(customer.getUser().getEmail(), "Reservation Confirmation", customerEmailContent);
        emailConfigService.sendEmail("reservepointtp@gmail.com", "New Reservation Request", adminEmailContent);
    }

    public void sendReservationApprovalEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String customerEmailContent = emailConfigService.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "Your reservation for <strong>" + service.getName() + "</strong> has been approved.",
                "Scheduled Date: <strong>" + reservation.getOrderedTime() + "</strong>",
                "View Reservation",
                "http://localhost:8080/reservations"
        );

        emailConfigService.sendEmail(customer.getUser().getEmail(), "Reservation Approved", customerEmailContent);
    }

    public void sendReservationConfirmationEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String customerEmailContent = emailConfigService.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "We are pleased to confirm that your reservation for <strong>" + service.getName() + "</strong> has been successfully scheduled.",
                "Appointment Date: <strong>" + reservation.getOrderedTime() + "</strong>",
                "View Reservation Details",
                "http://localhost:8080/reservations"
        );

        emailConfigService.sendEmail(customer.getUser().getEmail(), "Reservation Confirmed", customerEmailContent);
    }

    public void sendReservationRejectionEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String customerEmailContent = emailConfigService.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "Unfortunately, your reservation for <strong>" + service.getName() + "</strong> has been rejected.",
                "Reason: <strong>" + reservation.getRejectionReason() + "</strong>",
                "Review Reservation",
                "http://localhost:8080/reservations"
        );

        emailConfigService.sendEmail(customer.getUser().getEmail(), "Reservation Rejected", customerEmailContent);
    }

    public void sendReservationCancellationEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String customerEmailContent = emailConfigService.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "We regret to inform you that your reservation for <strong>" + service.getName() + "</strong> has been canceled.",
                "Reason: <strong>" + reservation.getRejectionReason() + "</strong>",
                "Review Reservation",
                "http://localhost:8080/reservations"
        );

        emailConfigService.sendEmail(customer.getUser().getEmail(), "Reservation Canceled", customerEmailContent);
    }
}