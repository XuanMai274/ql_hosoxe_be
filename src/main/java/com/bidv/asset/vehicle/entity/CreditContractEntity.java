package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "credit_contract")
@Getter
@Setter
public class CreditContractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "credit_contract_id_seq")
    @SequenceGenerator(
            name = "credit_contract_id_seq",
            sequenceName = "credit_contract_id_seq",
            allocationSize = 1
    )
    private Long id;

    // ===== SỐ HĐTD =====
    @Column(name = "contract_number", nullable = false, unique = true)
    private String contractNumber;

    // ===== NGÀY KÝ HĐTD =====
    @Column(name = "contract_date")
    private LocalDate contractDate;
    @Column(name="update_at")
    private LocalDateTime updateAt;
    // ===== GHTD =====
    @Column(name = "credit_limit", nullable = false, precision = 19, scale = 0)
    private BigDecimal creditLimit;              // GHTD

    @Column(name = "used_limit", nullable = false, precision = 19, scale = 0)
    private BigDecimal usedLimit;                // GHTD đã sử dụng

    @Column(name = "remaining_limit", nullable = false, precision = 19, scale = 0)
    private BigDecimal remainingLimit;           // GHTD còn được sử dụng

    // ===== AUDIT =====
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ===== QUAN HỆ =====
    @OneToMany(mappedBy = "creditContract", fetch = FetchType.LAZY)
    private List<GuaranteeLetterEntity> guaranteeLetters;

    // ===== TỰ ĐỘNG TÍNH =====
    @PrePersist
    @PreUpdate
    public void calculateRemainingLimit() {
        if (creditLimit == null) creditLimit = BigDecimal.ZERO;
        if (usedLimit == null) usedLimit = BigDecimal.ZERO;
        this.remainingLimit = creditLimit.subtract(usedLimit);
    }
}
