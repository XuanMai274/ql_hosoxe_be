package com.bidv.asset.vehicle.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {
    private Boolean success;
    private List<InvoiceData> data;
    private String message;
}
