package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.CreditContractDTO;
import com.bidv.asset.vehicle.entity.CreditContractEntity;
import com.bidv.asset.vehicle.entity.GuaranteeLetterEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CreditContractMapper {

    public CreditContractDTO toDto(CreditContractEntity entity) {
        if (entity == null) return null;

        CreditContractDTO dto = new CreditContractDTO();
        dto.setId(entity.getId());
        dto.setContractNumber(entity.getContractNumber());
        dto.setContractDate(entity.getContractDate());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getGuaranteeLetters() != null) {
            dto.setGuaranteeLetterIds(
                    entity.getGuaranteeLetters()
                            .stream()
                            .map(GuaranteeLetterEntity::getId)
                            .collect(Collectors.toList())
            );
        }
        return dto;
    }

    public CreditContractEntity toEntity(CreditContractDTO dto) {
        if (dto == null) return null;

        CreditContractEntity entity = new CreditContractEntity();
        entity.setId(dto.getId());
        entity.setContractNumber(dto.getContractNumber());
        entity.setContractDate(dto.getContractDate());
        entity.setCreatedAt(dto.getCreatedAt());

        return entity;
    }
}