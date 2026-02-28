package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.VehicleDTO;
import com.bidv.asset.vehicle.DTO.VehicleListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface VehicleService {
    Page<VehicleListDTO> getVehicles(
            String chassisNumber,
            String status,
            String manufacturer,
            String ref,
            Pageable pageable
    );
    VehicleDTO getVehicleDetail(Long id);
    public VehicleDTO updateVehicle(Long id, VehicleDTO dto);
    List<VehicleDTO> getVehiclesByStatus(String status);
    List<VehicleDTO> findByIds(List<Long> ids);
    Page<VehicleDTO> getAvailableVehicles(String status, String chassisNumber, String manufacturerCode, String ref, Pageable pageable);
    List<VehicleDTO> getVehiclesByExportId(Long exportId);
}
