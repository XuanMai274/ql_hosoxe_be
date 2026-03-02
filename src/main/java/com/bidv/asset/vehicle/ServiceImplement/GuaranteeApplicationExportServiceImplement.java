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
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GuaranteeApplicationExportServiceImplement
        implements GuaranteeApplicationExportService {

    private static final String TEMPLATE_PATH = "/templates/DeNghiCapBaoLanh/";
    BigDecimal gate= BigDecimal.valueOf(0);
    // =====================================================
    // ================= MAIN EXPORT =======================
    // =====================================================
    @Override
    public Map<String, byte[]> exportAll(GuaranteeApplicationDTO dto)
            throws IOException {

        Map<String, byte[]> results = new LinkedHashMap<>();

        // luôn có danh sách xe
        results.put(
                "danh-sach-xe-de-nghi-cap-bao-lanh.docx",
                exportVehicleList(dto)
        );

        // chọn template theo hãng
        String manufacturer = Optional.ofNullable(dto.getManufacturerDTO())
                .map(m -> m.getCode())
                .orElse("");

        if (manufacturer.contains("HYUNDAI")) {
            results.put(
                    "de-nghi-cap-bao-lanh-hyundai.docx",
                    exportCommon(dto, "de-nghi-cap-bao-lanh-hyundai.docx")
            );
            gate= BigDecimal.valueOf(85);
        } else if(manufacturer.contains("VINFAST")) {
            results.put(
                    "de-nghi-cap-bao-lanh-vinfast.docx",
                    exportCommon(dto, "de-nghi-cap-bao-lanh-vinfast.docx")
            );
            gate= BigDecimal.valueOf(75);
        }

        return results;
    }

    // =====================================================
    // =============== EXPORT COMMON =======================
    // =====================================================
    private byte[] exportCommon(
            GuaranteeApplicationDTO dto,
            String templateName
    ) throws IOException {

        XWPFDocument doc = loadTemplate(TEMPLATE_PATH + templateName);

        Map<String, String> data = buildCommonData(dto);

        replaceAll(doc, data);

        forceTimesNewRoman(doc);

        return writeDoc(doc);
    }

    // =====================================================
    // ============ EXPORT VEHICLE LIST ====================
    // =====================================================
    private byte[] exportVehicleList(GuaranteeApplicationDTO dto)
            throws IOException {

        XWPFDocument doc = loadTemplate(
                TEMPLATE_PATH + "danh-sach-xe-de-nghi-cap-bao-lanh.docx"
        );

        Map<String, String> common = buildCommonData(dto);

        replaceAll(doc, common);

        replaceVehicleTable(doc,
                Optional.ofNullable(dto.getVehicles())
                        .orElse(Collections.emptyList()));

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
        map.put("{{expiryDate}}",safe(String.valueOf(dto.getGuaranteeTermDays())));
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
            int stt
    ) {

        Map<String, String> map = new HashMap<>();

        map.put("{{stt}}", String.valueOf(stt));
        map.put("{{vehicleType}}", safe(v.getVehicleType()));
        map.put("{{color}}", safe(v.getColor()));
        map.put("{{chassis}}", safe(v.getChassisNumber()));
        map.put("{{invoice}}", safe(v.getInvoiceNumber()));
        map.put("{{price}}", formatMoney(v.getVehiclePrice()));
        map.put("{{guarantee}}", formatMoney(v.getGuaranteeAmount()));
        map.put("{{gate}}",safe(String.valueOf(gate)));
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
            List<GuaranteeApplicationVehicleDTO> vehicles
    ) {
        for (XWPFTable table : doc.getTables()) {
            processTableRecursive(table, vehicles);
        }
    }

    private void processTableRecursive(
            XWPFTable table,
            List<GuaranteeApplicationVehicleDTO> vehicles
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
                            buildVehicleData(v, j + 1);

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
            for (XWPFTableRow row : table.getRows()) {
                replaceRowPlaceholders(row, data);
            }
        }
    }

    private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> data) {

        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.getText(0);
            if (text == null) continue;

            String replaced = text;

            for (Map.Entry<String, String> e : data.entrySet()) {
                if (replaced.contains(e.getKey())) {
                    replaced = replaced.replace(e.getKey(), e.getValue());
                }
            }

            if (!text.equals(replaced)) {
                run.setText(replaced, 0); // giữ nguyên style
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
    }

    private void copyRow(XWPFTableRow source, XWPFTableRow target) {
        target.getCtRow().setTrPr(source.getCtRow().getTrPr());
        for (XWPFTableCell cell : source.getTableCells()) {
            XWPFTableCell newCell = target.addNewTableCell();
            newCell.setText(cell.getText());
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