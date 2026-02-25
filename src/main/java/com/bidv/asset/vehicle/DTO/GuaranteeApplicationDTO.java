package com.bidv.asset.vehicle.DTO;

import com.bidv.asset.vehicle.entity.ManufacturerEntity;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuaranteeApplicationDTO {

    private Long id;

    private String applicationNumber;
    private String subGuaranteeContractNumber;

    private Integer guaranteeTermDays;
    private LocalDate expiryDate;

    private Integer totalVehicleCount;
    private BigDecimal totalVehicleAmount;
    private BigDecimal totalGuaranteeAmount;

    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;

    private ManufacturerDTO manufacturerDTO;
    private CreditContractDTO creditContractDTO;
    private MortgageContractDTO mortgageContractDTO;
    private CustomerDTO customerDTO;

    private List<GuaranteeApplicationVehicleDTO> vehicles;
}