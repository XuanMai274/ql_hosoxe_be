package com.bidv.asset.vehicle.DTO;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuaranteeApplicationVehicleDTO {

    private Long id;

    private String vehicleName;
    private String vehicleType;
    private String color;
    private String chassisNumber;
    private String invoiceNumber;
    private String paymentMethod;
    private String bankName;

    private BigDecimal vehiclePrice;
    private BigDecimal guaranteeAmount;
}
