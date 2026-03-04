package com.bidv.asset.vehicle.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FullProcessNKGNRequest {
    private WarehouseImportRequestDTO warehouseRequest;
    private DisbursementDTO disbursementRequest;
}
