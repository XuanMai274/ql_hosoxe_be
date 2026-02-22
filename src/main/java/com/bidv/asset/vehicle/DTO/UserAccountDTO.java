package com.bidv.asset.vehicle.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountDTO {

    private Long id;
    private String username;
    private String status;
    private String accountType;
    private String email;
    private Long roleId;
    private String roleCode;
    private String passwordHash;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private Long employeeId;
    private Integer failedAttempts;
    private LocalDateTime lockUntil;
}
