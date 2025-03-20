package cz.cervenka.reserve_point.database.repositories;

import cz.cervenka.reserve_point.database.entities.ReservationEntity;
import cz.cervenka.reserve_point.database.entities.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    @Query("SELECT r FROM ReservationEntity r " +
            "JOIN r.customer c " +
            "JOIN c.user u " +
            "JOIN r.service s " +
            "WHERE (:name IS NULL OR u.name LIKE %:name%) " +
            "AND (:surname IS NULL OR u.surname LIKE %:surname%) " +
            "AND (:date IS NULL OR r.createdAt >= :date) " +
            "AND (:serviceType IS NULL OR s.name LIKE %:serviceType%)")
    List<ReservationEntity> findFilteredReservations(
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("date") LocalDate date,
            @Param("serviceType") String serviceType
    );

    List<ReservationEntity> findByCustomer(CustomerEntity customer);
}
