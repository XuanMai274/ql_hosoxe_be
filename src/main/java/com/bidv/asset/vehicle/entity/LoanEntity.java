package com.bidv.asset.vehicle.entity;

import com.bidv.asset.vehicle.enums.LoanStatus;
import com.bidv.asset.vehicle.enums.LoanType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "loan",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_vehicle_unique",
                        columnNames = {"vehicle_id"}
                )
        },
        indexes = {
        @Index(name = "idx_loan_account", columnList = "account_number"),
        @Index(name = "idx_loan_contract", columnList = "loan_contract_number"),
        @Index(name = "idx_loan_docid", columnList = "doc_id"),
        @Index(name = "idx_loan_customer", columnList = "customer_id"),
        @Index(name = "idx_loan_vehicle", columnList = "vehicle_id"),
        @Index(name = "idx_loan_guarantee", columnList = "guarantee_letter_id"),
        @Index(name = "idx_loan_credit_contract", columnList = "credit_contract_id"),
        @Index(name = "idx_loan_type", columnList = "loan_type"),
        // composite index
        @Index(name = "idx_loan_customer_status", columnList = "customer_id, loan_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_id_seq")
    @SequenceGenerator(name = "loan_id_seq", sequenceName = "loan_id_seq")
    private Long id;
    // ===== Thông tin hợp đồng vay =====
    @Column(name = "account_number")
    private String accountNumber; // SỐ TK
    @Column(name = "loan_contract_number", nullable = false)
    private String loanContractNumber; // HĐ VAY
    @Column(name = "loan_term")
    private Integer loanTerm; // THỜI HẠN (tháng)
    @Column(name = "loan_date")
    private LocalDate loanDate; // NGÀY VAY
    @Column(name = "due_date")
    private LocalDate dueDate; // NGÀY ĐẾN HẠN
    @Column(name = "loan_amount", precision = 18, scale = 2)
    private BigDecimal loanAmount; // SỐ TIỀN VAY (HTC-VN)
    // lưu mã định danh tương ứng với hệ thống
    @Column(name = "doc_id")
    private String docId;
    // ===== Trả nợ (không lưu lịch sử) =====
    @Column(name = "last_payment_date")
    private LocalDate lastPaymentDate; // NGÀY TRẢ

    @Column(name = "total_paid_amount", precision = 18, scale = 2)
    private BigDecimal totalPaidAmount; // SỐ TIỀN ĐÃ TRẢ

    // ===== TSBĐ & mục đích =====
    @Column(name = "collateral_and_purpose", columnDefinition = "TEXT")
    private String collateralAndPurpose; // TSBĐ và mục đích cho vay
    @Column(name = "withdrawn_chassis_number")
    private String withdrawnChassisNumber; // SK XE RÚT
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_status", nullable = false)
    private LoanStatus loanStatus;
    // ===== Audit =====
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // tự tăng số ca hợp đồng con
    @Column(name = "child_sequence")
    private Integer childSequence;
    // ===== File đính kèm =====
    @OneToMany(mappedBy = "loan", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LoanFileEntity> files;
    // ===== CUSTOMER =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;
    // ===== GUARANTEE =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guarantee_letter_id")
    private GuaranteeLetterEntity guaranteeLetter;
    // ===== Xe liên quan =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private VehicleEntity vehicle;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_contract_id", nullable = false)
    private CreditContractEntity creditContract;
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false)
    private LoanType loanType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disbursement_id", nullable = false)
    private DisbursementEntity disbursement;

}
