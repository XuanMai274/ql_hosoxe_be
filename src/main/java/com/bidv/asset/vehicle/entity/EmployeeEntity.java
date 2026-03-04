package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_id_seq")
    @SequenceGenerator(name = "employee_id_seq", sequenceName = "employee_id_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false)
    private String employeeCode;

    private String fullName;
    private String position;
    private String email;
    private String phone;
    private String status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", unique = true)
    private UserAccountEntity userAccount;
}
