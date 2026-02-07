package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.ExtractionResult;
import com.bidv.asset.vehicle.DTO.InvoiceData;
import com.bidv.asset.vehicle.DTO.InvoiceResponse;
import com.bidv.asset.vehicle.DTO.UploadResponse;
import com.bidv.asset.vehicle.DTO.VehicleInfo;
import com.bidv.asset.vehicle.Service.ExcelService;
import com.bidv.asset.vehicle.Service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/officer/extract")
public class ExtractionAPI {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private ExcelService excelService;

    @PostMapping("/pdf")
    public ResponseEntity<ExtractionResult> extractPdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || !file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
            return ResponseEntity.badRequest()
                    .body(new ExtractionResult(false, null, "Vui lòng upload file PDF hợp lệ."));
        }
        try {
            InvoiceResponse data = pdfService.extractPdf(file);
            return ResponseEntity.ok(new ExtractionResult(true, data, "Thành công"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ExtractionResult(false, null, e.getMessage()));
        }
    }

    @PostMapping("/pdf-multiple")
    public ResponseEntity<UploadResponse> extractMultiplePdf(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "excelFile", required = false) MultipartFile excelFile) {
        if (files == null || files.isEmpty()) {
            UploadResponse errorResponse = new UploadResponse();
            errorResponse.setSuccess(false);
            errorResponse.setData(null);
            errorResponse.setMessage("Vui lòng upload ít nhất 1 file PDF.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        List<String> validVins = new ArrayList<>();
        String soLDBL = "";

        // Process Excel file for cross-validation if provided
        if (excelFile != null && !excelFile.isEmpty()) {
            try {
                List<Map<String, Object>> excelData = excelService.extractExcel(excelFile);
                if (!excelData.isEmpty()) {
                    for (Map<String, Object> row : excelData) {
                        for (Map.Entry<String, Object> entry : row.entrySet()) {
                            String key = entry.getKey().toLowerCase();
                            String value = String.valueOf(entry.getValue());

                            if (key.contains("vin") || key.contains("số khung")) {
                                if (entry.getValue() != null)
                                    validVins.add(value.trim());
                            }
                            if (key.contains("ld/bl") || key.contains("lệnh điều")) {
                                if (soLDBL.isEmpty())
                                    soLDBL = value;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Log error
            }
        }

        List<InvoiceData> invoiceDataList = new ArrayList<>();
        StringBuilder errorMessages = new StringBuilder();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);

            // Validate file
            if (file.isEmpty() || !file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                errorMessages.append(String.format("File %d (%s) không hợp lệ. ",
                        i + 1, file.getOriginalFilename()));
                continue;
            }

            try {
                // Extract PDF
                InvoiceResponse invoiceResponse = pdfService.extractPdf(file);

                // Convert to InvoiceData
                InvoiceData invoiceData = new InvoiceData();
                invoiceData.setInvoiceNumber(invoiceResponse.getInvoiceNumber());

                // Extract date info if available
                invoiceData.setTotalAmount(invoiceResponse.getTotalAmount());
                invoiceData.setDay(invoiceResponse.getDay());
                invoiceData.setMonth(invoiceResponse.getMonth());
                invoiceData.setYear(invoiceResponse.getYear());

                // Filter/Validate vehicle list against Excel if applicable
                List<VehicleInfo> extractedVehicles = invoiceResponse.getVehicleList();
                List<VehicleInfo> validatedVehicles = new ArrayList<>();

                if (!validVins.isEmpty()) {
                    for (VehicleInfo v : extractedVehicles) {
                        if (validVins.contains(v.getChassisNumber())) {
                            validatedVehicles.add(v);
                        } else {
                            errorMessages.append(String.format(
                                    "Xe có SK %s trong file %s không khớp với danh sách VIN trong Excel. ",
                                    v.getChassisNumber(), file.getOriginalFilename()));
                        }
                    }
                } else {
                    validatedVehicles = extractedVehicles;
                }

                invoiceData.setVehicleList(validatedVehicles);
                if (!validatedVehicles.isEmpty()) {
                    invoiceDataList.add(invoiceData);
                }

            } catch (Exception e) {
                errorMessages.append(String.format("File %d (%s) lỗi: %s. ",
                        i + 1, file.getOriginalFilename(), e.getMessage()));
            }
        }

        UploadResponse response = new UploadResponse();

        if (invoiceDataList.isEmpty()) {
            response.setSuccess(false);
            response.setData(null);
            response.setMessage("Không có dữ liệu hợp lệ được xử lý. " + errorMessages.toString());
            return ResponseEntity.badRequest().body(response);
        }

        response.setSuccess(true);
        response.setData(invoiceDataList);

        String successMsg = "Thành công" + (soLDBL.isEmpty() ? "" : " cho Số LD/BL: " + soLDBL);
        if (errorMessages.length() > 0) {
            response.setMessage(successMsg + ". Lưu ý: " + errorMessages.toString());
        } else {
            response.setMessage(successMsg);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/excel")
    public ResponseEntity<ExtractionResult> extractExcel(@RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename().toLowerCase();
        if (file.isEmpty() || !(filename.endsWith(".xlsx") || filename.endsWith(".xls"))) {
            return ResponseEntity.badRequest()
                    .body(new ExtractionResult(false, null, "Vui lòng upload file Excel hợp lệ."));
        }
        try {
            List<Map<String, Object>> data = excelService.extractExcel(file);
            return ResponseEntity.ok(new ExtractionResult(true, data, "Thành công"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ExtractionResult(false, null, e.getMessage()));
        }
    }
}
