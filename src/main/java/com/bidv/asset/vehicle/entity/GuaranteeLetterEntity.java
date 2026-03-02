package com.bidv.asset.vehicle.entity;

import com.bidv.asset.vehicle.DTO.BranchAuthorizedRepresentativeDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "guarantee_letter",
        indexes = {
                @Index(name = "idx_gl_contract_number", columnList = "guarantee_contract_number"),
                @Index(name = "idx_gl_notice_number", columnList = "guarantee_notice_number"),
                @Index(name = "idx_gl_customer", columnList = "customer_id"),
                @Index(name = "idx_gl_credit_contract", columnList = "credit_contract_id"),
                @Index(name = "idx_gl_manufacturer", columnList = "manufacturer_id"),
                @Index(name = "idx_gl_status", columnList = "status")
        }
)
@AllArgsConstructor
@NoArgsConstructor
public class GuaranteeLetterEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guarantee_letter_id_seq")
    @SequenceGenerator(
            name = "guarantee_letter_id_seq",
            sequenceName = "guarantee_letter_id_seq"
    )
    private Long id;
    // ===== GUARANTEE CONTRACT =====
    @Column(name = "guarantee_contract_number")
    private String guaranteeContractNumber;
    @Column(name = "guarantee_contract_date")
    private LocalDate guaranteeContractDate;
    @Column(name = "guarantee_notice_number")
    private String guaranteeNoticeNumber;
    @Column(name = "guarantee_notice_date")
    private LocalDate guaranteeNoticeDate;
    @Column(name = "reference_code")
    private String referenceCode;
    @Column(name = "guarantee_term_days")
    private Integer guaranteeTermDays;
    // ===== GUARANTEE AMOUNT =====
    @Column(name = "expected_guarantee_amount", precision = 18, scale = 2)
    private BigDecimal expectedGuaranteeAmount; // so tien bao lanh du kien(nhap khi tao bao lanh)
    @Column(name = "total_guarantee_amount", precision = 18, scale = 2)
    private BigDecimal totalGuaranteeAmount;// số tiền bảo lãnh thực te(Thay doi khi co xe them)
    @Column(name = "used_amount")
    private BigDecimal usedAmount;
    @Column(name = "remaining_amount")
    private BigDecimal remainingAmount;
    // ===== VEHICLE COUNT =====
    @Column(name = "expected_vehicle_count")
    private Integer expectedVehicleCount;
    @Column(name = "imported_vehicle_count")
    private Integer importedVehicleCount;
    @Column(name = "exported_vehicle_count")
    private Integer exportedVehicleCount;
    // ================== SALE CONTRACT (HỢP ĐỒNG MUA BÁN) ==================
    @Column(name = "sale_contract")
    private String saleContract; // tên/lọai hop đồng mua bán
    @Column(name = "sale_contract_amount", precision = 18, scale = 2)
    private BigDecimal saleContractAmount; // giá tiền hop dong mua ban
    // ===== THỜI HẠN BẢO LÃNH =====
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    // Trạng thái
    @Column(name = "status")
    private String status;
    // số tiền đã giải ngân
    @Column(name="disbursement")
    private BigDecimal disbursement;
    // số xe đã nhập kho
    @Column(name="vehicle_warehouse_count")
    private Integer vehicleWarehouseCount;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorized_representative_id")
    private BranchAuthorizedRepresentativeEntity authorizedRepresentative;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private ManufacturerEntity manufacturer;
    @OneToMany(
            mappedBy = "guaranteeLetter",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<VehicleEntity> vehicles;
    @OneToOne(
            mappedBy = "guaranteeLetter",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private GuaranteeLetterFileEntity file;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_contract_id")
    private CreditContractEntity creditContract;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mortgage_contract_id")
    private MortgageContractEntity mortgageContract;
    // liên kết với đơn đề nghị cấp bảo lãnh
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guarantee_application_id")
    private GuaranteeApplicationEntity guaranteeApplication;
}
