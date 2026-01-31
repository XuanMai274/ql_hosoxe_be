package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.VehicleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends CrudRepository<VehicleEntity,Long> {
    boolean existsByChassisNumber(String chassisNumber);

}
