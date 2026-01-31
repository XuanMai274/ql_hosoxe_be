package com.bidv.asset.vehicle.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDTO {
    private Long id;
    private Integer stt;
    private String vehicleName;
    private String status;
    private String fundingSource;

    private LocalDate importDate;
    private LocalDate exportDate;

    private String assetName;
    private String chassisNumber;
    private String engineNumber;
    private String modelType;
    private String color;
    private Integer seats;
    private BigDecimal price;
    private String originalCopy;
    private String importDocs;
    private String registrationOrderNumber;
    private LocalDate docsDeliveryDate;
    private String description;
    private LocalDateTime createdAt;
    private InvoiceDTO invoiceId;
    private List<Long> dossierIds;
    private List<Long> documentIds;
    private GuaranteeLetterDTO guaranteeLetterDTO;
}
