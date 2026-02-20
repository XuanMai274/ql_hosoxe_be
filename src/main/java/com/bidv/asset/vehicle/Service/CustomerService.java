package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.CustomerCreateRequestDTO;
import com.bidv.asset.vehicle.DTO.CustomerDTO;

import java.util.List;

public interface CustomerService {
    CustomerDTO createCustomerWithAccount(CustomerCreateRequestDTO request);
    CustomerDTO create(CustomerDTO dto);

    CustomerDTO update(Long id, CustomerDTO dto);

    void delete(Long id);

    CustomerDTO getById(Long id);

    List<CustomerDTO> getAll();
}
