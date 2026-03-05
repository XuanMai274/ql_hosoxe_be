package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.ExportDeXuatRequest;
import com.bidv.asset.vehicle.DTO.GuaranteeLetterDTO;
import com.bidv.asset.vehicle.DTO.XuatDeXuatBaoLanh;
import com.bidv.asset.vehicle.Service.GuaranteeLetterExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/officer/guarantee-export-letters")
public class GuaranteeLetterExportAPI {
        @Autowired()
        GuaranteeLetterExportService guaranteeLetterExportService;

        // @PostMapping("/preview")
        // public ResponseEntity<byte[]> preview(@RequestBody GuaranteeLetterDTO
        // dto,String template) throws Exception {
        //
        // byte[] word =
        // guaranteeLetterExportService.generateDeXuatBaoLanh(dto,template);
        //
        // return ResponseEntity.ok()
        // .header(HttpHeaders.CONTENT_TYPE,
        // "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        // .body(word);
        // }
        @PostMapping("/export/thu-bao-lanh")
        public ResponseEntity<byte[]> exportThuBaoLanh(@RequestBody GuaranteeLetterDTO dto,
                        @RequestParam("template") String template) throws Exception {

                byte[] word = guaranteeLetterExportService.generateThuBaoLanh(dto, template);

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=Thu_Bao_Lanh.docx")
                                .header(HttpHeaders.CONTENT_TYPE,
                                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                                .body(word);
        }

        @PostMapping("/export/de-xuat-cap-bao-lanh")
        public ResponseEntity<byte[]> exportDeXuatCapBaoLanh(
                        @RequestBody ExportDeXuatRequest request,
                        @RequestParam("template") String template) throws Exception {

                byte[] word = guaranteeLetterExportService.generateDeXuatBaoLanh(
                                request,
                                template);

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=De_Xuat_Cap_Bao_Lanh.docx")
                                .header(HttpHeaders.CONTENT_TYPE,
                                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                                .body(word);
        }

        @PostMapping("/export/phan-xet-duyet")
        public ResponseEntity<byte[]> exportPhanXetDuyet(@RequestBody ExportDeXuatRequest request,
                        @RequestParam("template") String template) throws Exception {

                byte[] word = guaranteeLetterExportService.generateXetDuyet(request, template);

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=Xet_Duyet_Bao_Lanh.docx")
                                .header(HttpHeaders.CONTENT_TYPE,
                                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                                .body(word);
        }

        @PostMapping("/export/phan-y_kien")
        public ResponseEntity<byte[]> exportPhanYKien(@RequestBody GuaranteeLetterDTO dto,
                        @RequestParam("template") String template) throws Exception {

                byte[] word = guaranteeLetterExportService.generateYKien(dto, template);

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=Xet_Duyet_Bao_Lanh.docx")
                                .header(HttpHeaders.CONTENT_TYPE,
                                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                                .body(word);
        }
}
