package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.MortgageContractDTO;
import com.bidv.asset.vehicle.DTO.VehicleDTO;
import com.bidv.asset.vehicle.Repository.VehicleRepository;
import com.bidv.asset.vehicle.Service.VehicleWarehouseExportService;
import com.bidv.asset.vehicle.Utill.VietnameseNumberUtil;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
@Service
public class VehicleWarehouseExportImplement implements VehicleWarehouseExportService {
    @Autowired
    VehicleRepository vehicleRepository;

    /* ========================================================= */
    /* ======================= PNK ============================== */
    /* ========================================================= */

    @Override
    public byte[] generatePNK(List<VehicleDTO> vehicles,String importNumber) throws IOException {

        if (vehicles == null || vehicles.isEmpty()) {
            throw new RuntimeException("Danh sách xe trống");
        }

        String manufacturer =
                vehicles.get(0).getManufacturerDTO().getName();

        List<VehicleDTO> filtered =
                filterByManufacturer(vehicles, manufacturer);

        if (filtered.isEmpty()) {
            throw new RuntimeException("Không có xe thuộc loại " + manufacturer);
        }

        XWPFDocument doc = loadTemplate("/templates/NhapKho/PNK.docx");

        BigDecimal total = calculateTotal(filtered);

        replaceVehicleTable(doc, filtered);

        Map<String,String> map =
                buildCommonData(vehicles, total,importNumber);

        replaceAll(doc, map);
        forceTimesNewRoman(doc);
        return writeDoc(doc);
    }

    /* ========================================================= */
    /* =================== BÁO CÁO ĐỊNH GIÁ ===================== */
    /* ========================================================= */

    public byte[] generateBaoCaoDinhGia(
            List<VehicleDTO> vehicles,String importNumber
    ) throws IOException {

        if (vehicles == null || vehicles.isEmpty()) {
            throw new RuntimeException("Danh sách xe trống");
        }

        String manufacturer =
                vehicles.get(0).getManufacturerDTO().getName();

        List<VehicleDTO> filtered =
                filterByManufacturer(vehicles, manufacturer);

        XWPFDocument doc =
                loadTemplate("/templates/NhapKho/bao-cao-dinh-gia-tai-san.docx");

        BigDecimal total = calculateTotal(filtered);

        replaceVehicleTable(doc, filtered);

        Map<String,String> map =
                buildCommonData(filtered, total,importNumber);

        replaceAll(doc, map);
        forceTimesNewRoman(doc);
        return writeDoc(doc);
    }

    /* ========================================================= */
    /* ================= BIÊN BẢN ĐỊNH GIÁ ====================== */
    /* ========================================================= */

    public byte[] generateBienBanDinhGia(
            List<VehicleDTO> vehicles,String importNumber
    ) throws IOException {

        if (vehicles == null || vehicles.isEmpty()) {
            throw new RuntimeException("Danh sách xe trống");
        }

        String manufacturer =
                vehicles.get(0).getManufacturerDTO().getName();

        List<VehicleDTO> filtered =
                filterByManufacturer(vehicles, manufacturer);

        XWPFDocument doc =
                loadTemplate("/templates/NhapKho/bien-ban-dinh-gia-NK.docx");

        BigDecimal total = calculateTotal(filtered);

        replaceVehicleTable(doc, filtered);

        Map<String,String> map =
                buildCommonData(filtered, total,importNumber);

        replaceAll(doc, map);
        forceTimesNewRoman(doc);
        return writeDoc(doc);
    }

    /* ========================================================= */
    /* ================= PHỤ LỤC HĐ HYUNDAI ===================== */
    /* ========================================================= */
    public byte[] generatePhuLucHopDongTheChap(
            List<VehicleDTO> vehicles,String importNumber
    ) throws IOException {

        String manufacturerCode = validateAndGetManufacturerCode(vehicles);

        switch (manufacturerCode.toUpperCase()) {

            case "HYUNDAI":
                return generatePhuLucHyundai(vehicles,importNumber);

            case "VINFAST":
                return generatePhuLucVinfast(vehicles,importNumber);

            default:
                throw new RuntimeException(
                        "Chưa cấu hình template cho hãng: " + manufacturerCode);
        }
    }
    public byte[] generatePhuLucHyundai(
            List<VehicleDTO> vehicles,String importNumber
    ) throws IOException {

        validateSingleManufacturer(vehicles, "HYUNDAI");

        XWPFDocument doc =
                loadTemplate("/templates/NhapKho/phu-luc-hop-dong-thue-chap-hyndai.docx");

        BigDecimal total = calculateTotal(vehicles);

        replaceVehicleTable(doc, vehicles);

        Map<String,String> map =
                buildCommonData(vehicles, total,importNumber);

        replaceAll(doc, map);
        forceTimesNewRoman(doc);
        return writeDoc(doc);
    }

