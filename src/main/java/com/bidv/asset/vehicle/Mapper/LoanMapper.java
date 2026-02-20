package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.*;
import com.bidv.asset.vehicle.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoanMapper {

    private final CreditContractMapper creditContractMapper;
    private final LoanFileMapper loanFileMapper;

    /* ================= ENTITY → DTO ================= */
    public LoanDTO toDto(LoanEntity entity) {

        if (entity == null) return null;

        LoanDTO dto = new LoanDTO();

        dto.setId(entity.getId());

        dto.setAccountNumber(entity.getAccountNumber());
        dto.setLoanContractNumber(entity.getLoanContractNumber());
        dto.setLoanTerm(entity.getLoanTerm());
        dto.setLoanDate(entity.getLoanDate());
        dto.setDueDate(entity.getDueDate());
        dto.setLoanAmount(entity.getLoanAmount());
        dto.setDocId(entity.getDocId());

        dto.setLastPaymentDate(entity.getLastPaymentDate());
        dto.setTotalPaidAmount(entity.getTotalPaidAmount());

        dto.setCollateralAndPurpose(entity.getCollateralAndPurpose());
        dto.setWithdrawnChassisNumber(entity.getWithdrawnChassisNumber());

        dto.setLoanStatus(entity.getLoanStatus());
        dto.setLoanType(entity.getLoanType());

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        /* ===== RELATION ===== */

        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getId());
        }

        if (entity.getVehicle() != null) {
            dto.setVehicleId(entity.getVehicle().getId());
        }

        if (entity.getGuaranteeLetter() != null) {
            dto.setGuaranteeLetterId(entity.getGuaranteeLetter().getId());
        }

        dto.setCreditContractDTO(
                creditContractMapper.toDto(entity.getCreditContract())
        );

        /* ===== FILES ===== */

        if (entity.getFiles() != null) {
            dto.setFiles(
                    entity.getFiles()
                            .stream()
                            .map(loanFileMapper::toDto)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    /* ================= DTO → ENTITY ================= */
    public LoanEntity toEntity(LoanDTO dto) {

        if (dto == null) return null;

        LoanEntity entity = new LoanEntity();

        entity.setId(dto.getId());

        entity.setAccountNumber(dto.getAccountNumber());
        entity.setLoanContractNumber(dto.getLoanContractNumber());
        entity.setLoanTerm(dto.getLoanTerm());
        entity.setLoanDate(dto.getLoanDate());
        entity.setDueDate(dto.getDueDate());
        entity.setLoanAmount(dto.getLoanAmount());
        entity.setDocId(dto.getDocId());

        entity.setLastPaymentDate(dto.getLastPaymentDate());
        entity.setTotalPaidAmount(dto.getTotalPaidAmount());

        entity.setCollateralAndPurpose(dto.getCollateralAndPurpose());
        entity.setWithdrawnChassisNumber(dto.getWithdrawnChassisNumber());

        entity.setLoanStatus(dto.getLoanStatus());
        entity.setLoanType(dto.getLoanType());

        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        /* ===== RELATION ===== */

        if (dto.getCustomerId() != null) {
            CustomerEntity customer = new CustomerEntity();
            customer.setId(dto.getCustomerId());
            entity.setCustomer(customer);
        }

        if (dto.getVehicleId() != null) {
            VehicleEntity vehicle = new VehicleEntity();
            vehicle.setId(dto.getVehicleId());
            entity.setVehicle(vehicle);
        }

        if (dto.getGuaranteeLetterId() != null) {
            GuaranteeLetterEntity g = new GuaranteeLetterEntity();
            g.setId(dto.getGuaranteeLetterId());
            entity.setGuaranteeLetter(g);
        }

        if (dto.getCreditContractDTO() != null) {
            CreditContractEntity credit = new CreditContractEntity();
            credit.setId(dto.getCreditContractDTO().getId());
            entity.setCreditContract(credit);
        }

        /*
         * FILE mapping KHÔNG làm ở mapper
         * → xử lý ở Service
         */

        return entity;
    }
}
