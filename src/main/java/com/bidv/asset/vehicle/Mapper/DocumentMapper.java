package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.DocumentDTO;
import com.bidv.asset.vehicle.entity.DocumentEntity;
import com.bidv.asset.vehicle.entity.VehicleEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

    public DocumentDTO toDto(DocumentEntity entity) {
        if (entity == null) return null;

        DocumentDTO dto = new DocumentDTO();
        dto.setId(entity.getId());
        dto.setFileName(entity.getFileName());
        dto.setFileType(entity.getFileType());
        dto.setFilePath(entity.getFilePath());
        dto.setUploadDate(entity.getUploadDate());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setVehicleId(entity.getVehicle() != null ? entity.getVehicle().getId() : null);

        return dto;
    }

    public DocumentEntity toEntity(DocumentDTO dto) {
        if (dto == null) return null;

        DocumentEntity entity = new DocumentEntity();
        entity.setId(dto.getId());
        entity.setFileName(dto.getFileName());
        entity.setFileType(dto.getFileType());
        entity.setFilePath(dto.getFilePath());
        entity.setUploadDate(dto.getUploadDate());
        entity.setCreatedAt(dto.getCreatedAt());

        if (dto.getVehicleId() != null) {
            VehicleEntity vehicle = new VehicleEntity();
            vehicle.setId(dto.getVehicleId());
            entity.setVehicle(vehicle);
        }

        return entity;
    }
}
