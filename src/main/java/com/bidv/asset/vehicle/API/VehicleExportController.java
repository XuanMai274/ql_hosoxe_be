package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.Service.VehicleExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/officer/vehicles/export")
@RequiredArgsConstructor
public class VehicleExportController {
    @Autowired
    VehicleExportService vehicleExportService;
    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportExcel() {

        byte[] file = vehicleExportService.exportVehicleExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=danh_sach_xe.xlsx")
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(file);
    }
}
