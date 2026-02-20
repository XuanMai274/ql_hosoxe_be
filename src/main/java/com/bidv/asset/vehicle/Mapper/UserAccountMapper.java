package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.UserAccountDTO;
import com.bidv.asset.vehicle.entity.EmployeeEntity;
import com.bidv.asset.vehicle.entity.RoleEntity;
import com.bidv.asset.vehicle.entity.UserAccountEntity;
import org.springframework.stereotype.Component;

@Component
public class UserAccountMapper {

    public UserAccountDTO toDto(UserAccountEntity entity) {
        if (entity == null) return null;

        UserAccountDTO dto = new UserAccountDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setPasswordHash(entity.getPasswordHash());
        dto.setStatus(entity.getStatus());
        dto.setAccountType(entity.getAccountType());
        dto.setRoleId(entity.getRole() != null ? entity.getRole().getId() : null);
        dto.setEmployeeId(entity.getEmployee() != null ? entity.getEmployee().getId() : null);
        dto.setCreatedAt(entity.getCreateAt());
        dto.setUpdateAt(entity.getUpdateAt());
        return dto;
    }

    public UserAccountEntity toEntity(UserAccountDTO dto) {
        if (dto == null) return null;

        UserAccountEntity entity = new UserAccountEntity();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setPasswordHash(dto.getPasswordHash());
        entity.setStatus(dto.getStatus());
        entity.setAccountType(dto.getAccountType());

        if (dto.getRoleId() != null) {
            RoleEntity role = new RoleEntity();
            role.setId(dto.getRoleId());
            entity.setRole(role);
        }

        return entity;
    }
}