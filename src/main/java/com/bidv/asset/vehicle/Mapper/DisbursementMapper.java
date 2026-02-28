package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.DisbursementDTO;
import com.bidv.asset.vehicle.entity.CreditContractEntity;
import com.bidv.asset.vehicle.entity.DisbursementEntity;
import com.bidv.asset.vehicle.entity.MortgageContractEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DisbursementMapper {

    private final CreditContractMapper creditContractMapper;
    private final MortgageContractMapper mortgageContractMapper;
    /* ================= ENTITY → DTO ================= */
    public DisbursementDTO toDto(DisbursementEntity entity) {
        if (entity == null) return null;

        DisbursementDTO dto = new DisbursementDTO();

        dto.setId(entity.getId());
        dto.setLoanContractNumber(entity.getLoanContractNumber());
        dto.setUsedLimit(entity.getUsedLimit());
        dto.setRemainingLimit(entity.getRemainingLimit());
        dto.setIssuedGuaranteeBalance(entity.getIssuedGuaranteeBalance());
        dto.setVehicleLoanBalance(entity.getVehicleLoanBalance());
        dto.setRealEstateLoanBalance(entity.getRealEstateLoanBalance());
        dto.setTotalCollateralValue(entity.getTotalCollateralValue());
        dto.setRealEstateValue(entity.getRealEstateValue());
        dto.setCollateralValueAfterFactor(entity.getCollateralValueAfterFactor());
        dto.setRealEstateValueAfterFactor(entity.getRealEstateValueAfterFactor());
        dto.setDisbursementDate(entity.getDisbursementDate());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreditLimit(entity.getCreditLimit());
        dto.setDisbursementAmount(entity.getDisbursementAmount());
        dto.setLoanTerm(entity.getLoanTerm());
        dto.setStartDate(entity.getStartDate());
        dto.setDueDate(entity.getDueDate());
        /* ===== RELATION ===== */
        if (entity.getCreditContract() != null) {
            dto.setCreditContractId(entity.getCreditContract().getId());
            dto.setCreditContractDTO(creditContractMapper.toDto(entity.getCreditContract()));
        }

        if (entity.getMortgageContract() != null) {
            dto.setMortgageContractId(entity.getMortgageContract().getId());
            dto.setMortgageContractDTO(mortgageContractMapper.toDTO(entity.getMortgageContract()));
        }

        if (entity.getLoans() != null) {
            dto.setLoanIds(
                    entity.getLoans()
                            .stream()
                            .map(loan -> loan.getId())
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    /* ================= DTO → ENTITY ================= */
    public DisbursementEntity toEntity(DisbursementDTO dto) {
        if (dto == null) return null;

        DisbursementEntity entity = new DisbursementEntity();

        entity.setId(dto.getId());
        entity.setLoanContractNumber(dto.getLoanContractNumber());
        entity.setUsedLimit(dto.getUsedLimit());
        entity.setRemainingLimit(dto.getRemainingLimit());
        entity.setIssuedGuaranteeBalance(dto.getIssuedGuaranteeBalance());
        entity.setVehicleLoanBalance(dto.getVehicleLoanBalance());
        entity.setRealEstateLoanBalance(dto.getRealEstateLoanBalance());
        entity.setDisbursementAmount(dto.getDisbursementAmount());
        entity.setCreditLimit(dto.getCreditLimit());
        entity.setLoanTerm(dto.getLoanTerm());
        entity.setStartDate(dto.getStartDate());
        entity.setDueDate(dto.getDueDate());
        // Logic tính toán hệ số
        entity.setTotalCollateralValue(dto.getTotalCollateralValue());
        entity.setRealEstateValue(dto.getRealEstateValue());

        if (dto.getTotalCollateralValue() != null) {
            entity.setCollateralValueAfterFactor(dto.getTotalCollateralValue().multiply(new java.math.BigDecimal("0.85")));
        }
        if (dto.getRealEstateValue() != null) {
            entity.setRealEstateValueAfterFactor(dto.getRealEstateValue().multiply(new java.math.BigDecimal("0.8")));
        }

        entity.setDisbursementDate(dto.getDisbursementDate());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        /* ===== RELATION ===== */
        if (dto.getCreditContractId() != null) {
            CreditContractEntity creditContract = new CreditContractEntity();
            creditContract.setId(dto.getCreditContractId());
            entity.setCreditContract(creditContract);
        } else if (dto.getCreditContractDTO() != null) {
            CreditContractEntity creditContract = new CreditContractEntity();
            creditContract.setId(dto.getCreditContractDTO().getId());
            entity.setCreditContract(creditContract);
        }

        if (dto.getMortgageContractId() != null) {
            MortgageContractEntity mortgageContract = new MortgageContractEntity();
            mortgageContract.setId(dto.getMortgageContractId());
            entity.setMortgageContract(mortgageContract);
        } else if (dto.getMortgageContractDTO() != null) {
            MortgageContractEntity mortgageContract = new MortgageContractEntity();
            mortgageContract.setId(dto.getMortgageContractDTO().getId());
            entity.setMortgageContract(mortgageContract);
        }

        return entity;
    }
}
