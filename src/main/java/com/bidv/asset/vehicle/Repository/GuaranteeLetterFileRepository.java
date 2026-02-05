package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.GuaranteeLetterFileEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuaranteeLetterFileRepository extends CrudRepository<GuaranteeLetterFileEntity,Long> {
    @Query("""
            select max(f.version)
            from GuaranteeLetterFileEntity f
            where f.guaranteeLetter.id = :guaranteeLetterId
            """)
    Optional<Integer> getMaxVersion(Long guaranteeLetterId);

    @Modifying
    @Query("""
            update GuaranteeLetterFileEntity f
            set f.isActive = false
            where f.guaranteeLetter.id = :guaranteeLetterId
            and f.isActive = true
            """)
    void disableActiveFiles(Long guaranteeLetterId);
}
