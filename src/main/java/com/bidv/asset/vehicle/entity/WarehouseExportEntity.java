package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "warehouse_export",
    indexes = {
        @Index(name = "idx_wh_export_number", columnList = "export_number")
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class WarehouseExportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "export_number", unique = true)
    private String exportNumber;

    @Column(name = "export_date")
    private LocalDateTime exportDate;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "status")
    private String status; // PENDING, APPROVED, REJECTED

    @Column(name = "total_debt_collection", precision = 18, scale = 2)
    private BigDecimal totalDebtCollection;
    @Column(name = "total_collateral_value", precision = 18, scale = 2)
    private BigDecimal totalCollateralValue; // Tổng TSHTTTL

    @Column(name = "real_estate_value", precision = 18, scale = 2)
    private BigDecimal realEstateValue; // BDS
    @Column(name = "vehicle_count")
    private Integer vehicleCount;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "warehouseExport", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<VehicleEntity> vehicles;

    private LocalDateTime createdAt;
    private String createdBy;
    public void addVehicle(VehicleEntity vehicle) {
        vehicles.add(vehicle);
        vehicle.setWarehouseExport(this);
    }

    public void setVehicles(List<VehicleEntity> vehicles) {
        this.vehicles = vehicles;
        if (vehicles != null) {
            vehicles.forEach(v -> v.setWarehouseExport(this));
        }
    }
}
