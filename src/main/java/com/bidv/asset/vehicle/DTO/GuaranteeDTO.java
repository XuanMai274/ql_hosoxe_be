package com.bidv.asset.vehicle.DTO;
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
public class GuaranteeDTO {
    private Long id;
    private Long vehicleId;

    private String guaranteeNumber;
    private LocalDate guaranteeDate;
    private BigDecimal amount;

    private BigDecimal remainingGuaranteeBalance;
    private String ref;

    private String letterNumber;
    private LocalDate letterDate;
    private LocalDate paymentDate;

    private LocalDateTime createdAt;

}
