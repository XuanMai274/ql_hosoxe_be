package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.GuaranteeLetterDTO;
import com.bidv.asset.vehicle.Service.GuaranteeLetterExportService;
import com.bidv.asset.vehicle.Utill.VietnameseNumberUtil;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
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
    public byte[] generateWord(GuaranteeLetterDTO dto) throws IOException {

        // 1. Load template
        InputStream is = getClass()
                .getResourceAsStream("/templates/thu-bao-lanh-vinfast.docx");
        if (is == null) {
            throw new FileNotFoundException("Không tìm thấy file template thu-bao-lanh-vinfast.docx");
        }

        XWPFDocument doc = new XWPFDocument(is);

        // 2. Chuẩn bị dữ liệu
        String guaranteeDateTitle = toVietnameseDate(
                dto.getGuaranteeContractDate() != null
                        ? dto.getGuaranteeContractDate()
                        : LocalDate.now()
        );

        BigDecimal expectedAmount = dto.getExpectedGuaranteeAmount();
        String expectedAmountText = VietnameseNumberUtil.toVietnamese(expectedAmount);

        Map<String, String> data = new HashMap<>();
        data.put("{{GUARANTEE_NUMBER}}", dto.getGuaranteeContractNumber());
        data.put("{{GUARANTEE_DATE}}",
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        data.put("{{GUARANTEE_DATE_TITLE}}", guaranteeDateTitle);
        data.put("{{SALE_CONTRACT}}", dto.getSaleContract());
        data.put("{{SALE_CONTRACT_AMOUNT}}", formatMoney(dto.getSaleContractAmount()));
        data.put("{{EXPECTED_GUARANTEE_AMOUNT}}", formatMoney(expectedAmount));
        data.put("{{EXPECTED_GUARANTEE_AMOUNT_TEXT}}", expectedAmountText);
        data.put("{{EXPECTED_VEHICLE_COUNT}}",
                dto.getExpectedVehicleCount() != null
                        ? dto.getExpectedVehicleCount().toString()
                        : "");

        // 3. Replace placeholder (CHUẨN, thay được mọi trường hợp)
        replaceAllPlaceholders(doc, data);

        // 4. Export file
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        doc.write(out);
        doc.close();

        return out.toByteArray();
    }

    /**
     * Replace placeholder ở toàn bộ document (paragraph + table)
     */
    private void replaceAllPlaceholders(XWPFDocument doc, Map<String, String> data) {

        // Paragraph thường
        for (XWPFParagraph p : doc.getParagraphs()) {
            replaceInParagraph(p, data);
        }

        // Paragraph trong table
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

    /**
     * Replace placeholder theo paragraph (FIX TRIỆT ĐỂ lỗi split run)
     */
    private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> data) {

        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.getText(0);
            if (text != null) {
                fullText.append(text);
            }
        }

        String replacedText = fullText.toString();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            replacedText = replacedText.replace(entry.getKey(), entry.getValue());
        }

        if (!replacedText.equals(fullText.toString())) {
            int runCount = paragraph.getRuns().size();
            for (int i = runCount - 1; i >= 0; i--) {
                paragraph.removeRun(i);
            }

            XWPFRun newRun = paragraph.createRun();
            newRun.setText(replacedText);
        }
    }

    // ===== UTIL =====

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
}
