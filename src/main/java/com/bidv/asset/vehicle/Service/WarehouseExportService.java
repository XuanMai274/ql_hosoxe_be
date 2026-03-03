package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.WarehouseExportDTO;

public interface WarehouseExportService {
    WarehouseExportDTO requestExport(WarehouseExportDTO dto);

    WarehouseExportDTO approveExport(WarehouseExportDTO dto);

    WarehouseExportDTO rejectExport(Long id);

    java.util.List<WarehouseExportDTO> getPendingRequests();

    org.springframework.data.domain.Page<WarehouseExportDTO> getAll(String exportNumber,
            org.springframework.data.domain.Pageable pageable);

    WarehouseExportDTO getById(Long id);

    WarehouseExportDTO updateWarehouseExport(Long id, WarehouseExportDTO dto);
}
