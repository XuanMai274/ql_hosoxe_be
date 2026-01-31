package com.bidv.asset.vehicle.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleListDTO {
    private Long id;
    private Integer stt;
    private String vehicleName;
    private String status;
    private String chassisNumber;
    private String engineNumber;
    private BigDecimal price;
    private String guaranteeContractNumber;
}
