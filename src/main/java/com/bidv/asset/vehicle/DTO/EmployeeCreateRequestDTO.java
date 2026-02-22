package com.bidv.asset.vehicle.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeCreateRequestDTO {

    private EmployeeDTO employee;

    private String username;
    private String password;
    private Long roleId;
}
