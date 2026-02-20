package com.bidv.asset.vehicle.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreditContractDTO {

    private Long id;

    private String contractNumber;
    private LocalDate contractDate;

    private BigDecimal creditLimit;
    private BigDecimal usedLimit;
    private BigDecimal remainingLimit;
    private BigDecimal guaranteeBalance;
    private BigDecimal vehicleLoanBalance;
    private BigDecimal realEstateLoanBalance;

    private List<Long> mortgageContractIds;
    private List<Long> guaranteeIds;
    private List<Long> loanIds;
    private String status;
    private Long customerId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
