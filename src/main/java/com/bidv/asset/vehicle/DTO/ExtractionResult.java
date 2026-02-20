package com.bidv.asset.vehicle.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtractionResult {
    private boolean success;
    private Object data;
    private String message;
}
