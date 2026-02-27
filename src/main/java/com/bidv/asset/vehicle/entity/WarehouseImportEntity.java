package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

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

    // Danh sách xe nhập
    @ManyToMany
    @JoinTable(
            name = "warehouse_import_vehicle",
            joinColumns = @JoinColumn(name = "warehouse_import_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_id")
    )
    private List<VehicleEntity> vehicles;

    private LocalDateTime createdAt;
}
