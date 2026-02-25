package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.MortgageContractSequenceEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MortgageContractSequenceRepository
        extends JpaRepository<MortgageContractSequenceEntity, Long> {

    // ===== LOCK RECORD =====
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from MortgageContractSequenceEntity s where s.mortgageContractId = :id")
    Optional<MortgageContractSequenceEntity> findByIdForUpdate(@Param("id") Long id);
}
