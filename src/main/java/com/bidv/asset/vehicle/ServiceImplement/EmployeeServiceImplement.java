package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.EmployeeCreateRequestDTO;
import com.bidv.asset.vehicle.DTO.EmployeeDTO;
import com.bidv.asset.vehicle.Mapper.EmployeeMapper;
import com.bidv.asset.vehicle.Repository.EmployeeRepository;
import com.bidv.asset.vehicle.Service.EmployeeService;
import com.bidv.asset.vehicle.Service.UserAccountService;
import com.bidv.asset.vehicle.entity.EmployeeEntity;
import com.bidv.asset.vehicle.entity.RoleEntity;
import com.bidv.asset.vehicle.entity.UserAccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class EmployeeServiceImplement implements EmployeeService {
    @Autowired
    UserAccountService userAccountService;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    com.bidv.asset.vehicle.Repository.RoleRepository roleRepo;
    @Autowired
    EmployeeMapper employeeMapper;

    @Override
    public EmployeeDTO createEmployeeWithAccount(EmployeeCreateRequestDTO request) {
        UserAccountEntity account = userAccountService.create(
                request.getUsername(),
                request.getEmployee() != null ? request.getEmployee().getEmail() : null,
                request.getPassword(),
                request.getRoleId());

        EmployeeEntity employee = employeeMapper.toEntity(
                request.getEmployee(),
                account);

        return employeeMapper.toDto(employeeRepository.save(employee));
    }

    // ================= UPDATE =================
    @Override
    public EmployeeDTO update(Long id, EmployeeDTO dto) {

        EmployeeEntity entity = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Update fields
        entity.setEmployeeCode(dto.getEmployeeCode());
        entity.setFullName(dto.getFullName());
        entity.setPosition(dto.getPosition());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setStatus(dto.getStatus());

        // Cập nhật vai trò nếu có
        if (dto.getRoleId() != null && entity.getUserAccount() != null) {
            RoleEntity newRole = roleRepo.findById(dto.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            entity.getUserAccount().setRole(newRole);
        }

        return employeeMapper.toDto(employeeRepository.save(entity));
    }

    // ================= DELETE =================
    @Override
    public void delete(Long id) {

        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found");
        }

        employeeRepository.deleteById(id);
    }

    // ================= GET BY ID =================
    @Override
    public EmployeeDTO getById(Long id) {

        EmployeeEntity entity = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return employeeMapper.toDto(entity);
    }

    // ================= GET ALL (with search & pagination) =================
    @Override
    public Page<EmployeeDTO> getAll(Pageable pageable, String keyword) {
        return employeeRepository.search(keyword, pageable)
                .map(employeeMapper::toDto);
    }
}
