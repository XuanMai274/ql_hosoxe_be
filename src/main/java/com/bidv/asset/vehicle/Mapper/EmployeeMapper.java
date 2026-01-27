package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.EmployeeDTO;
import com.bidv.asset.vehicle.entity.EmployeeEntity;
import com.bidv.asset.vehicle.entity.UserAccountEntity;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeDTO toDto(EmployeeEntity entity) {
        if (entity == null) return null;

        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(entity.getId());
        dto.setEmployeeCode(entity.getEmployeeCode());
        dto.setFullName(entity.getFullName());
        dto.setPosition(entity.getPosition());
        dto.setDepartment(entity.getDepartment());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setStatus(entity.getStatus());
        dto.setUserAccountId(entity.getUserAccount() != null ? entity.getUserAccount().getId() : null);

        return dto;
    }

    public EmployeeEntity toEntity(EmployeeDTO dto) {
        if (dto == null) return null;

        EmployeeEntity entity = new EmployeeEntity();
        entity.setId(dto.getId());
        entity.setEmployeeCode(dto.getEmployeeCode());
        entity.setFullName(dto.getFullName());
        entity.setPosition(dto.getPosition());
        entity.setDepartment(dto.getDepartment());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setStatus(dto.getStatus());

        if (dto.getUserAccountId() != null) {
            UserAccountEntity user = new UserAccountEntity();
            user.setId(dto.getUserAccountId());
            entity.setUserAccount(user);
        }

        return entity;
    }
}