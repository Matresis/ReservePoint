package cz.cervenka.reserve_point.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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
        PENDING, APPROVED, CONFIRMED, CANCELED
    }

    @Column(nullable = true)
    private LocalDateTime orderedTime;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String rejectionReason;

    @Setter
    @Getter
    @Transient
    private String formattedCreatedAt;

    @Setter
    @Getter
    @Transient
    private String formattedOrderTime;
}