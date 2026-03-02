package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.DisbursementExportRequest;
import com.bidv.asset.vehicle.DTO.WarehouseExportDTO;
import com.bidv.asset.vehicle.Service.DisbursementExportService;
import com.bidv.asset.vehicle.Service.DisbursementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController

public class DisbursementExportAPI {
    @Autowired
    DisbursementService disbursementService;
    @Autowired DisbursementExportService disbursementExportService;
    @PostMapping("/officer/disbursements/export-all")
    public ResponseEntity<byte[]> exportAll(@RequestBody DisbursementExportRequest request) throws IOException {
        Map<String, byte[]> files = disbursementExportService.exportAll(
                request.getDisbursementDTO(), request.getVehicleIds());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                ZipEntry ze = new ZipEntry(entry.getKey());
                zos.putNextEntry(ze);
                zos.write(entry.getValue());
                zos.closeEntry();
            }
            zos.finish();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"HoSoGiaiNgan.zip\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(baos.toByteArray());
        }
    }
    @PostMapping("customer/disbursements/export-specific")
    public ResponseEntity<byte[]> exportSpecific(@RequestBody DisbursementExportRequest request) throws IOException {
        Map<String, byte[]> files = disbursementExportService.exportSpecific(  request.getDisbursementDTO(), request.getVehicleIds());
        String fileName=request.getDisbursementDTO().getLoanContractNumber();
        return createZipResponse(files, fileName);
    }
    private ResponseEntity<byte[]> createZipResponse(Map<String, byte[]> files, String fileName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                ZipEntry ze = new ZipEntry(entry.getKey());
                zos.putNextEntry(ze);
                zos.write(entry.getValue());
                zos.closeEntry();
            }
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(baos.toByteArray());
    }
}
