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
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private LocalDate invoiceDate;

    private String sellerName;
    private String sellerTaxCode;
    private String buyerName;
    private String buyerTaxCode;

    private BigDecimal totalAmount;
    private BigDecimal vatAmount;
    private LocalDateTime createdAt;

    private List<Long> vehicleIds;

}
