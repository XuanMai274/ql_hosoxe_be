package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_account")
@Getter
@Setter
@NoArgsConstructor
public class UserAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_account_id_seq")
    @SequenceGenerator(name = "user_account_id_seq", sequenceName = "user_account_id_seq")
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    private String status;
    private String accountType;
    @Column(unique = true)
    private String email;
    @Column(name = "refresh_token", length = 512)
    private String refreshToken;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Integer failedAttempts = 0;
    private LocalDateTime lockUntil;
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;
    @OneToOne(mappedBy = "userAccount")
    private EmployeeEntity employee;
    @OneToOne(mappedBy = "userAccount")
    private CustomerEntity customer;

}
