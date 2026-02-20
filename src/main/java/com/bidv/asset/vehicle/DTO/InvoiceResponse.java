package com.bidv.asset.vehicle.DTO;

import lombok.Data;
import java.util.List;

@Data
public class InvoiceResponse {
    private String invoiceNumber;
    private String totalAmount;
    private String day;
    private String month;
    private String year;
    private List<VehicleInfo> vehicleList;
}
