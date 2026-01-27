package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.RoleDTO;
import com.bidv.asset.vehicle.entity.RoleEntity;
import com.bidv.asset.vehicle.entity.UserAccountEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
@Component
public class RoleMapper {

    public RoleDTO toDto(RoleEntity entity) {
        if (entity == null) return null;

        RoleDTO dto = new RoleDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        return dto;
    }

    public RoleEntity toEntity(RoleDTO dto) {
        if (dto == null) return null;

        RoleEntity entity = new RoleEntity();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());

        return entity;
    }
}
