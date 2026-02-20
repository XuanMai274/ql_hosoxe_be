package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface CustomerRepository
        extends JpaRepository<CustomerEntity, Long> {

    Optional<CustomerEntity> findByCif(String cif);

    boolean existsByCif(String cif);
}

