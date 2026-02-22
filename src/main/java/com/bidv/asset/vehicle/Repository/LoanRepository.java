package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.LoanEntity;
import com.bidv.asset.vehicle.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
    Page<LoanEntity> findAllByLoanStatus(LoanStatus status, Pageable pageable);
    @Query("""
    SELECT l FROM LoanEntity l
    JOIN l.vehicle v
    WHERE (:loanContractNumber IS NULL OR l.loanContractNumber LIKE :loanContractNumber)
    AND (:chassisNumber IS NULL OR v.chassisNumber LIKE :chassisNumber)
    AND (:status IS NULL  OR l.loanStatus = :status)
    AND (:docId IS NULL OR l.docId LIKE :docId)
    AND (:dueDateFrom IS NULL OR l.dueDate BETWEEN :dueDateFrom AND :dueDateTo)
""")
    Page<LoanEntity> searchLoans(
            @Param("loanContractNumber") String loanContractNumber,
            @Param("chassisNumber") String chassisNumber,
            @Param("status") LoanStatus status,
            @Param("docId") String docId,
            @Param("dueDateFrom") LocalDate dueDateFrom,
            @Param("dueDateTo") LocalDate dueDateTo,
            Pageable pageable
    );

}
