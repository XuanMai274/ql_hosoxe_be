package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id_seq")
    @SequenceGenerator(name = "customer_id_seq", sequenceName = "customer_id_seq", allocationSize = 1)
    private Long id;

    // ===== Thông tin cơ bản =====
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "cif", unique = true, nullable = false)
    private String cif;

    @Column(name = "customer_type")
    private String customerType;

    // ===== Thông tin pháp lý =====
    private String businessRegistrationNo;
    private String taxCode;

    // ===== Thông tin liên hệ =====
    private String address;
    private String phone;
    private String fax;
    private String email;

    // ===== Người đại diện =====
    private String representativeName;
    private String representativeTitle;

    // ===== Tài khoản ngân hàng =====
    private String bankAccountNo;
    private String bankName;

    // ===== Trạng thái =====
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ===== User =====
    @OneToOne
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccountEntity userAccount;

    // ===== Guarantee =====
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<GuaranteeLetterEntity> guaranteeLetters;

    // ===== HDBD =====
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MortgageContractEntity> mortgageContracts;

    // ===== HDTD =====
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CreditContractEntity> creditContracts;
    // ======== Đề xuất cấp bảo lãnh========
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<GuaranteeApplicationEntity> guaranteeApplications;
}
