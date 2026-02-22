package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.VehicleDTO;
import com.bidv.asset.vehicle.DTO.VehicleExcelDTO;
import com.bidv.asset.vehicle.Repository.VehicleRepository;
import com.bidv.asset.vehicle.Service.VehicleExportService;
import com.bidv.asset.vehicle.Utill.ExcelExportUtil;
import com.bidv.asset.vehicle.Utill.VietnameseNumberUtil;
import com.bidv.asset.vehicle.entity.GuaranteeLetterEntity;
import com.bidv.asset.vehicle.entity.InvoiceEntity;
import com.bidv.asset.vehicle.entity.VehicleEntity;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class VehicleExportServiceImplement implements VehicleExportService {

    @Autowired
    VehicleRepository vehicleRepository;

    /* ========================================================= */
    /* ======================= EXCEL ============================ */
    /* ========================================================= */

    @Override
    public byte[] exportVehicleExcel(
            String chassisNumber,
            String status,
            String manufacturer,
            String ref
    ) {

        List<VehicleEntity> vehicles =
                vehicleRepository.searchVehiclesForExcel(
                        chassisNumber,
                        status,
                        manufacturer,
                        ref
                );

        List<VehicleExcelDTO> excelData = new ArrayList<>();
        int stt = 1;

        for (VehicleEntity v : vehicles) {
            VehicleExcelDTO dto = mapToExcelDTO(v);
            dto.setStt(stt++);
            excelData.add(dto);
        }

        return ExcelExportUtil.exportVehicleExcel(excelData);
    }

    /* ========================================================= */
    /* ======================= PNK ============================== */
    /* ========================================================= */

    @Override
    public byte[] generatePNK(List<VehicleDTO> vehicles) throws IOException {

        XWPFDocument doc = loadTemplate("/templates/NhapKho/PNK.docx");

        BigDecimal total = BigDecimal.ZERO;

        for (VehicleDTO v : vehicles) {
            if (v.getPrice() != null) {
                total = total.add(v.getPrice());
            }
        }

        replaceVehicleTable(doc, vehicles);
        // HEADER
        Map<String,String> header = buildHeaderData(vehicles);

        header.put("{{TONG}}", formatMoney(total));
        header.put("{{TONG_TEXT}}",
                VietnameseNumberUtil.toVietnamese(total));
        header.put("{{CURRENT _DATE}}", formatDate(LocalDate.now()));
        header.put("{{CURRENT _DATE_TITLE}}", formatDateTitle(LocalDate.now()));

        replaceAll(doc, header);

        return writeDoc(doc);
    }

    /* ========================================================= */
    /* =================== BÁO CÁO ĐỊNH GIÁ ===================== */
    /* ========================================================= */

    public byte[] generateBaoCaoDinhGia(List<VehicleDTO> vehicles) throws IOException {

        XWPFDocument doc = loadTemplate("/templates/NhapKho/bao-cao-dinh-gia-tai-san.docx");

        BigDecimal total = calculateTotal(vehicles);

        replaceVehicleTable(doc, vehicles);
        // HEADER
        Map<String,String> header = buildHeaderData(vehicles);
        Map<String,String> map = new HashMap<>();
        map.put("{{TONG}}", formatMoney(total));
        map.put("{{TONG_TEXT}}",
                VietnameseNumberUtil.toVietnamese(total));
        map.put("{{TONG_80%}}", formatMoney(total.multiply(BigDecimal.valueOf(0.8))));

        replaceAll(doc,map);

        return writeDoc(doc);
    }

    /* ========================================================= */
    /* ================= BIÊN BẢN ĐỊNH GIÁ ====================== */
    /* ========================================================= */

    public byte[] generateBienBanDinhGia(List<VehicleDTO> vehicles) throws IOException {

        XWPFDocument doc = loadTemplate("/templates/NhapKho/bien-ban-dinh-gia-NK.docx");

        BigDecimal total = calculateTotal(vehicles);

        replaceVehicleTable(doc, vehicles);
        // HEADER
        Map<String,String> header = buildHeaderData(vehicles);
        Map<String,String> map = new HashMap<>();

        map.put("{{CURRENT_DATE}}", formatDate(LocalDate.now()));
        map.put("{{TONG}}", formatMoney(total));
        map.put("{{TONG_TEXT}}",
                VietnameseNumberUtil.toVietnamese(total));

        replaceAll(doc,map);

        return writeDoc(doc);
    }

    /* ========================================================= */
    /* ================= PHỤ LỤC HĐ HYUNDAI ===================== */
    /* ========================================================= */

    public byte[] generatePhuLucHyundai(List<VehicleDTO> vehicles) throws IOException {

        XWPFDocument doc = loadTemplate("/templates/NhapKho/phu-luc-hop-dong-thue-chap-hyndai.docx");

        BigDecimal total = calculateTotal(vehicles);

        replaceVehicleTable(doc, vehicles);
        // HEADER
        Map<String,String> header = buildHeaderData(vehicles);
        Map<String,String> map = new HashMap<>();
        map.put("{{TONG}}", formatMoney(total));
        map.put("{{TONG_TEXT}}",
                VietnameseNumberUtil.toVietnamese(total));
        replaceAll(doc,map);

        return writeDoc(doc);
    }

    /* ========================================================= */
    /* ================= PHỤ LỤC HĐ VINFAST ===================== */
    /* ========================================================= */

    public byte[] generatePhuLucVinfast(List<VehicleDTO> vehicles) throws IOException {

        XWPFDocument doc = loadTemplate("/templates/NhapKho/phu-luc-hop-dong-thue-chap-vinfast.docx");

        BigDecimal total = calculateTotal(vehicles);

        replaceVehicleTable(doc, vehicles);
        // HEADER
        Map<String,String> header = buildHeaderData(vehicles);
        Map<String,String> map = new HashMap<>();
        map.put("{{TONG}}", formatMoney(total));
        map.put("{{TONG_TEXT}}",
                VietnameseNumberUtil.toVietnamese(total));
        map.put("{{CURRENT_DATE_TITLE}}", formatDateTitle(LocalDate.now()));

        replaceAll(doc,map);

        return writeDoc(doc);
    }

    /* ========================================================= */
    /* ================= TABLE REPLACEMENT ====================== */
    /* ========================================================= */

    @Override
    public void replaceVehicleTable(XWPFDocument doc, List<VehicleDTO> vehicles) {

        for (XWPFTable table : doc.getTables()) {

            boolean found = false; // ✅ đặt ở đây

            for (int i = 0; i < table.getRows().size(); i++) {

                XWPFTableRow templateRow = table.getRow(i);

                String firstCell = templateRow.getCell(0).getText();

                if (firstCell != null &&
                        firstCell.toLowerCase().contains("{{stt}}")) {

                    int templateIndex = i;

                    for (int j = 0; j < vehicles.size(); j++) {

                        VehicleDTO v = vehicles.get(j);

                        XWPFTableRow newRow =
                                table.insertNewTableRow(templateIndex + j);

                        copyRow(templateRow, newRow);

                        Map<String,String> data =
                                buildVehicleData(v, j + 1);

                        replaceRowPlaceholders(newRow, data);
                    }

                    table.removeRow(templateIndex + vehicles.size());

                    found = true;   // ✅ đánh dấu đã xử lý bảng
                }

                if (found) break; // ✅ thoát vòng row
            }
        }
    }

    /* ========================================================= */
    /* ================= VEHICLE PLACEHOLDER ==================== */
    /* ========================================================= */

    private Map<String,String> buildVehicleData(VehicleDTO v, int stt) {

        Map<String,String> map = new HashMap<>();

        map.put("{{stt}}", String.valueOf(stt));
        map.put("{{description}}", safe(v.getDescription()));
        map.put("{{vehicle_info}}", safe(v.getDescription()));
        map.put("{{chassis}}", safe(v.getChassisNumber()));
        map.put("{{engine}}", safe(v.getEngineNumber()));
        map.put("{{model_type}}", safe(v.getModelType()));
        map.put("{{color}}", safe(v.getColor()));
        map.put("{{seats}}", v.getSeats() == null ? "" : v.getSeats().toString());
        map.put("{{price}}", formatMoney(v.getPrice()));
        map.put("{{importDossier}}", safe(v.getImportDossier()));
//        if (v.getGuaranteeLetterDTO()!=null &&
//                v.getGuaranteeLetterDTO().getManufacturerDTO()!=null) {
//
//            map.put("{{manufacturer}}",
//                    safe(v.getGuaranteeLetterDTO()
//                            .getManufacturerDTO()
//                            .getName()));
//        }

        if (v.getGuaranteeLetterDTO() != null) {
            map.put("{{HDBD}}",
                    safe(v.getGuaranteeLetterDTO().getGuaranteeContractNumber()));

            map.put("{{HDBD_DATE}}",
                    formatDate(v.getGuaranteeLetterDTO().getGuaranteeContractDate()));
        }

        return map;
    }
    private Map<String,String> buildHeaderData(List<VehicleDTO> vehicles) {

        Map<String,String> map = new HashMap<>();

        if (vehicles == null || vehicles.isEmpty()) return map;

        VehicleDTO first = vehicles.get(0);

        if (first.getGuaranteeLetterDTO() != null) {

            map.put("{{HDBD}}",
                    safe(first.getGuaranteeLetterDTO()
                            .getGuaranteeContractNumber()));

            map.put("{{HDBD_DATE}}",
                    formatDate(first.getGuaranteeLetterDTO()
                            .getGuaranteeContractDate()));
        }

        return map;
    }

    /* ========================================================= */
    /* ================= HELPER ================================ */
    /* ========================================================= */

    private BigDecimal calculateTotal(List<VehicleDTO> vehicles) {

        BigDecimal total = BigDecimal.ZERO;

        for (VehicleDTO v : vehicles) {
            if (v.getPrice() != null) {
                total = total.add(v.getPrice());
            }
        }

        return total;
    }

    private void replaceAll(XWPFDocument doc, Map<String,String> data) {

        for (XWPFParagraph p : doc.getParagraphs()) {
            replaceInParagraph(p, data);
        }

        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                replaceRowPlaceholders(row,data);
            }
        }
    }

    private void copyRow(XWPFTableRow source, XWPFTableRow target) {

        target.getCtRow().setTrPr(source.getCtRow().getTrPr());

        for (XWPFTableCell cell : source.getTableCells()) {

            XWPFTableCell newCell = target.addNewTableCell();

            newCell.getCTTc().setTcPr(cell.getCTTc().getTcPr());

            newCell.setText(cell.getText());
        }
    }

    private void replaceRowPlaceholders(XWPFTableRow row,
                                        Map<String,String> data) {

        for (XWPFTableCell cell : row.getTableCells()) {
            for (XWPFParagraph p : cell.getParagraphs()) {
                replaceInParagraph(p,data);
            }
        }
    }

    private void replaceInParagraph(XWPFParagraph paragraph,
                                    Map<String,String> data) {

        String text = paragraph.getText();
        if (text == null) return;

        String replaced = text;

        for (Map.Entry<String,String> e : data.entrySet()) {
            replaced = replaced.replace(e.getKey(), e.getValue());
        }

        if (!replaced.equals(text)) {

            int runCount = paragraph.getRuns().size();

            for (int i = runCount - 1; i >= 0; i--) {
                paragraph.removeRun(i);
            }

            paragraph.createRun().setText(replaced);
        }
    }


    private XWPFDocument loadTemplate(String path) throws IOException {

        var is = getClass().getResourceAsStream(path);

        if (is == null) {
            throw new IOException("Không tìm thấy template: " + path);
        }

        return new XWPFDocument(is);
    }

    private byte[] writeDoc(XWPFDocument doc) throws IOException {

        try (var out = new java.io.ByteArrayOutputStream()) {
            doc.write(out);
            return out.toByteArray();
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String formatMoney(BigDecimal value) {

        if (value == null) return "";

        return NumberFormat
                .getInstance(new Locale("vi","VN"))
                .format(value);
    }

    private String formatDate(LocalDate date) {

        if (date == null) return "";

        return String.format("%02d/%02d/%d",
                date.getDayOfMonth(),
                date.getMonthValue(),
                date.getYear());
    }

    private String formatDateTitle(LocalDate date) {

        if (date == null) return "";

        return "ngày " + date.getDayOfMonth()
                + " tháng " + date.getMonthValue()
                + " năm " + date.getYear();
    }

    /* ========================================================= */
    /* ================= MAPPING ================================ */
    /* ========================================================= */

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
        dto.setImportDocs(v.getImportDocs());
        dto.setImportDate(v.getImportDate());
        dto.setExportDate(v.getExportDate());
        dto.setDocsDeliveryDate(v.getDocsDeliveryDate());
        dto.setImportDossier(v.getImportDossier());
        dto.setOriginalCopy(v.getOriginalCopy());
        dto.setRegistrationOrderNumber(v.getRegistrationOrderNumber());
        dto.setDescription(v.getDescription());

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
        }

        dto.setVehicleCreatedAt(v.getCreatedAt());

        return dto;
    }
}
