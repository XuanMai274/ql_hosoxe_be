package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.GuaranteeLetterDTO;
import com.bidv.asset.vehicle.Service.GuaranteeLetterExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("officer/guarantee-export-letters")
public class GuaranteeLetterExportAPI {
    @Autowired()
    GuaranteeLetterExportService guaranteeLetterExportService;
    @PostMapping("/preview")
    public ResponseEntity<byte[]> preview(@RequestBody GuaranteeLetterDTO dto) throws Exception {

        byte[] word = guaranteeLetterExportService.generateWord(dto);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                .body(word);
    }
    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@RequestBody GuaranteeLetterDTO dto) throws Exception {

        byte[] word = guaranteeLetterExportService.generateWord(dto);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Thu_Bao_Lanh.docx")
                .header(HttpHeaders.CONTENT_TYPE,
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                .body(word);
    }
}
