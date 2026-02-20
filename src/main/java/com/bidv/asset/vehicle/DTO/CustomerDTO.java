package com.bidv.asset.vehicle.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {

    private Long id;

    // ===== Thông tin cơ bản =====
    private String customerName;
    private String cif;
    private String customerType;

    // ===== Pháp lý =====
    private String businessRegistrationNo;
    private String taxCode;

    // ===== Liên hệ =====
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

    // ===== Quan hệ =====
    private Long userAccountId;
}
