package com.bidv.asset.vehicle.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisbursementExportRequest {
    private DisbursementDTO disbursementDTO;
    private List<Long> vehicleIds;
}
