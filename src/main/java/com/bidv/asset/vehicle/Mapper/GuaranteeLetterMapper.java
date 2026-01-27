package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.GuaranteeLetterDTO;
import com.bidv.asset.vehicle.entity.CreditContractEntity;
import com.bidv.asset.vehicle.entity.GuaranteeLetterEntity;
import org.springframework.stereotype.Component;

@Component
public class GuaranteeLetterMapper {

    public GuaranteeLetterDTO toDto(GuaranteeLetterEntity entity) {
        if (entity == null) return null;

        GuaranteeLetterDTO dto = new GuaranteeLetterDTO();
        dto.setId(entity.getId());
        dto.setCreditContractId(
                entity.getCreditContract() != null ? entity.getCreditContract().getId() : null
        );
        dto.setGuaranteeContractNumber(entity.getGuaranteeContractNumber());
        dto.setGuaranteeContractDate(entity.getGuaranteeContractDate());
        dto.setGuaranteeNoticeNumber(entity.getGuaranteeNoticeNumber());
        dto.setGuaranteeNoticeDate(entity.getGuaranteeNoticeDate());
        dto.setReferenceCode(entity.getReferenceCode());
        dto.setTotalGuaranteeAmount(entity.getTotalGuaranteeAmount());
        dto.setUsedAmount(entity.getUsedAmount());
        dto.setRemainingAmount(entity.getRemainingAmount());
        dto.setExpectedVehicleCount(entity.getExpectedVehicleCount());
        dto.setImportedVehicleCount(entity.getImportedVehicleCount());
        dto.setExportedVehicleCount(entity.getExportedVehicleCount());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setExpectedGuaranteeAmount(entity.getExpectedGuaranteeAmount());
        dto.setSaleContract(entity.getSaleContract());
        dto.setSaleContractAmount(entity.getSaleContractAmount());
        return dto;
    }

    public GuaranteeLetterEntity toEntity(GuaranteeLetterDTO dto) {
        if (dto == null) return null;

        GuaranteeLetterEntity entity = new GuaranteeLetterEntity();
        entity.setId(dto.getId());
        entity.setGuaranteeContractNumber(dto.getGuaranteeContractNumber());
        entity.setGuaranteeContractDate(dto.getGuaranteeContractDate());
        entity.setGuaranteeNoticeNumber(dto.getGuaranteeNoticeNumber());
        entity.setGuaranteeNoticeDate(dto.getGuaranteeNoticeDate());
        entity.setReferenceCode(dto.getReferenceCode());
        entity.setTotalGuaranteeAmount(dto.getTotalGuaranteeAmount());
        entity.setUsedAmount(dto.getUsedAmount());
        entity.setRemainingAmount(dto.getRemainingAmount());
        entity.setExpectedVehicleCount(dto.getExpectedVehicleCount());
        entity.setImportedVehicleCount(dto.getImportedVehicleCount());
        entity.setExportedVehicleCount(dto.getExportedVehicleCount());
        entity.setStatus(dto.getStatus());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setExpectedGuaranteeAmount(dto.getExpectedGuaranteeAmount());
        entity.setSaleContract(dto.getSaleContract());
        entity.setSaleContractAmount(dto.getSaleContractAmount());
        if (dto.getCreditContractId() != null) {
            CreditContractEntity cc = new CreditContractEntity();
            cc.setId(dto.getCreditContractId());
            entity.setCreditContract(cc);
        }

        return entity;
    }
}