package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "disbursement",
        indexes = {
                @Index(name = "idx_disbursement_contract", columnList = "loan_contract_number"),
                @Index(name = "idx_disbursement_credit_contract", columnList = "credit_contract_id"),
                @Index(name = "idx_disbursement_child_seq", columnList = "credit_contract_id, child_sequence")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisbursementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "disbursement_id_seq")
    @SequenceGenerator(name = "disbursement_id_seq", sequenceName = "disbursement_id_seq")
    private Long id;

    // ===== Thông tin tổng thể giải ngân =====

    @Column(name = "loan_contract_number", nullable = false)
    private String loanContractNumber;

    @Column(name = "used_limit", precision = 18, scale = 2)
    private BigDecimal usedLimit;

    @Column(name = "remaining_limit", precision = 18, scale = 2)
    private BigDecimal remainingLimit;

    @Column(name = "issued_guarantee_balance", precision = 18, scale = 2)
    private BigDecimal issuedGuaranteeBalance;

    @Column(name = "vehicle_loan_balance", precision = 18, scale = 2)
    private BigDecimal vehicleLoanBalance;

    @Column(name = "real_estate_loan_balance", precision = 18, scale = 2)
    private BigDecimal realEstateLoanBalance;

    @Column(name = "total_collateral_value", precision = 18, scale = 2)
    private BigDecimal totalCollateralValue; // Tổng TSHTTTL

    @Column(name = "real_estate_value", precision = 18, scale = 2)
    private BigDecimal realEstateValue; // BDS

    @Column(name = "collateral_value_after_factor", precision = 18, scale = 2)
    private BigDecimal collateralValueAfterFactor; // TSHTTTL sau nhân hệ số (0.85)

    @Column(name = "real_estate_value_after_factor", precision = 18, scale = 2)
    private BigDecimal realEstateValueAfterFactor; // BDS sau nhân hệ số (0.8)
    @Column(name="disbursement_amount")
    private BigDecimal disbursementAmount;
    @Column(name = "disbursement_date")
    private LocalDate disbursementDate;
    @Column(name="creditLimit")
    private BigDecimal creditLimit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Column(name = "child_sequence")
    private Integer childSequence;
    @Column(name = "loan_term")
    private Integer loanTerm; // thời hạn vay (số ngày)
    @Column(name = "start_date")
    private LocalDate startDate; // ngày bắt đầu vay
    @Column(name = "due_date")
    private LocalDate dueDate; // ngày hết hạn vay

    @Column(name = "interest_amount", precision = 18, scale = 2)
    private BigDecimal interestAmount; // Tiền lãi

    @Column(name = "total_amount_paid", precision = 18, scale = 2)
    private BigDecimal totalAmountPaid; // Tổng số tiền đã trả

    @Column(name = "withdrawn_vehicles_count")
    private Integer withdrawnVehiclesCount; // Số xe đã rút

    @Column(name = "total_vehicles_count")
    private Integer totalVehiclesCount; // Tổng số xe vay

    @Column(name = "status")
    private String status; // ACTIVE/PAID_OFF
    // ===== Quan hệ =====

    // N Disbursement - 1 CreditContract
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_contract_id")
    private CreditContractEntity creditContract;

    // N Disbursement - 1 MortgageContract
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mortgage_contract_id")
    private MortgageContractEntity mortgageContract;

    // 1 Disbursement - N Loan
    @OneToMany(mappedBy = "disbursement",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<LoanEntity> loans;
}