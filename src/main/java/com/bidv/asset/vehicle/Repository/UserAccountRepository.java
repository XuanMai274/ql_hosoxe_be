package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository
        extends JpaRepository<UserAccountEntity, Long> {

    Optional<UserAccountEntity> findByUsername(String username);

    Optional<UserAccountEntity> findByEmail(String email);

    Optional<UserAccountEntity> findByRefreshToken(String refreshToken);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
