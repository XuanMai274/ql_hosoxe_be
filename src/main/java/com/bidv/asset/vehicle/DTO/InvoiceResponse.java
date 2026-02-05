package com.bidv.asset.vehicle.DTO;

import lombok.Data;
import java.util.List;

@Data
public class InvoiceResponse {
    private String invoiceNumber;
    private List<VehicleInfo> vehicleList;
}
