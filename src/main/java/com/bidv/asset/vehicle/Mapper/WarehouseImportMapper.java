package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.WarehouseImportDTO;
import com.bidv.asset.vehicle.entity.VehicleEntity;
import com.bidv.asset.vehicle.entity.WarehouseImportEntity;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WarehouseImportMapper {
    @Autowired ManufacturerMapper manufacturerMapper;
    @Autowired MortgageContractMapper mortgageContractMapper;
    /*
     ===============================
            ENTITY -> DTO
     ===============================
     */
    public  WarehouseImportDTO toDTO(WarehouseImportEntity entity) {

        if (entity == null) return null;

        WarehouseImportDTO dto = new WarehouseImportDTO();

        dto.setId(entity.getId());
        dto.setImportNumber(entity.getImportNumber());
        dto.setCreatedAt(entity.getCreatedAt());

        // Manufacturer
        if (entity.getManufacturer() != null) {
            dto.setManufacturerDTO(
                    manufacturerMapper.toDto(entity.getManufacturer())
            );
        }

        // Mortgage Contract
        if (entity.getMortgageContract() != null) {
            dto.setMortgageContractDTO(
                    mortgageContractMapper.toDTO(entity.getMortgageContract())
            );
        }

        // Vehicle IDs
        if (entity.getVehicles() != null) {
            List<Long> vehicleIds = entity.getVehicles()
                    .stream()
                    .map(VehicleEntity::getId)
                    .collect(Collectors.toList());

            dto.setVehicleIds(vehicleIds);
        }

        return dto;
    }


    /*
     ===============================
            DTO -> ENTITY
     ===============================
     */
    public  WarehouseImportEntity toEntity(WarehouseImportDTO dto) {

        if (dto == null) return null;

        WarehouseImportEntity entity = new WarehouseImportEntity();

        entity.setId(dto.getId());
        entity.setImportNumber(dto.getImportNumber());
        entity.setCreatedAt(dto.getCreatedAt());

        // manufacturer, mortgageContract, vehicles
        // sẽ set trong service sau khi findById()

        return entity;
    }
}
