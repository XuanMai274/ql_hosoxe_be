package com.bidv.asset.vehicle.DTO;

import com.bidv.asset.vehicle.enums.LoanStatus;
import com.bidv.asset.vehicle.enums.LoanType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {

    private Long id;

    // ===== Loan Info =====
    private String accountNumber;
    private String loanContractNumber;
    private Integer loanTerm;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private BigDecimal loanAmount;
    private String docId;

    // ===== Payment =====
    private LocalDate lastPaymentDate;
    private BigDecimal totalPaidAmount;

    // ===== Business =====
    private String collateralAndPurpose;
    private String withdrawnChassisNumber;

    private LoanStatus loanStatus;
    private LoanType loanType;

    // ===== Relation =====
    private Long customerId;
    private Long vehicleId;
    private Long guaranteeLetterId;
    private CustomerDTO customerDTO;
    private VehicleDTO vehicleDTO;
    private GuaranteeLetterDTO guaranteeLetterDTO;

    private CreditContractDTO creditContractDTO;

    // ===== Files =====
    private List<LoanFileDTO> files;

    // ===== Audit =====
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
