package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.FullProcessNKGNRequest;
import com.bidv.asset.vehicle.DTO.FullProcessResponse;
import com.bidv.asset.vehicle.DTO.WarehouseImportDTO;
import com.bidv.asset.vehicle.DTO.WarehouseImportRequestDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WarehouseImportService {
    WarehouseImportDTO importWarehouse(WarehouseImportRequestDTO request);

    Page<WarehouseImportDTO> getAll(String importNumber, Pageable pageable);

    WarehouseImportDTO getById(Long id);

    WarehouseImportDTO updateWarehouseImport(Long id, WarehouseImportDTO dto);

    FullProcessResponse executeFullProcess(
            FullProcessNKGNRequest request);
}
