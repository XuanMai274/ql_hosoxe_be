package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id_seq")
    @SequenceGenerator(name = "customer_id_seq", sequenceName = "customer_id_seq")
    private Long id;

    // ===== Thông tin cơ bản =====
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "cif", unique = true, nullable = false)
    private String cif;

    @Column(name = "customer_type")
    private String customerType; // INDIVIDUAL / CORPORATE

    // ===== Thông tin pháp lý =====
    @Column(name = "business_registration_no")
    private String businessRegistrationNo; // Mã số doanh nghiệp

    @Column(name = "tax_code")
    private String taxCode;

    // ===== Thông tin liên hệ =====
    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "fax")
    private String fax;

    @Column(name = "email")
    private String email;

    // ===== Người đại diện pháp luật =====
    @Column(name = "representative_name")
    private String representativeName;

    @Column(name = "representative_title")
    private String representativeTitle;

    // ===== Thông tin tài khoản ngân hàng =====
    @Column(name = "bank_account_no")
    private String bankAccountNo;

    @Column(name = "bank_name")
    private String bankName;

    // ===== Trạng thái =====
    @Column(name = "status")
    private String status; // ACTIVE / INACTIVE

    // ===== Audit =====
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== Liên kết User =====
    @OneToOne
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccountEntity userAccount;
}
