package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "mortgage_contract",
        indexes = {
                @Index(name = "idx_mortgage_contract_number", columnList = "contract_number"),
                @Index(name = "idx_mortgage_customer", columnList = "customer_id")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MortgageContractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mortgage_id_seq")
    @SequenceGenerator(name = "mortgage_id_seq", sequenceName = "mortgage_id_seq")
    private Long id;

    @Column(name = "contract_number", nullable = false, unique = true)
    private String contractNumber; // Số HĐBD

    @Column(name = "contract_date")
    private LocalDate contractDate;

    @Column(name = "total_collateral_value", precision = 18, scale = 2)
    private BigDecimal totalCollateralValue; // Tổng giá trị TSBĐ

    @Column(name = "remaining_collateral_value", precision = 18, scale = 2)
    private BigDecimal remainingCollateralValue;
    @Column(name="security_registration_number")
    private String securityRegistrationNumber; // số đơn đăng kí giao dịch đảm bảo
    @Column(name="personal_id_number")
    private String personalIdNumber; // mã cá nhân
    // loại xe
    @Column(name = "template_code")
    private String templateCode;
    @Column
    private String status;
    // ===== CUSTOMER =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    // ===== HDTD sử dụng tài sản này =====
    @ManyToMany(mappedBy = "mortgageContracts")
    private List<CreditContractEntity> creditContracts;
    @OneToMany(
            mappedBy = "mortgageContract",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private List<GuaranteeLetterEntity> guaranteeLetters;
    // LOẠI XE
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id")
    private ManufacturerEntity manufacturer;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
