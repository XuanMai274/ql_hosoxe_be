package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.CreditContractEntity;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditContractRepository extends CrudRepository<CreditContractEntity,Long> {
    @NonNull
    List<CreditContractEntity> findAll();
}
