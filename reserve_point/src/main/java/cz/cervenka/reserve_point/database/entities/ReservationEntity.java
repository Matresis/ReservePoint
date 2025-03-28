package cz.cervenka.reserve_point.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Setter
@Getter
@Entity
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Status {
        PENDING, APPROVED, CONFIRMED, CANCELED, PENDING_CONFIRMATION
    }

    @Column
    private LocalDateTime orderedTime;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Setter
    @Getter
    @Transient
    private String formattedCreatedAt;

    @Setter
    @Getter
    @Transient
    private String formattedOrderTime;

    public void formatOrderTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        this.formattedOrderTime = (orderedTime != null) ? orderedTime.format(formatter) : "N/A";
    }

    public void formatCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        this.formattedCreatedAt = (createdAt != null) ? createdAt.format(formatter) : "N/A";
    }
}