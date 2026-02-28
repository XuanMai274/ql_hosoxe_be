package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.WarehouseExportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseExportRepository extends JpaRepository<WarehouseExportEntity, Long> {
    Optional<WarehouseExportEntity> findByExportNumber(String exportNumber);

    @Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING(export_number, INSTR(export_number, '.') + 1, 2) AS UNSIGNED)), 0) " +
           "FROM warehouse_export WHERE export_number LIKE %?1%", nativeQuery = true)
    Integer findMaxRunningNo(String baseNumberPart);
}
