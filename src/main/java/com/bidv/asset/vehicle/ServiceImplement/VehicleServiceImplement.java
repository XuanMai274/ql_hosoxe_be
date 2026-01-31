package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.VehicleDetailDTO;
import com.bidv.asset.vehicle.DTO.VehicleListDTO;
import com.bidv.asset.vehicle.Mapper.GuaranteeLetterMapper;
import com.bidv.asset.vehicle.Mapper.InvoiceMapper;
import com.bidv.asset.vehicle.Mapper.VehicleMapper;
import com.bidv.asset.vehicle.Repository.VehicleRepository;
import com.bidv.asset.vehicle.Service.VehicleService;
import com.bidv.asset.vehicle.entity.VehicleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class VehicleServiceImplement implements VehicleService {
    @Autowired
    VehicleRepository vehicleRepository;
    @Autowired
    VehicleMapper vehicleMapper;
    @Autowired
    GuaranteeLetterMapper guaranteeLetterMapper;
    @Autowired
    InvoiceMapper invoiceMapper;
    @Override
    public Page<VehicleListDTO> getVehicles(
            String chassisNumber,
            String status,
            String manufacturer,
            String guaranteeContractNumber,
            Pageable pageable
    ) {
        return vehicleRepository.searchVehicles(
                chassisNumber,
                status,
                manufacturer,
                guaranteeContractNumber,
                pageable
        );
    }

    @Override
    public VehicleDetailDTO getVehicleDetail(Long id) {
        VehicleEntity v = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe"));

        return new VehicleDetailDTO(
                vehicleMapper.toDto(v),
                guaranteeLetterMapper.toDto(v.getGuaranteeLetter()),
                invoiceMapper.toDto(v.getInvoice())
        );
    }
}
