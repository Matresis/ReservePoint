package cz.cervenka.rp_backend.database.repositories;

import cz.cervenka.rp_backend.database.entities.CustomerEntity;
import cz.cervenka.rp_backend.database.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer> {
    Optional<CustomerEntity> findByUser(UserEntity user);
}
