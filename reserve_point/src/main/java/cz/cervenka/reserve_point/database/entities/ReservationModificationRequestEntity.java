package cz.cervenka.reserve_point.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Setter
public class ReservationModificationRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationEntity reservation;

    private String requestedNotes;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity requestedService;

    private LocalDateTime requestedOrderTime;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    @Transient
    private String formattedRequestedOrderTime;

    public void formatRequestedOrderTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        this.formattedRequestedOrderTime = (requestedOrderTime != null) ? requestedOrderTime.format(formatter) : "N/A";
    }
}