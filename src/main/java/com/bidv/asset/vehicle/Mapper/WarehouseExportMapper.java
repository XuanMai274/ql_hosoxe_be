package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.WarehouseExportDTO;
import com.bidv.asset.vehicle.entity.VehicleEntity;
import com.bidv.asset.vehicle.entity.WarehouseExportEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WarehouseExportMapper {

    private final VehicleMapper vehicleMapper;

    public WarehouseExportDTO toDto(WarehouseExportEntity entity) {
        if (entity == null) return null;

        WarehouseExportDTO dto = WarehouseExportDTO.builder()
                .id(entity.getId())
                .exportNumber(entity.getExportNumber())
                .exportDate(entity.getExportDate())
                .requestDate(entity.getRequestDate())
                .status(entity.getStatus())
                .totalDebtCollection(entity.getTotalDebtCollection())
                .vehicleCount(entity.getVehicleCount())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .totalCollateralValue(entity.getTotalCollateralValue())
                .realEstateValue(entity.getRealEstateValue())
                .build();

        if (entity.getVehicles() != null) {
            dto.setVehicleIds(entity.getVehicles().stream()
                    .map(VehicleEntity::getId)
                    .collect(Collectors.toList()));
            
            dto.setVehicles(entity.getVehicles().stream()
                    .map(vehicleMapper::toDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public WarehouseExportEntity toEntity(WarehouseExportDTO dto) {
        if (dto == null) return null;

        return WarehouseExportEntity.builder()
                .id(dto.getId())
                .exportNumber(dto.getExportNumber())
                .exportDate(dto.getExportDate())
                .requestDate(dto.getRequestDate())
                .status(dto.getStatus())
                .totalDebtCollection(dto.getTotalDebtCollection())
                .vehicleCount(dto.getVehicleCount())
                .description(dto.getDescription())
                .createdAt(dto.getCreatedAt())
                .createdBy(dto.getCreatedBy())
                .realEstateValue(dto.getRealEstateValue())
                .totalCollateralValue(dto.getTotalCollateralValue())
                .build();
    }
}
