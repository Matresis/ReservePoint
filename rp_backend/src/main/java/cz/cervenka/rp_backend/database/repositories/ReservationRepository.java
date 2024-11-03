package cz.cervenka.rp_backend.database.repositories;

import cz.cervenka.rp_backend.database.entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
}