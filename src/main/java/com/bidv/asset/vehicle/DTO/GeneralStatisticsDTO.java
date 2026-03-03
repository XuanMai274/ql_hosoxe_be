package com.bidv.asset.vehicle.DTO;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralStatisticsDTO {
    // 1. Xe (Vehicle)
    private long totalVehicles;
    private long inWarehouseCount;
    private long returnedToCustomerCount;

    // 2. Bảo lãnh (Guarantee)
    private BigDecimal totalIssuedGuaranteeBalance; // Dư bảo lãnh phát hành (issued_guarantee_balance)
    private long activeGuaranteeLetterCount; // Số lượng thư bảo lãnh còn hiệu lực
    private BigDecimal actualGuaranteeBalance; // Dư bảo lãnh thực tế (guarantee_balance)

    // 3. Khoản vay (Loan)
    private long activeDisbursementCount; // Số lượng hợp đồng giải ngân ACTIVE
    private BigDecimal totalVehicleLoanBalance; // Dư nợ vay xe (vehicle_loan_balance)
    private BigDecimal totalRealEstateLoanBalance; // Dư vay BĐS (real_estate_loan_balance)

    private List<ManufacturerStats> manufacturerStats;
    private List<MonthlyStats> monthlyStats;
    private List<CustomerStats> topCustomers;

    @Data
    @AllArgsConstructor
    public static class ManufacturerStats {
        private String name;
        private long count;
    }

    @Data
    @AllArgsConstructor
    public static class MonthlyStats {
        private String month;
        private long count;
    }

    @Data
    @AllArgsConstructor
    public static class CustomerStats {
        private String name;
        private long count;
    }
}
