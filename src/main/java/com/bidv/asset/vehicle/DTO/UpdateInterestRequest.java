package com.bidv.asset.vehicle.DTO;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateInterestRequest {
    private Long disbursementId;
    private BigDecimal interestAmount;
}
