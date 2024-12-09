package cz.cervenka.rp_backend.database.repositories;

import cz.cervenka.rp_backend.database.entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findByEmail(String email);

    @Query("SELECT r FROM ReservationEntity r WHERE " +
            "(:name IS NULL OR r.name LIKE %:name%) AND " +
            "(:surname IS NULL OR r.surname LIKE %:surname%) AND " +
            "(:date IS NULL OR r.created_at = :date) AND " +
            "(:serviceType IS NULL OR r.serviceType LIKE %:serviceType%)")
    List<ReservationEntity> findFilteredReservations(
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("date") LocalDate date,
            @Param("serviceType") String serviceType);

}