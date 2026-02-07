package com.bidv.asset.vehicle.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceData {
    private String invoiceNumber;
    private String totalAmount;
    private String day;
    private String month;
    private String year;
    private List<VehicleInfo> vehicleList;
}
