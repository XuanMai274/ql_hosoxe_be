package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.GuaranteeLetterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GuaranteeLetterRepository extends CrudRepository<GuaranteeLetterEntity,Long> {
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
    Page<GuaranteeLetterEntity> searchGuaranteeLetters(
            @Param("manufacturerCode") String manufacturerCode,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );
    @Query("""
        SELECT gl FROM GuaranteeLetterEntity gl
        JOIN gl.manufacturer m
        WHERE (
            COALESCE(:keyword, '') = ''
            OR LOWER(gl.guaranteeContractNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(gl.referenceCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        AND (COALESCE(:manufacturerCode, '') = '' OR m.code = :manufacturerCode)
        AND (CAST(:fromDate AS date) IS NULL OR gl.guaranteeContractDate >= :fromDate)
        AND (CAST(:toDate AS date) IS NULL OR gl.guaranteeContractDate <= :toDate)
        """)
    Page<GuaranteeLetterEntity> search(
            @Param("keyword") String keyword,
            @Param("manufacturerCode") String manufacturerCode,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );
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
            Pageable pageable
    );

}
