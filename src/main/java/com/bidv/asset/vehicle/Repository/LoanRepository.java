package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.CreditContractEntity;
import com.bidv.asset.vehicle.entity.LoanEntity;
import com.bidv.asset.vehicle.enums.LoanStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
            Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CreditContractEntity c WHERE c.id = :id")
    Optional<CreditContractEntity> findByIdForUpdate(@Param("id") Long id);

//    @Query("""
//                SELECT COALESCE(MAX(l.childSequence), 0)
//                FROM LoanEntity l
//                WHERE l.creditContract.id = :creditContractId
//            """)
//    Integer findMaxChildSequence(@Param("creditContractId") Long creditContractId);

    /**
     * Lấy danh sách khoản vay theo customerId (phân trang)
     */
    Page<LoanEntity> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * Lấy danh sách khoản vay theo customerId, sắp xếp theo ngày tạo giảm dần
     */
    List<LoanEntity> findByCustomerIdOrderByCreatedAtDesc(@Param("customerId") Long customerId);
        @Query("""
        SELECT l
        FROM LoanEntity l
        JOIN FETCH l.disbursement d
        WHERE l.id IN :loanIds
    """)
        List<LoanEntity> findAllWithDisbursementByIdIn(@Param("loanIds") List<Long> loanIds);


        @Query("""
        SELECT l
        FROM LoanEntity l
        WHERE l.disbursement.id IN :disbursementIds
    """)
    List<LoanEntity> findByDisbursementIds(@Param("disbursementIds") List<Long> disbursementIds);
}
