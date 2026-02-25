package com.bidv.asset.vehicle.DTO;

import com.bidv.asset.vehicle.entity.BranchAuthorizedRepresentativeEntity;
import jakarta.persistence.Column;
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
public class GuaranteeLetterDTO {

    private Long id;

    // ===== RELATION =====
    private CreditContractDTO creditContractDTO;
    private MortgageContractDTO mortgageContractDTO;
    private ManufacturerDTO manufacturerDTO;
    private BranchAuthorizedRepresentativeDTO branchAuthorizedRepresentativeDTO;
    private CustomerDTO customerDTO;

    // ===== GUARANTEE CONTRACT =====
    private String guaranteeContractNumber;
    private LocalDate guaranteeContractDate;
    private String guaranteeNoticeNumber;
    private LocalDate guaranteeNoticeDate;
    private String referenceCode;

    // ===== GUARANTEE AMOUNT =====
    private BigDecimal expectedGuaranteeAmount;
    private BigDecimal totalGuaranteeAmount;
    private BigDecimal usedAmount;
    private BigDecimal remainingAmount;
    // số tiền đã giải ngân
    private BigDecimal disbursement;
    // ===== VEHICLE COUNT =====
    private Integer expectedVehicleCount;
    private Integer importedVehicleCount;
    private Integer exportedVehicleCount;
    // số xe đã nhập kho
    private Integer vehicleWarehouseCount;
    // ===== SALE CONTRACT =====
    private String saleContract;
    private BigDecimal saleContractAmount;

    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // thời gian hết hạn của bảo lãnh
    private LocalDate expiryDate;
    // ===== CHILD =====
    private List<Long> vehicleIds;
    private GuaranteeLetterFileDTO fileId;
    private GuaranteeApplicationDTO guaranteeApplicationDTO;
}
