package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.entity.VehicleCatalogEntity;
import java.util.List;

public interface VehicleCatalogService {
    List<VehicleCatalogEntity> getAll();

    VehicleCatalogEntity save(VehicleCatalogEntity entity);

    void delete(Long id);

    Integer getSeatsByModelName(String modelName);
}
