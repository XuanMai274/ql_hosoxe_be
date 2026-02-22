package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.RoleDTO;

import java.util.List;

public interface RoleService {
    RoleDTO create(RoleDTO dto);

    RoleDTO update(Long id, RoleDTO dto);

    void delete(Long id);

    RoleDTO getById(Long id);

    List<RoleDTO> getAll();
}
