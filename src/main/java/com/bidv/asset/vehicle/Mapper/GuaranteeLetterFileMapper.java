package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.GuaranteeLetterFileDTO;
import com.bidv.asset.vehicle.entity.GuaranteeLetterEntity;
import com.bidv.asset.vehicle.entity.GuaranteeLetterFileEntity;
import org.springframework.stereotype.Component;

@Component
public class GuaranteeLetterFileMapper {

    /* ================= ENTITY → DTO ================= */
    public GuaranteeLetterFileDTO toDto(GuaranteeLetterFileEntity entity) {

        if (entity == null) return null;

        GuaranteeLetterFileDTO dto = new GuaranteeLetterFileDTO();

        dto.setId(entity.getId());

        // FK
        if (entity.getGuaranteeLetter() != null) {
            dto.setGuaranteeLetterId(entity.getGuaranteeLetter().getId());
        }

        dto.setFileName(entity.getFileName());
        dto.setFilePath(entity.getFilePath());
        dto.setFileType(entity.getFileType());
        dto.setFileSize(entity.getFileSize());
        dto.setFileHash(entity.getFileHash());
        dto.setVersion(entity.getVersion());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }


    /* ================= DTO → ENTITY ================= */
    public GuaranteeLetterFileEntity toEntity(GuaranteeLetterFileDTO dto) {

        if (dto == null) return null;

        GuaranteeLetterFileEntity entity = new GuaranteeLetterFileEntity();

        entity.setId(dto.getId());

        // FK chỉ set ID (không fetch DB)
        if (dto.getGuaranteeLetterId() != null) {
            GuaranteeLetterEntity guaranteeLetterEntity=new GuaranteeLetterEntity();
            guaranteeLetterEntity.setId(dto.getGuaranteeLetterId());
            entity.setGuaranteeLetter(
                    guaranteeLetterEntity
            );

        }

        entity.setFileName(dto.getFileName());
        entity.setFilePath(dto.getFilePath());
        entity.setFileType(dto.getFileType());
        entity.setFileSize(dto.getFileSize());
        entity.setFileHash(dto.getFileHash());
        entity.setVersion(dto.getVersion());
        entity.setIsActive(dto.getIsActive());
        entity.setCreatedAt(dto.getCreatedAt());

        return entity;
    }
}
