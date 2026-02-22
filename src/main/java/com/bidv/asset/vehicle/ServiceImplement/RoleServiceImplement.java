package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.RoleDTO;
import com.bidv.asset.vehicle.Mapper.RoleMapper;
import com.bidv.asset.vehicle.Repository.RoleRepository;
import com.bidv.asset.vehicle.Service.RoleService;
import com.bidv.asset.vehicle.entity.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImplement implements RoleService {

    @Autowired
    private RoleRepository roleRepo;
    @Autowired RoleMapper roleMapper;
    // ===== CREATE =====
    @Override
    public RoleDTO create(RoleDTO dto) {

        if (roleRepo.existsByCode(dto.getCode())) {
            throw new RuntimeException("Role code already exists");
        }

        RoleEntity entity = roleMapper.toEntity(dto);

        return roleMapper.toDto(roleRepo.save(entity));
    }

    // ===== UPDATE =====
    @Override
    public RoleDTO update(Long id, RoleDTO dto) {

        RoleEntity entity = roleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        entity.setCode(dto.getCode());
        entity.setName(dto.getName());

        return roleMapper.toDto(roleRepo.save(entity));
    }

    // ===== DELETE =====
    @Override
    public void delete(Long id) {

        if (!roleRepo.existsById(id)) {
            throw new RuntimeException("Role not found");
        }

        roleRepo.deleteById(id);
    }

    // ===== GET BY ID =====
    @Override
    public RoleDTO getById(Long id) {

        return roleRepo.findById(id)
                .map(roleMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    // ===== GET ALL =====
    @Override
    public List<RoleDTO> getAll() {

        return roleRepo.findAll()
                .stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }
}