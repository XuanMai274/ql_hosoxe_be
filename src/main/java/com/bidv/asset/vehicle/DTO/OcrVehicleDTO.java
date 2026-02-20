package com.bidv.asset.vehicle.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OcrVehicleDTO {
    private String chassis_number;
    private String engine_number;
    private String vehicle_description;
    private String color;
    private String number_of_seats;
    private String quantity;
}

