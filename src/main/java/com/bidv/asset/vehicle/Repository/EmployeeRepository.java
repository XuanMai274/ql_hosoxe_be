package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.EmployeeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    Optional<EmployeeEntity> findByEmployeeCode(String employeeCode);

    @Query("SELECT e FROM EmployeeEntity e WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(e.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<EmployeeEntity> search(@Param("keyword") String keyword, Pageable pageable);
}
