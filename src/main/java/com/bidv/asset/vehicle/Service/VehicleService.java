package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.VehicleDetailDTO;
import com.bidv.asset.vehicle.DTO.VehicleListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface VehicleService {
    Page<VehicleListDTO> getVehicles(
            String chassisNumber,
            String status,
            String manufacturer,
            String guaranteeContractNumber,
            Pageable pageable
    );
    VehicleDetailDTO getVehicleDetail(Long id);
}
