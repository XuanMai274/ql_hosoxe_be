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
public class MortgageContractDTO {

    private Long id;

    private String contractNumber;
    private LocalDate contractDate;

    private BigDecimal totalCollateralValue;
    private BigDecimal remainingCollateralValue;
    private String status;

    // ===== RELATION =====
    private Long customerId;
    private Long manufacturerId;

    private List<Long> creditContractIds;
    private List<Long> guaranteeLetterIds;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
