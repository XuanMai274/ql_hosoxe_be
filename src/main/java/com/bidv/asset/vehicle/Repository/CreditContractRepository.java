package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.CreditContractEntity;
<<<<<<< HEAD
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
=======
import jakarta.persistence.LockModeType;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
>>>>>>> origin/XuanMai
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
<<<<<<< HEAD
=======
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CreditContractEntity c where c.id = :id")
    Optional<CreditContractEntity> findByIdForUpdate(@Param("id") Long id);
>>>>>>> origin/XuanMai
}
