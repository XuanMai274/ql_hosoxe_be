package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.InvoiceDTO;
import com.bidv.asset.vehicle.entity.InvoiceEntity;
import com.bidv.asset.vehicle.entity.VehicleEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
@Component
public class InvoiceMapper {

    public InvoiceDTO toDto(InvoiceEntity entity) {
        if (entity == null) return null;

        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(entity.getId());
        dto.setInvoiceNumber(entity.getInvoiceNumber());
        dto.setInvoiceDate(entity.getInvoiceDate());
        dto.setSellerName(entity.getSellerName());
        dto.setSellerTaxCode(entity.getSellerTaxCode());
        dto.setBuyerName(entity.getBuyerName());
        dto.setBuyerTaxCode(entity.getBuyerTaxCode());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setVatAmount(entity.getVatAmount());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getVehicles() != null) {
            dto.setVehicleIds(
                    entity.getVehicles()
                            .stream()
                            .map(VehicleEntity::getId)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public InvoiceEntity toEntity(InvoiceDTO dto) {
        if (dto == null) return null;

        InvoiceEntity entity = new InvoiceEntity();
        entity.setId(dto.getId());
        entity.setInvoiceNumber(dto.getInvoiceNumber());
        entity.setInvoiceDate(dto.getInvoiceDate());
        entity.setSellerName(dto.getSellerName());
        entity.setSellerTaxCode(dto.getSellerTaxCode());
        entity.setBuyerName(dto.getBuyerName());
        entity.setBuyerTaxCode(dto.getBuyerTaxCode());
        entity.setTotalAmount(dto.getTotalAmount());
        entity.setVatAmount(dto.getVatAmount());
        entity.setCreatedAt(dto.getCreatedAt());

        return entity;
    }
}
