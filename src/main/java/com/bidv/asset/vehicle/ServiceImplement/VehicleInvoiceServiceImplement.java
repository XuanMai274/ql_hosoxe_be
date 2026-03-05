package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.Utill.MoneyUtil;

import com.bidv.asset.vehicle.DTO.*;
import com.bidv.asset.vehicle.Mapper.InvoiceMapper;
import com.bidv.asset.vehicle.Repository.*;
import com.bidv.asset.vehicle.Service.GuaranteeLetterService;
import com.bidv.asset.vehicle.Service.OcrClient;
import com.bidv.asset.vehicle.Service.VehicleInvoiceService;
import com.bidv.asset.vehicle.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

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
    InvoiceMapper invoiceMapper;
    @Autowired
    OcrClient ocrClient;
    @Autowired
    ManufacturerRepository manufacturerRepository;
    @Override
    @Transactional
    public List<VehicleDTO> createInvoiceWithVehicles(CreateInvoiceVehicleRequest request) {
        System.out.println("Đang tạo hóa đơn số: " + request.getInvoice().getInvoiceNumber() + " với số lượng xe là: "
                + request.getVehicles().size());
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

            Optional<VehicleEntity> existing = vehicleRepository.findByChassisNumber(v.getChassisNumber());
            VehicleEntity vehicle = existing.orElse(new VehicleEntity());
            boolean isNew = existing.isEmpty();

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
//            Gắn loại xe
            /* GẮN HÓA ĐƠN */
            ManufacturerEntity manufacturerEntity=manufacturerRepository.findByIdManu(v.getManufacturerDTO().getId());
            vehicle.setManufacturerEntity(manufacturerEntity);
            vehicle.setInvoice(invoice);
            vehicle.setImportDossier(
                    "(1) Phiếu kiểm tra chất lượng xuất xưởng (01 bản gốc + 02 bản sao y bản chính),SK: "
                            + v.getChassisNumber()
                            + ",Giấy chứng nhận an toàn kỹ thuật và bảo vệ môi trường ô tô sản xuất, lắp ráp (Sao y bản chính), (3) Hóa đơn VAT số: "
                            + vehicle.getInvoice().getInvoiceNumber() + " ngày: "
                            + vehicle.getInvoice().getInvoiceDate());

            /* GẮN THƯ BẢO LÃNH */
            GuaranteeLetterEntity gl = guaranteeLetterRepository
                    .findById(v.getGuaranteeLetterDTO().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thư bảo lãnh"));

            vehicle.setGuaranteeLetter(gl);
            /* ===== TÍNH guaranteeAmount ===== */
            BigDecimal rate = MoneyUtil.rate(gl.getManufacturer().getGuaranteeRate());
 
            BigDecimal guaranteeAmount = MoneyUtil.format(vehicle.getPrice().multiply(rate));
            vehicle.setGuaranteeAmount(guaranteeAmount);
            vehicle = vehicleRepository.save(vehicle);
            /* ================== 3. UPDATE GUARANTEE LETTER ================== */
            guaranteeLetterService.updateAfterVehicleImported(gl.getId(), guaranteeAmount);
            /* ================== 3. GẮN DOCUMENTS (NẾU CÓ) ================== */
            /* ================== GẮN DOCUMENTS ================== */
            if (v.getDocuments() != null && !v.getDocuments().isEmpty()) {

                List<Long> docIds = v.getDocuments()
                        .stream()
                        .map(DocumentDTO::getId)
                        .toList();

                List<DocumentEntity> docs = (List<DocumentEntity>) documentRepository.findAllById(docIds);

                for (DocumentEntity doc : docs) {
                    doc.setVehicle(vehicle);
                }

                documentRepository.saveAll(docs);
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

    @Override
    public List<InvoiceDTO> findAll() {
        List<InvoiceDTO> invoiceDTOs = new ArrayList<>();
        List<InvoiceEntity> invoiceEntities = (List<InvoiceEntity>) invoiceRepository.findAll();
        for (InvoiceEntity invoice : invoiceEntities) {
            invoiceDTOs.add(invoiceMapper.toDto(invoice));
        }
        return invoiceDTOs;
    }

    public List<VehicleDTO> extractAndValidate(MultipartFile file) {

        OcrResponseDTO ocr = ocrClient.extract(file);

        return mapAndValidateVehicles(ocr);
    }

}
