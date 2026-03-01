package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.GuaranteeApplicationDTO;
import com.bidv.asset.vehicle.DTO.GuaranteeApplicationVehicleDTO;
import com.bidv.asset.vehicle.Repository.GuaranteeApplicationRepository;
import com.bidv.asset.vehicle.Service.GuaranteeApplicationExportService;
import com.bidv.asset.vehicle.Utill.VietnameseNumberUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GuaranteeApplicationExportServiceImplement implements GuaranteeApplicationExportService {

    private final GuaranteeApplicationRepository repository;
    private final com.bidv.asset.vehicle.Mapper.GuaranteeApplicationMapper mapper;

    private static final String TEMPLATE_PATH = "/templates/DeNghiCapBaoLanh/";

    /* ========================================================= */
    /* ================= EXPORT ĐỀ NGHỊ ======================== */
    /* ========================================================= */

    @Override
    public byte[] exportDeNghiCapBaoLanh(Long applicationId) throws Exception {

        GuaranteeApplicationDTO dto = getData(applicationId);
        String template = resolveTemplatePath(dto);

        try (XWPFDocument doc = loadTemplate(TEMPLATE_PATH + template)) {

            Map<String, String> data = buildCommonData(dto);

            replaceAll(doc, data);

            // 🔥 QUAN TRỌNG: file đề nghị KHÔNG có bảng xe
            forceTimesNewRoman(doc);

            return writeDoc(doc);
        }
    }

    /* ========================================================= */
    /* ================= EXPORT DANH SÁCH XE =================== */
    /* ========================================================= */

    @Override
    public byte[] exportDanhSachXeBaoLanh(Long applicationId) throws Exception {

        GuaranteeApplicationDTO dto = getData(applicationId);

        try (XWPFDocument doc =
                     loadTemplate(TEMPLATE_PATH + "danh-sach-xe-de-nghi-cap-bao-lanh.docx")) {

            Map<String, String> data = buildCommonData(dto);

            replaceAll(doc, data);

            // ✅ CHỈ file này mới replace bảng
            replaceVehicleTable(doc, dto.getVehicles());

            forceTimesNewRoman(doc);

            return writeDoc(doc);
        }
    }

    /* ========================================================= */
    /* ================= TEMPLATE SELECT ======================= */
    /* ========================================================= */

    private String resolveTemplatePath(GuaranteeApplicationDTO dto) {

        if (dto.getVehicles() == null) {
            return "de-nghi-cap-bao-lanh.docx";
        }

        for (GuaranteeApplicationVehicleDTO v : dto.getVehicles()) {
            String type = safe(v.getVehicleType()).toLowerCase();

            if (type.contains("vinfast")) {
                return "de-nghi-cap-bao-lanh-vinfast.docx";
            }

            if (type.contains("hyundai")) {
                return "de-nghi-cap-bao-lanh-hyundai.docx";
            }
        }

        return "de-nghi-cap-bao-lanh.docx";
    }

    /* ========================================================= */
    /* ================= BUILD DATA ============================ */
    /* ========================================================= */

    private Map<String, String> buildCommonData(GuaranteeApplicationDTO dto) {

        Map<String, String> map = new HashMap<>();

        map.put("{{HDBDCT}}", safe(dto.getSubGuaranteeContractNumber()));
        map.put("{{CURRENT_DATE}}", formatDate(LocalDate.now()));
        map.put("{{CURRENT_DATE_TITLE}}", formatDate(LocalDate.now()));

        if (dto.getCreditContractDTO() != null) {
            map.put("{{HDTD}}", safe(dto.getCreditContractDTO().getContractNumber()));
            map.put("{{HDTD_DATE}}",
                    formatDate(dto.getCreditContractDTO().getContractDate()));
        }

        if (dto.getMortgageContractDTO() != null) {
            map.put("{{HDBD}}",
                    safe(dto.getMortgageContractDTO().getContractNumber()));
        }

        map.put("{{TONG_XE}}",
                String.valueOf(Optional.ofNullable(dto.getTotalVehicleCount()).orElse(0)));

        map.put("{{TONG}}",
                formatMoney(dto.getTotalGuaranteeAmount()));

        map.put("{{TONG_BL}}",
                formatMoney(dto.getTotalGuaranteeAmount()));

        map.put("{{TONG_TEXT}}",
                VietnameseNumberUtil.toVietnamese(
                        Optional.ofNullable(dto.getTotalGuaranteeAmount())
                                .orElse(BigDecimal.ZERO)));

        map.put("{{expiryDate}}",
                String.valueOf(Optional.ofNullable(dto.getGuaranteeTermDays()).orElse(0)));

        return map;
    }

    /* ========================================================= */
    /* ================= TABLE VEHICLE ========================= */
    /* ========================================================= */

    private void replaceVehicleTable(XWPFDocument doc, List<GuaranteeApplicationVehicleDTO> vehicles) {
        if (vehicles == null || vehicles.isEmpty()) return;

        for (XWPFTable table : doc.getTables()) {
            processTableRecursive(table, vehicles);
        }
    }

    private void processTableRecursive(XWPFTable table, List<GuaranteeApplicationVehicleDTO> vehicles) {

        for (int i = 0; i < table.getRows().size(); i++) {

            XWPFTableRow row = table.getRow(i);

            String rowText = getRowText(row);

            if (rowText.contains("{{stt}}")) {

                int templateIndex = i;

                for (int j = 0; j < vehicles.size(); j++) {

                    GuaranteeApplicationVehicleDTO v = vehicles.get(j);

                    XWPFTableRow newRow =
                            table.insertNewTableRow(templateIndex + j);

                    copyRowSafe(row, newRow);

                    Map<String, String> vData = buildVehicleData(v, j + 1);
                    replaceRowPlaceholders(newRow, vData);
                }

                table.removeRow(templateIndex + vehicles.size());
                break;
            }
        }

        // recursive nested tables
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                for (XWPFTable nested : cell.getTables()) {
                    processTableRecursive(nested, vehicles);
                }
            }
        }
    }

    private void copyRowSafe(XWPFTableRow source, XWPFTableRow target) {

        target.getCtRow().setTrPr(source.getCtRow().getTrPr());

        // tạo đủ cell trước
        for (int i = 0; i < source.getTableCells().size(); i++) {
            target.createCell();
        }

        for (int i = 0; i < source.getTableCells().size(); i++) {

            XWPFTableCell sourceCell = source.getCell(i);
            XWPFTableCell targetCell = target.getCell(i);

            targetCell.getCTTc().setTcPr(sourceCell.getCTTc().getTcPr());

            // clear paragraph mặc định
            targetCell.removeParagraph(0);

            for (XWPFParagraph p : sourceCell.getParagraphs()) {

                XWPFParagraph newP = targetCell.addParagraph();
                newP.getCTP().setPPr(p.getCTP().getPPr());

                for (XWPFRun r : p.getRuns()) {
                    XWPFRun newRun = newP.createRun();
                    newRun.getCTR().setRPr(r.getCTR().getRPr());
                    newRun.setText(r.text());
                }
            }
        }
    }

    private Map<String, String> buildVehicleData(
            GuaranteeApplicationVehicleDTO v, int stt) {

        Map<String, String> map = new HashMap<>();

        map.put("{{stt}}", String.valueOf(stt));
        map.put("{{vehicleType}}", safe(v.getVehicleType()));
        map.put("{{color}}", safe(v.getColor()));
        map.put("{{chassis}}", safe(v.getChassisNumber()));
        map.put("{{invoice}}", safe(v.getInvoiceNumber()));
        map.put("{{price}}", formatMoney(v.getVehiclePrice()));
        map.put("{{guarantee}}", formatMoney(v.getGuaranteeAmount()));

//        // 🔥 FIX template của bạn có {{gate}}
//        map.put("{{gate}}",
//                v.getGuaranteeRate() == null ? "0"
//                        : v.getGuaranteeRate().toString());

        return map;
    }

    /* ========================================================= */
    /* ================= REPLACE CORE ========================== */
    /* ========================================================= */

    private void replaceAll(XWPFDocument doc, Map<String, String> data) {

        for (XWPFParagraph p : doc.getParagraphs()) {
            replaceInParagraph(p, data);
        }

        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                replaceRowPlaceholders(row, data);
            }
        }

        for (XWPFHeader header : doc.getHeaderList()) {
            for (XWPFParagraph p : header.getParagraphs()) {
                replaceInParagraph(p, data);
            }
        }

        for (XWPFFooter footer : doc.getFooterList()) {
            for (XWPFParagraph p : footer.getParagraphs()) {
                replaceInParagraph(p, data);
            }
        }
    }

    private void replaceInParagraph(XWPFParagraph paragraph,
                                    Map<String, String> data) {

        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) return;

        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : runs) {
            String text = run.getText(0);
            if (text != null) fullText.append(text);
        }

        String replaced = fullText.toString();
        boolean modified = false;

        for (Map.Entry<String, String> e : data.entrySet()) {
            if (replaced.contains(e.getKey())) {
                replaced = replaced.replace(e.getKey(), e.getValue());
                modified = true;
            }
        }

        if (!modified) return;

        for (int i = runs.size() - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }

        paragraph.createRun().setText(replaced);
    }

    private void replaceRowPlaceholders(XWPFTableRow row, Map<String, String> data) {
        for (XWPFTableCell cell : row.getTableCells()) {
            for (XWPFParagraph p : cell.getParagraphs()) {
                replaceInParagraph(p, data);
            }
            for (XWPFTable table : cell.getTables()) {
                for (XWPFTableRow r : table.getRows()) {
                    replaceRowPlaceholders(r, data);
                }
            }
        }
    }

    /* ========================================================= */

    private GuaranteeApplicationDTO getData(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));
    }

    private XWPFDocument loadTemplate(String path) throws IOException {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) throw new IOException("Không tìm thấy template: " + path);
        return new XWPFDocument(is);
    }

    private byte[] writeDoc(XWPFDocument doc) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            doc.write(out);
            return out.toByteArray();
        }
    }

    private void forceTimesNewRoman(XWPFDocument doc) {
        for (XWPFParagraph p : doc.getParagraphs()) {
            for (XWPFRun r : p.getRuns()) {
                r.setFontFamily("Times New Roman");
            }
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "0";
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(value);
    }

    private String formatDate(LocalDate date) {
        if (date == null) return "";
        return String.format("%02d/%02d/%d",
                date.getDayOfMonth(),
                date.getMonthValue(),
                date.getYear());
    }
    private String getRowText(XWPFTableRow row) {
        StringBuilder sb = new StringBuilder();

        for (XWPFTableCell cell : row.getTableCells()) {
            sb.append(cell.getText());
        }

        return sb.toString();
    }
}