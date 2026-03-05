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
    private String ref;
    private boolean hasDocument;

    public VehicleListDTO(Long id, Integer stt, String vehicleName, String status, String chassisNumber,
            String engineNumber, BigDecimal price, String ref, Long docCount) {
        this.id = id;
        this.stt = stt;
        this.vehicleName = vehicleName;
        this.status = status;
        this.chassisNumber = chassisNumber;
        this.engineNumber = engineNumber;
        this.price = price;
        this.ref = ref;
        this.hasDocument = docCount != null && docCount > 0;
    }
}
