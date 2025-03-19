package cz.cervenka.reserve_point.database.repositories;

import cz.cervenka.reserve_point.database.entities.ReservationModificationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationModificationRequestRepository extends JpaRepository<ReservationModificationRequestEntity, Long> {
}
