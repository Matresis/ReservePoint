package cz.cervenka.reserve_point.database.repositories;

import cz.cervenka.reserve_point.database.entities.ReservationConfirmationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationConfirmationRequestRepository extends JpaRepository<ReservationConfirmationRequestEntity, Long> {
}
