package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.GuaranteeLetterDTO;
import com.bidv.asset.vehicle.DTO.InvoiceDTO;
import com.bidv.asset.vehicle.DTO.VehicleDTO;
import com.bidv.asset.vehicle.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VehicleMapper {

    /* ===================== ENTITY → DTO ===================== */
    public VehicleDTO toDto(VehicleEntity entity) {
        if (entity == null) return null;

        VehicleDTO dto = new VehicleDTO();

        dto.setId(entity.getId());
        dto.setStt(entity.getStt());
        dto.setVehicleName(entity.getVehicleName());
        dto.setStatus(entity.getStatus());
        dto.setFundingSource(entity.getFundingSource());

        dto.setImportDate(entity.getImportDate());
        dto.setExportDate(entity.getExportDate());

        dto.setAssetName(entity.getAssetName());
        dto.setChassisNumber(entity.getChassisNumber());
        dto.setEngineNumber(entity.getEngineNumber());
        dto.setModelType(entity.getModelType());
        dto.setColor(entity.getColor());
        dto.setSeats(entity.getSeats());
        dto.setPrice(entity.getPrice());

        dto.setOriginalCopy(entity.getOriginalCopy());
        dto.setImportDocs(entity.getImportDocs());
        dto.setRegistrationOrderNumber(entity.getRegistrationOrderNumber());
        dto.setDocsDeliveryDate(entity.getDocsDeliveryDate());

        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());

        /* ===== INVOICE ===== */
        if (entity.getInvoice() != null) {
            InvoiceDTO invoiceDTO = new InvoiceDTO();
            invoiceDTO.setId(entity.getInvoice().getId());
            dto.setInvoiceId(invoiceDTO);
        }

        /* ===== DOSSIERS ===== */
        if (entity.getDossiers() != null) {
            dto.setDossierIds(
                    entity.getDossiers()
                            .stream()
                            .map(VehicleDossierEntity::getId)
                            .collect(Collectors.toList())
            );
        }

        /* ===== DOCUMENTS ===== */
        if (entity.getDocuments() != null) {
            dto.setDocumentIds(
                    entity.getDocuments()
                            .stream()
                            .map(DocumentEntity::getId)
                            .collect(Collectors.toList())
            );
        }

        /* ===== GUARANTEE LETTER ===== */
        if (entity.getGuaranteeLetter() != null) {
            GuaranteeLetterDTO guaranteeDTO = new GuaranteeLetterDTO();
            guaranteeDTO.setId(entity.getGuaranteeLetter().getId());
            guaranteeDTO.setGuaranteeContractNumber(
                    entity.getGuaranteeLetter().getGuaranteeContractNumber()
            );
            dto.setGuaranteeLetterDTO(guaranteeDTO);
        }

        return dto;
    }

    /* ===================== DTO → ENTITY ===================== */
    public VehicleEntity toEntity(VehicleDTO dto) {
        if (dto == null) return null;

        VehicleEntity entity = new VehicleEntity();

        entity.setId(dto.getId());
        entity.setStt(dto.getStt());
        entity.setVehicleName(dto.getVehicleName());
        entity.setStatus(dto.getStatus());
        entity.setFundingSource(dto.getFundingSource());

        entity.setImportDate(dto.getImportDate());
        entity.setExportDate(dto.getExportDate());

        entity.setAssetName(dto.getAssetName());
        entity.setChassisNumber(dto.getChassisNumber());
        entity.setEngineNumber(dto.getEngineNumber());
        entity.setModelType(dto.getModelType());
        entity.setColor(dto.getColor());
        entity.setSeats(dto.getSeats());
        entity.setPrice(dto.getPrice());

        entity.setOriginalCopy(dto.getOriginalCopy());
        entity.setImportDocs(dto.getImportDocs());
        entity.setRegistrationOrderNumber(dto.getRegistrationOrderNumber());
        entity.setDocsDeliveryDate(dto.getDocsDeliveryDate());

        entity.setDescription(dto.getDescription());
        entity.setCreatedAt(dto.getCreatedAt());

        /* ===== INVOICE (ID ONLY) ===== */
        if (dto.getInvoiceId() != null && dto.getInvoiceId().getId() != null) {
            InvoiceEntity invoice = new InvoiceEntity();
            invoice.setId(dto.getInvoiceId().getId());
            entity.setInvoice(invoice);
        }

        /* ===== GUARANTEE LETTER (ID ONLY – REQUIRED) ===== */
        if (dto.getGuaranteeLetterDTO() != null && dto.getGuaranteeLetterDTO().getId() != null) {
            GuaranteeLetterEntity guaranteeLetter = new GuaranteeLetterEntity();
            guaranteeLetter.setId(dto.getGuaranteeLetterDTO().getId());
            entity.setGuaranteeLetter(guaranteeLetter);
        }

        // dossiers & documents: KHÔNG map ở đây (xử lý ở service)

        return entity;
    }
}
