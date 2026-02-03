package com.bidv.asset.vehicle.DTO;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class OcrDataDTO {
    private String invoice_number;
    private List<OcrVehicleDTO> vehicle_list;
}
