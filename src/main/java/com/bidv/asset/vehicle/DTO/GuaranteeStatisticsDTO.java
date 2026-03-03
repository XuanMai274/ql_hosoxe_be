package com.bidv.asset.vehicle.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuaranteeStatisticsDTO {
    private long totalVehicles;
    private long inWarehouseCount;
    private long disbursedCount;
}
