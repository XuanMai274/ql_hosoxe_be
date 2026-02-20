package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.DTO.VehicleListDTO;
import com.bidv.asset.vehicle.entity.VehicleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity,Long> {
    // tìm danh sách nhiều xe cùng 1 lúc
    @Query("""
        SELECT v FROM VehicleEntity v
        LEFT JOIN FETCH v.guaranteeLetter g
        LEFT JOIN FETCH g.creditContract
        LEFT JOIN FETCH g.manufacturer
        WHERE v.id IN :ids
        """)
    List<VehicleEntity> findAllWithGuaranteeByIds(List<Long> ids);
    Optional<VehicleEntity> findDetailById(Long id);
    // tìm xe theo số khug
    Optional<VehicleEntity> findByChassisNumber(String chassisNumber);
    // dùng khi update
    boolean existsByChassisNumberAndIdNot(String chassisNumber, Long id);
    // tìm xe theo trạng thái
    List<VehicleEntity> findByStatus(String status);
    boolean existsByChassisNumber(String chassisNumber);
    @Query("""
    select new com.bidv.asset.vehicle.DTO.VehicleListDTO(
        v.id,
        v.stt,
        v.vehicleName,
        v.status,
        v.chassisNumber,
        v.engineNumber,
        v.price,
        gl.referenceCode
    )
    from VehicleEntity v
    join v.guaranteeLetter gl
    join gl.manufacturer m
    where (
        coalesce(:chassisNumber, '') = ''
        or lower(v.chassisNumber) like lower(concat('%', :chassisNumber, '%'))
    )
    and (
        coalesce(:status, '') = ''
        or v.status = :status
    )
    and (
        coalesce(:manufacturerCode, '') = ''
        or m.code = :manufacturerCode
    )
    and (
        coalesce(:ref, '') = ''
        or lower(gl.referenceCode)
            like lower(concat('%', :ref, '%'))
    )
""")
    Page<VehicleListDTO> searchVehicles(
            @Param("chassisNumber") String chassisNumber,
            @Param("status") String status,
            @Param("manufacturerCode") String manufacturerCode,
            @Param("ref") String ref,
            Pageable pageable
    );
    @Query("""
    select v
    from VehicleEntity v
    join fetch v.guaranteeLetter gl
    join fetch gl.manufacturer m
    where (
        coalesce(:chassisNumber, '') = ''
        or lower(v.chassisNumber) like lower(concat('%', :chassisNumber, '%'))
    )
    and (
        coalesce(:status, '') = ''
        or v.status = :status
    )
    and (
        coalesce(:manufacturerCode, '') = ''
        or m.code = :manufacturerCode
    )
    and (
        coalesce(:ref, '') = ''
        or lower(gl.referenceCode)
            like lower(concat('%', :ref, '%'))
    )
    order by v.createdAt desc
""")
    List<VehicleEntity> searchVehiclesForExcel(
            @Param("chassisNumber") String chassisNumber,
            @Param("status") String status,
            @Param("manufacturerCode") String manufacturerCode,
            @Param("ref") String ref
    );
    // lấy lên thông tin xe
    @EntityGraph(attributePaths = {
            "guaranteeLetter",
            "invoice",
            "documents"
    })

//    @Query("""
//    select new com.bidv.asset.vehicle.DTO.VehicleListDTO(
//        v.id,
//        v.stt,
//        v.vehicleName,
//        v.status,
//        v.chassisNumber,
//        v.engineNumber,
//        v.price,
//        gl.guaranteeContractNumber
//    )
//    from VehicleEntity v
//    join v.guaranteeLetter gl
//    where (
//        COALESCE(:chassisNumber, '') = ''
//        or lower(v.chassisNumber) like lower(concat('%', :chassisNumber, '%'))
//    )
//    and (
//        COALESCE(:status, '') = ''
//        or v.status = :status
//    )
//    and (
//        :minPrice is null
//        or v.price >= :minPrice
//    )
//    and (
//        :maxPrice is null
//        or v.price <= :maxPrice
//    )
//    order by v.createdAt desc
//""")
//    Page<VehicleListDTO> searchVehicles(
//            @Param("chassisNumber") String chassisNumber,
//            @Param("status") String status,
//            @Param("minPrice") BigDecimal minPrice,
//            @Param("maxPrice") BigDecimal maxPrice,
//            Pageable pageable
//    );

    @Query("""
        select v from VehicleEntity v
        left join fetch v.guaranteeLetter g
        left join fetch v.invoice i
        left join fetch g.manufacturer
        left join fetch g.authorizedRepresentative
    """)
    List<VehicleEntity> findAllForExcel();

}
