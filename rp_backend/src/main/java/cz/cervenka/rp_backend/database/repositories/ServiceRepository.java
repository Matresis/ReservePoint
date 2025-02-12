package cz.cervenka.rp_backend.database.repositories;

import cz.cervenka.rp_backend.database.entities.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    Optional<ServiceEntity> findByName(String name);
}
