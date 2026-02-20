package com.bidv.asset.vehicle.entity;

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
@Table(name = "credit_contract")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditContractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "credit_contract_seq")
    @SequenceGenerator(name = "credit_contract_seq", sequenceName = "credit_contract_seq")
    private Long id;

    @Column(name = "contract_number", nullable = false)
    private String contractNumber;

    @Column(name = "contract_date")
    private LocalDate contractDate;

    // ===== Tổng hạn mức =====
    @Column(name = "creditLimit", precision = 18, scale = 2)
    private BigDecimal creditLimit;

    // ===== Đã sử dụng =====
    @Column(name = "used_limit", precision = 18, scale = 2)
    private BigDecimal usedLimit = BigDecimal.ZERO;

    // ===== Còn sử dụng =====
    @Column(name = "remaining_limit", precision = 18, scale = 2)
    private BigDecimal remainingLimit = BigDecimal.ZERO;

    // ===== Dư bảo lãnh =====
    @Column(name = "guarantee_balance", precision = 18, scale = 2)
    private BigDecimal guaranteeBalance = BigDecimal.ZERO;

    // ===== Dư nợ vay xe =====
    @Column(name = "vehicle_loan_balance", precision = 18, scale = 2)
    private BigDecimal vehicleLoanBalance = BigDecimal.ZERO;

    // ===== Dư vay BĐS (max 9 tỷ) =====
    @Column(name = "real_estate_loan_balance", precision = 18, scale = 2)
    private BigDecimal realEstateLoanBalance = BigDecimal.ZERO;
    @Column(name="status")
    private String status;
    // ===== Liên kết HDBD =====
    @ManyToMany
    @JoinTable(
            name = "credit_contract_mortgage",
            joinColumns = @JoinColumn(name = "credit_contract_id"),
            inverseJoinColumns = @JoinColumn(name = "mortgage_contract_id")
    )
    private List<MortgageContractEntity> mortgageContracts;

    // ===== Guarantee =====
    @OneToMany(mappedBy = "creditContract")
    private List<GuaranteeLetterEntity> guarantees;

    // ===== Loan =====
    @OneToMany(mappedBy = "creditContract")
    private List<LoanEntity> loans;
    @ManyToOne(fetch = FetchType.LAZY)
    // ===== Khách hàng =====
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

