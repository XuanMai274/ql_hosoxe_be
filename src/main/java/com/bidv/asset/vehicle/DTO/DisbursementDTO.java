package com.bidv.asset.vehicle.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisbursementDTO {

    private Long id;

    // ===== Thông tin tổng thể giải ngân =====
    private String loanContractNumber;
    private BigDecimal creditLimit;
    private BigDecimal usedLimit;
    private BigDecimal remainingLimit;
    private BigDecimal issuedGuaranteeBalance;
    private BigDecimal vehicleLoanBalance;
    private BigDecimal realEstateLoanBalance;
    private BigDecimal totalCollateralValue;
    private BigDecimal realEstateValue;
    private BigDecimal collateralValueAfterFactor;
    private BigDecimal realEstateValueAfterFactor;
    private LocalDate disbursementDate;
    private Integer loanTerm; // thời hạn vay (số ngày)
    private LocalDate startDate; // ngày bắt đầu vay
    private LocalDate dueDate; // ngày hết hạn vay
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // ===== Giải ngân =====
    private BigDecimal disbursementAmount;
    // ===== Quan hệ =====
    private Long creditContractId;
    private String creditContractNumber;
    private CreditContractDTO creditContractDTO;
    private Integer vehicleCount;

    private Long mortgageContractId;
    private MortgageContractDTO mortgageContractDTO;

    private List<Long> loanIds;
    private List<LoanDTO> loans;
}
