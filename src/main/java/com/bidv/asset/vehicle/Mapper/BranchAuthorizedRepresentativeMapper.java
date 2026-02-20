package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.BranchAuthorizedRepresentativeDTO;
import com.bidv.asset.vehicle.entity.BranchAuthorizedRepresentativeEntity;
import com.bidv.asset.vehicle.entity.GuaranteeLetterEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BranchAuthorizedRepresentativeMapper {

    public BranchAuthorizedRepresentativeDTO toDto(BranchAuthorizedRepresentativeEntity entity) {

        if (entity == null) return null;

        BranchAuthorizedRepresentativeDTO dto = new BranchAuthorizedRepresentativeDTO();

        dto.setId(entity.getId());
        dto.setBranchCode(entity.getBranchCode());
        dto.setBranchName(entity.getBranchName());
        dto.setRepresentativeName(entity.getRepresentativeName());
        dto.setRepresentativeTitle(entity.getRepresentativeTitle());
        dto.setAuthorizationDocNo(entity.getAuthorizationDocNo());
        dto.setAuthorizationDocDate(entity.getAuthorizationDocDate());
        dto.setAuthorizationIssuer(entity.getAuthorizationIssuer());
        dto.setEffectiveFrom(entity.getEffectiveFrom());
        dto.setEffectiveTo(entity.getEffectiveTo());
        dto.setIsActive(entity.getIsActive());

        if (entity.getGuaranteeLetterEntity() != null) {
            List<Long> guaranteeIds = entity.getGuaranteeLetterEntity()
                    .stream()
                    .map(GuaranteeLetterEntity::getId)
                    .collect(Collectors.toList());

            dto.setGuaranteeLetterIds(guaranteeIds);
        } else {
            dto.setGuaranteeLetterIds(Collections.emptyList());
        }

        return dto;
    }

    public BranchAuthorizedRepresentativeEntity toEntity(BranchAuthorizedRepresentativeDTO dto) {

        if (dto == null) return null;

        BranchAuthorizedRepresentativeEntity entity = new BranchAuthorizedRepresentativeEntity();

        entity.setId(dto.getId());
        entity.setBranchCode(dto.getBranchCode());
        entity.setBranchName(dto.getBranchName());
        entity.setRepresentativeName(dto.getRepresentativeName());
        entity.setRepresentativeTitle(dto.getRepresentativeTitle());
        entity.setAuthorizationDocNo(dto.getAuthorizationDocNo());
        entity.setAuthorizationDocDate(dto.getAuthorizationDocDate());
        entity.setAuthorizationIssuer(dto.getAuthorizationIssuer());
        entity.setEffectiveFrom(dto.getEffectiveFrom());
        entity.setEffectiveTo(dto.getEffectiveTo());
        entity.setIsActive(dto.getIsActive());

        return entity;
    }
}
