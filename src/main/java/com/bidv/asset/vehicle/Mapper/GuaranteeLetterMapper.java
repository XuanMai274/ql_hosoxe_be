package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.GuaranteeLetterDTO;
import com.bidv.asset.vehicle.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GuaranteeLetterMapper {

    private final CreditContractMapper creditContractMapper;
    private final ManufacturerMapper manufacturerMapper;
    private final BranchAuthorizedRepresentativeMapper authorizedRepresentativeMapper;

    /* ===================== ENTITY → DTO ===================== */
    public GuaranteeLetterDTO toDto(GuaranteeLetterEntity entity) {
        if (entity == null) return null;

        GuaranteeLetterDTO dto = new GuaranteeLetterDTO();

        dto.setId(entity.getId());

        // ===== GUARANTEE CONTRACT =====
        dto.setGuaranteeContractNumber(entity.getGuaranteeContractNumber());
        dto.setGuaranteeContractDate(entity.getGuaranteeContractDate());
        dto.setGuaranteeNoticeNumber(entity.getGuaranteeNoticeNumber());
        dto.setGuaranteeNoticeDate(entity.getGuaranteeNoticeDate());
        dto.setReferenceCode(entity.getReferenceCode());

        // ===== AMOUNT =====
        dto.setExpectedGuaranteeAmount(entity.getExpectedGuaranteeAmount());
        dto.setTotalGuaranteeAmount(entity.getTotalGuaranteeAmount());
        dto.setUsedAmount(entity.getUsedAmount());
        dto.setRemainingAmount(entity.getRemainingAmount());

        // ===== VEHICLE COUNT =====
        dto.setExpectedVehicleCount(entity.getExpectedVehicleCount());
        dto.setImportedVehicleCount(entity.getImportedVehicleCount());
        dto.setExportedVehicleCount(entity.getExportedVehicleCount());

        // ===== SALE CONTRACT =====
        dto.setSaleContract(entity.getSaleContract());
        dto.setSaleContractAmount(entity.getSaleContractAmount());

        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // ===== RELATION =====
        dto.setCreditContractDTO(
                creditContractMapper.toDto(entity.getCreditContract())
        );

        dto.setManufacturerDTO(
                manufacturerMapper.toDto(entity.getManufacturer())
        );

        dto.setBranchAuthorizedRepresentativeDTO(
                authorizedRepresentativeMapper.toDto(entity.getAuthorizedRepresentative())
        );

        // ===== VEHICLES (ID ONLY) =====
        if (entity.getVehicles() != null) {
            dto.setVehicles(
                    entity.getVehicles()
                            .stream()
                            .map(VehicleEntity::getId)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    /* ===================== DTO → ENTITY ===================== */
    public GuaranteeLetterEntity toEntity(GuaranteeLetterDTO dto) {
        if (dto == null) return null;

        GuaranteeLetterEntity entity = new GuaranteeLetterEntity();

        entity.setId(dto.getId());

        // ===== GUARANTEE CONTRACT =====
        entity.setGuaranteeContractNumber(dto.getGuaranteeContractNumber());
        entity.setGuaranteeContractDate(dto.getGuaranteeContractDate());
        entity.setGuaranteeNoticeNumber(dto.getGuaranteeNoticeNumber());
        entity.setGuaranteeNoticeDate(dto.getGuaranteeNoticeDate());
        entity.setReferenceCode(dto.getReferenceCode());

        // ===== AMOUNT =====
        entity.setExpectedGuaranteeAmount(dto.getExpectedGuaranteeAmount());
        entity.setTotalGuaranteeAmount(dto.getTotalGuaranteeAmount());
        entity.setUsedAmount(dto.getUsedAmount());
        entity.setRemainingAmount(dto.getRemainingAmount());

        // ===== VEHICLE COUNT =====
        entity.setExpectedVehicleCount(dto.getExpectedVehicleCount());
        entity.setImportedVehicleCount(dto.getImportedVehicleCount());
        entity.setExportedVehicleCount(dto.getExportedVehicleCount());

        // ===== SALE CONTRACT =====
        entity.setSaleContract(dto.getSaleContract());
        entity.setSaleContractAmount(dto.getSaleContractAmount());

        entity.setStatus(dto.getStatus());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        // ===== RELATION: SET ID ONLY =====
        if (dto.getCreditContractDTO() != null) {
            CreditContractEntity creditContract = new CreditContractEntity();
            creditContract.setId(dto.getCreditContractDTO().getId());
            entity.setCreditContract(creditContract);
        }

        if (dto.getManufacturerDTO() != null) {
            ManufacturerEntity manufacturer = new ManufacturerEntity();
            manufacturer.setId(dto.getManufacturerDTO().getId());
            entity.setManufacturer(manufacturer);
        }

        if (dto.getBranchAuthorizedRepresentativeDTO() != null) {
            BranchAuthorizedRepresentativeEntity rep =
                    new BranchAuthorizedRepresentativeEntity();
            rep.setId(dto.getBranchAuthorizedRepresentativeDTO().getId());
            entity.setAuthorizedRepresentative(rep);
        }

        // KHÔNG map vehicles ở mapper
        // → Service sẽ chịu trách nhiệm attach VehicleEntity vào GuaranteeLetter

        return entity;
    }
}
