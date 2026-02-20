package com.bidv.asset.vehicle.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class XuatDeXuatBaoLanh {

    // GHTD đã sử dụng
    private BigDecimal totalGuaranteeAmount;

    // Dư nợ vay tiền còn lại
    private BigDecimal usedAmount;

    // Số dư cấp bảo lãnh
    private BigDecimal guaranteeBalance;

    // Số dư vay ngắn hạn khác
    private BigDecimal shortTermLoanBalance;

    // GHTD còn được sử dụng
    private BigDecimal remainingAmount;
}