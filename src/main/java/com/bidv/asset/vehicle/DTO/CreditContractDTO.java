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
@Data
@Builder
public class CreditContractDTO {

    private Long id;

    private String contractNumber;
    private LocalDate contractDate;

    private BigDecimal creditLimit;
    private BigDecimal usedLimit;// sử dụng thực tế
    private BigDecimal remainingLimit;// còn lại thực tế
    private BigDecimal guaranteeBalance;// bảo lãnh thực tế
    private BigDecimal issuedGuaranteeBalance; // bảo lãnh phát hành
    private BigDecimal outstandingGuaranteeAmount;
    private BigDecimal vehicleLoanBalance;
    private BigDecimal realEstateLoanBalance;

    private List<MortgageContractDTO> mortgageContractIds;
    private List<Long> guaranteeIds;
    private List<Long> loanIds;
    private String status;
    private Long customerId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
