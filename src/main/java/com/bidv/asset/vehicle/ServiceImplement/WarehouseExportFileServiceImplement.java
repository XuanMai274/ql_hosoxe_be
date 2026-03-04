package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.*;
import com.bidv.asset.vehicle.Mapper.VehicleMapper;
import com.bidv.asset.vehicle.Repository.MortgageContractRepository;
import com.bidv.asset.vehicle.Repository.VehicleRepository;
import com.bidv.asset.vehicle.Repository.WarehouseExportRepository;
import com.bidv.asset.vehicle.Service.WarehouseExportFileService;
import com.bidv.asset.vehicle.Utill.VietnameseNumberUtil;
import com.bidv.asset.vehicle.entity.MortgageContractEntity;
import com.bidv.asset.vehicle.entity.VehicleEntity;
import com.bidv.asset.vehicle.entity.WarehouseExportEntity;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabStop;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseExportFileServiceImplement implements WarehouseExportFileService {

    private final WarehouseExportRepository warehouseExportRepository;
    private final VehicleRepository vehicleRepository;
    private final MortgageContractRepository mortgageContractRepository;
    private final VehicleMapper vehicleMapper;

    private static final String TEMPLATE_PATH = "/templates/XuatKho/";

    @Override
    public Map<String, byte[]> exportAll(Long exportId, List<Long> vehicleIds) throws IOException {
        WarehouseExportEntity export = warehouseExportRepository.findById(exportId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy WarehouseExport"));

        List<VehicleEntity> vehicleEntities;
        if (vehicleIds == null || vehicleIds.isEmpty()) {
            vehicleEntities = export.getVehicles();
        } else {
            vehicleEntities = vehicleRepository.findAllById(vehicleIds);
        }

        if (vehicleEntities == null || vehicleEntities.isEmpty()) {
            throw new RuntimeException("Danh sách xe trống, không thể xuất file");
        }
        List<VehicleDTO> allVehicles = vehicleEntities.stream().map(vehicleMapper::toDto).toList();

        Map<String, byte[]> results = new HashMap<>();

        // Phân loại xe cho Officer
        // Logic:
        // - Xe inSafe = false hoặc hãng không phải VINFAST: Xuất như bình thường trong tất cả các file.
        // - Xe VINFAST và inSafe = true: Chỉ xuất trong "biên bản đề nghị giao trả" và "tờ trình xuất két".
        //   Các file khác (to-trinh, phieu-xuat-kho, de-nghi-thu-no, dang-ky-giao-dich) KHÔNG có xe này.

        List<VehicleDTO> vehiclesForGeneral = allVehicles.stream()
                .filter(v -> {
                    boolean isVinfast = v.getManufacturerDTO() != null && "VINFAST".equalsIgnoreCase(v.getManufacturerDTO().getCode());
                    if (isVinfast) {
                        return v.getInSafe() == null || !v.getInSafe();
                    }
                    return true;
                }).toList();

        List<VehicleDTO> vinfastInSafe = allVehicles.stream()
                .filter(v -> v.getManufacturerDTO() != null && "VINFAST".equalsIgnoreCase(v.getManufacturerDTO().getCode())
                        && v.getInSafe() != null && v.getInSafe())
                .toList();

        // 1. Các file chung (chỉ chứa xe inSafe=false hoặc non-Vinfast)
        String[] officerGeneralTemplates = {
                "to-trinh.docx",
                "phieu-xuat-kho.docx"
        };
        for (String template : officerGeneralTemplates) {
            if (!vehiclesForGeneral.isEmpty()) {
                results.put(template, generateDoc(template, export, vehiclesForGeneral, null));
            }
        }

        // 2. Biên bản đề nghị giao trả (Chứa TẤT CẢ xe)
        results.put("bien-ban-de-nghi-giao-tra.docx", generateDoc("bien-ban-de-nghi-giao-tra.docx", export, allVehicles, null));

        // 3. Tờ trình xuất két (Chỉ dành cho xe Vinfast có inSafe=true)
        if (!vinfastInSafe.isEmpty()) {
            results.put("xuat-ket.docx", generateDoc("xuat-ket.docx", export, vinfastInSafe, "VINFAST"));
        }

        // 4. Đăng ký giao dịch đảm bảo (Chỉ chứa xe inSafe=false)
        Map<String, List<VehicleDTO>> groupedByManufacturer = vehiclesForGeneral.stream()
                .filter(v -> v.getManufacturerDTO() != null)
                .collect(Collectors.groupingBy(v -> v.getManufacturerDTO().getCode()));

        if (groupedByManufacturer.containsKey("VINFAST")) {
            results.put("dang-ky-giao-dich-dam-bao-vinfast.docx",
                    generateDoc("dang-ky-giao-dich-dam-bao-vinfast.docx", export, groupedByManufacturer.get("VINFAST"), "VINFAST"));
        }
        if (groupedByManufacturer.containsKey("HYUNDAI")) {
            results.put("dang-ky-giao-dich-bao-dam-hyundai.docx",
                    generateDoc("dang-ky-giao-dich-bao-dam-hyundai.docx", export, groupedByManufacturer.get("HYUNDAI"), "HYUNDAI"));
        }
        // 5. Giấy đề nghị thu nợ cho tất cả các xe
        // 2. Biên bản đề nghị giao trả (Chứa TẤT CẢ xe)
        results.put("de-nghi-thu-no.docx", generateDoc("de-nghi-thu-no.docx", export, allVehicles, null));

        return results;
    }

    @Override
    public Map<String, byte[]> exportSpecific(Long exportId, List<Long> vehicleIds) throws IOException {
        WarehouseExportEntity export = warehouseExportRepository.findById(exportId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy WarehouseExport"));

        List<VehicleEntity> vehicleEntities;
        if (vehicleIds == null || vehicleIds.isEmpty()) {
            vehicleEntities = export.getVehicles();
        } else {
            vehicleEntities = vehicleRepository.findAllById(vehicleIds);
        }

        if (vehicleEntities == null || vehicleEntities.isEmpty()) {
            throw new RuntimeException("Danh sách xe trống, không thể xuất file");
        }
        List<VehicleDTO> allVehicles = vehicleEntities.stream().map(vehicleMapper::toDto).toList();

        Map<String, byte[]> results = new HashMap<>();

        // 1. Biên bản đề nghị giao trả: Luôn xuất đủ tất cả xe
        results.put("bien-ban-de-nghi-giao-tra.docx", generateDoc("bien-ban-de-nghi-giao-tra.docx", export, allVehicles, null));

        // 2. Đăng ký giao dịch đảm bảo:
        // - Với Vinfast: Chỉ lọc những xe có inSafe = false (hoặc null)
        // - Các hãng khác: Giữ nguyên
        List<VehicleDTO> vehiclesForRegistration = allVehicles.stream()
                .filter(v -> {
                    boolean isVinfast = v.getManufacturerDTO() != null && "VINFAST".equalsIgnoreCase(v.getManufacturerDTO().getCode());
                    if (isVinfast) {
                        return v.getInSafe() == null || !v.getInSafe();
                    }
                    return true;
                }).toList();

        Map<String, List<VehicleDTO>> groupedByManufacturer = vehiclesForRegistration.stream()
                .filter(v -> v.getManufacturerDTO() != null)
                .collect(Collectors.groupingBy(v -> v.getManufacturerDTO().getCode()));

        if (groupedByManufacturer.containsKey("VINFAST")) {
            results.put("dang-ky-giao-dich-dam-bao-vinfast.docx",
                    generateDoc("dang-ky-giao-dich-dam-bao-vinfast.docx", export, groupedByManufacturer.get("VINFAST"), "VINFAST"));
        }
        if (groupedByManufacturer.containsKey("HYUNDAI")) {
            results.put("dang-ky-giao-dich-bao-dam-hyundai.docx",
                    generateDoc("dang-ky-giao-dich-bao-dam-hyundai.docx", export, groupedByManufacturer.get("HYUNDAI"), "HYUNDAI"));
        }

        return results;
    }

    private byte[] generateDoc(String templateName, WarehouseExportEntity export, List<VehicleDTO> vehicles, String manufacturerCode) throws IOException {
        XWPFDocument doc = loadTemplate(TEMPLATE_PATH + templateName);
        if ("de-nghi-thu-no.docx".equals(templateName)) {
            replaceDebtRequestTable(doc, vehicles);
        }
        // Tìm DisbursementDTO từ danh sách xe
        DisbursementDTO disbursementDTO = null;
        if (vehicles != null) {
            for (VehicleDTO v : vehicles) {
                if (v.getLoan() != null && v.getLoan().getDisbursementDTO() != null) {
                    disbursementDTO = v.getLoan().getDisbursementDTO();
                    break;
                }
            }
        }

        // ManufacturerDTO nếu có manufacturerCode
        ManufacturerDTO manufacturerDTO = null;
        if (manufacturerCode != null && vehicles != null) {
            manufacturerDTO = vehicles.stream()
                    .map(VehicleDTO::getManufacturerDTO)
                    .filter(m -> m != null && manufacturerCode.equals(m.getCode()))
                    .findFirst().orElse(null);
        }
        BigDecimal totalVehicleAmountHDMB = vehicles.stream()
                .map(VehicleDTO::getPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalVehicleAmountVay = vehicles.stream()
                .map(VehicleDTO::getGuaranteeAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, String> data = buildData(export, vehicles, manufacturerDTO, disbursementDTO,totalVehicleAmountHDMB,totalVehicleAmountVay);
        
        replaceAll(doc, data);
        if (!"de-nghi-thu-no.docx".equals(templateName)) {
            replaceVehicleTable(doc, vehicles);
        }
        replaceLoanDetailBlock(doc, vehicles);
        forceTimesNewRoman(doc);

        return writeDoc(doc);
    }

    private Map<String, String> buildData(WarehouseExportEntity export, List<VehicleDTO> vehicles, ManufacturerDTO manufacturer, DisbursementDTO dto,BigDecimal totalVehicleAmountHDMB,BigDecimal totalVehicleAmountVay) {
        Map<String, String> map = new HashMap<>();
        
        map.put("{{exportNumber}}", safe(export.getExportNumber()));
        map.put("{{exportDate}}", formatDate(export.getExportDate() != null ? export.getExportDate().toLocalDate() : null));
        map.put("{{requestDate}}", formatDate(export.getRequestDate() != null ? export.getRequestDate().toLocalDate() : null));
//        map.put("{{description}}", safe(export.getDescription()));
        
        BigDecimal totalCollateral = Optional.ofNullable(export.getTotalCollateralValue()).orElse(BigDecimal.ZERO);
        map.put("{{totalCollateral}}", formatMoney(totalCollateral));
        map.put("{{totalCollateralText}}", VietnameseNumberUtil.toVietnamese(totalCollateral));
        map.put("realEstate",formatMoney(export.getRealEstateValue()));
        BigDecimal totalDebt = Optional.ofNullable(export.getTotalDebtCollection()).orElse(BigDecimal.ZERO);
        map.put("{{totalDebt}}", formatMoney(totalDebt));
        map.put("{{totalDebtText}}", VietnameseNumberUtil.toVietnamese(totalDebt));
        map.put("{{tongHDMB}}",formatMoney(totalVehicleAmountHDMB));
        BigDecimal realEstate = Optional.ofNullable(export.getRealEstateValue()).orElse(BigDecimal.ZERO);
        map.put("{{realEstate}}", formatMoney(realEstate));
       // map.put("TONG_TSBD",formatMoney(totalCollateral.add(realEstate)));
        map.put("{{TONG_TSBD}}", formatMoney(totalCollateral.add(realEstate)));
        //Tổng của vay(75%/85%)
        map.put("{{tong}}",formatMoney(totalVehicleAmountVay));
        if (!vehicles.isEmpty()) {
            VehicleDTO refVehicle = vehicles.get(0);
            
            if (refVehicle.getLoan() != null) {
                map.put("{{loanContractNumber}}", safe(refVehicle.getLoan().getLoanContractNumber()));
                map.put("{{loanDate}}", formatDate(refVehicle.getLoan().getLoanDate()));
                map.put("{{dueDate}}", formatDate(refVehicle.getLoan().getDueDate()));
            }

            if (manufacturer != null) {
                ManufacturerDTO finalManufacturer = manufacturer;
                manufacturer = vehicles.stream()
                        .map(VehicleDTO::getManufacturerDTO)
                        .filter(m -> m != null && finalManufacturer.getCode().equals(m.getCode()))
                        .findFirst().orElse(refVehicle.getManufacturerDTO());
            } else {
                manufacturer = refVehicle.getManufacturerDTO();
            }

            if (manufacturer != null) {
                map.put("{{manufacturerName}}", safe(manufacturer.getName()));
                map.put("{{manufacturer_name}}", safe(manufacturer.getName()));
                map.put("{{manufacturer_code}}", safe(manufacturer.getCode()));

                BigDecimal rate = Optional.ofNullable(manufacturer.getGuaranteeRate())
                        .orElse(BigDecimal.ZERO);

                BigDecimal percent = rate.multiply(BigDecimal.valueOf(100));

                map.put("{{gate}}",
                        percent.stripTrailingZeros().toPlainString());

                Long customerId = null;
                if (refVehicle.getLoan() != null && refVehicle.getLoan().getCustomerDTO() != null) {
                    customerId = refVehicle.getLoan().getCustomerId();
                } else if (refVehicle.getGuaranteeLetterDTO() != null && refVehicle.getGuaranteeLetterDTO().getCreditContractDTO() != null
                        && refVehicle.getGuaranteeLetterDTO().getCreditContractDTO().getCustomerId() != null) {
                    customerId = refVehicle.getGuaranteeLetterDTO().getCreditContractDTO().getCustomerId();
                }
                
                if (customerId != null) {
                    Optional<MortgageContractEntity> mc = mortgageContractRepository.findFirstByCustomerIdAndManufacturerIdAndStatus(
                            customerId, manufacturer.getId(), "ACTIVE");
                    if (mc.isPresent()) {
                        map.put("{{HDBD}}", safe(mc.get().getContractNumber()));
                        map.put("{{HDBD_DATE}}", formatDate(mc.get().getContractDate()));
                    }
                }
            }
        }

        // Bổ sung các trường từ DisbursementDTO (dto)
        if (dto != null) {
            map.put("{{disbursementAmount}}", formatMoney(dto.getDisbursementAmount()));
            map.put("{{disbursementAmountText}}", VietnameseNumberUtil.toVietnamese(dto.getDisbursementAmount()));
            map.put("{{creditLimit}}", formatMoney(dto.getCreditLimit()));
            map.put("{{usedLimit}}", formatMoney(dto.getUsedLimit()));
            map.put("{{remainingLimit}}", formatMoney(dto.getRemainingLimit()));
            map.put("{{realEstateFactor}}", formatMoney(dto.getRealEstateValueAfterFactor()));
            map.put("{{totalCollateralValue}}", formatMoney(dto.getTotalCollateralValue()));
            map.put("{{collateralFactor}}", formatMoney(dto.getCollateralValueAfterFactor()));
            map.put("{{issuedGuaranteeBalance}}", formatMoney(dto.getIssuedGuaranteeBalance()));
            map.put("{{vehicleLoanBalance}}", formatMoney(dto.getVehicleLoanBalance()));
            map.put("{{realEstateLoanBalance}}", formatMoney(dto.getRealEstateLoanBalance()));
            map.put("{{disbursementDate}}", formatDate(dto.getDisbursementDate()));
            map.put("{{loan_term}}", safe(String.valueOf(dto.getLoanTerm())));
            
            BigDecimal tongTSBDFactor = (dto.getCollateralValueAfterFactor()).add((dto.getRealEstateValueAfterFactor()));
            map.put("{{TONG_HE}}", formatMoney(tongTSBDFactor));
            
            BigDecimal gttdAddVay = (dto.getUsedLimit()).add((dto.getDisbursementAmount()));
            map.put("{{GHTDSD_VAY}}", formatMoney(gttdAddVay));
            
            BigDecimal totalVehicleAmount = vehicles.stream()
                    .map(VehicleDTO::getGuaranteeAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            map.put("{{total_vehicle}}", formatMoney(totalVehicleAmount));
            map.put("{{total_vehicle_text}}", VietnameseNumberUtil.toVietnamese(totalVehicleAmount));
            
            map.put("{{totalVehiclesCount}}", String.valueOf(dto.getTotalVehiclesCount() != null ? dto.getTotalVehiclesCount() : 0));
            map.put("{{withdrawnVehiclesCount}}", String.valueOf(dto.getWithdrawnVehiclesCount() != null ? dto.getWithdrawnVehiclesCount() : 0));
            map.put("{{totalAmountPaid}}", formatMoney(dto.getTotalAmountPaid()));
            map.put("{{status}}", safe(dto.getStatus()));

            BigDecimal interestAmount = dto.getInterestAmount();
            if (interestAmount == null && dto.getDisbursementAmount() != null && dto.getLoanTerm() != null) {
                BigDecimal rateDiff = new BigDecimal("0.0114"); // 1.14%
                BigDecimal daysInYear = new BigDecimal("365");
                interestAmount = dto.getDisbursementAmount()
                        .multiply(rateDiff)
                        .multiply(BigDecimal.valueOf(dto.getLoanTerm()))
                        .divide(daysInYear, 0, RoundingMode.HALF_UP);
            }
            map.put("{{TONG_CL}}", formatMoney(interestAmount));
            map.put("{{interestAmount}}", formatMoney(interestAmount));

            if (dto.getCreditContractDTO() != null) {
                map.put("{{HDTD}}", safe(dto.getCreditContractDTO().getContractNumber()));
                map.put("{{HDTD_DATE}}", formatDate(dto.getCreditContractDTO().getContractDate()));
                LocalDate hdtdDate = dto.getCreditContractDTO().getContractDate();
                LocalDate hdtdDateExpired = hdtdDate.plusDays(365);
                map.put("{{HDTD_DATE_expired}}", formatDate(hdtdDateExpired));
                
                List<MortgageContractEntity> mortgageContracts = mortgageContractRepository.findByCreditContracts_Id(dto.getCreditContractDTO().getId());
                map.put("{{MORTGAGE_CONTRACT_BLOCK}}", buildMortgageBlockFromEntities(mortgageContracts));
            }
        }

//        map.put("{{LOAN_DETAIL_BLOCK}}", buildLoanDetailBlock(vehicles));
        map.put("{{MORTGAGE_BLOCK}}", buildMortgageBlock(vehicles));
        LocalDate now = LocalDate.now();
        map.put("{{CURRENT_DATE}}", formatDate(now));
        map.put("{{CURRENT_DATE_TITLE}}", toVietnameseDate(now));
        map.put("{{CURRENT_DAY}}", String.valueOf(now.getDayOfMonth()));
        map.put("{{CURRENT_MONTH}}", String.valueOf(now.getMonthValue()));
        map.put("{{CURRENT_YEAR}}", String.valueOf(now.getYear()));
        // gán dữ liệu cho đơn đăng kí giao dịch đảm bảo
        VehicleDTO first = vehicles.get(0);

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
        return map;
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

            if (firstCell != null && firstCell.contains("{{stt}}")) {

                int templateIndex = i;

                for (int j = 0; j < vehicles.size(); j++) {

                    VehicleDTO v = vehicles.get(j);

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

        // xử lý nested table
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                for (XWPFTable nested : cell.getTables()) {
                    processTableRecursive(nested, vehicles);
                }
            }
        }
    }
//    private void processTableRecursive(XWPFTable table, List<VehicleDTO> vehicles) {
//        for (int i = 0; i < table.getRows().size(); i++) {
//            XWPFTableRow templateRow = table.getRow(i);
//            if (templateRow.getCell(0) == null) continue;
//
//            String firstCellText = templateRow.getCell(0).getText();
//            if (firstCellText == null || !firstCellText.contains("{{stt}}")) continue;
//
//            int templateIndex = i;
//            BigDecimal totalPrincipal = BigDecimal.ZERO;
//            BigDecimal totalInterest = BigDecimal.ZERO;
//
//            for (int j = 0; j < vehicles.size(); j++) {
//                VehicleDTO v = vehicles.get(j);
//                XWPFTableRow newRow = table.insertNewTableRow(templateIndex + j);
//                copyRow(templateRow, newRow);
//                LoanDTO loan=v.getLoan();
//                Map<String, String> rowData = buildVehicleData(v, j + 1);
//                replaceRowPlaceholders(newRow, rowData);
//
//                totalPrincipal = totalPrincipal.add(Optional.ofNullable(v.getPrice()).orElse(BigDecimal.ZERO));
//
//                BigDecimal interest = BigDecimal.ZERO;
//                if (v.getLoan() != null && v.getLoan().getDisbursementDTO() != null) {
//                    interest = Optional.ofNullable(v.getLoan().getDisbursementDTO().getInterestAmount()).orElse(BigDecimal.ZERO);
//                }
//                totalInterest = totalInterest.add(interest);
//            }
//
////            // 2. Thêm dòng tổng cộng nếu không phải bảng con đặc biệt (Thực hiện TRƯỚC khi xóa templateRow)
////            boolean isRegistrationTable = table.getText() != null && table.getText().contains("{{chassis}}") && table.getText().contains("{{engine}}");
////            if (!isRegistrationTable) {
////                XWPFTableRow totalRow = table.insertNewTableRow(templateIndex + vehicles.size());
////                copyRow(templateRow, totalRow);
////                Map<String, String> totalMap = new HashMap<>();
////                totalMap.put("{{stt}}", "");
////                totalMap.put("{{vehicleName}}", "Tổng cộng");
////                totalMap.put("{{price}}", formatMoney(totalPrincipal));
////                totalMap.put("{{interestRate}}", formatMoney(totalInterest));
////
////                replaceRowPlaceholders(totalRow, totalMap);
////            }
//
//            // 3. Xóa dòng template sau khi đã sử dụng để copy sang tất cả các dòng (kể cả dòng tổng cộng)
//            table.removeRow(templateIndex + vehicles.size());
//            break;
//        }
//
//        for (XWPFTableRow row : table.getRows()) {
//            for (XWPFTableCell cell : row.getTableCells()) {
//                List<XWPFTable> nestedTables = cell.getTables();
//                if (nestedTables != null) {
//                    for (XWPFTable nested : nestedTables) {
//                        processTableRecursive(nested, vehicles);
//                    }
//                }
//            }
//        }
//    }

    private Map<String, String> buildVehicleData(VehicleDTO v, int stt) {
        Map<String, String> map = new HashMap<>();
        map.put("{{stt}}", String.valueOf(stt));
        map.put("{{vehicleName}}", safe(v.getVehicleName()));
        map.put("{{chassis}}", safe(v.getChassisNumber()));
        map.put("{{engine}}", safe(v.getEngineNumber()));
        map.put("{{model}}", safe(v.getModelType()));
        map.put("{{color}}", safe(v.getColor()));
        map.put("{{price}}", formatMoney(v.getGuaranteeAmount()));
        map.put("{{guaranteeAmount}}", formatMoney(v.getGuaranteeAmount()));
        map.put("{{priceHDMB}}",formatMoney(v.getPrice()));
        map.put("{{description}}", safe(v.getDescription()));
        map.put("{{importDossier}}",safe(v.getImportDossier()));
        map.put("{{manufacturer}}",safe(v.getManufacturerDTO().getCode()));
        map.put("{{doc_id}}",safe(v.getLoan() != null

                ? safe(v.getLoan().getDocId())
                : " "));
        map.put("{{accountNumber}}",
                v.getLoan() != null
                        ? safe(v.getLoan().getAccountNumber())
                        : " ");
        // ===== XỬ LÝ LÃI THEO TRẠNG THÁI =====
        BigDecimal interestToShow = null;

        if (v.getLoan() != null) {

            DisbursementDTO d = v.getLoan().getDisbursementDTO();

            if (d != null && d.getStatus() != null) {

                String status = String.valueOf(d.getStatus()).trim();

                if ("PAID_OFF".equalsIgnoreCase(status)) {
                    interestToShow = d.getInterestAmount();
                }
            }
        }

        map.put("{{interestAmount}}",
                interestToShow != null
                        ? formatMoney(interestToShow)
                        : "-");

        map.put("{{payment_note}}", buildPaymentNote(v));

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
        if (source == null || target == null) return;
        
        try {
            if (source.getCtRow().isSetTrPr() && source.getCtRow().getTrPr() != null) {
                target.getCtRow().setTrPr((org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr) source.getCtRow().getTrPr().copy());
            }
        } catch (Exception ignored) {}

        // Đồng bộ số lượng ô
        List<XWPFTableCell> sourceCells = source.getTableCells();
        while (target.getTableCells().size() < sourceCells.size()) {
            target.addNewTableCell();
        }

        for (int i = 0; i < sourceCells.size(); i++) {
            XWPFTableCell sourceCell = sourceCells.get(i);
            XWPFTableCell targetCell = target.getCell(i);
            
            if (sourceCell == null || targetCell == null) continue;

            // Copy định dạng ô
            if (sourceCell.getCTTc().isSetTcPr()) {
                targetCell.getCTTc().setTcPr((org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr) sourceCell.getCTTc().getTcPr().copy());
            }

            // Xóa nội dung mặc định của ô đích an toàn
            int pSize = targetCell.getParagraphs().size();
            for (int p = pSize - 1; p >= 0; p--) {
                targetCell.removeParagraph(p);
            }

            // Copy từng đoạn văn
            for (XWPFParagraph sourceP : sourceCell.getParagraphs()) {
                XWPFParagraph targetP = targetCell.addParagraph();
                if (sourceP.getCTP().isSetPPr()) {
                    targetP.getCTP().setPPr((org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr) sourceP.getCTP().getPPr().copy());
                }
                
                for (XWPFRun sourceR : sourceP.getRuns()) {
                    XWPFRun targetR = targetP.createRun();
                    // Copy định dạng Run (Font, Size, Color, etc.)
                    if (sourceR.getCTR().isSetRPr()) {
                        targetR.getCTR().setRPr((org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr) sourceR.getCTR().getRPr().copy());
                    }
                    targetR.setText(sourceR.text());
                }
            }
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
    private List<String> buildLoanDetailLines(List<VehicleDTO> vehicles) {

        List<String> lines = new ArrayList<>();

        for (VehicleDTO v : vehicles) {

            if (v.getLoan() == null) continue;

            String contractNumber = safe(v.getLoan().getLoanContractNumber());
            String loanDate = formatDate(v.getLoan().getLoanDate());
            BigDecimal amount = Optional.ofNullable(v.getGuaranteeAmount())
                    .orElse(BigDecimal.ZERO);

            lines.add("Số hợp đồng tín dụng cụ thể: " + contractNumber + " ngày " + loanDate);
            lines.add("Tổng số tiền trả nợ:\t\t\t" + formatMoney(amount) + " đồng");
            lines.add("Trong đó:");
            lines.add("\t+ Trả món vay ứng trước:\t- đồng");
            lines.add("\t+ Trả món vay thanh toán tiền còn lại:\t" + formatMoney(amount) + " đồng");
            lines.add("\t+ Trả lãi liên quan:\t- đồng");
            lines.add(""); // dòng trống giữa các hợp đồng
        }

        return lines;
    }
    private String buildMortgageBlock(List<VehicleDTO> vehicles) {

        Map<String, LocalDate> mortgageMap = new LinkedHashMap<>();

        for (VehicleDTO v : vehicles) {

            if (v.getGuaranteeLetterDTO().getMortgageContractDTO().getContractNumber() == null) continue;

            mortgageMap.put(
                    v.getGuaranteeLetterDTO().getMortgageContractDTO().getContractNumber(),
                    v.getGuaranteeLetterDTO().getMortgageContractDTO().getContractDate()
            );
        }

        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, LocalDate> entry : mortgageMap.entrySet()) {

            sb.append("- Căn cứ Hợp đồng thế chấp tài sản số ")
                    .append(entry.getKey())
                    .append(" ngày ")
                    .append(formatDate(entry.getValue()))
                    .append(" và các văn bản sửa đổi bổ sung kèm theo ký giữa Công ty CP XNK TM Huy Tiến Dũng và BIDV Đồng Tháp.")
                    .append("\n");
        }

        return sb.length() > 0
                ? sb.append("\n").toString()
                : "";
    }
    private String buildPaymentNote(VehicleDTO vehicle) {

        if (vehicle == null || vehicle.getLoan() == null) return "";

        LoanDTO loan = vehicle.getLoan();
        DisbursementDTO dis = loan.getDisbursementDTO();

        if (dis == null) return "";

        String contractNumber = safe(loan.getLoanContractNumber());
        String loanDate = formatDate(loan.getLoanDate());
        String chassis = safe(vehicle.getChassisNumber());

        // Lấy 6 ký tự cuối
        if (chassis.length() > 6) {
            chassis = chassis.substring(chassis.length() - 6);
        }

        BigDecimal principal = Optional.ofNullable(dis.getDisbursementAmount())
                .orElse(BigDecimal.ZERO);

        BigDecimal interest = Optional.ofNullable(dis.getInterestAmount())
                .orElse(BigDecimal.ZERO);

        if ("PAID_OFF".equalsIgnoreCase(dis.getStatus())) {

            return String.format(
                    "Thu tất toán hợp đồng số %s ngày %s; SK: %s; gốc %s; lãi: %s",
                    contractNumber,
                    loanDate,
                    chassis,
                    formatMoney(principal),
                    formatMoney(interest)
            );
        }

        if ("ACTIVE".equalsIgnoreCase(dis.getStatus())) {

            return String.format(
                    "Thu một phần nợ gốc của HD số %s ngày %s",
                    contractNumber,
                    loanDate
            );
        }

        return "";
    }
    private void replaceLoanDetailBlock(XWPFDocument doc, List<VehicleDTO> vehicles) {

        for (XWPFParagraph paragraph : doc.getParagraphs()) {

            String text = paragraph.getText();
            if (text == null || !text.contains("{{LOAN_DETAIL_BLOCK}}")) continue;

            // Xóa run cũ
            for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                paragraph.removeRun(i);
            }

            // Thêm nội dung chuẩn format
            insertLoanBlock(paragraph, vehicles);
        }
    }
    private void insertLoanBlock(XWPFParagraph paragraph, List<VehicleDTO> vehicles) {
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        // 🔹 Tạo right tab stop sát lề phải
        CTTabStop tabStop = paragraph.getCTP()
                .getPPr()
                .addNewTabs()
                .addNewTab();

        tabStop.setVal(STTabJc.RIGHT);
        tabStop.setPos(BigInteger.valueOf(9000));
        // 9000 ~ sát lề phải A4 (có thể chỉnh 8500-9500 tùy margin)

        for (VehicleDTO v : vehicles) {

            if (v.getLoan() == null) continue;

            String contractNumber = safe(v.getLoan().getLoanContractNumber());
            String loanDate = formatDate(v.getLoan().getLoanDate());
            BigDecimal amount = Optional.ofNullable(v.getGuaranteeAmount())
                    .orElse(BigDecimal.ZERO);

            // 1️ Số hợp đồng
            XWPFRun r1 = paragraph.createRun();
            r1.setText("Số hợp đồng tín dụng cụ thể: "
                    + contractNumber + " ngày " + loanDate);
            r1.addBreak();

            // 2️ Tổng tiền
            XWPFRun r2 = paragraph.createRun();
            r2.setText("Tổng số tiền trả nợ:");
            r2.addTab(); // 👈 chỉ 1 tab
            r2.setText(formatMoney(amount) + " đồng");
            r2.addBreak();

            // 3️ Trong đó
            XWPFRun r3 = paragraph.createRun();
            r3.setText("Trong đó:");
            r3.addBreak();

            // 4️ Ứng trước
            XWPFRun r4 = paragraph.createRun();
            r4.setText("+ Trả món vay ứng trước:");
            r4.addTab();
            r4.setText("- đồng");
            r4.addBreak();

            // 5 Thanh toán còn lại
            XWPFRun r5 = paragraph.createRun();
            r5.setText("+ Trả món vay thanh toán tiền còn lại:");
            r5.addTab();
            r5.setText(formatMoney(amount) + " đồng");
            r5.addBreak();

            // 6️ Lãi
            XWPFRun r6 = paragraph.createRun();
            r6.setText("+ Trả lãi liên quan:");
            r6.addTab();
            r6.setText("- đồng");
            r6.addBreak();
            r6.addBreak();;
        }
    }
    private String buildMortgageBlockFromEntities(List<MortgageContractEntity> contracts) {
        if (contracts == null || contracts.isEmpty()) return "";
        return contracts.stream()
                .map(c -> String.format(
                        "+ Hợp đồng thế chấp %s ngày %s để bảo đảm cho các nghĩa vụ của Công ty tại Ngân hàng.",
                        safe(c.getContractNumber()),
                        formatDate(c.getContractDate())
                ))
                .collect(Collectors.joining("\n"));
    }

    private BigDecimal nvl(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }

    private String toVietnameseDate(LocalDate date) {
        if (date == null) return "";
        return "ngày " + date.getDayOfMonth()
                + " tháng " + date.getMonthValue()
                + " năm " + date.getYear();
    }
    // phần dành cho giấy thu nợ
    private void replaceDebtRequestTable(XWPFDocument doc, List<VehicleDTO> vehicles) {
        if (vehicles == null || vehicles.isEmpty()) return;

        for (XWPFTable table : doc.getTables()) {
            for (int i = 0; i < table.getRows().size(); i++) {
                XWPFTableRow templateRow = table.getRow(i);
                if (templateRow.getCell(0) == null) continue;

                String firstCell = templateRow.getCell(0).getText();
                if (firstCell == null || !firstCell.contains("{{stt}}")) continue;

                int templateIndex = i;
                int rowIndex = 0;

                for (int j = 0; j < vehicles.size(); j++) {
                    VehicleDTO v = vehicles.get(j);
                    if (v.getLoan() == null) continue;

                    rowIndex++;
                    DisbursementDTO dis = v.getLoan().getDisbursementDTO();
                    boolean isPaidOff = dis != null && "PAID_OFF".equalsIgnoreCase(dis.getStatus());
                    boolean isLast = (j == vehicles.size() - 1);

                    XWPFTableRow newRow = table.insertNewTableRow(templateIndex + rowIndex - 1);
                    copyRow(templateRow, newRow);

                    Map<String, String> rowData = new HashMap<>();
                    rowData.put("{{stt}}", String.valueOf(rowIndex));
                    rowData.put("{{accountNumber}}", safe(v.getLoan().getAccountNumber()));

                    BigDecimal vehiclePrincipal = Optional.ofNullable(v.getGuaranteeAmount()).orElse(BigDecimal.ZERO);
                    rowData.put("{{price}}", formatMoney(vehiclePrincipal));

                    BigDecimal interest = BigDecimal.ZERO;
                    if (isPaidOff && isLast) {
                        interest = dis.getInterestAmount() != null ? dis.getInterestAmount() : BigDecimal.ZERO;
                    }
                    rowData.put("{{interestAmount}}", formatMoney(interest));

                    String note = "";
                    String contractNo = safe(v.getLoan().getLoanContractNumber());
                    String loanDate = formatDate(v.getLoan().getLoanDate());
                    String chassis = safe(v.getChassisNumber());
                    if (chassis.length() > 6) chassis = chassis.substring(chassis.length() - 6);

                    if (isPaidOff && isLast) {
                        BigDecimal totalDisPrincipal = dis.getDisbursementAmount();
                        note = String.format("Thu tất toán hợp đồng số %s ngày %s; SK: %s; gốc %s; lãi: %s",
                                contractNo, loanDate, chassis, formatMoney(totalDisPrincipal), formatMoney(interest));
                    } else {
                        note = String.format("Thu một phần nợ gốc của HD số %s ngày %s; SK: %s",
                                contractNo, loanDate, chassis);
                    }
                    rowData.put("{{payment_note}}", note);

                    replaceRowPlaceholders(newRow, rowData);
                }

                table.removeRow(templateIndex + vehicles.size());
                return;
            }
        }
    }
}
