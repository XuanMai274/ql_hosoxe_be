package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.Service.GuaranteeApplicationExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer/guarantee-export")
public class GuaranteeApplicationExportAPI {
    @Autowired GuaranteeApplicationExportService service;
    @GetMapping("/de-nghi/{id}")
    public ResponseEntity<byte[]> exportDeNghi(
            @PathVariable Long id) throws Exception {

        byte[] file = service.exportDeNghiCapBaoLanh(id);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=de-nghi-cap-bao-lanh.docx")
                .header("Content-Type",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                .body(file);
    }

    @GetMapping("/danh-sach-xe/{id}")
    public ResponseEntity<byte[]> exportDanhSach(
            @PathVariable Long id) throws Exception {

        byte[] file = service.exportDanhSachXeBaoLanh(id);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=danh-sach-xe-cap-bao-lanh.docx")
                .body(file);
    }
}
