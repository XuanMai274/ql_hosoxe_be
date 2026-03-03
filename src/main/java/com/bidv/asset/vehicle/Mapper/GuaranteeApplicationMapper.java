package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.*;
import com.bidv.asset.vehicle.entity.*;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GuaranteeApplicationMapper {

    // =====================================================
    // ENTITY → DTO
    // =====================================================
    public GuaranteeApplicationDTO toDTO(GuaranteeApplicationEntity entity) {

        if (entity == null) return null;

        return GuaranteeApplicationDTO.builder()
                .id(entity.getId())
                .applicationNumber(entity.getApplicationNumber())
                .subGuaranteeContractNumber(entity.getSubGuaranteeContractNumber())
                .guaranteeTermDays(entity.getGuaranteeTermDays())
                .expiryDate(entity.getExpiryDate())
                .totalVehicleCount(entity.getTotalVehicleCount())
                .totalVehicleAmount(entity.getTotalVehicleAmount())
                .totalGuaranteeAmount(entity.getTotalGuaranteeAmount())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .approvedAt(entity.getApprovedAt())

                // ===== MAP BASIC RELATION ONLY =====
                .manufacturerDTO(mapManufacturerBasic(entity.getManufacturer()))
                .creditContractDTO(mapCreditBasic(entity.getCreditContract()))
                .mortgageContractDTO(mapMortgageBasic(entity.getMortgageContract()))
                .customerDTO(mapCustomerBasic(entity.getCustomer()))

                .vehicles(toVehicleDTOList(entity.getVehicles()))
                .build();
    }

    // =====================================================
    // DTO → ENTITY
    // =====================================================
    public  GuaranteeApplicationEntity toEntity(
            GuaranteeApplicationDTO dto,
            ManufacturerEntity manufacturer,
            CreditContractEntity creditContract,
            MortgageContractEntity mortgageContract,
            CustomerEntity customer
    ) {

        if (dto == null) return null;

        GuaranteeApplicationEntity entity = new GuaranteeApplicationEntity();

        entity.setId(dto.getId());
        entity.setApplicationNumber(dto.getApplicationNumber());
        entity.setSubGuaranteeContractNumber(dto.getSubGuaranteeContractNumber());
        entity.setGuaranteeTermDays(dto.getGuaranteeTermDays());
        entity.setExpiryDate(dto.getExpiryDate());
        entity.setTotalVehicleCount(dto.getTotalVehicleCount());
        entity.setTotalVehicleAmount(dto.getTotalVehicleAmount());
        entity.setTotalGuaranteeAmount(dto.getTotalGuaranteeAmount());
        entity.setStatus(dto.getStatus());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setApprovedAt(dto.getApprovedAt());

        // ===== SET RELATION (ONLY REFERENCE) =====
        entity.setManufacturer(manufacturer);
        entity.setCreditContract(creditContract);
        entity.setMortgageContract(mortgageContract);
        entity.setCustomer(customer);

        // ===== MAP VEHICLES =====
        if (dto.getVehicles() != null) {
            List<GuaranteeApplicationVehicleEntity> vehicles =
                    dto.getVehicles().stream()
                            .map(v -> toVehicleEntity(v, entity))
                            .collect(Collectors.toList());

            entity.setVehicles(vehicles);
        }

        return entity;
    }

    // =====================================================
    // VEHICLE MAPPING
    // =====================================================
    private static List<GuaranteeApplicationVehicleDTO> toVehicleDTOList(
            List<GuaranteeApplicationVehicleEntity> list) {

        if (list == null) return null;

        return list.stream()
                .map(GuaranteeApplicationMapper::toVehicleDTO)
                .collect(Collectors.toList());
    }

    private static GuaranteeApplicationVehicleDTO toVehicleDTO(
            GuaranteeApplicationVehicleEntity entity) {

        return GuaranteeApplicationVehicleDTO.builder()
                .id(entity.getId())
                .vehicleName(entity.getVehicleName())
                .vehicleType(entity.getVehicleType())
                .color(entity.getColor())
                .chassisNumber(entity.getChassisNumber())
                .invoiceNumber(entity.getInvoiceNumber())
                .paymentMethod(entity.getPaymentMethod())
                .bankName(entity.getBankName())
                .vehiclePrice(entity.getVehiclePrice())
                .guaranteeAmount(entity.getGuaranteeAmount())
                .build();
    }

    private static GuaranteeApplicationVehicleEntity toVehicleEntity(
            GuaranteeApplicationVehicleDTO dto,
            GuaranteeApplicationEntity parent) {

        GuaranteeApplicationVehicleEntity entity =
                new GuaranteeApplicationVehicleEntity();

        entity.setId(dto.getId());
        entity.setVehicleName(dto.getVehicleName());
        entity.setVehicleType(dto.getVehicleType());
        entity.setColor(dto.getColor());
        entity.setChassisNumber(dto.getChassisNumber());
        entity.setInvoiceNumber(dto.getInvoiceNumber());
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setBankName(dto.getBankName());
        entity.setVehiclePrice(dto.getVehiclePrice());
        entity.setGuaranteeAmount(dto.getGuaranteeAmount());

        entity.setGuaranteeApplication(parent);

        return entity;
    }

    // =====================================================
    // BASIC RELATION MAPPING (IMPORTANT PART)
    // =====================================================

    private static ManufacturerDTO mapManufacturerBasic(ManufacturerEntity entity) {
        if (entity == null) return null;

        ManufacturerDTO m = new ManufacturerDTO();
        m.setId(entity.getId());
        m.setCode(entity.getCode());
        m.setName(entity.getName());
        m.setGuaranteeRate(entity.getGuaranteeRate());
        return m;
    }

    private static CreditContractDTO mapCreditBasic(CreditContractEntity entity) {
        if (entity == null) return null;

        return CreditContractDTO.builder()
                .id(entity.getId())
                .contractNumber(entity.getContractNumber())
                .contractDate(entity.getContractDate())
                .build();
    }

    private static MortgageContractDTO mapMortgageBasic(MortgageContractEntity entity) {
        if (entity == null) return null;

        return MortgageContractDTO.builder()
                .id(entity.getId())
                .contractNumber(entity.getContractNumber())
                .contractDate(entity.getContractDate())
                .build();
    }

    private static CustomerDTO mapCustomerBasic(CustomerEntity entity) {
        if (entity == null) return null;

        return CustomerDTO.builder()
                .id(entity.getId())
                .customerName(entity.getCustomerName())
                .cif(entity.getCif())
                .build();
    }
}
