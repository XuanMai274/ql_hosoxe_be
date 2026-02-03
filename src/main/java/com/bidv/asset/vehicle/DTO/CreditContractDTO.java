package com.bidv.asset.vehicle.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreditContractDTO {

    private Long id;

    // ===== SỐ HĐTD =====
    private String contractNumber;

    // ===== NGÀY KÝ =====
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate contractDate;

    // ===== GHTD =====
    private BigDecimal creditLimit;       // Tổng GHTD
    private BigDecimal usedLimit;         // GHTD đã sử dụng
    private BigDecimal remainingLimit;    // GHTD còn được sử dụng

    // ===== AUDIT =====
    private LocalDateTime createdAt;

    // ===== LIÊN KẾT =====
    private List<Long> guaranteeLetterIds;
}
