package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.VehicleExcelDTO;
import com.bidv.asset.vehicle.Repository.VehicleRepository;
import com.bidv.asset.vehicle.Service.VehicleExportService;
import com.bidv.asset.vehicle.Utill.ExcelExportUtil;
import com.bidv.asset.vehicle.entity.GuaranteeLetterEntity;
import com.bidv.asset.vehicle.entity.InvoiceEntity;
import com.bidv.asset.vehicle.entity.VehicleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleExportServiceImplement implements VehicleExportService {
    @Autowired
    VehicleRepository vehicleRepository;
    @Override
    public byte[] exportVehicleExcel() {
        List<VehicleEntity> vehicles = vehicleRepository.findAllForExcel();

        List<VehicleExcelDTO> excelData = new ArrayList<>();

        int stt = 1;
        for (VehicleEntity v : vehicles) {
            VehicleExcelDTO dto = mapToExcelDTO(v);
            dto.setStt(stt++);
            excelData.add(dto);
        }

        return ExcelExportUtil.exportVehicleExcel(excelData);
    }

    /* ================= MAPPING ================= */
    private VehicleExcelDTO mapToExcelDTO(VehicleEntity v) {

        VehicleExcelDTO dto = new VehicleExcelDTO();

        dto.setVehicleName(v.getVehicleName());
        dto.setAssetName(v.getAssetName());
        dto.setStatus(v.getStatus());
        dto.setFundingSource(v.getFundingSource());
        dto.setChassisNumber(v.getChassisNumber());
        dto.setEngineNumber(v.getEngineNumber());
        dto.setModelType(v.getModelType());
        dto.setColor(v.getColor());
        dto.setSeats(v.getSeats());
        dto.setPrice(v.getPrice());

        dto.setImportDate(v.getImportDate());
        dto.setExportDate(v.getExportDate());
        dto.setDocsDeliveryDate(v.getDocsDeliveryDate());

        dto.setOriginalCopy(v.getOriginalCopy());
        dto.setImportDocs(v.getImportDocs());
        dto.setRegistrationOrderNumber(v.getRegistrationOrderNumber());
        dto.setDescription(v.getDescription());

        /* ===== INVOICE ===== */
        if (v.getInvoice() != null) {
            InvoiceEntity i = v.getInvoice();
            dto.setInvoiceNumber(i.getInvoiceNumber());
            dto.setInvoiceDate(i.getInvoiceDate());
            dto.setSellerName(i.getSellerName());
            dto.setSellerTaxCode(i.getSellerTaxCode());
            dto.setBuyerName(i.getBuyerName());
            dto.setBuyerTaxCode(i.getBuyerTaxCode());
            dto.setInvoiceTotalAmount(i.getTotalAmount());
            dto.setVatAmount(i.getVatAmount());
        }

        /* ===== GUARANTEE ===== */
        if (v.getGuaranteeLetter() != null) {
            GuaranteeLetterEntity g = v.getGuaranteeLetter();

            dto.setGuaranteeContractNumber(g.getGuaranteeContractNumber());
            dto.setGuaranteeContractDate(g.getGuaranteeContractDate());
            dto.setGuaranteeNoticeNumber(g.getGuaranteeNoticeNumber());
            dto.setGuaranteeNoticeDate(g.getGuaranteeNoticeDate());
            dto.setReferenceCode(g.getReferenceCode());

            dto.setExpectedGuaranteeAmount(g.getExpectedGuaranteeAmount());
            dto.setTotalGuaranteeAmount(g.getTotalGuaranteeAmount());
            dto.setUsedAmount(g.getUsedAmount());
            dto.setRemainingAmount(g.getRemainingAmount());

            dto.setExpectedVehicleCount(g.getExpectedVehicleCount());
            dto.setImportedVehicleCount(g.getImportedVehicleCount());
            dto.setExportedVehicleCount(g.getExportedVehicleCount());

            dto.setSaleContract(g.getSaleContract());
            dto.setSaleContractAmount(g.getSaleContractAmount());

            dto.setGuaranteeCreatedAt(g.getCreatedAt());

            if (g.getAuthorizedRepresentative() != null) {
                dto.setAuthorizedRepresentativeName(
                        g.getAuthorizedRepresentative().getRepresentativeName()
                );
            }

            if (g.getManufacturer() != null) {
                dto.setManufacturerName(
                        g.getManufacturer().getName()
                );
            }
        }

        dto.setVehicleCreatedAt(v.getCreatedAt());

        return dto;
    }

}
