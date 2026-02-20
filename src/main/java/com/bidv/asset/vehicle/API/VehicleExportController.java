package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.ExportPNKRequest;
import com.bidv.asset.vehicle.DTO.VehicleDTO;
import com.bidv.asset.vehicle.Service.VehicleExportService;
import com.bidv.asset.vehicle.Service.VehicleService;
import com.bidv.asset.vehicle.ServiceImplement.VehicleExportServiceImplement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/officer/vehicles/export")
@RequiredArgsConstructor
public class VehicleExportController {
    @Autowired
    VehicleExportService vehicleExportService;
    @Autowired
    VehicleService vehicleService;
    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(required = false) String chassisNumber,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) String ref
    ) {

        byte[] file = vehicleExportService.exportVehicleExcel(
                chassisNumber,
                status,
                manufacturer,
                ref
        );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=danh_sach_xe.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(file);
    }
    @PostMapping("/export-pnk")
    public ResponseEntity<byte[]> exportPNK(
            @RequestBody ExportPNKRequest request) throws IOException {

        List<VehicleDTO> vehicles =
                vehicleService.findByIds(request.getVehicleIds());

        byte[] file =
                vehicleExportService.generatePNK(vehicles);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=PNK.docx")
                .body(file);
    }
    @PostMapping("/export-bao-cao-dinh-gia")
    public ResponseEntity<byte[]> exportBaoCaoDinhGia(
            @RequestBody ExportPNKRequest request) throws IOException {

        List<VehicleDTO> vehicles =
                vehicleService.findByIds(request.getVehicleIds());

        byte[] file =
                ((VehicleExportServiceImplement)vehicleExportService)
                        .generateBaoCaoDinhGia(vehicles);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=BAO_CAO_DINH_GIA.docx")
                .body(file);
    }

    @PostMapping("/export-bien-ban-dinh-gia")
    public ResponseEntity<byte[]> exportBienBanDinhGia(
            @RequestBody ExportPNKRequest request) throws IOException {

        List<VehicleDTO> vehicles =
                vehicleService.findByIds(request.getVehicleIds());

        byte[] file =
                ((VehicleExportServiceImplement)vehicleExportService)
                        .generateBienBanDinhGia(vehicles);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=BIEN_BAN_DINH_GIA.docx")
                .body(file);
    }

    @PostMapping("/export-phu-luc-hyundai")
    public ResponseEntity<byte[]> exportPhuLucHyundai(
            @RequestBody ExportPNKRequest request) throws IOException {

        List<VehicleDTO> vehicles =
                vehicleService.findByIds(request.getVehicleIds());

        byte[] file =
                ((VehicleExportServiceImplement)vehicleExportService)
                        .generatePhuLucHyundai(vehicles);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=PHU_LUC_HYUNDAI.docx")
                .body(file);
    }

    @PostMapping("/export-phu-luc-vinfast")
    public ResponseEntity<byte[]> exportPhuLucVinfast(
            @RequestBody ExportPNKRequest request) throws IOException {

        List<VehicleDTO> vehicles =
                vehicleService.findByIds(request.getVehicleIds());

        byte[] file =
                ((VehicleExportServiceImplement)vehicleExportService)
                        .generatePhuLucVinfast(vehicles);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=PHU_LUC_VINFAST.docx")
                .body(file);
    }

    private ResponseEntity<byte[]> buildResponse(byte[] file, String fileName) {

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=" + fileName)
                .body(file);
    }

}
