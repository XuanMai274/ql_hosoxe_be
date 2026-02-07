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
            keyMapping.put("stt", "index");
            keyMapping.put("tên xe", "vehicleDescription");
            keyMapping.put("số khung", "chassisNumber");
            keyMapping.put("vin", "chassisNumber");
            keyMapping.put("số máy", "engineNumber");
            keyMapping.put("số bảo lãnh", "guaranteeNumber");
            keyMapping.put("hợp đồng", "contractNumber");
            keyMapping.put("hóa đơn", "invoiceNumber");
            keyMapping.put("tên đại lý", "dealerName");
            keyMapping.put("đơn giá", "unitPrice");
            keyMapping.put("mã thư bảo lãnh", "guaranteeLetterCode");

            // Nhóm dữ liệu theo cột "guaranteeNumber"
            Map<String, List<Map<String, Object>>> groupedData = new java.util.LinkedHashMap<>();

            for (Map<String, Object> row : rawData) {
                String groupKey = "Other";
                Map<String, Object> englishRow = new java.util.LinkedHashMap<>();

                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String originalKey = entry.getKey();
                    String lowerKey = originalKey.toLowerCase();
                    String englishKey = originalKey; // Default to original if no mapping found

                    // Find matching English key from mapping
                    for (Map.Entry<String, String> mapEntry : keyMapping.entrySet()) {
                        if (lowerKey.contains(mapEntry.getKey())) {
                            englishKey = mapEntry.getValue();
                            break;
                        }
                    }

                    englishRow.put(englishKey, entry.getValue());

                    // Determine groupKey based on guarantee number
                    if (englishKey.equals("guaranteeNumber")) {
                        Object val = entry.getValue();
                        if (val != null && !val.toString().trim().isEmpty()) {
                            groupKey = val.toString().trim();
                        }
                    }
                }

                groupedData.computeIfAbsent(groupKey, k -> new java.util.ArrayList<>()).add(englishRow);
            }

            return ResponseEntity.ok(new ExtractionResult(true, groupedData, "Extracted and grouped successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ExtractionResult(false, null, e.getMessage()));
        }
    }
}
