package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.ManufacturerDTO;
import com.bidv.asset.vehicle.entity.ManufacturerEntity;
import com.bidv.asset.vehicle.entity.VehicleEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
@Component
public class ManufacturerMapper {

    public ManufacturerDTO toDto(ManufacturerEntity entity) {
        if (entity == null) return null;

        ManufacturerDTO dto = new ManufacturerDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getVehicles() != null) {
            dto.setVehicleIds(
                    entity.getVehicles()
                            .stream()
                            .map(VehicleEntity::getId)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public ManufacturerEntity toEntity(ManufacturerDTO dto) {
        if (dto == null) return null;

        ManufacturerEntity entity = new ManufacturerEntity();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setCreatedAt(dto.getCreatedAt());

        return entity;
    }
}