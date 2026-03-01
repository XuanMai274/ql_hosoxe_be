package com.bidv.asset.vehicle.DTO;

import jakarta.persistence.Column;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class WarehouseExportDTO {
    private Long id;
    private String exportNumber;
    private LocalDateTime exportDate;
    private LocalDateTime requestDate;
    private String status;
    private BigDecimal totalDebtCollection;
    private Integer vehicleCount;
    private String description;
    private List<Long> vehicleIds;
    private List<VehicleDTO> vehicles;
    private LocalDateTime createdAt;
    private String createdBy;
    private BigDecimal totalCollateralValue; // Tổng TSHTTTL
    private BigDecimal realEstateValue; // BDS
}
