package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.ManufacturerDTO;
import com.bidv.asset.vehicle.entity.GuaranteeLetterEntity;
import com.bidv.asset.vehicle.entity.ManufacturerEntity;
import com.bidv.asset.vehicle.entity.VehicleEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ManufacturerMapper {

    public ManufacturerDTO toDto(ManufacturerEntity entity) {
        if (entity == null)
            return null;

        ManufacturerDTO dto = new ManufacturerDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setLogo(entity.getLogo());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setGuaranteeRate(entity.getGuaranteeRate());
        dto.setTemplateCode(entity.getTemplateCode());
        if (entity.getGuaranteeLetterEntities() != null) {
            dto.setGuaranteeLetterIds(
                    entity.getGuaranteeLetterEntities()
                            .stream()
                            .map(GuaranteeLetterEntity::getId)
                            .toList());
        }

        return dto;
    }

    public ManufacturerEntity toEntity(ManufacturerDTO dto) {
        if (dto == null)
            return null;

        ManufacturerEntity entity = new ManufacturerEntity();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setLogo(dto.getLogo());
        entity.setDescription(dto.getDescription());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setGuaranteeRate(dto.getGuaranteeRate());
        entity.setTemplateCode(dto.getTemplateCode());
        return entity;
    }

    public void updateEntity(ManufacturerEntity entity, ManufacturerDTO dto) {
        if (dto == null)
            return;
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setLogo(dto.getLogo());
        entity.setDescription(dto.getDescription());
        entity.setGuaranteeRate(dto.getGuaranteeRate());
        entity.setTemplateCode(dto.getTemplateCode());
    }
}
