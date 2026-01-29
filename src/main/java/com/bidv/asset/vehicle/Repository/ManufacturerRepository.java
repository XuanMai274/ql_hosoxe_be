package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.ManufacturerEntity;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManufacturerRepository extends CrudRepository<ManufacturerEntity,Long> {
    @NonNull
    List<ManufacturerEntity> findAll();
}
