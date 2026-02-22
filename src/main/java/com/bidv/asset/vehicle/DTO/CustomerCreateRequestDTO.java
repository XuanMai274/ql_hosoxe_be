package com.bidv.asset.vehicle.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerCreateRequestDTO {

    private CustomerDTO customer;

    private String username;
    private String password;
    private Long roleId;
}