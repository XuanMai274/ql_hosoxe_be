package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.MortgageContractDTO;
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
                buildCommonData(filtered, total);

        replaceAll(doc, map);

        return writeDoc(doc);
    }

    /* ========================================================= */
    /* =================== BÁO CÁO ĐỊNH GIÁ ===================== */
    /* ========================================================= */

    public byte[] generateBaoCaoDinhGia(
            List<VehicleDTO> vehicles
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
                buildCommonData(filtered, total);

        replaceAll(doc, map);

        return writeDoc(doc);
    }

    /* ========================================================= */
    /* ================= BIÊN BẢN ĐỊNH GIÁ ====================== */
    /* ========================================================= */

    public byte[] generateBienBanDinhGia(
            List<VehicleDTO> vehicles
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
                buildCommonData(filtered, total);

        replaceAll(doc, map);

        return writeDoc(doc);
    }

    /* ========================================================= */
    /* ================= PHỤ LỤC HĐ HYUNDAI ===================== */
    /* ========================================================= */
    public byte[] generatePhuLucHopDongTheChap(
            List<VehicleDTO> vehicles
    ) throws IOException {

        String manufacturerCode = validateAndGetManufacturerCode(vehicles);

        switch (manufacturerCode.toUpperCase()) {

            case "HYUNDAI":
                return generatePhuLucHyundai(vehicles);

            case "VINFAST":
                return generatePhuLucVinfast(vehicles);

            default:
                throw new RuntimeException(
                        "Chưa cấu hình template cho hãng: " + manufacturerCode);
        }
    }
    public byte[] generatePhuLucHyundai(
            List<VehicleDTO> vehicles
    ) throws IOException {

        validateSingleManufacturer(vehicles, "HYUNDAI");

        XWPFDocument doc =
                loadTemplate("/templates/NhapKho/phu-luc-hop-dong-thue-chap-hyndai.docx");

        BigDecimal total = calculateTotal(vehicles);

        replaceVehicleTable(doc, vehicles);

        Map<String,String> map =
                buildCommonData(vehicles, total);

        replaceAll(doc, map);

        return writeDoc(doc);
    }

    /* ========================================================= */
    /* ================= PHỤ LỤC HĐ VINFAST ===================== */
    /* ========================================================= */

    public byte[] generatePhuLucVinfast(
            List<VehicleDTO> vehicles
    ) throws IOException {

        validateSingleManufacturer(vehicles, "VINFAST");

        XWPFDocument doc =
                loadTemplate("/templates/NhapKho/phu-luc-hop-dong-thue-chap-vinfast.docx");

        BigDecimal total = calculateTotal(vehicles);

        replaceVehicleTable(doc, vehicles);

        Map<String,String> map =
                buildCommonData(vehicles, total);

        replaceAll(doc, map);

        return writeDoc(doc);
    }
    // ĐĂNG KÝ GIAO DỊCH ĐẢM BẢO
//    public byte[] generateDangKiGiaoDichDamBao(
//            List<VehicleDTO> vehicles
//    ) throws IOException {
//
//        String manufacturerCode = validateAndGetManufacturerCode(vehicles);
//
//        switch (manufacturerCode.toUpperCase()) {
//
//            case "HYUNDAI":
//                return generateDangKyHyundai(vehicles);
//
//            case "VINFAST":
//                return generateDangKyVinfast(vehicles);
//
//            default:
//                throw new RuntimeException(
//                        "Chưa cấu hình đăng ký giao dịch đảm bảo cho hãng: "
//                                + manufacturerCode);
//        }
//    }
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
            BigDecimal total
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
        MortgageContractDTO matchedMortgage =
                findMortgageByVehicle(first);
        System.out.println("HDBD"+matchedMortgage.getContractNumber());
        System.out.println("HDBD"+matchedMortgage.getContractDate());
        if (matchedMortgage != null) {

            System.out.println("HDBD " + matchedMortgage.getContractNumber());
            System.out.println("HDBD DATE " + matchedMortgage.getContractDate());

            map.put("{{HDBD}}",
                    safe(matchedMortgage.getContractNumber()));

            map.put("{{HDBD_DATE}}",
                    formatDate(matchedMortgage.getContractDate()));
        }

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
        map.put("{{CURRENT_DATE_TITLE}}", formatDateTitle(now));

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

        if (vehicle.getManufacturerDTO() == null
                || vehicle.getManufacturerDTO().getCode() == null
                || vehicle.getGuaranteeLetterDTO() == null
                || vehicle.getGuaranteeLetterDTO().getCreditContractDTO() == null
                || vehicle.getGuaranteeLetterDTO()
                .getCreditContractDTO()
                .getMortgageContractIds() == null) {
            return null;
        }

        String manufacturerCode =
                vehicle.getManufacturerDTO().getCode();
        System.out.println("Mortgage list: " +
                vehicle.getGuaranteeLetterDTO()
                        .getCreditContractDTO()
                        .getMortgageContractIds());
        return vehicle.getGuaranteeLetterDTO()
                .getCreditContractDTO()
                .getMortgageContractIds()
                .stream()
                .filter(m -> m.getManufacturerDTO() != null
                        && manufacturerCode.equalsIgnoreCase(
                        m.getManufacturerDTO().getCode()))
                .findFirst()
                .orElse(null);
    }
}
