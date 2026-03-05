package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.GuaranteeApplicationDTO;
import com.bidv.asset.vehicle.DTO.GuaranteeApplicationVehicleDTO;
import com.bidv.asset.vehicle.Service.GuaranteeApplicationExportService;
import com.bidv.asset.vehicle.Utill.VietnameseNumberUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class GuaranteeApplicationExportServiceImplement
        implements GuaranteeApplicationExportService {

    @Value("${app.template-root}")
    private String templateRoot;
    // =====================================================
    // ================= MAIN EXPORT =======================
    // =====================================================
    @Override
    public Map<String, byte[]> exportAll(GuaranteeApplicationDTO dto)
            throws IOException {

        Map<String, byte[]> results = new LinkedHashMap<>();


        // chọn template theo hãng
        String manufacturer = Optional.ofNullable(dto.getManufacturerDTO())
                .map(m -> m.getCode())
                .orElse("");
        if (manufacturer.contains("HYUNDAI")) {
            results.put(
                    "de-nghi-cap-bao-lanh-hyundai.docx",
                    exportCommon(dto, "de-nghi-cap-bao-lanh-hyundai.docx")
            );
        } else if(manufacturer.contains("VINFAST")) {
            results.put(
                    "de-nghi-cap-bao-lanh-vinfast.docx",
                    exportCommon(dto, "de-nghi-cap-bao-lanh-vinfast.docx")
            );
        }
        BigDecimal gate = calculateGate(manufacturer);
        // luôn có danh sách xe
        results.put(
                "danh-sach-xe-de-nghi-cap-bao-lanh.docx",
                exportVehicleList(dto,gate)
        );
        return results;
    }
    private BigDecimal calculateGate(String manufacturer) {

        if (manufacturer == null) {
            return BigDecimal.ZERO;
        }

        String code = manufacturer.trim().toUpperCase();

        switch (code) {
            case "HYUNDAI":
                return BigDecimal.valueOf(85);
            case "VINFAST":
                return BigDecimal.valueOf(75);
            default:
                return BigDecimal.ZERO;
        }
    }
    // =====================================================
    // =============== EXPORT COMMON =======================
    // =====================================================
    private byte[] exportCommon(
            GuaranteeApplicationDTO dto,
            String templateName
    ) throws IOException {

        XWPFDocument doc = loadTemplate("DeNghiCapBaoLanh/" + templateName);

        Map<String, String> data = buildCommonData(dto);

        replaceAll(doc, data);

        forceTimesNewRoman(doc);

        return writeDoc(doc);
    }

    // =====================================================
    // ============ EXPORT VEHICLE LIST ====================
    // =====================================================
    private byte[] exportVehicleList(GuaranteeApplicationDTO dto, BigDecimal gate)
            throws IOException {

        XWPFDocument doc = loadTemplate(
                "DeNghiCapBaoLanh/danh-sach-xe-de-nghi-cap-bao-lanh.docx"
        );

        Map<String, String> common = buildCommonData(dto);

        replaceAll(doc, common);

        replaceVehicleTable(doc,
                Optional.ofNullable(dto.getVehicles())
                        .orElse(Collections.emptyList()),gate);

        forceTimesNewRoman(doc);

        return writeDoc(doc);
    }

    // =====================================================
    // ================= BUILD COMMON ======================
    // =====================================================
    private Map<String, String> buildCommonData(GuaranteeApplicationDTO dto) {

        Map<String, String> map = new HashMap<>();


        map.put("{{CURRENT_DATE}}", formatDate(LocalDate.now()));
        map.put("{{CURRENT_DATE_TITLE}}", toVietnameseDate(LocalDate.now()));
        map.put("{{HDBDCT}}", safe(dto.getSubGuaranteeContractNumber()));
        if (dto.getCreditContractDTO() != null) {
            map.put("{{HDTD}}",
                    safe(dto.getCreditContractDTO().getContractNumber()));
            map.put("{{HDTD_DATE}}",
                    formatDate(dto.getCreditContractDTO().getContractDate()));
        }

        if (dto.getMortgageContractDTO() != null) {
            map.put("{{HDBD}}",
                    safe(dto.getMortgageContractDTO().getContractNumber()));
            map.put("{{HDBD_DATE}}",
                    formatDate(dto.getMortgageContractDTO().getContractDate()));
        }

        map.put("{{TONG_XE}}",
                String.valueOf(Optional.ofNullable(dto.getTotalVehicleCount()).orElse(0)));

        map.put("{{TONG}}", formatMoney(dto.getTotalGuaranteeAmount()));
        map.put("{{TONG_BL}}", formatMoney(dto.getTotalGuaranteeAmount()));
        map.put("{{TONGHDMB}}",formatMoney(dto.getTotalVehicleAmount()));
        map.put("{{TONG_TEXT}}",
                VietnameseNumberUtil.toVietnamese(
                        Optional.ofNullable(dto.getTotalGuaranteeAmount())
                                .orElse(BigDecimal.ZERO)));

        map.put("{{expiryDate}}",
                String.valueOf(Optional.ofNullable(dto.getGuaranteeTermDays()).orElse(0)));

        return map;
    }

    // =====================================================
    // ================= BUILD VEHICLE =====================
    // =====================================================
    private Map<String, String> buildVehicleData(
            GuaranteeApplicationVehicleDTO v,
            int stt,
            BigDecimal gate
    ) {

        Map<String, String> map = new HashMap<>();

        map.put("{{stt}}", String.valueOf(stt));
        map.put("{{vehicleType}}", safe(v.getVehicleType()));
        map.put("{{color}}", safe(v.getColor()));
        map.put("{{chassis}}", safe(v.getChassisNumber()));
        map.put("{{invoice}}", safe(v.getInvoiceNumber()));
        map.put("{{price}}", formatMoney(v.getVehiclePrice()));
        map.put("{{guarantee}}", formatMoney(v.getGuaranteeAmount()));
        map.put("{{gate}}", gate.toPlainString() + "%");
//        map.put("{{gate}}",
//                v.get() == null
//                        ? "0"
//                        : v.getGuaranteeRate().toPlainString());

        return map;
    }

    // =====================================================
    // ================= VEHICLE TABLE =====================
    // =====================================================
    private void replaceVehicleTable(
            XWPFDocument doc,
            List<GuaranteeApplicationVehicleDTO> vehicles,BigDecimal gate
    ) {
        for (XWPFTable table : doc.getTables()) {
            processTableRecursive(table, vehicles,gate);
        }
    }

    private void processTableRecursive(
            XWPFTable table,
            List<GuaranteeApplicationVehicleDTO> vehicles,
            BigDecimal gate
    ) {
        for (int i = 0; i < table.getRows().size(); i++) {
            XWPFTableRow row = table.getRow(i);
            if (row.getCell(0) == null) continue;

            String firstCell = row.getCell(0).getText();

            if (firstCell != null && firstCell.contains("{{stt}}")) {

                int templateIndex = i;

                for (int j = 0; j < vehicles.size(); j++) {
                    GuaranteeApplicationVehicleDTO v = vehicles.get(j);

                    XWPFTableRow newRow =
                            table.insertNewTableRow(templateIndex + j);

                    copyRow(row, newRow);

                    Map<String, String> vData =
                            buildVehicleData(v, j + 1,gate);

                    replaceRowPlaceholders(newRow, vData);
                }

                table.removeRow(templateIndex + vehicles.size());
                break;
            }
        }
    }

    // =====================================================
    // ================= REPLACE CORE ======================
    // =====================================================
    private void replaceAll(XWPFDocument doc, Map<String, String> data) {

        for (XWPFParagraph p : doc.getParagraphs()) {
            replaceInParagraph(p, data);
        }

        for (XWPFTable table : doc.getTables()) {
            processTable(table, data);
        }
    }

    private void processTable(XWPFTable table, Map<String, String> data) {

        for (XWPFTableRow row : table.getRows()) {

            for (XWPFTableCell cell : row.getTableCells()) {

                for (XWPFParagraph p : cell.getParagraphs()) {
                    replaceInParagraph(p, data);
                }

                // xử lý nested table nếu có
                for (XWPFTable nested : cell.getTables()) {
                    processTable(nested, data);
                }
            }
        }
    }
    private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> data) {

        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) return;

        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : runs) {
            if (run.getText(0) != null) {
                fullText.append(run.getText(0));
            }
        }

        String text = fullText.toString();
        if (text.isEmpty()) return;

        String replaced = text;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            replaced = replaced.replace(entry.getKey(), entry.getValue());
        }

        if (!text.equals(replaced)) {

            // giữ run đầu để preserve style
            XWPFRun firstRun = runs.get(0);
            firstRun.setText(replaced, 0);

            // xóa run còn lại
            for (int i = runs.size() - 1; i > 0; i--) {
                paragraph.removeRun(i);
            }
        }
    }
    private void replaceRowPlaceholders(
            XWPFTableRow row,
            Map<String, String> data
    ) {
        for (XWPFTableCell cell : row.getTableCells()) {
            for (XWPFParagraph p : cell.getParagraphs()) {
                replaceInParagraph(p, data);
            }
        }
    }

    // =====================================================
    // ================= UTIL ==============================
    // =====================================================
    private XWPFDocument loadTemplate(String relativePath) throws IOException {
        Path path = Paths.get(templateRoot, relativePath);
        if (!Files.exists(path)) {
            throw new IOException("Không tìm thấy template: " + path.toAbsolutePath());
        }
        return new XWPFDocument(Files.newInputStream(path));
    }

    private byte[] writeDoc(XWPFDocument doc) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            doc.write(out);
            return out.toByteArray();
        }
    }

    private String safe(String v) {
        return v == null ? "" : v;
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

    private void forceTimesNewRoman(XWPFDocument doc) {

        for (XWPFParagraph p : doc.getParagraphs()) {
            for (XWPFRun r : p.getRuns()) {
                r.setFontFamily("Times New Roman");
            }
        }

        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            r.setFontFamily("Times New Roman");
                        }
                    }
                }
            }
        }
    }

    private void copyRow(XWPFTableRow source, XWPFTableRow target) {
        target.getCtRow().setTrPr(source.getCtRow().getTrPr());
        for (XWPFTableCell cell : source.getTableCells()) {
            XWPFTableCell newCell = target.addNewTableCell();
            newCell.getCTTc().setTcPr(cell.getCTTc().getTcPr());
            for (XWPFParagraph p : cell.getParagraphs()) {
                XWPFParagraph newP = newCell.addParagraph();
                newP.getCTP().setPPr(p.getCTP().getPPr());
                for (XWPFRun r : p.getRuns()) {
                    XWPFRun newRun = newP.createRun();
                    newRun.getCTR().setRPr(r.getCTR().getRPr());
                    newRun.setText(r.text());
                }
            }
            newCell.removeParagraph(0);
        }
    }
    // ngày thàng năm
    private String toVietnameseDate(LocalDate date) {
        if (date == null) return "";
        return "ngày " + date.getDayOfMonth()
                + " tháng " + date.getMonthValue()
                + " năm " + date.getYear();
    }
}