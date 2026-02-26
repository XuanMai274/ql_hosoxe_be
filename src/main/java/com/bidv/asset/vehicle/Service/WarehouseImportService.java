package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.WarehouseImportDTO;
import com.bidv.asset.vehicle.DTO.WarehouseImportRequestDTO;

public interface WarehouseImportService {
    WarehouseImportDTO importWarehouse(WarehouseImportRequestDTO request);
}
