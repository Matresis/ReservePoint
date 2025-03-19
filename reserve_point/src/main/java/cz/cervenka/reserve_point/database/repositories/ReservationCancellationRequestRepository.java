package cz.cervenka.reserve_point.database.repositories;

import cz.cervenka.reserve_point.database.entities.ReservationCancellationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationCancellationRequestRepository extends JpaRepository<ReservationCancellationRequestEntity, Long> {
}
