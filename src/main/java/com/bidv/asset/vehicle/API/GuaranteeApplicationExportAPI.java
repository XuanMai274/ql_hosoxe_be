package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.GuaranteeApplicationDTO;
import com.bidv.asset.vehicle.Service.GuaranteeApplicationExportService;
import com.bidv.asset.vehicle.Service.GuaranteeApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/customer/guarantee-export")
public class GuaranteeApplicationExportAPI {
    @Autowired GuaranteeApplicationExportService service;
    @Autowired
    GuaranteeApplicationService guaranteeApplicationService;
    @GetMapping("/export-all/{id}")
    public ResponseEntity<byte[]> exportAll(@PathVariable Long id) throws Exception {

        GuaranteeApplicationDTO dto = guaranteeApplicationService.getById(id);

        Map<String, byte[]> files = service.exportAll(dto);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(baos);

        for (Map.Entry<String, byte[]> entry : files.entrySet()) {
            ZipEntry zipEntry = new ZipEntry(entry.getKey());
            zipOut.putNextEntry(zipEntry);
            zipOut.write(entry.getValue());
            zipOut.closeEntry();
        }

        zipOut.close();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=bao-lanh.zip")
                .header("Content-Type", "application/zip")
                .body(baos.toByteArray());
    }
}
