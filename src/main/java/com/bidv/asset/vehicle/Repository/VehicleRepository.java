package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.DTO.VehicleListDTO;
import com.bidv.asset.vehicle.entity.VehicleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends CrudRepository<VehicleEntity,Long> {
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
        gl.guaranteeContractNumber
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
        or lower(m.code) = lower(:manufacturerCode)
    )
    and (
        coalesce(:guaranteeContractNumber, '') = ''
        or lower(gl.guaranteeContractNumber) like lower(concat('%', :guaranteeContractNumber, '%'))
    )
    order by v.createdAt desc
""")
    // phần tìm kiếm tại danh sách hồ sơ xe
    Page<VehicleListDTO> searchVehicles(
            @Param("chassisNumber") String chassisNumber,
            @Param("status") String status,
            @Param("manufacturerCode") String manufacturerCode,
            @Param("guaranteeContractNumber") String guaranteeContractNumber,
            Pageable pageable
    );
    @Query("""
    select v
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
        coalesce(:manufacturer, '') = ''
        or m.code = :manufacturer
    )
    and (
        coalesce(:guaranteeContractNumber, '') = ''
        or lower(gl.guaranteeContractNumber)
            like lower(concat('%', :guaranteeContractNumber, '%'))
    )
    order by v.createdAt desc
    """)
    List<VehicleEntity> searchVehiclesForExcel(
            @Param("chassisNumber") String chassisNumber,
            @Param("status") String status,
            @Param("manufacturer") String manufacturer,
            @Param("guaranteeContractNumber") String guaranteeContractNumber
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
