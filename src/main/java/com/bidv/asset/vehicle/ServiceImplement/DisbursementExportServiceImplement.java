package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.DisbursementDTO;
import com.bidv.asset.vehicle.DTO.ManufacturerDTO;
import com.bidv.asset.vehicle.DTO.MortgageContractDTO;
import com.bidv.asset.vehicle.DTO.VehicleDTO;
import com.bidv.asset.vehicle.Mapper.VehicleMapper;
import com.bidv.asset.vehicle.Repository.MortgageContractRepository;
import com.bidv.asset.vehicle.Repository.VehicleRepository;
import com.bidv.asset.vehicle.Service.DisbursementExportService;
import com.bidv.asset.vehicle.Utill.VietnameseNumberUtil;
import com.bidv.asset.vehicle.entity.ManufacturerEntity;
import com.bidv.asset.vehicle.entity.MortgageContractEntity;
import com.bidv.asset.vehicle.entity.VehicleEntity;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisbursementExportServiceImplement implements DisbursementExportService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;
    @Autowired
    MortgageContractRepository mortgageContractRepository;
    private static final String TEMPLATE_PATH = "/templates/GiaiNgan/";

    @Override
    public byte[] exportDocx(String templateName, DisbursementDTO disbursementDTO, List<Long> vehicleIds) throws IOException {
        XWPFDocument doc = loadTemplate(TEMPLATE_PATH + templateName);

        List<VehicleDTO> vehicles = vehicleRepository.findAllById(vehicleIds)
                .stream().map(vehicleMapper::toDto).toList();
        BigDecimal totalVehicleAmount = vehicles.stream()
                .map(VehicleDTO::getGuaranteeAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ManufacturerDTO manufacturer = vehicles.stream()
                .map(VehicleDTO::getManufacturerDTO)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Map<String, String> data = buildData(disbursementDTO, totalVehicleAmount,manufacturer);
        
        // Thay thế trong toàn bộ document
        replaceAll(doc, data);
        
        // Thay thế bảng xe
        replaceVehicleTable(doc, vehicles);
        
        // Đảm bảo font chữ
        forceTimesNewRoman(doc);

        return writeDoc(doc);
    }

    @Override
    public Map<String, byte[]> exportAll(DisbursementDTO disbursementDTO, List<Long> vehicleIds) throws IOException {
        // Lấy hãng xe từ danh sách vehicleIds
        List<VehicleEntity> vehicles = vehicleRepository.findAllById(vehicleIds);

        if (vehicles.isEmpty()) {
            throw new RuntimeException("Không tìm thấy xe");
        }

        // Giả sử tất cả xe cùng hãng
        String manufacturer = vehicles.get(0).getManufacturerEntity().getCode();

        String contractTemplate;

        if (manufacturer.contains("hyundai")) {
            contractTemplate = "hop-dong-tin-dung-cu-the_hyundai.docx";
        } else if (manufacturer.contains("VINFAST")) {
            contractTemplate = "hop-dong-tin-dung-cu-the-hyundai.docx";
        }
        String[] templates = {
                "de-xuat-giai-ngan.docx",
                "duyet-ngan-hang.docx",
                "phieu-tiep-nhan-ho-so_K.docx",
                "phieu-tiep-nhan-ho-so.docx",
                "y-kien-quan-tri.docx"
        };

        Map<String, byte[]> results = new HashMap<>();
        for (String template : templates) {
            try {
                results.put(template, exportDocx(template, disbursementDTO, vehicleIds));
            } catch (Exception e) {
                System.err.println("Lỗi khi export template " + template + ": " + e.getMessage());
            }
        }
        return results;
    }

    @Override
    public Map<String, byte[]> exportSpecific(
            DisbursementDTO disbursementDTO,
            List<Long> vehicleIds
    ) throws IOException {
        // Lấy hãng xe từ danh sách vehicleIds
        List<VehicleEntity> vehicles = vehicleRepository.findAllById(vehicleIds);

        if (vehicles.isEmpty()) {
            throw new RuntimeException("Không tìm thấy xe");
        }

        // Giả sử tất cả xe cùng hãng
        String manufacturer = vehicles.get(0).getManufacturerEntity().getCode();

        String contractTemplate;

        if (manufacturer.contains("hyundai")) {
            contractTemplate = "hop-dong-tin-dung-cu-the_hyundai.docx";
        } else if (manufacturer.contains("VINFAST")) {
            contractTemplate = "hop-dong-tin-dung-cu-the-hyundai.docx";
        }
        String[] templates = {
                "phieu-tiep-nhan-ho-so_K.docx"
        };

        Map<String, byte[]> results = new LinkedHashMap<>();

        for (String template : templates) {
            try {
                results.put(template,
                        exportDocx(template, disbursementDTO, vehicleIds));
            } catch (Exception e) {
                System.err.println("Lỗi khi export template "
                        + template + ": " + e.getMessage());
            }
        }

        return results;
    }

    private Map<String, String> buildData(DisbursementDTO dto, BigDecimal totalVehicleAmount,ManufacturerDTO manufacturer){
        Map<String, String> map = new HashMap<>();
        
        map.put("{{loanContractNumber}}", safe(dto.getLoanContractNumber()));
        map.put("{{disbursementAmount}}", formatMoney(dto.getDisbursementAmount()));
        map.put("{{disbursementAmountText}}", VietnameseNumberUtil.toVietnamese(dto.getDisbursementAmount()));
        map.put("{{creditLimit}}", formatMoney(dto.getCreditLimit()));
        map.put("{{usedLimit}}", formatMoney(dto.getUsedLimit()));
        map.put("{{remainingLimit}}", formatMoney(dto.getRemainingLimit()));
        map.put("{{totalCollateral}}", formatMoney(dto.getTotalCollateralValue()));
        map.put("{{realEstate}}", formatMoney(dto.getRealEstateValue()));
        map.put("{{realEstateFactor}}", formatMoney(dto.getRealEstateValueAfterFactor()));
        map.put("{{collateralFactor}}", formatMoney(dto.getCollateralValueAfterFactor()));
        map.put("{{issuedGuaranteeBalance}}", formatMoney(dto.getIssuedGuaranteeBalance()));
        map.put("{{vehicleLoanBalance}}", formatMoney(dto.getVehicleLoanBalance()));
        map.put("{{realEstateLoanBalance}}", formatMoney(dto.getRealEstateLoanBalance()));
        map.put("{{disbursementDate}}", formatDate(dto.getDisbursementDate()));
        map.put("{{loan_term}}",safe(String.valueOf(dto.getLoanTerm())));
        map.put("{{dueDate}}",formatDate(dto.getDueDate()));
        map.put("{{loanDate}}",formatDate(dto.getStartDate()));
        BigDecimal tongTSBD=dto.getTotalCollateralValue().add(dto.getRealEstateValue());
        map.put("{{TONG_TSBD}}",formatMoney(tongTSBD));
        BigDecimal tongTSBDFactor=dto.getCollateralValueAfterFactor().add(dto.getRealEstateValueAfterFactor());
        map.put("{{TONG_HE}}",formatMoney(tongTSBDFactor));
        BigDecimal gttdAddVay=dto.getUsedLimit().add(dto.getDisbursementAmount());
        map.put("{{GHTDSD_VAY}}",formatMoney(gttdAddVay));
        map.put("{{total_vehicle}}", formatMoney(totalVehicleAmount));
        map.put("{{totalVehiclesCount}}", String.valueOf(dto.getTotalVehiclesCount() != null ? dto.getTotalVehiclesCount() : 0));
        map.put("{{withdrawnVehiclesCount}}", String.valueOf(dto.getWithdrawnVehiclesCount() != null ? dto.getWithdrawnVehiclesCount() : 0));
//        map.put("{{interestRate}}", dto.getInterestRate() != null ? dto.getInterestRate().multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString() + "%" : "");
        map.put("{{totalAmountPaid}}", formatMoney(dto.getTotalAmountPaid()));
        map.put("{{status}}", safe(dto.getStatus()));
        map.put("{{manufacturer_name}}",safe(manufacturer.getName()));
        map.put("{{manufacturer_code}}",safe(manufacturer.getCode()));
        BigDecimal rate = Optional.ofNullable(manufacturer.getGuaranteeRate())
                .orElse(BigDecimal.ZERO);

        BigDecimal result = rate.subtract(BigDecimal.TEN);

        map.put("{{gate}}", result.toPlainString());
        map.put("{{total_vehicle_text}}",
                VietnameseNumberUtil.toVietnamese(totalVehicleAmount));
        BigDecimal disbursementAmount = dto.getDisbursementAmount();
        BigDecimal loanTerm = BigDecimal.valueOf(dto.getLoanTerm()); // số ngày

        BigDecimal rateDiff = new BigDecimal("0.0114"); // 1.14%
        BigDecimal daysInYear = new BigDecimal("365");
        BigDecimal tongCL = dto.getInterestAmount();
        if (tongCL == null && disbursementAmount != null && loanTerm != null) {
            tongCL = disbursementAmount
                    .multiply(rateDiff)
                    .multiply(loanTerm)
                    .divide(daysInYear, 0, RoundingMode.HALF_UP);
        }
        map.put("{{TONG_CL}}", formatMoney(tongCL));
        map.put("{{interestAmount}}", formatMoney(tongCL));
        if (dto.getCreditContractDTO() != null) {
            map.put("{{HDTD}}", safe(dto.getCreditContractDTO().getContractNumber()));
            map.put("{{HDTD_DATE}}", formatDate(dto.getCreditContractDTO().getContractDate()));
            LocalDate hdtdDate = dto.getCreditContractDTO().getContractDate();  // ngày bắt đầu
            LocalDate hdtdDateExpired = hdtdDate.plusDays(365);  // +365 ngày
            map.put("{{HDTD_DATE_expired}}", formatDate(hdtdDateExpired));
        }

        if (dto.getMortgageContractDTO() != null) {
            map.put("{{HDBD}}", safe(dto.getMortgageContractDTO().getContractNumber()));
            map.put("{{HDBD_DATE}}", formatDate(dto.getMortgageContractDTO().getContractDate()));
        }


        LocalDate now = LocalDate.now();
        map.put("{{CURRENT_DATE}}", formatDate(now));
        map.put("{{CURRENT_DATE_TITLE}}", toVietnameseDate(now));
        map.put("{{CURRENT_DAY}}", String.valueOf(now.getDayOfMonth()));
        map.put("{{CURRENT_MONTH}}", String.valueOf(now.getMonthValue()));
        map.put("{{CURRENT_YEAR}}", String.valueOf(now.getYear()));
        // thay thế tất cả hợp đồng bảo đảm
        List<MortgageContractEntity> mortgageContracts =
                mortgageContractRepository
                        .findByCreditContracts_Id(dto.getCreditContractDTO().getId());
        map.put("{{MORTGAGE_CONTRACT_BLOCK}}", buildMortgageBlock(mortgageContracts));
        return map;
    }
    private String buildMortgageBlock(List<MortgageContractEntity> contracts) {

        if (contracts == null || contracts.isEmpty()) {
            return "";
        }

        return contracts.stream()
                .map(c -> String.format(
                        "+ Hợp đồng thế chấp %s ngày %s để bảo đảm cho các nghĩa vụ của Công ty tại Ngân hàng.",
                        safe(c.getContractNumber()),
                        formatDate(c.getContractDate())
                ))
                .collect(Collectors.joining("\n"));
    }
    private void replaceVehicleTable(XWPFDocument doc, List<VehicleDTO> vehicles) {
        for (XWPFTable table : doc.getTables()) {
            processTableRecursive(table, vehicles);
        }
    }

    private void processTableRecursive(XWPFTable table, List<VehicleDTO> vehicles) {
        for (int i = 0; i < table.getRows().size(); i++) {
            XWPFTableRow row = table.getRow(i);
            if (row.getCell(0) == null) continue;

            String firstCell = row.getCell(0).getText();
            if (firstCell != null && firstCell.toLowerCase().contains("{{stt}}")) {
                int templateIndex = i;
                for (int j = 0; j < vehicles.size(); j++) {
                    VehicleDTO v = vehicles.get(j);
                    XWPFTableRow newRow = table.insertNewTableRow(templateIndex + j);
                    copyRow(row, newRow);
                    
                    Map<String, String> vData = buildVehicleData(v, j + 1);
                    replaceRowPlaceholders(newRow, vData);
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

    private Map<String, String> buildVehicleData(VehicleDTO v, int stt) {
        Map<String, String> map = new HashMap<>();
        map.put("{{stt}}", String.valueOf(stt));
        map.put("{{vehicleName}}", safe(v.getVehicleName()));
        map.put("{{chassis}}", safe(v.getChassisNumber()));
        map.put("{{engine}}", safe(v.getEngineNumber()));
        map.put("{{model}}", safe(v.getModelType()));
        map.put("{{color}}", safe(v.getColor()));
        map.put("{{price}}", formatMoney(v.getGuaranteeAmount()));
        map.put("{{description}}", safe(v.getDescription()));
        map.put("{{gate}}",safe(String.valueOf(v.getGuaranteeLetterDTO().getManufacturerDTO().getGuaranteeRate())));
        map.put("{{TBL}}",safe(v.getGuaranteeLetterDTO().getGuaranteeNoticeNumber()));
        map.put("{{REF}}",safe(v.getGuaranteeLetterDTO().getReferenceCode()));
        return map;
    }

    private void replaceAll(XWPFDocument doc, Map<String, String> data) {
        for (XWPFParagraph p : doc.getParagraphs()) {
            replaceInParagraph(p, data);
        }
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                replaceRowPlaceholders(row, data);
            }
        }
        // Thay thế trong header/footer nếu cần (tùy chọn)
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

    private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> data) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) return;

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

        XWPFRun newRun = paragraph.createRun();
        if (oldRPr != null) {
            newRun.getCTR().setRPr(oldRPr);
        }
        newRun.setText(replaced);
    }
    // ngày thàng năm
    private String toVietnameseDate(LocalDate date) {
        if (date == null) return "";
        return "ngày " + date.getDayOfMonth()
                + " tháng " + date.getMonthValue()
                + " năm " + date.getYear();
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

    private void forceTimesNewRoman(XWPFDocument doc) {
        for (XWPFParagraph p : doc.getParagraphs()) {
            for (XWPFRun r : p.getRuns()) r.setFontFamily("Times New Roman");
        }
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) r.setFontFamily("Times New Roman");
                    }
                }
            }
        }
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

    private String safe(String value) { return value == null ? "" : value; }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "0";
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(value);
    }

    private String formatDate(LocalDate date) {
        if (date == null) return "";
        return String.format("%02d/%02d/%d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }
}
