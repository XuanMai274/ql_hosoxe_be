package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.DisbursementDTO;
import com.bidv.asset.vehicle.DTO.LoanDTO;
import com.bidv.asset.vehicle.entity.DisbursementEntity;
import com.bidv.asset.vehicle.entity.LoanEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DisbursementMapper {

    @Autowired
    LoanMapper loanMapper;

    @Autowired
    CreditContractMapper creditContractMapper;

    @Autowired
    MortgageContractMapper mortgageContractMapper;

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

        // Relationships
        if (entity.getCreditContract() != null) {
            dto.setCreditContractId(entity.getCreditContract().getId());
            dto.setCreditContractDTO(creditContractMapper.toDto(entity.getCreditContract()));
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
}
