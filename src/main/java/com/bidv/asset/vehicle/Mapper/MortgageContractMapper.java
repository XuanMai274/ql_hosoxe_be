package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.ManufacturerDTO;
import com.bidv.asset.vehicle.DTO.MortgageContractDTO;
import com.bidv.asset.vehicle.entity.*;

import java.util.List;
import java.util.stream.Collectors;

public class MortgageContractMapper {

    // =====================================================
    // ENTITY → DTO
    // =====================================================
    public static MortgageContractDTO toDTO(MortgageContractEntity entity) {

        if (entity == null) return null;

        MortgageContractDTO dto = new MortgageContractDTO();

        dto.setId(entity.getId());
        dto.setContractNumber(entity.getContractNumber());
        dto.setContractDate(entity.getContractDate());
        dto.setTotalCollateralValue(entity.getTotalCollateralValue());
        dto.setRemainingCollateralValue(entity.getRemainingCollateralValue());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setSecurityRegistrationNumber(entity.getSecurityRegistrationNumber());
        dto.setPersonalIdNumber(entity.getPersonalIdNumber());
        // ===== CUSTOMER =====
        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getId());
        }

        // ===== MANUFACTURER =====
        if (entity.getManufacturer() != null) {
            ManufacturerDTO manufacturerDTO=new ManufacturerDTO();
            manufacturerDTO.setId(entity.getManufacturer().getId());
            manufacturerDTO.setTemplateCode(entity.getTemplateCode());
            dto.setManufacturerDTO(manufacturerDTO);
        }

        // ===== CREDIT CONTRACT IDS =====
        if (entity.getCreditContracts() != null) {
            dto.setCreditContractIds(
                    entity.getCreditContracts()
                            .stream()
                            .map(CreditContractEntity::getId)
                            .collect(Collectors.toList())
            );
        }

        // ===== GUARANTEE LETTER IDS =====
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


    // =====================================================
    // DTO → ENTITY (CREATE)
    // =====================================================
    public static MortgageContractEntity toEntity(
            MortgageContractDTO dto,
            CustomerEntity customer,
            ManufacturerEntity manufacturer,
            List<CreditContractEntity> creditContracts
    ) {

        if (dto == null) return null;

        MortgageContractEntity entity = new MortgageContractEntity();

        entity.setId(dto.getId());
        entity.setContractNumber(dto.getContractNumber());
        entity.setContractDate(dto.getContractDate());
        entity.setStatus(dto.getStatus());
        entity.setTotalCollateralValue(dto.getTotalCollateralValue());
        entity.setRemainingCollateralValue(dto.getRemainingCollateralValue());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setSecurityRegistrationNumber(dto.getSecurityRegistrationNumber());
        entity.setPersonalIdNumber(dto.getPersonalIdNumber());
        entity.setCustomer(customer);
        entity.setManufacturer(manufacturer);
        entity.setCreditContracts(creditContracts);
//        entity.setGuaranteeLetters(guaranteeLetters);

        return entity;
    }


    // =====================================================
    // UPDATE ENTITY FROM DTO
    // =====================================================
    public static void updateEntity(
            MortgageContractEntity entity,
            MortgageContractDTO dto,
            CustomerEntity customer,
            ManufacturerEntity manufacturer,
            List<CreditContractEntity> creditContracts,
            List<GuaranteeLetterEntity> guaranteeLetters
    ) {

        if (entity == null || dto == null) return;

        entity.setContractNumber(dto.getContractNumber());
        entity.setContractDate(dto.getContractDate());
        entity.setStatus(dto.getStatus());
        entity.setTotalCollateralValue(dto.getTotalCollateralValue());
        entity.setRemainingCollateralValue(dto.getRemainingCollateralValue());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setSecurityRegistrationNumber(dto.getSecurityRegistrationNumber());
        entity.setPersonalIdNumber(dto.getPersonalIdNumber());
        entity.setCustomer(customer);
        entity.setManufacturer(manufacturer);

        if (creditContracts != null) {
            entity.setCreditContracts(creditContracts);
        }

        if (guaranteeLetters != null) {
            entity.setGuaranteeLetters(guaranteeLetters);
        }
    }
}
