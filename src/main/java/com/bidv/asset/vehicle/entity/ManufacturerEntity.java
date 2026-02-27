package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "manufacturer")
@Getter @Setter
@NoArgsConstructor
public class ManufacturerEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "manufacturer_id_seq"
    )
    @SequenceGenerator(
            name = "manufacturer_id_seq",
            sequenceName = "manufacturer_id_seq"
    )
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name="logo")
    private String logo;
    @Column (name="description")
    private String description;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column()
    private BigDecimal guaranteeRate; // 0.75 / 0.85
    @Column()
    private String templateCode; // VINFAST_V1 / HYUNDAI_V1
    @OneToMany(mappedBy = "manufacturer", fetch = FetchType.LAZY)
    private List<GuaranteeApplicationEntity> guaranteeApplications;
    @OneToMany(mappedBy = "manufacturer", fetch = FetchType.LAZY)
    private List<GuaranteeLetterEntity> guaranteeLetterEntities;
    @OneToMany(
            mappedBy = "manufacturer",
            fetch = FetchType.LAZY
    )
    private List<WarehouseImportEntity> warehouseImports;
    // HDBD
    @OneToMany(mappedBy = "manufacturer",fetch = FetchType.LAZY)
    private  List<MortgageContractEntity> mortgageContractEntities;
    // liên kết với xe
    @OneToMany(mappedBy="manufacturerEntity",fetch = FetchType.LAZY)
    private List<VehicleEntity> vehicleEntities;
}
