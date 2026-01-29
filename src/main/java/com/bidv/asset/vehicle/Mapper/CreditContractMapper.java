package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.CreditContractDTO;
import com.bidv.asset.vehicle.entity.CreditContractEntity;
import com.bidv.asset.vehicle.entity.GuaranteeLetterEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CreditContractMapper {

    /* ===================== ENTITY -> DTO ===================== */
    public CreditContractDTO toDto(CreditContractEntity entity) {
        if (entity == null) return null;

        CreditContractDTO dto = new CreditContractDTO();
        dto.setId(entity.getId());
        dto.setContractNumber(entity.getContractNumber());
        dto.setContractDate(entity.getContractDate());
        dto.setCreatedAt(entity.getCreatedAt());

        // Map OneToMany -> List<Long>
        if (entity.getGuaranteeLetters() != null) {
            List<Long> guaranteeIds = entity.getGuaranteeLetters()
                    .stream()
                    .map(GuaranteeLetterEntity::getId)
                    .collect(Collectors.toList());
            dto.setGuaranteeLetterIds(guaranteeIds);
        } else {
            dto.setGuaranteeLetterIds(Collections.emptyList());
        }

        return dto;
    }

    /* ===================== DTO -> ENTITY ===================== */
    public CreditContractEntity toEntity(CreditContractDTO dto) {
        if (dto == null) return null;

        CreditContractEntity entity = new CreditContractEntity();
        entity.setId(dto.getId());
        entity.setContractNumber(dto.getContractNumber());
        entity.setContractDate(dto.getContractDate());
        entity.setCreatedAt(dto.getCreatedAt());

        /*
         * Không map guaranteeLetters tại đây
         * Quan hệ phải xử lý ở Service:
         *  - load GuaranteeLetterEntity theo id
         *  - set creditContract cho từng GuaranteeLetter
         */

        return entity;
    }
}
