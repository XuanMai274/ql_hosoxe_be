package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.DTO.VehicleListDTO;
import com.bidv.asset.vehicle.entity.VehicleEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {
    // tìm danh sách nhiều xe cùng 1 lúc
    @Query("""
                SELECT DISTINCT v FROM VehicleEntity v
                LEFT JOIN FETCH v.guaranteeLetter g
                LEFT JOIN FETCH g.creditContract cc
                LEFT JOIN FETCH cc.mortgageContracts mc
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
    @EntityGraph(attributePaths = {
            "guaranteeLetter",
            "guaranteeLetter.manufacturer",
            "guaranteeLetter.creditContract",
            "invoice",
            "manufacturerEntity"
    })
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
                    gl.referenceCode,
                    (select count(d) from DocumentEntity d where d.vehicle.id = v.id)
                )
                from VehicleEntity v
                join v.guaranteeLetter gl
                join v.manufacturerEntity m
                where (
                    cast(:customerId as long) is null or gl.customer.id = :customerId
                )
                and (
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
            @Param("customerId") Long customerId,
            @Param("chassisNumber") String chassisNumber,
            @Param("status") String status,
            @Param("manufacturerCode") String manufacturerCode,
            @Param("ref") String ref,
            Pageable pageable);

    @Query("""
                select v
                from VehicleEntity v
                join fetch v.guaranteeLetter gl
                join fetch v.manufacturerEntity m
                where (
                         :chassisNumber is null
                         or lower(v.chassisNumber) like lower(concat('%', :chassisNumber, '%'))
                     )
                     and (
                         :status is null
                         or v.status = :status
                     )
                     and (
                         :manufacturerCode is null
                         or lower(m.code) = lower(:manufacturerCode)
                     )
                     and (
                         :ref is null
                         or lower(gl.referenceCode)
                             like lower(concat('%', :ref, '%'))
                     )
                order by v.createdAt desc
            """)
    List<VehicleEntity> searchVehiclesForExcel(
            @Param("chassisNumber") String chassisNumber,
            @Param("status") String status,
            @Param("manufacturerCode") String manufacturerCode,
            @Param("ref") String ref);

    // lấy lên thông tin xe
    @EntityGraph(attributePaths = {
            "guaranteeLetter",
            "invoice",
            "documents"
    })

    // @Query("""
    // select new com.bidv.asset.vehicle.DTO.VehicleListDTO(
    // v.id,
    // v.stt,
    // v.vehicleName,
    // v.status,
    // v.chassisNumber,
    // v.engineNumber,
    // v.price,
    // gl.guaranteeContractNumber
    // )
    // from VehicleEntity v
    // join v.guaranteeLetter gl
    // where (
    // COALESCE(:chassisNumber, '') = ''
    // or lower(v.chassisNumber) like lower(concat('%', :chassisNumber, '%'))
    // )
    // and (
    // COALESCE(:status, '') = ''
    // or v.status = :status
    // )
    // and (
    // :minPrice is null
    // or v.price >= :minPrice
    // )
    // and (
    // :maxPrice is null
    // or v.price <= :maxPrice
    // )
    // order by v.createdAt desc
    // """)
    // Page<VehicleListDTO> searchVehicles(
    // @Param("chassisNumber") String chassisNumber,
    // @Param("status") String status,
    // @Param("minPrice") BigDecimal minPrice,
    // @Param("maxPrice") BigDecimal maxPrice,
    // Pageable pageable
    // );

    @Query("""
                select v from VehicleEntity v
                left join fetch v.guaranteeLetter g
                left join fetch v.invoice i
                left join fetch g.manufacturer
                left join fetch g.authorizedRepresentative
            """)
    List<VehicleEntity> findAllForExcel();

    // tránh 2 user cùng nhập kho xe cùng 1 lúc
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from VehicleEntity v where v.id = :id")
    Optional<VehicleEntity> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT SUM(v.price) FROM VehicleEntity v WHERE v.status = :status")
    BigDecimal sumPriceByStatus(@Param("status") String status);

    // --- NEW METHODS FOR WAREHOUSE EXPORT ---

    // Tìm danh sách xe theo ID của đơn yêu cầu xuất kho (Officer xem)
    @EntityGraph(attributePaths = { "guaranteeLetter", "manufacturerEntity", "loans" })
    List<VehicleEntity> findByWarehouseExportId(Long warehouseExportId);

    // Tìm danh sách xe theo ID của phiếu nhập kho
    @EntityGraph(attributePaths = { "guaranteeLetter", "manufacturerEntity" })
    List<VehicleEntity> findByWarehouseImportId(Long warehouseImportId);

    // Tìm danh sách xe sẵn sàng để yêu cầu xuất (Trạng thái phù hợp và chưa thuộc
    // đơn nào)
    @EntityGraph(attributePaths = { "guaranteeLetter", "manufacturerEntity", "guaranteeLetter.manufacturer" })
    @Query("""
                SELECT v FROM VehicleEntity v
                LEFT JOIN v.guaranteeLetter gl
                LEFT JOIN v.manufacturerEntity m
                WHERE v.status = :status
                AND v.warehouseImport IS NULL
                AND v.warehouseExport IS NULL
                AND (:chassisNumber IS NULL OR LOWER(v.chassisNumber) LIKE :chassisNumber)
                AND (:manufacturerCode IS NULL OR m.code = :manufacturerCode)
                AND (:ref IS NULL OR LOWER(gl.referenceCode) LIKE :ref)
            """)
    Page<VehicleEntity> findAvailableForExport(
            @Param("status") String status,
            @Param("chassisNumber") String chassisNumber,
            @Param("manufacturerCode") String manufacturerCode,
            @Param("ref") String ref,
            Pageable pageable);

    @EntityGraph(attributePaths = { "guaranteeLetter", "manufacturerEntity", "guaranteeLetter.manufacturer", "loans" })
    @Query("""
                SELECT v FROM VehicleEntity v
                LEFT JOIN v.guaranteeLetter gl
                LEFT JOIN v.manufacturerEntity m
                LEFT JOIN v.loans l
                WHERE v.status = :status
                AND v.warehouseExport IS NULL
                AND (:chassisNumber IS NULL OR LOWER(v.chassisNumber) LIKE :chassisNumber)
                AND (:manufacturerCode IS NULL OR m.code = :manufacturerCode)
                AND (:loanContractNumber IS NULL OR LOWER(l.loanContractNumber) LIKE :loanContractNumber)
            """)
    Page<VehicleEntity> findAvailableForExportForCustomer(
            @Param("status") String status,
            @Param("chassisNumber") String chassisNumber,
            @Param("manufacturerCode") String manufacturerCode,
            @Param("loanContractNumber") String loanContractNumber,
            Pageable pageable);

    @Query("SELECT COUNT(v) FROM VehicleEntity v")
    long countAllVehicles();

    long countByWarehouseImportIsNotNullAndWarehouseExportIsNull();

    long countByWarehouseExportIsNotNull();

    @Query("SELECT COUNT(v) FROM VehicleEntity v WHERE v.loans IS NOT EMPTY")
    long countByLoansIsNotEmpty();

    // tìm xe vinfast trong thời hạn cần nhập kho chính thức
    @Query("""
                SELECT v FROM VehicleEntity v
                WHERE v.manufacturerEntity.code = :manufacturerCode
                  AND v.status = :status
                  AND v.inSafe = true
            """)
    List<VehicleEntity> findVinfastInSafe(
            @Param("manufacturerCode") String manufacturerCode,
            @Param("status") String status);

    @Modifying
    @Query("""
            UPDATE VehicleEntity v
            SET v.inSafe = :inSafe
            WHERE v.id IN :ids
            """)
    int updateInSafeByIds(@Param("ids") List<Long> ids,
            @Param("inSafe") Boolean inSafe);
}
