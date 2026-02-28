package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.WarehouseExportDTO;

public interface WarehouseExportService {
    WarehouseExportDTO requestExport(WarehouseExportDTO dto);
    WarehouseExportDTO approveExport(Long id);
    WarehouseExportDTO rejectExport(Long id);
    java.util.List<WarehouseExportDTO> getPendingRequests();
}
