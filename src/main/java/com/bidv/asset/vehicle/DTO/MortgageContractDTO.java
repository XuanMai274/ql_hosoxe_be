package com.bidv.asset.vehicle.DTO;

import jakarta.persistence.Column;
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
    private String securityRegistrationNumber; // số đơn đăng kí giao dịch đảm bảo
    private String personalIdNumber; // mã cá nhân
    private BigDecimal totalCollateralValue;
    private BigDecimal remainingCollateralValue;
    private String status;
    private ManufacturerDTO manufacturerDTO;
    // ===== RELATION =====
    private Long customerId;
    private CustomerDTO customerDTO;

    private List<Long> creditContractIds;
    private List<Long> guaranteeLetterIds;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
