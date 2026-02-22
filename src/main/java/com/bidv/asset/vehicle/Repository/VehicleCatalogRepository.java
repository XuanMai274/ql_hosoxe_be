package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.VehicleCatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleCatalogRepository extends JpaRepository<VehicleCatalogEntity, Long> {
    Optional<VehicleCatalogEntity> findByModelName(String modelName);

    // Thêm tìm kiếm gần đúng nếu cần
    Optional<VehicleCatalogEntity> findFirstByModelNameContainingIgnoreCase(String modelName);
}
