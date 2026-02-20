package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.VehicleDossierDTO;
import com.bidv.asset.vehicle.entity.VehicleDossierEntity;
import com.bidv.asset.vehicle.entity.VehicleEntity;
import org.springframework.stereotype.Component;

@Component
public class VehicleDossierMapper {

    public VehicleDossierDTO toDto(VehicleDossierEntity entity) {
        if (entity == null) return null;

        VehicleDossierDTO dto = new VehicleDossierDTO();
        dto.setId(entity.getId());
        dto.setVehicleId(entity.getVehicle() != null ? entity.getVehicle().getId() : null);
        dto.setStatus(entity.getStatus());
        dto.setImportDate(entity.getImportDate());
        dto.setExportDate(entity.getExportDate());
        dto.setOriginalCopy(entity.getOriginalCopy());
        dto.setImportDocs(entity.getImportDocs());
        dto.setRegistrationOrderNumber(entity.getRegistrationOrderNumber());
        dto.setDocsDeliveryDate(entity.getDocsDeliveryDate());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }

    public VehicleDossierEntity toEntity(VehicleDossierDTO dto) {
        if (dto == null) return null;

        VehicleDossierEntity entity = new VehicleDossierEntity();
        entity.setId(dto.getId());
        entity.setStatus(dto.getStatus());
        entity.setImportDate(dto.getImportDate());
        entity.setExportDate(dto.getExportDate());
        entity.setOriginalCopy(dto.getOriginalCopy());
        entity.setImportDocs(dto.getImportDocs());
        entity.setRegistrationOrderNumber(dto.getRegistrationOrderNumber());
        entity.setDocsDeliveryDate(dto.getDocsDeliveryDate());
        entity.setCreatedAt(dto.getCreatedAt());

        if (dto.getVehicleId() != null) {
            VehicleEntity vehicle = new VehicleEntity();
            vehicle.setId(dto.getVehicleId());
            entity.setVehicle(vehicle);
        }

        return entity;
    }
}
