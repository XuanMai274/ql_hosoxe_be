package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.MortgageContractEntity;
import com.bidv.asset.vehicle.entity.WarehouseImportEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseImportRepository extends JpaRepository<WarehouseImportEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from MortgageContractEntity m where m.id = :id")
    Optional<MortgageContractEntity> findByIdForUpdate(@Param("id") Long id);

    /**
     * Lấy danh sách phiếu nhập kho theo customerId
     * (thông qua vehicles → guaranteeLetter → customer)
     */
    @Query("""
                SELECT DISTINCT wi FROM WarehouseImportEntity wi
                JOIN wi.vehicles v
                JOIN v.guaranteeLetter gl
                JOIN gl.customer c
                WHERE c.id = :customerId
                ORDER BY wi.createdAt DESC
            """)
    List<WarehouseImportEntity> findByCustomerId(@Param("customerId") Long customerId);
}
