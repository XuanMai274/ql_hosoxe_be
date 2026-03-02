package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.GuaranteeApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuaranteeApplicationRepository
        extends JpaRepository<GuaranteeApplicationEntity, Long> {

    Optional<GuaranteeApplicationEntity> findByApplicationNumber(String applicationNumber);

    GuaranteeApplicationEntity findById(long id);

    @org.springframework.data.jpa.repository.Query(value = "SELECT g FROM GuaranteeApplicationEntity g " +
            "WHERE (cast(:customerId as long) IS NULL OR g.customer.id = :customerId) " +
            "AND (cast(:manufacturerId as long) IS NULL OR g.manufacturer.id = :manufacturerId) " +
            "AND (cast(:fromDate as timestamp) IS NULL OR g.createdAt >= :fromDate) " +
            "AND (cast(:toDate as timestamp) IS NULL OR g.createdAt <= :toDate) " +
            "ORDER BY g.createdAt DESC", countQuery = "SELECT count(g) FROM GuaranteeApplicationEntity g " +
                    "WHERE (cast(:customerId as long) IS NULL OR g.customer.id = :customerId) " +
                    "AND (cast(:manufacturerId as long) IS NULL OR g.manufacturer.id = :manufacturerId) " +
                    "AND (cast(:fromDate as timestamp) IS NULL OR g.createdAt >= :fromDate) " +
                    "AND (cast(:toDate as timestamp) IS NULL OR g.createdAt <= :toDate)")
    org.springframework.data.domain.Page<GuaranteeApplicationEntity> search(
            @org.springframework.data.repository.query.Param("customerId") Long customerId,
            @org.springframework.data.repository.query.Param("manufacturerId") Long manufacturerId,
            @org.springframework.data.repository.query.Param("fromDate") java.time.LocalDateTime fromDate,
            @org.springframework.data.repository.query.Param("toDate") java.time.LocalDateTime toDate,
            org.springframework.data.domain.Pageable pageable);
}