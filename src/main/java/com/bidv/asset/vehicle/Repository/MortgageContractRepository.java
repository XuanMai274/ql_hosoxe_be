package com.bidv.asset.vehicle.Repository;
import com.bidv.asset.vehicle.entity.MortgageContractEntity;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MortgageContractRepository
        extends JpaRepository<MortgageContractEntity, Long> {

    Optional<MortgageContractEntity> findByContractNumber(String contractNumber);

    boolean existsByContractNumber(String contractNumber);
    Optional<MortgageContractEntity>
    findFirstByCustomerIdAndManufacturerIdAndStatus(
            Long customerId,
            Long manufacturerId,
            String status
    );
    @Query("""
    SELECT m
    FROM MortgageContractEntity m
    JOIN m.creditContracts c
    WHERE c.id = :creditId
    AND m.manufacturer.id = :manufacturerId
""")
    List<MortgageContractEntity> findByCreditContractAndManufacturer(
            @Param("creditId") Long creditId,
            @Param("manufacturerId") Long manufacturerId);
}