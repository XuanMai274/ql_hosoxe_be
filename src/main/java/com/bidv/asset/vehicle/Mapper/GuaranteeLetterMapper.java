package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.*;
import com.bidv.asset.vehicle.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class GuaranteeLetterMapper {
    @Autowired
    GuaranteeApplicationMapper guaranteeApplicationMapper;

    // =====================================================
    // ENTITY → DTO
    // =====================================================
    public GuaranteeLetterDTO toDto(GuaranteeLetterEntity entity) {

        if (entity == null) {
            return null;
        }

        GuaranteeLetterDTO dto = new GuaranteeLetterDTO();

        dto.setId(entity.getId());
        dto.setDisbursement(entity.getDisbursement());
        dto.setVehicleWarehouseCount(entity.getVehicleWarehouseCount());
        dto.setExpiryDate(entity.getExpiryDate());
        dto.setGuaranteeTermDays(entity.getGuaranteeTermDays());
        // ===== RELATION =====
        if (entity.getCreditContract() != null) {

            CreditContractEntity credit = entity.getCreditContract();

            CreditContractDTO cc = new CreditContractDTO();
            cc.setId(credit.getId());
            cc.setContractNumber(credit.getContractNumber());
            cc.setContractDate(credit.getContractDate());

            cc.setCreditLimit(credit.getCreditLimit());
            cc.setUsedLimit(credit.getUsedLimit());
            cc.setRemainingLimit(credit.getRemainingLimit());
            cc.setIssuedGuaranteeBalance(credit.getIssuedGuaranteeBalance());
            cc.setOutstandingGuaranteeAmount(credit.getOutstandingGuaranteeAmount());
            cc.setGuaranteeBalance(credit.getGuaranteeBalance());
            cc.setVehicleLoanBalance(credit.getVehicleLoanBalance());
            cc.setRealEstateLoanBalance(credit.getRealEstateLoanBalance());

            cc.setStatus(credit.getStatus());
            cc.setCreatedAt(credit.getCreatedAt());
            cc.setUpdatedAt(credit.getUpdatedAt());

            dto.setCreditContractDTO(cc);
        }
        if (entity.getMortgageContract() != null) {
            MortgageContractDTO mc = new MortgageContractDTO();
            mc.setId(entity.getMortgageContract().getId());
            mc.setContractNumber(entity.getMortgageContract().getContractNumber());
            mc.setContractDate(entity.getMortgageContract().getContractDate());
            mc.setSecurityRegistrationNumber(entity.getMortgageContract().getSecurityRegistrationNumber());
            mc.setPersonalIdNumber(entity.getMortgageContract().getPersonalIdNumber());
            dto.setMortgageContractDTO(mc);
        }

        if (entity.getManufacturer() != null) {
            ManufacturerDTO manu = new ManufacturerDTO();
            manu.setId(entity.getManufacturer().getId());
            manu.setName(entity.getManufacturer().getName());
            manu.setCode(entity.getManufacturer().getCode());
            manu.setTemplateCode(entity.getManufacturer().getTemplateCode());
            dto.setManufacturerDTO(manu);
        }

        if (entity.getAuthorizedRepresentative() != null) {
            BranchAuthorizedRepresentativeDTO repDTO = new BranchAuthorizedRepresentativeDTO();
            repDTO.setId(entity.getAuthorizedRepresentative().getId());
            repDTO.setRepresentativeName(entity.getAuthorizedRepresentative().getRepresentativeName());
            repDTO.setBranchName(entity.getAuthorizedRepresentative().getBranchName());
            repDTO.setRepresentativeTitle(entity.getAuthorizedRepresentative().getRepresentativeTitle());
            repDTO.setAuthorizationDocNo(entity.getAuthorizedRepresentative().getAuthorizationDocNo());
            repDTO.setAuthorizationDocDate(entity.getAuthorizedRepresentative().getAuthorizationDocDate());
            repDTO.setAuthorizationIssuer(entity.getAuthorizedRepresentative().getAuthorizationIssuer());
            dto.setBranchAuthorizedRepresentativeDTO(repDTO);
        }

        if (entity.getCustomer() != null) {
            CustomerDTO cus = new CustomerDTO();
            cus.setId(entity.getCustomer().getId());
            dto.setCustomerDTO(cus);
        }
        if (entity.getGuaranteeApplication() != null) {
            dto.setGuaranteeApplicationDTO(guaranteeApplicationMapper.toDTO(entity.getGuaranteeApplication()));

        }
        // ===== GUARANTEE CONTRACT =====
        dto.setGuaranteeContractNumber(entity.getGuaranteeContractNumber());
        dto.setGuaranteeContractDate(entity.getGuaranteeContractDate());
        dto.setGuaranteeNoticeNumber(entity.getGuaranteeNoticeNumber());
        dto.setGuaranteeNoticeDate(entity.getGuaranteeNoticeDate());
        dto.setReferenceCode(entity.getReferenceCode());

        // ===== GUARANTEE AMOUNT =====
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

        // ===== THỜI HẠN BẢO LÃNH =====
        dto.setExpiryDate(entity.getExpiryDate());

        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // ===== VEHICLE IDS =====
        if (entity.getVehicles() != null) {
            dto.setVehicleIds(
                    entity.getVehicles()
                            .stream()
                            .map(VehicleEntity::getId)
                            .collect(Collectors.toList()));
        } else {
            dto.setVehicleIds(Collections.emptyList());
        }

        // ===== FILE =====
        if (entity.getFile() != null) {
            GuaranteeLetterFileDTO fileDTO = new GuaranteeLetterFileDTO();
            fileDTO.setId(entity.getFile().getId());
            fileDTO.setFileName(entity.getFile().getFileName());
            fileDTO.setFileHash(entity.getFile().getFileHash());
            fileDTO.setFilePath(entity.getFile().getFilePath());
            fileDTO.setFileType(entity.getFile().getFileType());
            fileDTO.setIsActive(entity.getFile().getIsActive());
            dto.setFileId(fileDTO);
        }

        return dto;
    }

    // =====================================================
    // DTO → ENTITY (CREATE)
    // =====================================================
    public GuaranteeLetterEntity toEntity(GuaranteeLetterDTO dto) {

        if (dto == null) {
            return null;
        }

        GuaranteeLetterEntity entity = new GuaranteeLetterEntity();

        // ===== BASIC FIELD =====
        entity.setGuaranteeContractNumber(dto.getGuaranteeContractNumber());
        entity.setGuaranteeContractDate(dto.getGuaranteeContractDate());
        entity.setGuaranteeNoticeNumber(dto.getGuaranteeNoticeNumber());
        entity.setGuaranteeNoticeDate(dto.getGuaranteeNoticeDate());
        entity.setReferenceCode(dto.getReferenceCode());
        entity.setExpectedGuaranteeAmount(dto.getExpectedGuaranteeAmount());
        entity.setTotalGuaranteeAmount(dto.getTotalGuaranteeAmount());
        entity.setUsedAmount(dto.getUsedAmount());
        entity.setRemainingAmount(dto.getRemainingAmount());
        entity.setGuaranteeTermDays(dto.getGuaranteeTermDays());
        entity.setExpectedVehicleCount(dto.getExpectedVehicleCount());
        entity.setImportedVehicleCount(dto.getImportedVehicleCount());
        entity.setExportedVehicleCount(dto.getExportedVehicleCount());

        entity.setSaleContract(dto.getSaleContract());
        entity.setSaleContractAmount(dto.getSaleContractAmount());

        entity.setExpiryDate(dto.getExpiryDate());

        entity.setStatus(dto.getStatus());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        if (dto.getGuaranteeApplicationDTO() != null && dto.getGuaranteeApplicationDTO().getId() != null) {
            GuaranteeApplicationEntity ga = new GuaranteeApplicationEntity();
            ga.setId(dto.getGuaranteeApplicationDTO().getId());
            entity.setGuaranteeApplication(ga);
        }

        return entity;
    }

    // =====================================================
    // UPDATE ENTITY
    // =====================================================
    public void updateEntity(GuaranteeLetterEntity entity, GuaranteeLetterDTO dto) {

        if (entity == null || dto == null) {
            return;
        }

        entity.setGuaranteeContractNumber(dto.getGuaranteeContractNumber());
        entity.setGuaranteeContractDate(dto.getGuaranteeContractDate());
        entity.setGuaranteeNoticeNumber(dto.getGuaranteeNoticeNumber());
        entity.setGuaranteeNoticeDate(dto.getGuaranteeNoticeDate());
        entity.setReferenceCode(dto.getReferenceCode());

        entity.setExpectedGuaranteeAmount(dto.getExpectedGuaranteeAmount());
        entity.setTotalGuaranteeAmount(dto.getTotalGuaranteeAmount());
        entity.setUsedAmount(dto.getUsedAmount());
        entity.setRemainingAmount(dto.getRemainingAmount());

        entity.setExpectedVehicleCount(dto.getExpectedVehicleCount());
        entity.setImportedVehicleCount(dto.getImportedVehicleCount());
        entity.setExportedVehicleCount(dto.getExportedVehicleCount());

        entity.setSaleContract(dto.getSaleContract());
        entity.setSaleContractAmount(dto.getSaleContractAmount());

        entity.setExpiryDate(dto.getExpiryDate());

        entity.setStatus(dto.getStatus());
        entity.setUpdatedAt(dto.getUpdatedAt());
    }

    // mapper một số trường cần thiết
    public GuaranteeLetterDTO toLiteDto(GuaranteeLetterEntity entity) {
        if (entity == null)
            return null;

        GuaranteeLetterDTO dto = new GuaranteeLetterDTO();
        dto.setId(entity.getId());
        dto.setGuaranteeContractNumber(entity.getGuaranteeContractNumber());

        if (entity.getCreditContract() != null) {
            CreditContractDTO cc = new CreditContractDTO();
            cc.setContractNumber(entity.getCreditContract().getContractNumber());
            cc.setContractDate(entity.getCreditContract().getContractDate());
            dto.setCreditContractDTO(cc);
        }

        return dto;
    }
}
