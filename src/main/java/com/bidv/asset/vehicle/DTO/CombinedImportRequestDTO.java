package com.bidv.asset.vehicle.DTO;

import lombok.Data;
import java.util.List;

@Data
public class CombinedImportRequestDTO {
    private WarehouseImportRequestDTO warehouseRequest;
    private DisbursementDTO disbursementRequest;
    private List<LoanDTO> loanRequests;
}
