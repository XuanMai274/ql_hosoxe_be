package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.GuaranteeLetterDTO;
import com.bidv.asset.vehicle.Service.GuaranteeLetterExportService;
import com.bidv.asset.vehicle.Utill.VietnameseNumberUtil;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class GuaranteeLetterExportServiceImplement implements GuaranteeLetterExportService {

    @Override
    public byte[] generateWord(GuaranteeLetterDTO dto, String template) throws IOException {

        if ("VINFAST_V1".equals(template)) {
            return generateVinfast(dto);
        }

        if ("HYUNDAI_V1".equals(template)) {
            return generateHyundai(dto);
        }

        throw new IllegalArgumentException("Template không được hỗ trợ: " + template);
    }

    // =====================================================
    // ================== VINFAST ==========================
    // =====================================================

    private byte[] generateVinfast(GuaranteeLetterDTO dto) throws IOException {
        XWPFDocument doc = loadTemplate("/templates/thu-bao-lanh-vinfast.docx");

        Map<String, String> data = buildCommonData(dto);
        // 👉 nếu Vinfast có field riêng thì thêm ở đây

        replaceAllPlaceholders(doc, data);
        return writeDoc(doc);
    }

    // =====================================================
    // ================== HYUNDAI ==========================
    // =====================================================

    private byte[] generateHyundai(GuaranteeLetterDTO dto) throws IOException {
        XWPFDocument doc = loadTemplate("/templates/thu-bao-lanh-hyundai.docx");

        Map<String, String> data = buildCommonData(dto);
        // 👉 nếu Hyundai có text khác thì chỉnh ở đây

        replaceAllPlaceholders(doc, data);
        return writeDoc(doc);
    }

    // =====================================================
    // ================= COMMON LOGIC ======================
    // =====================================================

    private XWPFDocument loadTemplate(String path) throws IOException {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException("Không tìm thấy template: " + path);
        }
        return new XWPFDocument(is);
    }

    private Map<String, String> buildCommonData(GuaranteeLetterDTO dto) {

        Map<String, String> data = new HashMap<>();

        String guaranteeDateTitle = toVietnameseDate(
                dto.getGuaranteeContractDate() != null
                        ? dto.getGuaranteeContractDate()
                        : LocalDate.now()
        );

        BigDecimal expectedAmount = dto.getExpectedGuaranteeAmount();

        data.put("{{GUARANTEE_NUMBER}}", safe(dto.getGuaranteeContractNumber()));
        data.put("{{GUARANTEE_DATE}}",
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        data.put("{{GUARANTEE_DATE_TITLE}}", guaranteeDateTitle);
        data.put("{{SALE_CONTRACT}}", safe(dto.getSaleContract()));
        data.put("{{SALE_CONTRACT_AMOUNT}}", formatMoney(dto.getSaleContractAmount()));
        data.put("{{EXPECTED_GUARANTEE_AMOUNT}}", formatMoney(expectedAmount));
        data.put("{{EXPECTED_GUARANTEE_AMOUNT_TEXT}}",
                VietnameseNumberUtil.toVietnamese(expectedAmount));

        if (dto.getBranchAuthorizedRepresentativeDTO() != null) {
            data.put("{{REPRESENTATIVE_NAME}}",
                    safe(dto.getBranchAuthorizedRepresentativeDTO().getRepresentativeName()));
            data.put("{{REPRESENTATIVE_TITLE}}",
                    safe(dto.getBranchAuthorizedRepresentativeDTO().getRepresentativeTitle()));
            data.put("{{AUTH_DOC_NO}}",
                    safe(dto.getBranchAuthorizedRepresentativeDTO().getAuthorizationDocNo()));
            data.put("{{AUTH_DOC_DATE}}",
                    dto.getBranchAuthorizedRepresentativeDTO().getAuthorizationDocDate() != null
                            ? dto.getBranchAuthorizedRepresentativeDTO()
                            .getAuthorizationDocDate()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            : "");
            data.put("{{AUTH_ISSUER}}",
                    safe(dto.getBranchAuthorizedRepresentativeDTO().getAuthorizationIssuer()));
        }

        data.put("{{EXPECTED_VEHICLE_COUNT}}",
                dto.getExpectedVehicleCount() != null
                        ? dto.getExpectedVehicleCount().toString()
                        : "");

        return data;
    }

    // =====================================================
    // ================= REPLACE LOGIC =====================
    // =====================================================

    private void replaceAllPlaceholders(XWPFDocument doc, Map<String, String> data) {

        for (XWPFParagraph p : doc.getParagraphs()) {
            replaceInParagraph(p, data);
        }

        doc.getTables().forEach(table ->
                table.getRows().forEach(row ->
                        row.getTableCells().forEach(cell ->
                                cell.getParagraphs().forEach(p ->
                                        replaceInParagraph(p, data)
                                )
                        )
                )
        );
    }

    private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> data) {

        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) return;

        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : runs) {
            String text = run.getText(0);
            if (text != null) {
                fullText.append(text);
            }
        }

        String replaced = fullText.toString();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            replaced = replaced.replace(entry.getKey(), entry.getValue());
        }

        if (replaced.equals(fullText.toString())) return;

        runs.get(0).setText(replaced, 0);
        for (int i = 1; i < runs.size(); i++) {
            runs.get(i).setText("", 0);
        }
    }

    // =====================================================
    // ================= UTIL ==============================
    // =====================================================

    private byte[] writeDoc(XWPFDocument doc) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        doc.write(out);
        doc.close();
        return out.toByteArray();
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "";
        return NumberFormat.getInstance(new Locale("vi", "VN"))
                .format(value) + " đồng";
    }

    private String toVietnameseDate(LocalDate date) {
        if (date == null) return "";
        return String.format(
                "ngày %d tháng %d năm %d",
                date.getDayOfMonth(),
                date.getMonthValue(),
                date.getYear()
        );
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
