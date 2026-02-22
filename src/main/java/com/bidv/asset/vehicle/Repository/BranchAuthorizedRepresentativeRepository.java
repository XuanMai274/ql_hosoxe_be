package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.BranchAuthorizedRepresentativeEntity;
import lombok.NonNull;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchAuthorizedRepresentativeRepository extends JpaRepository<BranchAuthorizedRepresentativeEntity,Long> {
    @NonNull
    List<BranchAuthorizedRepresentativeEntity> findAll();
    BranchAuthorizedRepresentativeEntity findById(long id);
}
