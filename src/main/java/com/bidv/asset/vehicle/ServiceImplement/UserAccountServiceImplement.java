package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.UserAccountDTO;
import com.bidv.asset.vehicle.Mapper.UserAccountMapper;
import com.bidv.asset.vehicle.Repository.RoleRepository;
import com.bidv.asset.vehicle.Repository.UserAccountRepository;
import com.bidv.asset.vehicle.Service.UserAccountService;
import com.bidv.asset.vehicle.entity.RoleEntity;
import com.bidv.asset.vehicle.entity.UserAccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImplement implements UserAccountService {

    @Autowired
    UserAccountRepository repo;
    @Autowired
    RoleRepository roleRepo;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserAccountEntity create(
            String username,
            String email,
            String rawPassword,
            Long roleId) {

        if (repo.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        if (email != null && repo.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        RoleEntity role = roleRepo.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        UserAccountEntity account = new UserAccountEntity();
        account.setUsername(username);
        account.setEmail(email);

        // nên encode password
        account.setPasswordHash(passwordEncoder.encode(rawPassword));

        account.setStatus("ACTIVE");
        account.setAccountType("LOCAL");
        account.setRole(role);
        account.setCreateAt(LocalDateTime.now());

        return repo.save(account);
    }
    @Override
    public UserAccountDTO update(Long id, UserAccountDTO dto) {
        UserAccountEntity entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        entity.setStatus(dto.getStatus());
        entity.setAccountType(dto.getAccountType());

        return new UserAccountMapper().toDto(repo.save(entity));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public UserAccountDTO getById(Long id) {
        return repo.findById(id)
                .map(new UserAccountMapper()::toDto)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }
}