package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.LoanFileDTO;
import com.bidv.asset.vehicle.entity.LoanFileEntity;
import org.springframework.stereotype.Component;

@Component
public class LoanFileMapper {

    public LoanFileDTO toDto(LoanFileEntity entity) {

        if (entity == null) return null;

        LoanFileDTO dto = new LoanFileDTO();

        dto.setId(entity.getId());
        dto.setFileName(entity.getFileName());
        dto.setFilePath(entity.getFilePath());
        dto.setFileType(entity.getFileType());
        dto.setFileSize(entity.getFileSize());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }
}
