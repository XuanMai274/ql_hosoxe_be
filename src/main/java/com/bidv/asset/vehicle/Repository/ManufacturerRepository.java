package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.ManufacturerEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManufacturerRepository extends JpaRepository<ManufacturerEntity,Long> {
    @NonNull
    List<ManufacturerEntity> findAll();
    @Query("SELECT m FROM ManufacturerEntity m WHERE m.id = :id")
    ManufacturerEntity findByIdManu(@Param("id") Long id);
}
