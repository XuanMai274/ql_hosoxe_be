package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.DisbursementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DisbursementRepository extends JpaRepository<DisbursementEntity, Long> {

    @Query("""
                SELECT d FROM DisbursementEntity d
                WHERE (:loanContractNumber IS NULL OR d.loanContractNumber LIKE %:loanContractNumber%)
                AND (:disbursementDateFrom IS NULL OR d.disbursementDate >= :disbursementDateFrom)
                AND (:disbursementDateTo IS NULL OR d.disbursementDate <= :disbursementDateTo)
                AND (:creditContractId IS NULL OR d.creditContract.id = :creditContractId)
            """)
    Page<DisbursementEntity> searchDisbursements(
            @Param("loanContractNumber") String loanContractNumber,
            @Param("disbursementDateFrom") LocalDate disbursementDateFrom,
            @Param("disbursementDateTo") LocalDate disbursementDateTo,
            @Param("creditContractId") Long creditContractId,
            Pageable pageable);

    @Query("""
            select max(d.childSequence)
            from DisbursementEntity d
            where d.creditContract.id = :creditId
            """)
    Integer findMaxChildSequence(@Param("creditId") Long creditId);

    /**
     * Lấy danh sách giải ngân của khách hàng
     */
    @Query("""
                SELECT d FROM DisbursementEntity d
                WHERE d.mortgageContract.customer.id = :customerId
                ORDER BY d.createdAt DESC
            """)
    List<DisbursementEntity> findByCustomerId(@Param("customerId") Long customerId);
}
