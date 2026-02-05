package com.bidv.asset.vehicle.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInfo {
    private String vehicleDescription;
    private String chassisNumber;
    private String engineNumber;
    private String color;
    private String numberOfSeats;
    private String quantity = "1";
}
