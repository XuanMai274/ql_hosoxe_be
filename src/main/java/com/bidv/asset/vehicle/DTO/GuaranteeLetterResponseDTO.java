package com.bidv.asset.vehicle.DTO;

import com.bidv.asset.vehicle.entity.*;
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
public class GuaranteeLetterResponseDTO {

    private Long id;

    // ===== FULL RELATION OBJECT =====
    private CreditContractEntity creditContract;
    private MortgageContractEntity mortgageContract;
    private ManufacturerEntity manufacturer;
    private BranchAuthorizedRepresentativeEntity branchAuthorizedRepresentative;
    private CustomerEntity customer;

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

    // ===== VEHICLE COUNT =====
    private Integer expectedVehicleCount;
    private Integer importedVehicleCount;
    private Integer exportedVehicleCount;

    // ===== SALE CONTRACT =====
    private String saleContract;
    private BigDecimal saleContractAmount;

    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ===== CHILD =====
    private List<VehicleEntity> vehicles;
    private GuaranteeLetterFileDTO file;
}
