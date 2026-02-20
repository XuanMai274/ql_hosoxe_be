package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.CreditContractEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditContractRepository extends JpaRepository<CreditContractEntity,Long> {
    @NonNull
    List<CreditContractEntity> findAll();
    Optional<CreditContractEntity> findByContractNumber(String contractNumber);

    boolean existsByContractNumber(String contractNumber);

    List<CreditContractEntity> findByCustomer_Id(Long customerId);
    Optional<CreditContractEntity> findFirstByCustomerIdAndStatus(
            Long customerId,
            String status
    );
}
