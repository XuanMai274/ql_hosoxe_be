package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.UserAccountDTO;
import com.bidv.asset.vehicle.entity.UserAccountEntity;

public interface UserAccountService {
    public UserAccountEntity create(
            String username,
            String rawPassword,
            Long roleId
    );

    UserAccountDTO update(Long id, UserAccountDTO dto);

    void delete(Long id);

    UserAccountDTO getById(Long id);
}
