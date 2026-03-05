package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "warehouse_import",
        indexes = {
                @Index(name = "idx_wh_import_number", columnList = "import_number")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseImportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Số nhập kho (02.01/2025/10987477/HDBD)
    @Column(name = "import_number", nullable = false, unique = true)
    private String importNumber;

    // Loại xe nhập kho
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private ManufacturerEntity manufacturer;

    // HĐBD gốc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mortgage_contract_id", nullable = false)
    private MortgageContractEntity mortgageContract;

    @OneToMany(mappedBy = "warehouseImport", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<VehicleEntity> vehicles;
    @Column(name="total_collateral_value")
    private BigDecimal totalCollateralValue; // Tổng TSHTTTL
    @Column(name="total_outstanding_balance")
    private BigDecimal totalOutstandingBalance; //tổng dư nợ
    private LocalDateTime createdAt;
}
