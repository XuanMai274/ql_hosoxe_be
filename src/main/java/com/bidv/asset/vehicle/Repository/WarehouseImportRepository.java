package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.MortgageContractEntity;
import com.bidv.asset.vehicle.entity.WarehouseImportEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Lấy danh sách phiếu nhập kho theo customerId (phân trang)
     */
    @Query(value = """
                SELECT DISTINCT wi FROM WarehouseImportEntity wi
                LEFT JOIN FETCH wi.manufacturer
                LEFT JOIN FETCH wi.vehicles
                WHERE wi.mortgageContract.customer.id = :customerId
                ORDER BY wi.createdAt DESC
            """, countQuery = """
                SELECT COUNT(wi) FROM WarehouseImportEntity wi
                WHERE wi.mortgageContract.customer.id = :customerId
            """)
    Page<WarehouseImportEntity> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);
}
