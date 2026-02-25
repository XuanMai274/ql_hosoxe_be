package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.GuaranteeApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface GuaranteeApplicationRepository
        extends JpaRepository<GuaranteeApplicationEntity, Long> {

    Optional<GuaranteeApplicationEntity>
    findByApplicationNumber(String applicationNumber);
    GuaranteeApplicationEntity findById(long id);
}