package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.VehicleDTO;
import com.bidv.asset.vehicle.DTO.VehicleListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VehicleService {
    Page<VehicleListDTO> getVehicles(
            String chassisNumber,
            String status,
            String manufacturer,
            String guaranteeContractNumber,
            Pageable pageable
    );
    VehicleDTO getVehicleDetail(Long id);
    public VehicleDTO updateVehicle(Long id, VehicleDTO dto);
}
