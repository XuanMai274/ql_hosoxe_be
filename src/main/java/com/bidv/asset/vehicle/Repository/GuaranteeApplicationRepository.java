package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.GuaranteeApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuaranteeApplicationRepository
                extends JpaRepository<GuaranteeApplicationEntity, Long> {

        Optional<GuaranteeApplicationEntity> findByApplicationNumber(String applicationNumber);

        long countByStatus(String status);

        GuaranteeApplicationEntity findById(long id);

        @org.springframework.data.jpa.repository.Query(value = "SELECT g FROM GuaranteeApplicationEntity g " +
                        "WHERE (cast(:customerId as long) IS NULL OR g.customer.id = :customerId) " +
                        "AND (cast(:manufacturerId as long) IS NULL OR g.manufacturer.id = :manufacturerId) " +
                        "AND (:status IS NULL OR g.status = :status) " +
                        "AND (cast(:fromDate as timestamp) IS NULL OR g.createdAt >= :fromDate) " +
                        "AND (cast(:toDate as timestamp) IS NULL OR g.createdAt <= :toDate) " +
                "ORDER BY CASE WHEN g.status LIKE 'PENDING%' OR g.status = 'SUBMITTED' THEN 0 ELSE 1 END ASC, g.createdAt DESC", countQuery = "SELECT count(g) FROM GuaranteeApplicationEntity g "
                                        +
                                        "WHERE (cast(:customerId as long) IS NULL OR g.customer.id = :customerId) " +
                                        "AND (cast(:manufacturerId as long) IS NULL OR g.manufacturer.id = :manufacturerId) "
                                        +
                                        "AND (:status IS NULL OR g.status = :status) " +
                                        "AND (cast(:fromDate as timestamp) IS NULL OR g.createdAt >= :fromDate) " +
                                        "AND (cast(:toDate as timestamp) IS NULL OR g.createdAt <= :toDate)")
        org.springframework.data.domain.Page<GuaranteeApplicationEntity> search(
                        @org.springframework.data.repository.query.Param("customerId") Long customerId,
                        @org.springframework.data.repository.query.Param("manufacturerId") Long manufacturerId,
                        @org.springframework.data.repository.query.Param("status") String status,
                        @org.springframework.data.repository.query.Param("fromDate") java.time.LocalDateTime fromDate,
                        @org.springframework.data.repository.query.Param("toDate") java.time.LocalDateTime toDate,
                        org.springframework.data.domain.Pageable pageable);

        @org.springframework.data.jpa.repository.Query(value = "SELECT g FROM GuaranteeApplicationEntity g " +
                        "WHERE (cast(:customerId as long) IS NULL OR g.customer.id = :customerId) " +
                        "AND (cast(:manufacturerId as long) IS NULL OR g.manufacturer.id = :manufacturerId) " +
                        "AND (:status IS NULL OR g.status = :status) " +
                        "AND g.status <> 'REJECTED' " +
                        "AND (cast(:fromDate as timestamp) IS NULL OR g.createdAt >= :fromDate) " +
                        "AND (cast(:toDate as timestamp) IS NULL OR g.createdAt <= :toDate) " +
                        "ORDER BY CASE WHEN g.status LIKE 'PENDING%' OR g.status = 'SUBMITTED' THEN 0 ELSE 1 END ASC, g.createdAt DESC", countQuery = "SELECT count(g) FROM GuaranteeApplicationEntity g "
                                        +
                                        "WHERE (cast(:customerId as long) IS NULL OR g.customer.id = :customerId) " +
                                        "AND (cast(:manufacturerId as long) IS NULL OR g.manufacturer.id = :manufacturerId) "
                                        +
                                        "AND (:status IS NULL OR g.status = :status) " +
                                        "AND g.status <> 'REJECTED' " +
                                        "AND (cast(:fromDate as timestamp) IS NULL OR g.createdAt >= :fromDate) " +
                                        "AND (cast(:toDate as timestamp) IS NULL OR g.createdAt <= :toDate)")
        org.springframework.data.domain.Page<GuaranteeApplicationEntity> searchExcludeRejected(
                        @org.springframework.data.repository.query.Param("customerId") Long customerId,
                        @org.springframework.data.repository.query.Param("manufacturerId") Long manufacturerId,
                        @org.springframework.data.repository.query.Param("status") String status,
                        @org.springframework.data.repository.query.Param("fromDate") java.time.LocalDateTime fromDate,
                        @org.springframework.data.repository.query.Param("toDate") java.time.LocalDateTime toDate,
                        org.springframework.data.domain.Pageable pageable);
}