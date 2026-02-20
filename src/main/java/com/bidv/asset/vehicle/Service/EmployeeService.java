package com.bidv.asset.vehicle.Service;
import com.bidv.asset.vehicle.DTO.EmployeeCreateRequestDTO;
import com.bidv.asset.vehicle.DTO.EmployeeDTO;

import java.util.List;


public interface EmployeeService {
    public EmployeeDTO createEmployeeWithAccount(EmployeeCreateRequestDTO request);
    EmployeeDTO update(Long id, EmployeeDTO dto);

    void delete(Long id);

    EmployeeDTO getById(Long id);

    List<EmployeeDTO> getAll();
}
