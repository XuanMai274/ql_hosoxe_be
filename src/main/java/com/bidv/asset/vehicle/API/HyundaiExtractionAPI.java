package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.ExtractionResult;
import com.bidv.asset.vehicle.Service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/officer/extract/hyundai")
public class HyundaiExtractionAPI {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private com.bidv.asset.vehicle.Service.VehicleCatalogService vehicleCatalogService;

    private String normalizeDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty())
            return dateStr;
        // Chuyển dd/MM/yyyy sang yyyy-MM-dd cho frontend type="date"
        if (dateStr.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
            String[] parts = dateStr.split("/");
            String d = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
            String m = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
            return parts[2] + "-" + m + "-" + d;
        }
        return dateStr;
    }

    @PostMapping("/excel")
    public ResponseEntity<ExtractionResult> extractExcel(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "excelFile", required = false) MultipartFile excelFile) {

        MultipartFile finalFile = (file != null && !file.isEmpty()) ? file : excelFile;

        if (finalFile == null || finalFile.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ExtractionResult(false, null,
                            "Không tìm thấy file Excel. Vui lòng gửi file với tên field là 'file' hoặc 'excelFile'."));
        }

        String filename = finalFile.getOriginalFilename().toLowerCase();
        if (!(filename.endsWith(".xlsx") || filename.endsWith(".xls"))) {
            return ResponseEntity.badRequest()
                    .body(new ExtractionResult(false, null, "Vui lòng upload file Excel hợp lệ (.xlsx, .xls)."));
        }
        try {
            List<Map<String, Object>> rawData = excelService.extractExcel(finalFile);

            // Mapping Vietnamese column names to English keys
            Map<String, String> keyMapping = new java.util.HashMap<>();
            keyMapping.put("tên xe", "vehicleDescription");
            keyMapping.put("số chỗ", "numberOfSeats");
            keyMapping.put("số khung", "chassisNumber");
            keyMapping.put("vin", "chassisNumber");
            keyMapping.put("số máy", "engineNumber");
            keyMapping.put("số bảo lãnh", "guaranteeNumber");
            keyMapping.put("hợp đồng", "contractNumber");
            keyMapping.put("hóa đơn htv", "invoiceNumber");
            keyMapping.put("hóa đơn vat", "invoiceNumber");
            keyMapping.put("hóa đơn", "invoiceNumber");
            keyMapping.put("số hóa đơn", "invoiceNumber");
            keyMapping.put("tên đại lý", "dealerName");
            keyMapping.put("đơn giá", "unitPrice");
            keyMapping.put("mã thư bảo lãnh", "guaranteeLetterCode");
            keyMapping.put("ngày hđ", "invoiceDate");

            // BƯỚC 1: PHÂN LOẠI VÀ CHUẨN BỊ DỮ LIỆU
            List<Map<String, Object>> mainRows = new java.util.ArrayList<>();
            Map<String, String> invoiceDateMap = new java.util.HashMap<>();

            for (Map<String, Object> row : rawData) {
                boolean isVehicleRow = false;
                boolean isInvoiceInfoRow = false;
                String vehicleInvNum = null;
                String extraInvNum = null;
                String invDateVal = null;

                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String k = entry.getKey().toLowerCase();
                    String v = entry.getValue() != null ? entry.getValue().toString().trim() : "";

                    if ((k.contains("số khung") || k.contains("vin")) && !v.isEmpty()) {
                        isVehicleRow = true;
                    }
                    if (k.contains("ngày hđ") && !v.isEmpty()) {
                        isInvoiceInfoRow = true;
                        invDateVal = normalizeDate(v);
                    }

                    // Ưu tiên Hóa đơn HTV cho dòng xe
                    if (k.contains("hóa đơn htv")) {
                        vehicleInvNum = v;
                    }
                    // Ưu tiên Hóa đơn VAT hoặc Số hóa đơn cho dòng thông tin hóa đơn
                    if (k.contains("hóa đơn vat") || k.contains("số hóa đơn") || k.equals("hóa đơn")) {
                        extraInvNum = v;
                    }

                    // Fallback chung nếu không tìm thấy cái đặc thù
                    if (k.contains("hóa đơn") && !v.isEmpty()) {
                        if (vehicleInvNum == null)
                            vehicleInvNum = v;
                        if (extraInvNum == null)
                            extraInvNum = v;
                    }
                }

                if (isVehicleRow) {
                    // Để join được, ta cần đảm bảo identifier trong mainRows là cái ta sẽ dùng để
                    // map
                    // Ở đây ta dùng vehicleInvNum (Hóa đơn HTV)
                    if (vehicleInvNum != null && !vehicleInvNum.isEmpty()) {
                        row.put("_joinKey", vehicleInvNum);
                        mainRows.add(row);
                    }
                } else if (isInvoiceInfoRow) {
                    // Lưu ngày vào map với key là extraInvNum (Hóa đơn VAT)
                    if (extraInvNum != null && !extraInvNum.isEmpty()) {
                        invoiceDateMap.put(extraInvNum, invDateVal);
                    }
                }
            }

            // BƯỚC 2: CHUYỂN ĐỔI KEY VÀ JOIN DỮ LIỆU
            Map<String, List<Map<String, Object>>> groupedData = new java.util.LinkedHashMap<>();

            for (Map<String, Object> row : mainRows) {
                String groupKey = "Other";
                Map<String, Object> englishRow = new java.util.LinkedHashMap<>();

                // Mapping keys
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String originalKey = entry.getKey();
                    String normalizedKey = originalKey.toLowerCase().replaceAll("\\s+", " ").trim();

                    if (normalizedKey.equals("stt") || normalizedKey.contains("stt"))
                        continue;

                    String englishKey = originalKey;
                    for (Map.Entry<String, String> mapEntry : keyMapping.entrySet()) {
                        if (normalizedKey.contains(mapEntry.getKey())) {
                            englishKey = mapEntry.getValue();
                            break;
                        }
                    }
                    englishRow.put(englishKey, entry.getValue());

                    if (englishKey.equals("guaranteeNumber")) {
                        Object val = entry.getValue();
                        if (val != null && !val.toString().trim().isEmpty()) {
                            groupKey = val.toString().trim();
                        }
                    }
                }

                // JOIN: Lấy ngày hóa đơn từ Table 2 (nếu có)
                Object joinKeyObj = row.get("_joinKey");
                String invStr = (joinKeyObj != null) ? joinKeyObj.toString().trim() : "";

                if (invStr.isEmpty()) {
                    Object currentInv = englishRow.get("invoiceNumber");
                    if (currentInv != null)
                        invStr = currentInv.toString().trim();
                }

                if (!invStr.isEmpty() && invoiceDateMap.containsKey(invStr)) {
                    englishRow.put("invoiceDate", invoiceDateMap.get(invStr));
                }

                // Tự động điền số chỗ ngồi
                Object seats = englishRow.get("numberOfSeats");
                String finalSeats = (seats != null) ? seats.toString().trim() : "";
                if (finalSeats.isEmpty()) {
                    Object desc = englishRow.get("vehicleDescription");
                    if (desc != null) {
                        Integer autoSeats = vehicleCatalogService.getSeatsByModelName(desc.toString());
                        if (autoSeats != null)
                            finalSeats = autoSeats.toString();
                    }
                }
                englishRow.put("numberOfSeats", finalSeats);

                groupedData.computeIfAbsent(groupKey, k -> new java.util.ArrayList<>()).add(englishRow);
            }

            return ResponseEntity.ok(
                    new ExtractionResult(true, groupedData, "Extraction successful with cross-table date matching."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ExtractionResult(false, null, e.getMessage()));
        }
    }
}
