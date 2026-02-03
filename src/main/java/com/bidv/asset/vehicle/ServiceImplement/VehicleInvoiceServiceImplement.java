package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.CreateInvoiceVehicleRequest;
import com.bidv.asset.vehicle.DTO.InvoiceDTO;
import com.bidv.asset.vehicle.DTO.OcrResponseDTO;
import com.bidv.asset.vehicle.DTO.VehicleDTO;
import com.bidv.asset.vehicle.Repository.DocumentRepository;
import com.bidv.asset.vehicle.Repository.GuaranteeLetterRepository;
import com.bidv.asset.vehicle.Repository.InvoiceRepository;
import com.bidv.asset.vehicle.Repository.VehicleRepository;
import com.bidv.asset.vehicle.Service.GuaranteeLetterService;
import com.bidv.asset.vehicle.Service.OcrClient;
import com.bidv.asset.vehicle.Service.VehicleInvoiceService;
import com.bidv.asset.vehicle.entity.DocumentEntity;
import com.bidv.asset.vehicle.entity.GuaranteeLetterEntity;
import com.bidv.asset.vehicle.entity.InvoiceEntity;
import com.bidv.asset.vehicle.entity.VehicleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.bidv.asset.vehicle.Mapper.VehicleOcrMapper.mapAndValidateVehicles;

@Service
public class VehicleInvoiceServiceImplement implements VehicleInvoiceService {
    @Autowired
    DocumentRepository documentRepository;
    @Autowired
    VehicleRepository vehicleRepository;
    @Autowired
    InvoiceRepository invoiceRepository;
    @Autowired
    GuaranteeLetterRepository guaranteeLetterRepository;
    @Autowired
    GuaranteeLetterService guaranteeLetterService;
    @Autowired
    OcrClient ocrClient;
    @Override
    @Transactional
    public List<VehicleDTO> createInvoiceWithVehicles(CreateInvoiceVehicleRequest request) {

        /* ================== 1. TẠO HÓA ĐƠN ================== */
        InvoiceEntity invoice = new InvoiceEntity();
        InvoiceDTO inv = request.getInvoice();

        invoice.setInvoiceNumber(inv.getInvoiceNumber());
        invoice.setInvoiceDate(inv.getInvoiceDate());
        invoice.setTotalAmount(inv.getTotalAmount());
        invoice.setCreatedAt(LocalDateTime.now());

        invoice = invoiceRepository.save(invoice);

        /* DANH SÁCH XE TRẢ VỀ */
        List<VehicleDTO> createdVehicles = new ArrayList<>();

        /* ================== 2. TẠO XE ================== */
        for (VehicleDTO v : request.getVehicles()) {

            if (vehicleRepository.existsByChassisNumber(v.getChassisNumber())) {
                throw new RuntimeException("Trùng số khung: " + v.getChassisNumber());
            }

            VehicleEntity vehicle = new VehicleEntity();
            vehicle.setStt(v.getStt());
            vehicle.setVehicleName(v.getVehicleName());
            vehicle.setStatus(v.getStatus());
            vehicle.setFundingSource(v.getFundingSource());
            vehicle.setImportDate(v.getImportDate());
            vehicle.setExportDate(v.getExportDate());
            vehicle.setAssetName(v.getVehicleName());
            vehicle.setChassisNumber(v.getChassisNumber());
            vehicle.setEngineNumber(v.getEngineNumber());
            vehicle.setModelType(v.getModelType());
            vehicle.setColor(v.getColor());
            vehicle.setSeats(v.getSeats());
            vehicle.setPrice(v.getPrice());
            vehicle.setOriginalCopy(v.getOriginalCopy());
            vehicle.setImportDocs(v.getImportDocs());
            vehicle.setRegistrationOrderNumber(v.getRegistrationOrderNumber());
            vehicle.setDocsDeliveryDate(v.getDocsDeliveryDate());
            vehicle.setDescription(v.getDescription());
            vehicle.setCreatedAt(LocalDateTime.now());

            /* GẮN HÓA ĐƠN */
            vehicle.setInvoice(invoice);

            /* GẮN THƯ BẢO LÃNH */
            GuaranteeLetterEntity gl = guaranteeLetterRepository
                    .findById(v.getGuaranteeLetterDTO().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thư bảo lãnh"));

            vehicle.setGuaranteeLetter(gl);

            vehicle = vehicleRepository.save(vehicle);
            /* ================== 3. UPDATE GUARANTEE LETTER ================== */
            guaranteeLetterService.updateAfterVehicleImported(gl, vehicle);
            /* ================== 3. GẮN DOCUMENTS (NẾU CÓ) ================== */
            if (v.getDocumentIds() != null) {
                for (Long docId : v.getDocumentIds()) {
                    DocumentEntity doc = documentRepository.findById(docId)
                            .orElseThrow(() -> new RuntimeException("Document không tồn tại"));
                    doc.setVehicle(vehicle);
                    documentRepository.save(doc);
                }
            }

            /* ================== 4. MAP ENTITY → DTO ================== */
            VehicleDTO dto = new VehicleDTO();
            dto.setId(vehicle.getId());
            dto.setStt(vehicle.getStt());
            dto.setVehicleName(vehicle.getVehicleName());
            dto.setChassisNumber(vehicle.getChassisNumber());
            dto.setStatus(vehicle.getStatus());

            createdVehicles.add(dto);
        }

        return createdVehicles;
    }
    public List<VehicleDTO> extractAndValidate(MultipartFile file) {

        OcrResponseDTO ocr = ocrClient.extract(file);

        return mapAndValidateVehicles(ocr);
    }
}
