package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.GuaranteeLetterFileDTO;
import com.bidv.asset.vehicle.Service.GuaranteeLetterFileService;
import com.bidv.asset.vehicle.Service.GuaranteeLetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({ "/officer/guarantee-files", "/customer/guarantee-files" })
@RequiredArgsConstructor
public class GuaranteeLetterFileAPI {
    @Autowired
    GuaranteeLetterFileService guaranteeLetterFileService;

    // ===== Upload / Replace =====
    @PostMapping("/upload")
    public GuaranteeLetterFileDTO upload(
            @RequestParam Long guaranteeLetterId,
            @RequestParam MultipartFile file) {
        return guaranteeLetterFileService.uploadFile(guaranteeLetterId, file);
    }

    // =========================================================
    // Preview PDF (XEM TRỰC TIẾP)
    // =========================================================
    @GetMapping("/{id}/view")
    public ResponseEntity<ByteArrayResource> view(@PathVariable Long id) {

        byte[] data = guaranteeLetterFileService.loadFile(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(new ByteArrayResource(data));
    }

    // =========================================================
    // Download PDF
    // =========================================================
    @GetMapping("/{id}/download")
    public ResponseEntity<ByteArrayResource> download(@PathVariable Long id) {

        byte[] data = guaranteeLetterFileService.loadFile(id);
        GuaranteeLetterFileDTO meta = guaranteeLetterFileService.getMeta(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + meta.getFileName() + "\"")
                .body(new ByteArrayResource(data));
    }

    // =========================================================
    // Delete file
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {

        guaranteeLetterFileService.deleteFile(id);

        Map<String, String> res = new HashMap<>();
        res.put("message", "Đã xóa file thư bảo lãnh");

        return ResponseEntity.ok(res);
    }

}