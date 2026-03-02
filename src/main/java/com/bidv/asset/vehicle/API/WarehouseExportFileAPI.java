package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.WarehouseExportDTO;
import com.bidv.asset.vehicle.Service.WarehouseExportFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController

@RequiredArgsConstructor
public class WarehouseExportFileAPI {

    private final WarehouseExportFileService warehouseExportFileService;

    @PostMapping("officer/warehouse-export-files/export-all")
    public ResponseEntity<byte[]> exportAll(@RequestBody WarehouseExportDTO dto) throws IOException {
        Map<String, byte[]> files = warehouseExportFileService.exportAll(dto.getId(), dto.getVehicleIds());
        return createZipResponse(files, "XuatKho.zip");
    }

    @PostMapping("customer/export-specific")
    public ResponseEntity<byte[]> exportSpecific(@RequestBody WarehouseExportDTO dto) throws IOException {
        Map<String, byte[]> files = warehouseExportFileService.exportSpecific(dto.getId(), dto.getVehicleIds());
        return createZipResponse(files, "XuatKho.zip");
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
