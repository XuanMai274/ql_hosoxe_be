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
public class EmployeeDTO {
    private Long id;
    private String employeeCode;
    private String fullName;
    private String position;
    private String department;
    private String email;
    private String phone;
    private String status;

    private Long userAccountId;
    private String username;

    private LocalDateTime createdAt;
}
