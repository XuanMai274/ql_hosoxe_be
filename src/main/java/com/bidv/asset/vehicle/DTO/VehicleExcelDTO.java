package com.bidv.asset.vehicle.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleExcelDTO {

    /* ====== STT ====== */
    private Integer stt;

    /* ====== THÔNG TIN XE ====== */
    private String vehicleName;
    private String assetName;
    private String status;
    private String fundingSource;

    private String chassisNumber;
    private String engineNumber;
    private String modelType;
    private String color;
    private Integer seats;
    private BigDecimal price;

    /* ====== NGÀY THÁNG ====== */
    private LocalDate importDate;
    private LocalDate exportDate;
    private LocalDate docsDeliveryDate;

    /* ====== HỒ SƠ XE ====== */
    private String originalCopy;
    private String importDocs;
    private String registrationOrderNumber;
    private String description;

    /* ====== HÓA ĐƠN ====== */
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private String sellerName;
    private String sellerTaxCode;
    private String buyerName;
    private String buyerTaxCode;
    private BigDecimal invoiceTotalAmount;
    private BigDecimal vatAmount;

    /* ====== THƯ BẢO LÃNH ====== */
    private String guaranteeContractNumber;
    private LocalDate guaranteeContractDate;
    private String guaranteeNoticeNumber;
    private LocalDate guaranteeNoticeDate;
    private String referenceCode;

    private BigDecimal expectedGuaranteeAmount;
    private BigDecimal totalGuaranteeAmount;
    private BigDecimal usedAmount;
    private BigDecimal remainingAmount;

    /* ====== SỐ LƯỢNG XE ====== */
    private Integer expectedVehicleCount;
    private Integer importedVehicleCount;
    private Integer exportedVehicleCount;

    /* ====== HỢP ĐỒNG MUA BÁN ====== */
    private String saleContract;
    private BigDecimal saleContractAmount;

    /* ====== ĐẠI DIỆN & NSX ====== */
    private String authorizedRepresentativeName;
    private String manufacturerName;

    /* ====== THỜI GIAN ====== */
    private LocalDateTime vehicleCreatedAt;
    private LocalDateTime guaranteeCreatedAt;
}