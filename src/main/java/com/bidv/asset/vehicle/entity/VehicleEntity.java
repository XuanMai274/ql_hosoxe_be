package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vehicles",
    indexes = {
        @Index(name = "idx_vehicle_chassis", columnList = "chassis_number"),
        @Index(name = "idx_vehicle_engine", columnList = "engine_number"),
        @Index(name = "idx_vehicle_guarantee", columnList = "guarantee_letter_id"),
        @Index(name = "idx_vehicle_invoice", columnList = "invoice_id"),
        @Index(name = "idx_vehicle_status", columnList = "status"),
            //composite index
            @Index(name = "idx_vehicle_gl_status", columnList = "guarantee_letter_id, status")
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class VehicleEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_id_seq")
    @SequenceGenerator(name = "vehicle_id_seq", sequenceName = "vehicle_id_seq")
    private Long id;

    private Integer stt;
    private String vehicleName;
    private String status;
    private String fundingSource;
    private LocalDate importDate;
    private LocalDate exportDate;
    private String assetName;
    @Column(unique = true, nullable = false)
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
    // danh sách bộ hồ sơ nhập kho
    @Column(name = "import_dossier")
    private String importDossier;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guarantee_letter_id", nullable = false)
    private GuaranteeLetterEntity guaranteeLetter;
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    private List<VehicleDossierEntity> dossiers;
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    private List<DocumentEntity> documents;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private InvoiceEntity invoice;
    @OneToMany(mappedBy = "vehicle")
    private List<LoanEntity> loans;
}