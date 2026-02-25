package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.CreditContractDTO;
import com.bidv.asset.vehicle.DTO.MortgageContractDTO;
import com.bidv.asset.vehicle.entity.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CreditContractMapper {

    public CreditContractDTO toDto(CreditContractEntity entity) {
        if (entity == null) return null;

        CreditContractDTO dto = new CreditContractDTO();

        dto.setId(entity.getId());
        dto.setContractNumber(entity.getContractNumber());
        dto.setContractDate(entity.getContractDate());

        dto.setCreditLimit(entity.getCreditLimit());
        dto.setUsedLimit(entity.getUsedLimit());
        dto.setRemainingLimit(entity.getRemainingLimit());
        dto.setGuaranteeBalance(entity.getGuaranteeBalance());
        dto.setVehicleLoanBalance(entity.getVehicleLoanBalance());
        dto.setRealEstateLoanBalance(entity.getRealEstateLoanBalance());
        dto.setStatus(entity.getStatus());
        dto.setIssuedGuaranteeBalance(entity.getIssuedGuaranteeBalance());
        dto.setOutstandingGuaranteeAmount(entity.getOutstandingGuaranteeAmount());
        if (entity.getMortgageContracts() != null) {

            List<MortgageContractDTO> mortgageDtos =
                    entity.getMortgageContracts()
                            .stream()
                            .map(this::mapMortgageToDto)
                            .collect(Collectors.toList());

            dto.setMortgageContractIds(mortgageDtos);

        } else {
            dto.setMortgageContractIds(Collections.emptyList());
        }

        if (entity.getGuarantees() != null) {
            dto.setGuaranteeIds(
                    entity.getGuarantees()
                            .stream()
                            .map(GuaranteeLetterEntity::getId)
                            .collect(Collectors.toList())
            );
        } else {
            dto.setGuaranteeIds(Collections.emptyList());
        }

        if (entity.getLoans() != null) {
            dto.setLoanIds(
                    entity.getLoans()
                            .stream()
                            .map(LoanEntity::getId)
                            .collect(Collectors.toList())
            );
        } else {
            dto.setLoanIds(Collections.emptyList());
        }

        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getId());
        }

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    public CreditContractEntity toEntity(CreditContractDTO dto) {
        if (dto == null) return null;

        CreditContractEntity entity = new CreditContractEntity();

        entity.setId(dto.getId());
        entity.setContractNumber(dto.getContractNumber());
        entity.setContractDate(dto.getContractDate());
        entity.setStatus(dto.getStatus());
        entity.setCreditLimit(dto.getCreditLimit());
        entity.setUsedLimit(dto.getUsedLimit());
        entity.setRemainingLimit(dto.getRemainingLimit());
        entity.setGuaranteeBalance(dto.getGuaranteeBalance());
        entity.setVehicleLoanBalance(dto.getVehicleLoanBalance());
        entity.setRealEstateLoanBalance(dto.getRealEstateLoanBalance());
        if (dto.getMortgageContractIds() != null) {

            List<MortgageContractEntity> mortgageEntities =
                    dto.getMortgageContractIds()
                            .stream()
                            .map(this::mapMortgageToEntity)
                            .collect(Collectors.toList());

            entity.setMortgageContracts(mortgageEntities);
        }

//        if (dto.getCustomerId() != null) {
//            CustomerEntity customer = new CustomerEntity();
//            customer.setId(dto.getCustomerId());
//            entity.setCustomer(customer);
//        }

        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }
    /* ===================================================== */
    /* ============== MORTGAGE MAPPING ===================== */
    /* ===================================================== */

    private MortgageContractDTO mapMortgageToDto(MortgageContractEntity entity) {

        if (entity == null) return null;

        MortgageContractDTO dto = new MortgageContractDTO();
        dto.setId(entity.getId());
        dto.setContractNumber(entity.getContractNumber());
        dto.setContractDate(entity.getContractDate());

        return dto;
    }

    private MortgageContractEntity mapMortgageToEntity(MortgageContractDTO dto) {

        if (dto == null) return null;

        MortgageContractEntity entity = new MortgageContractEntity();
        entity.setId(dto.getId());

        // Nếu chỉ cần set id để attach quan hệ thì vậy là đủ
        return entity;
    }
}