    /* ========================================================= */
    /* ================= PHỤ LỤC HĐ VINFAST ===================== */
    /* ========================================================= */

    public byte[] generatePhuLucVinfast(
            List<VehicleDTO> vehicles,String importNumber
    ) throws IOException {

        validateSingleManufacturer(vehicles, "VINFAST");

        XWPFDocument doc =
                loadTemplate("/templates/NhapKho/phu-luc-hop-dong-thue-chap-vinfast.docx");

        BigDecimal total = calculateTotal(vehicles);

        replaceVehicleTable(doc, vehicles);

        Map<String,String> map =
                buildCommonData(vehicles, total,importNumber);

        replaceAll(doc, map);
        forceTimesNewRoman(doc);
        return writeDoc(doc);
    }
    /* ========================================================= */
    /* ================= ĐĂNG KÝ GIAO DỊCH ĐẢM BẢO ============= */
    /* ========================================================= */
    @Override
    public byte[] generateDangKiGiaoDichDamBao(
            List<VehicleDTO> vehicles,String importNumber
    ) throws IOException {

        String manufacturerCode = validateAndGetManufacturerCode(vehicles);

        switch (manufacturerCode.toUpperCase()) {

            case "HYUNDAI":
                return generateDangKyHyundai(vehicles,importNumber);

            case "VINFAST":
                return generateDangKyVinfast(vehicles,importNumber);

            default:
                throw new RuntimeException(
                        "Chưa cấu hình đăng ký giao dịch đảm bảo cho hãng: "
                                + manufacturerCode);
        }

    }
    public byte[] generateDangKyVinfast(
            List<VehicleDTO> vehicles,String importNumber
    ) throws IOException {

        validateSingleManufacturer(vehicles, "VINFAST");

        XWPFDocument doc =
                loadTemplate("/templates/NhapKho/dang-ky-giao-dich-dam-bao-vinfast.docx");

        BigDecimal total = calculateTotal(vehicles);

        replaceVehicleTableDeep(doc, vehicles);

        Map<String,String> map =
                buildCommonData(vehicles, total,importNumber);

        replaceAll(doc, map);
        forceTimesNewRoman(doc);
        return writeDoc(doc);
    }
    public byte[] generateDangKyHyundai(
            List<VehicleDTO> vehicles,String importNumber
    ) throws IOException {

        validateSingleManufacturer(vehicles, "HYUNDAI");

        XWPFDocument doc =
                loadTemplate("/templates/NhapKho/dang-ky-giao-dich-bao-dam-hyundai.docx");

        BigDecimal total = calculateTotal(vehicles);

        replaceVehicleTableDeep(doc, vehicles);

        Map<String,String> map =
                buildCommonData(vehicles, total,importNumber);

        replaceAll(doc, map);
        forceTimesNewRoman(doc);
        return writeDoc(doc);
    }
    /* ========================================================= */
    /* =================THAY THẾ BẢNG CHO ĐĂNG KÝ GIAO DỊCH ĐẢM BẢO ============= */
    /* ========================================================= */
    private void replaceVehicleTableDeep(XWPFDocument doc, List<VehicleDTO> vehicles) {
        for (XWPFTable table : doc.getTables()) {
            processTableRecursive(table, vehicles);
        }
    }
    private void processTableRecursive(XWPFTable table, List<VehicleDTO> vehicles) {

        // ===== xử lý chính bảng hiện tại =====
        for (int i = 0; i < table.getRows().size(); i++) {

            XWPFTableRow row = table.getRow(i);
            if (row.getCell(0) == null) continue;

            String firstCell = row.getCell(0).getText();

            if (firstCell != null && firstCell.toLowerCase().contains("{{stt}}")) {

                int templateIndex = i;

                for (int j = 0; j < vehicles.size(); j++) {

                    VehicleDTO v = vehicles.get(j);

                    XWPFTableRow newRow =
                            table.insertNewTableRow(templateIndex + j);

                    copyRow(row, newRow);

                    Map<String, String> data =
                            buildVehicleData(v, j + 1);

                    replaceRowPlaceholders(newRow, data);
                }

                table.removeRow(templateIndex + vehicles.size());
                break;
            }
        }

        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {

                for (XWPFTable nested : cell.getTables()) {
                    processTableRecursive(nested, vehicles);
                }
            }
        }
    }

    /* ========================================================= */
    /* ================= TABLE REPLACEMENT ====================== */
    /* ========================================================= */

    @Override
    public void replaceVehicleTable(XWPFDocument doc, List<VehicleDTO> vehicles) {

        for (XWPFTable table : doc.getTables()) {

            boolean found = false;

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

                    found = true;
                }

                if (found) break;
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
        map.put("{{manufacturer}}",safe(v.getManufacturerDTO().getCode()));
//        if (v.getGuaranteeLetterDTO()!=null &&
//                v.getGuaranteeLetterDTO().getManufacturerDTO()!=null) {
//
//            map.put("{{manufacturer}}",
//                    safe(v.getGuaranteeLetterDTO()
//                            .getManufacturerDTO()
//                            .getName()));
//        }

//        if (v.getGuaranteeLetterDTO() != null) {
//            map.put("{{HDBD}}",
//                    safe(v.getGuaranteeLetterDTO().getGuaranteeContractNumber()));
//
//            map.put("{{HDBD_DATE}}",
//                    formatDate(v.getGuaranteeLetterDTO().getGuaranteeContractDate()));
//        }

        return map;
    }
    private Map<String,String> buildHeaderData(List<VehicleDTO> vehicles) {

        Map<String,String> map = new HashMap<>();

        if (vehicles == null || vehicles.isEmpty()) return map;

        VehicleDTO first = vehicles.get(0);

//        if (first.getGuaranteeLetterDTO() != null) {
//
//            map.put("{{HDBD}}",
//                    safe(first.getGuaranteeLetterDTO()
//                            .getGuaranteeContractNumber()));
//
//            map.put("{{HDBD_DATE}}",
//                    formatDate(first.getGuaranteeLetterDTO()
//                            .getGuaranteeContractDate()));
//        }

        return map;
    }
    // hàm build thông tin chung
    private Map<String, String> buildCommonData(
            List<VehicleDTO> vehicles,
            BigDecimal total,
            String importNumber
    ) {

        Map<String, String> map = new HashMap<>();

        if (vehicles == null || vehicles.isEmpty()) {
            return map;
        }

        VehicleDTO first = vehicles.get(0);

        /* ================= HDBD THEO LOẠI XE ================= */
        System.out.println("Vehicle ID: " + first.getId());

        System.out.println("GuaranteeLetter: "
                + first.getGuaranteeLetterDTO());

        if (first.getGuaranteeLetterDTO() != null) {
            System.out.println("CreditContract: "
                    + first.getGuaranteeLetterDTO().getCreditContractDTO());
        }
        MortgageContractDTO matchedMortgage = null;

        if (first.getGuaranteeLetterDTO() != null) {
            matchedMortgage =
                    first.getGuaranteeLetterDTO().getMortgageContractDTO();
        }

        if (matchedMortgage != null) {
            map.put("{{HDBD}}",
                    safe(matchedMortgage.getContractNumber()));

            map.put("{{HDBD_DATE}}",
                    formatDate(matchedMortgage.getContractDate()));
            map.put("{{DKGDDB}}",safe(matchedMortgage.getSecurityRegistrationNumber()));
            map.put("{{MCN}}",safe(matchedMortgage.getPersonalIdNumber()));
        }
        map.put("{{HDBDCT}}",safe(importNumber));
//        MortgageContractDTO matchedMortgage =
//                findMortgageByVehicle(first);
//        System.out.println("HDBD"+matchedMortgage.getContractNumber());
//        System.out.println("HDBD"+matchedMortgage.getContractDate());
//        System.out.println("HDBD " + matchedMortgage.getContractNumber());
//        System.out.println("HDBD DATE " + matchedMortgage.getContractDate());


        /* ================= CREDIT CONTRACT ================= */

        if (first.getGuaranteeLetterDTO() != null
                && first.getGuaranteeLetterDTO().getCreditContractDTO() != null) {

            var credit =
                    first.getGuaranteeLetterDTO().getCreditContractDTO();

            map.put("{{HDTD}}",
                    safe(credit.getContractNumber()));

            map.put("{{HDTD_DATE}}",
                    formatDate(credit.getContractDate()));
        }

        /* ================= TỔNG TIỀN ================= */

        if (total != null) {

            map.put("{{TONG}}",
                    formatMoney(total));

            map.put("{{TONG_TEXT}}",
                    VietnameseNumberUtil.toVietnamese(total));

            map.put("{{TONG_80%}}",
                    formatMoney(total.multiply(BigDecimal.valueOf(0.8))));
        }

        /* ================= NGÀY ================= */

        LocalDate now = LocalDate.now();
        map.put("{{CURRENT_DATE}}", formatDate(now));
        map.put("{{CURRENT_DATE_TITLE}}", toVietnameseDate(now));
        /* ================= Thông tin của đown đăng kí giao dịch đảm bảo ================= */

        return map;
    }
    /* ========================================================= */
    /* ================= HELPER ================================ */
    /* ========================================================= */
    // ngày thàng năm
    private String toVietnameseDate(LocalDate date) {
        if (date == null) return "";
        return "ngày " + date.getDayOfMonth()
                + " tháng " + date.getMonthValue()
                + " năm " + date.getYear();
    }
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

            // ❗ copy toàn bộ paragraph & run
            for (XWPFParagraph p : cell.getParagraphs()) {
                XWPFParagraph newP = newCell.addParagraph();
                newP.getCTP().setPPr(p.getCTP().getPPr());

                for (XWPFRun r : p.getRuns()) {
                    XWPFRun newRun = newP.createRun();
                    newRun.getCTR().setRPr(r.getCTR().getRPr());
                    newRun.setText(r.text());
                }
            }

            newCell.removeParagraph(0); // xóa paragraph rỗng mặc định
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

        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) return;

        // 👉 LƯU style run đầu trước khi xóa
        CTRPr oldRPr = null;
        if (runs.get(0).getCTR().isSetRPr()) {
            oldRPr = (CTRPr) runs.get(0).getCTR().getRPr().copy();
        }

        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : runs) {
            String text = run.getText(0);
            if (text != null) fullText.append(text);
        }

        String replaced = fullText.toString();
        for (Map.Entry<String,String> e : data.entrySet()) {
            replaced = replaced.replace(e.getKey(), e.getValue());
        }

        if (replaced.equals(fullText.toString())) return;

        // ❗ xóa run cũ
        for (int i = runs.size() - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }

        // ❗ tạo run mới
        XWPFRun newRun = paragraph.createRun();

        // 👉 restore style an toàn
        if (oldRPr != null) {
            newRun.getCTR().setRPr(oldRPr);
        }

        newRun.setText(replaced);
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

    private List<VehicleDTO> filterByManufacturer(
            List<VehicleDTO> vehicles,
            String manufacturerName
    ) {

        if (vehicles == null || vehicles.isEmpty()) {
            return Collections.emptyList();
        }

        return vehicles.stream()
                .filter(v -> v.getManufacturerDTO() != null
                        && manufacturerName.equalsIgnoreCase(
                        v.getManufacturerDTO().getName()))
                .toList();
    }
    private void validateSingleManufacturer(
            List<VehicleDTO> vehicles,
            String requiredCode
    ) {

        if (vehicles == null || vehicles.isEmpty()) {
            throw new RuntimeException("Danh sách xe trống");
        }

        for (VehicleDTO v : vehicles) {
            String actualCode =
                    v.getManufacturerDTO() != null
                            ? v.getManufacturerDTO().getCode()
                            : null;

            System.out.println("Required: " + requiredCode);
            System.out.println("Actual  : " + actualCode);
            if (v.getManufacturerDTO() == null
                    || v.getManufacturerDTO().getCode() == null) {

                throw new RuntimeException("Xe thiếu thông tin hãng");
            }

            if (!requiredCode.equalsIgnoreCase(
                    v.getManufacturerDTO().getCode())) {

                throw new RuntimeException(
                        "Danh sách không phải xe " + requiredCode);
            }
        }
    }
    private String validateAndGetManufacturerCode(
            List<VehicleDTO> vehicles
    ) {

        if (vehicles == null || vehicles.isEmpty()) {
            throw new RuntimeException("Danh sách xe trống");
        }

        if (vehicles.get(0).getManufacturerDTO() == null
                || vehicles.get(0).getManufacturerDTO().getCode() == null) {
            throw new RuntimeException("Xe thiếu thông tin hãng");
        }

        String code =
                vehicles.get(0).getManufacturerDTO().getCode();

        for (VehicleDTO v : vehicles) {

            if (v.getManufacturerDTO() == null
                    || v.getManufacturerDTO().getCode() == null
                    || !code.equalsIgnoreCase(
                    v.getManufacturerDTO().getCode())) {

                throw new RuntimeException(
                        "Danh sách chứa nhiều hãng xe khác nhau");
            }
        }

        return code;
    }
    private MortgageContractDTO findMortgageByVehicle(VehicleDTO vehicle) {

        if (vehicle == null
                || vehicle.getManufacturerDTO() == null
                || vehicle.getManufacturerDTO().getCode() == null
                || vehicle.getGuaranteeLetterDTO() == null
                || vehicle.getGuaranteeLetterDTO().getMortgageContractDTO() == null) {
            return null;
        }

        String manufacturerCode = vehicle.getManufacturerDTO().getCode();

        MortgageContractDTO mortgage =
                vehicle.getGuaranteeLetterDTO().getMortgageContractDTO();

        if (mortgage.getManufacturerDTO() != null
                && manufacturerCode.equalsIgnoreCase(
                mortgage.getManufacturerDTO().getCode())) {
            return mortgage;
        }

        return null;
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
}
