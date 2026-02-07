package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.InvoiceResponse;
import com.bidv.asset.vehicle.DTO.VehicleInfo;
import com.bidv.asset.vehicle.Service.PdfService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfServiceImplement implements PdfService {

    @Override
    public InvoiceResponse extractPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            InvoiceResponse response = new InvoiceResponse();

            // Invoice Number
            String invoiceNo = null;
            Pattern invPattern = Pattern.compile("Số\\s*\\(Inv\\s*No\\.\\)\\s*[:\\s]*\\n*(\\d+)",
                    Pattern.CASE_INSENSITIVE);
            Matcher invMatcher = invPattern.matcher(text);
            if (invMatcher.find()) {
                invoiceNo = invMatcher.group(1).trim();
            }
            if (invoiceNo == null) {
                String[] lines = text.split("\\n");
                for (int i = 0; i < Math.min(20, lines.length); i++) {
                    String line = lines[i].trim();
                    if (line.matches("\\d{7,8}")) {
                        invoiceNo = line;
                        break;
                    }
                }
            }
            response.setInvoiceNumber(invoiceNo);

            // Extract Total Amount (e.g., Cộng tiền hàng: 719.040.000)
            String totalAmount = findMatch(text, "Cộng tiền hàng\\s*[:\\s]*([\\d\\.,]+)", 1);
            if (totalAmount == null) {
                totalAmount = findMatch(text, "Tổng tiền thanh toán\\s*[:\\s]*([\\d\\.,]+)", 1);
            }
            response.setTotalAmount(totalAmount);

            // Extract Date (e.g., Ngày (Date) 05 tháng(month) 12 năm(year) 2025)
            String day = findMatch(text, "Ngày\\s*(?:\\(Date\\))?\\s*(\\d{1,2})", 1);
            String month = findMatch(text, "tháng\\s*(?:\\(month\\))?\\s*(\\d{1,2})", 1);
            String year = findMatch(text, "năm\\s*(?:\\(year\\))?\\s*(\\d{4})", 1);

            response.setDay(day);
            response.setMonth(month);
            response.setYear(year);

            // Vehicle List
            List<VehicleInfo> vehicleList = new ArrayList<>();
            Pattern vinPattern = Pattern.compile("(?:SK|Số khung)\\s*[:\\s]*([A-Z0-9]+)", Pattern.CASE_INSENSITIVE);
            Matcher vinMatcher = vinPattern.matcher(text);

            while (vinMatcher.find()) {
                VehicleInfo v = new VehicleInfo();
                v.setChassisNumber(vinMatcher.group(1).trim());

                int pos = vinMatcher.start();
                String prefix = text.substring(Math.max(0, pos - 400), pos);
                String suffix = text.substring(pos, Math.min(text.length(), pos + 400));

                v.setEngineNumber(findMatch(suffix, "(?:SM|Số máy)\\s*[:\\s]*([A-Z0-9]+)", 1));
                v.setColor(findMatch(prefix + suffix, "Màu\\s*[:\\s]*([^\\s;,\n]+)", 1));
                v.setNumberOfSeats(findMatch(prefix + suffix, "(\\d+)\\s*chỗ", 1));

                // Extract Unit Price (đơn giá)
                // Often looks like: Cái 1 660.481.818 660.481.818
                String pricePattern = "(?:Cái|Chiếc|Bộ|Lô|xe)\\s+\\d+\\s+([\\d\\.,]{7,})";
                v.setUnitPrice(findMatch(suffix, pricePattern, 1));

                // Description logic
                String desc = null;
                Pattern descPattern = Pattern.compile("\\n\\d+\\s*\\n(.*?)(?=\\n|;|,|\\d+\\s*chỗ|Màu|SK:)",
                        Pattern.DOTALL);
                Matcher descMatcher = descPattern.matcher(prefix);
                if (descMatcher.find()) {
                    while (descMatcher.find()) {
                        desc = descMatcher.group(1).trim();
                    }
                    if (desc == null) {
                        descMatcher.reset();
                        if (descMatcher.find())
                            desc = descMatcher.group(1).trim();
                    }
                }
                if (desc == null) {
                    String[] lines = prefix.split("\\n");
                    for (int j = lines.length - 1; j >= 0; j--) {
                        String line = lines[j].trim();
                        if (line.length() > 10 && !line.contains("STT") && !line.contains("Tên hàng")) {
                            desc = line;
                            break;
                        }
                    }
                }
                v.setVehicleDescription(desc != null ? desc.replace("\r", "").replace("\n", " ").trim() : null);
                vehicleList.add(v);
            }

            response.setVehicleList(vehicleList);
            return response;
        }
    }

    private String findMatch(String text, String regex, int group) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(group).trim();
        }
        return null;
    }
}
