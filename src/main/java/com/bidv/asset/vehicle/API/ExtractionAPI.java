package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.ExtractionResult;

import com.bidv.asset.vehicle.DTO.InvoiceResponse;
import com.bidv.asset.vehicle.Service.ExcelService;
import com.bidv.asset.vehicle.Service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
