package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.GuaranteeLetterEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GuaranteeLetterRepository extends JpaRepository<GuaranteeLetterEntity, Long> {
    GuaranteeLetterEntity findByGuaranteeContractNumber(String guaranteeContractNumber);

    GuaranteeLetterEntity findByReferenceCode(String referenceCode);

    @Query("""
                SELECT gl
                FROM GuaranteeLetterEntity gl
                JOIN gl.manufacturer m
                WHERE (:manufacturerCode IS NULL OR m.code = :manufacturerCode)
                  AND (:fromDate IS NULL OR gl.guaranteeContractDate >= :fromDate)
                  AND (:toDate IS NULL OR gl.guaranteeContractDate <= :toDate)
            """)
    @EntityGraph(attributePaths = {
            "manufacturer",
            "creditContract",
            "mortgageContract",
            "authorizedRepresentative",
            "customer"
    })
    Page<GuaranteeLetterEntity> searchGuaranteeLetters(
            @Param("manufacturerCode") String manufacturerCode,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable);

    @Query("""
                SELECT gl FROM GuaranteeLetterEntity gl
                JOIN gl.manufacturer m
                WHERE (
                    COALESCE(:keyword, '') = ''
                    OR LOWER(gl.guaranteeContractNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(gl.referenceCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )

                AND (COALESCE(:manufacturerCode, '') = '' OR m.code = :manufacturerCode)

                AND (:fromDate IS NULL OR gl.guaranteeContractDate >= :fromDate)

                AND (:toDate IS NULL OR gl.guaranteeContractDate <= :toDate)

                AND (
                    :hasLetterNumber IS NULL
                    OR (:hasLetterNumber = true AND gl.guaranteeNoticeNumber IS NOT NULL)
                    OR (:hasLetterNumber = false AND gl.guaranteeNoticeNumber IS NULL)
                )
            """)
    @EntityGraph(attributePaths = {
            "manufacturer",
            "creditContract",
            "mortgageContract",
            "authorizedRepresentative",
            "customer"
    })
    Page<GuaranteeLetterEntity> search(
            @Param("keyword") String keyword,
            @Param("manufacturerCode") String manufacturerCode,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("hasLetterNumber") Boolean hasLetterNumber,
            Pageable pageable);

    @Query("""
                SELECT gl
                FROM GuaranteeLetterEntity gl
                JOIN gl.manufacturer m
                WHERE
                    m.code = :manufacturerCode
                    AND (
                        gl.guaranteeNoticeNumber ILIKE CONCAT(:keyword, '%')
                        OR gl.referenceCode ILIKE CONCAT(:keyword, '%')
                    )
                ORDER BY gl.guaranteeContractDate DESC
            """)
    List<GuaranteeLetterEntity> suggestGuaranteeLetters(
            @Param("keyword") String keyword,
            @Param("manufacturerCode") String manufacturerCode,
            Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from GuaranteeLetterEntity g where g.id = :id")
    Optional<GuaranteeLetterEntity> findByIdForUpdate(@Param("id") Long id);

    /**
     * Lấy tất cả thư bảo lãnh đã quá hạn (expiryDate < today) và vẫn còn ACTIVE.
     * Scheduler sẽ dùng list này để batch-update sang EXPIRED.
     */
    @Query("""
                SELECT g FROM GuaranteeLetterEntity g
                WHERE g.status = 'ACTIVE'
                  AND g.expiryDate IS NOT NULL
                  AND g.expiryDate < :today
            """)
    List<GuaranteeLetterEntity> findExpiredActiveGuarantees(@Param("today") LocalDate today);

    long countByStatus(String status);
}
