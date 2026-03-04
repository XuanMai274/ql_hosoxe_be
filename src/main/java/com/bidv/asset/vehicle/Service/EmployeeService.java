package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.EmployeeCreateRequestDTO;
import com.bidv.asset.vehicle.DTO.EmployeeDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {
    public EmployeeDTO createEmployeeWithAccount(EmployeeCreateRequestDTO request);

    EmployeeDTO update(Long id, EmployeeDTO dto);

    void delete(Long id);

    EmployeeDTO getById(Long id);

    Page<EmployeeDTO> getAll(Pageable pageable, String keyword);
}
