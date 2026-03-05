package com.bidv.asset.vehicle.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FullProcessResponse {
    private WarehouseImportDTO warehouseImport;
    private DisbursementDTO disbursement;
    private List<LoanDTO> loans;
}
