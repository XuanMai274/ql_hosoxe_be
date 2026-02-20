package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity,Long> {
    List<DocumentEntity> findByVehicleId(Long vehicleId);
    List<DocumentEntity> findByStatus(String status);
}
