package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.GuaranteeApplicationVehicleDTO;
import com.bidv.asset.vehicle.entity.GuaranteeApplicationVehicleEntity;

import java.util.List;
import java.util.stream.Collectors;

public class GuaranteeApplicationVehicleMapper {
    // ==========================================
    // ENTITY → DTO
    // ==========================================
    public static GuaranteeApplicationVehicleDTO toDTO(
            GuaranteeApplicationVehicleEntity entity) {

        if (entity == null) {
            return null;
        }

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

    // ==========================================
    // ENTITY LIST → DTO LIST
    // ==========================================
    public static List<GuaranteeApplicationVehicleDTO> toDTOList(
            List<GuaranteeApplicationVehicleEntity> entities) {

        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(GuaranteeApplicationVehicleMapper::toDTO)
                .collect(Collectors.toList());
    }
}
