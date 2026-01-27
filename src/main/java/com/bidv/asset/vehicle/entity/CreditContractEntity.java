package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "credit_contract")
@Getter
@Setter
public class CreditContractEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "credit_contract_id_seq")
    @SequenceGenerator(
            name = "credit_contract_id_seq",
            sequenceName = "credit_contract_id_seq"
    )
    private Long id;

    @Column(name = "contract_number", nullable = false, unique = true)
    private String contractNumber;

    @Column(name = "contract_date")
    private LocalDate contractDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "creditContract", fetch = FetchType.LAZY)
    private List<GuaranteeLetterEntity> guaranteeLetters;
}
