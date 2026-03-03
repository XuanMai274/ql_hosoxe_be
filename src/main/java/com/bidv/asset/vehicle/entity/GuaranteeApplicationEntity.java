package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "guarantee_proposal",
        indexes = {
                @Index(name = "idx_ga_application_number", columnList = "application_number"),
                @Index(name = "idx_ga_credit_contract", columnList = "credit_contract_id"),
                @Index(name = "idx_ga_mortgage_contract", columnList = "mortgage_contract_id"),
                @Index(name = "idx_ga_status", columnList = "status")
        }
)
public class GuaranteeApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guarantee_application_seq")
    @SequenceGenerator(name = "guarantee_application_seq",
            sequenceName = "guarantee_application_seq")
    private Long id;

    // ===== BASIC INFO =====
    @Column(name = "application_number", unique = true)
    private String applicationNumber; // Số đơn đề nghị

    @Column(name = "sub_guarantee_contract_number")
    private String subGuaranteeContractNumber;
    // Số HĐBLCT con của HĐBĐ

    @Column(name = "guarantee_term_days")
    private Integer guaranteeTermDays; // Thời hạn bảo lãnh (ngày)

    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    // ===== TOTAL INFO =====
    @Column(name = "total_vehicle_count")
    private Integer totalVehicleCount;

    @Column(name = "total_vehicle_amount", precision = 18, scale = 2)
    private BigDecimal totalVehicleAmount;

    @Column(name = "total_guarantee_amount", precision = 18, scale = 2)
    private BigDecimal totalGuaranteeAmount;

    // ===== STATUS =====
    @Column(name = "status")
    private String status;
    // DRAFT / SUBMITTED / APPROVED / REJECTED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // ===== RELATION =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private ManufacturerEntity manufacturer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_contract_id")
    private CreditContractEntity creditContract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mortgage_contract_id")
    private MortgageContractEntity mortgageContract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @OneToMany(
            mappedBy = "guaranteeApplication",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<GuaranteeApplicationVehicleEntity> vehicles;
    // liên kết với thư bảo lãnh thực tế
    @OneToMany(
            mappedBy = "guaranteeApplication",
            fetch = FetchType.LAZY
    )
    private List<GuaranteeLetterEntity> guaranteeLetters;
}
