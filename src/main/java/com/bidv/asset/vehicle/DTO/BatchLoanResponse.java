package com.bidv.asset.vehicle.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class BatchLoanResponse {
    private int total;
    private BigDecimal totalAmount;
    private List<LoanDTO> loans;
}
