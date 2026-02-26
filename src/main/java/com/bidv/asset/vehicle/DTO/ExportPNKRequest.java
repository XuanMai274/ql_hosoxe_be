package com.bidv.asset.vehicle.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExportPNKRequest {
    private String importNumber;
    private String code;
    private List<Long> vehicleIds;
}
