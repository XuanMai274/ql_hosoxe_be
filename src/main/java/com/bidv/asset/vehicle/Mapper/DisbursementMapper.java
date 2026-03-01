package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.DisbursementDTO;
import com.bidv.asset.vehicle.DTO.LoanDTO;
import com.bidv.asset.vehicle.entity.DisbursementEntity;
import com.bidv.asset.vehicle.entity.LoanEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DisbursementMapper {

    @Autowired
    @Lazy
    LoanMapper loanMapper;

    @Autowired
    CreditContractMapper creditContractMapper;

    @Autowired
    MortgageContractMapper mortgageContractMapper;

    public DisbursementDTO toDtoSimple(DisbursementEntity entity) {
        if (entity == null)
            return null;

        DisbursementDTO dto = new DisbursementDTO();
        dto.setId(entity.getId());
        dto.setLoanContractNumber(entity.getLoanContractNumber());
        dto.setCreditLimit(entity.getCreditLimit());
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
        dto.setLoanTerm(entity.getLoanTerm());
        dto.setStartDate(entity.getStartDate());
        dto.setDueDate(entity.getDueDate());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setDisbursementAmount(entity.getDisbursementAmount());
        dto.setInterestAmount(entity.getInterestAmount());
        dto.setTotalAmountPaid(entity.getTotalAmountPaid());
        dto.setTotalVehiclesCount(entity.getTotalVehiclesCount());
        dto.setWithdrawnVehiclesCount(entity.getWithdrawnVehiclesCount());
        dto.setStatus(entity.getStatus());

        if (entity.getCreditContract() != null) {
            dto.setCreditContractId(entity.getCreditContract().getId());
            dto.setCreditContractNumber(entity.getCreditContract().getContractNumber());
        }

        if (entity.getLoans() != null) {
            dto.setVehicleCount(entity.getLoans().size());
        }

        return dto;
    }

    public DisbursementDTO toDto(DisbursementEntity entity) {
        if (entity == null)
            return null;

        DisbursementDTO dto = new DisbursementDTO();
        dto.setId(entity.getId());
        dto.setLoanContractNumber(entity.getLoanContractNumber());
        dto.setCreditLimit(entity.getCreditLimit());
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
        dto.setLoanTerm(entity.getLoanTerm());
        dto.setStartDate(entity.getStartDate());
        dto.setDueDate(entity.getDueDate());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setDisbursementAmount(entity.getDisbursementAmount());
        dto.setInterestAmount(entity.getInterestAmount());
        dto.setTotalAmountPaid(entity.getTotalAmountPaid());
        dto.setTotalVehiclesCount(entity.getTotalVehiclesCount());
        dto.setWithdrawnVehiclesCount(entity.getWithdrawnVehiclesCount());
        dto.setStatus(entity.getStatus());

        // Relationships
        if (entity.getCreditContract() != null) {
            dto.setCreditContractId(entity.getCreditContract().getId());
            dto.setCreditContractNumber(entity.getCreditContract().getContractNumber());
            dto.setCreditContractDTO(creditContractMapper.toDto(entity.getCreditContract()));
        }

        if (entity.getLoans() != null) {
            dto.setVehicleCount(entity.getLoans().size());
        } else {
            dto.setVehicleCount(0);
        }

        if (entity.getMortgageContract() != null) {
            dto.setMortgageContractId(entity.getMortgageContract().getId());
            dto.setMortgageContractDTO(mortgageContractMapper.toDTO(entity.getMortgageContract()));
        }

        // Children
        if (entity.getLoans() != null) {
            List<Long> loanIds = entity.getLoans().stream()
                    .map(LoanEntity::getId)
                    .collect(Collectors.toList());
            dto.setLoanIds(loanIds);

            List<LoanDTO> loanDTOs = entity.getLoans().stream()
                    .map(loanMapper::toDto)
                    .collect(Collectors.toList());
            dto.setLoans(loanDTOs);
        }

        return dto;
    }

    public DisbursementEntity toEntity(DisbursementDTO dto) {
        if (dto == null)
            return null;

        DisbursementEntity entity = new DisbursementEntity();
        entity.setId(dto.getId());
        entity.setLoanContractNumber(dto.getLoanContractNumber());
        entity.setUsedLimit(dto.getUsedLimit());
        entity.setCreditLimit(dto.getCreditLimit());
        entity.setRemainingLimit(dto.getRemainingLimit());
        entity.setIssuedGuaranteeBalance(dto.getIssuedGuaranteeBalance());
        entity.setVehicleLoanBalance(dto.getVehicleLoanBalance());
        entity.setRealEstateLoanBalance(dto.getRealEstateLoanBalance());
        entity.setTotalCollateralValue(dto.getTotalCollateralValue());
        entity.setRealEstateValue(dto.getRealEstateValue());
        entity.setCollateralValueAfterFactor(dto.getCollateralValueAfterFactor());
        entity.setRealEstateValueAfterFactor(dto.getRealEstateValueAfterFactor());
        entity.setDisbursementDate(dto.getDisbursementDate());
        entity.setLoanTerm(dto.getLoanTerm());
        entity.setStartDate(dto.getStartDate());
        entity.setDueDate(dto.getDueDate());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setDisbursementAmount(dto.getDisbursementAmount());
        entity.setInterestAmount(dto.getInterestAmount());
        entity.setTotalAmountPaid(dto.getTotalAmountPaid());
        entity.setTotalVehiclesCount(dto.getTotalVehiclesCount());
        entity.setWithdrawnVehiclesCount(dto.getWithdrawnVehiclesCount());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}
