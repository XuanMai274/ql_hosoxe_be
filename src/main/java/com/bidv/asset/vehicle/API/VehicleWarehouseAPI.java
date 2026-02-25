package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.ExportPNKRequest;
import com.bidv.asset.vehicle.DTO.VehicleDTO;
import com.bidv.asset.vehicle.Service.VehicleWarehouseExportService;
import com.bidv.asset.vehicle.Service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/officer/vehicles/nhapkho")
public class API {
    @Autowired
    VehicleWarehouseExportService vehicleWarehouseExportService;
    @Autowired
    VehicleService vehicleService;
    @PostMapping("/export-pnk")
    public ResponseEntity<byte[]> exportPNK(
            @RequestBody ExportPNKRequest request) throws IOException {

        List<VehicleDTO> vehicles =
                vehicleService.findByIds(request.getVehicleIds());

        byte[] file =
                vehicleWarehouseExportService.generatePNK(vehicles);

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
                vehicleWarehouseExportService.generateBaoCaoDinhGia(vehicles);

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
                vehicleWarehouseExportService.generateBienBanDinhGia(vehicles);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=BIEN_BAN_DINH_GIA.docx")
                .body(file);
    }

    @PostMapping("/phu-luc-hop-dong-the-chap")
    public ResponseEntity<byte[]> exportPhuLuc(
            @RequestBody List<Long> vehicleIds
    ) throws IOException {

        List<VehicleDTO> vehicles =
                vehicleService.findByIds(vehicleIds);

        byte[] file =
                vehicleWarehouseExportService
                        .generatePhuLucHopDongTheChap(vehicles);

        return buildResponse(file, "PhuLuc.docx");
    }
    @PostMapping("/dang-ky-giao-dich-dam-bao")
    public ResponseEntity<byte[]> exportDamBao(
            @RequestBody List<Long> vehicleIds
    ) throws IOException {

        List<VehicleDTO> vehicles =
                vehicleService.findByIds(vehicleIds);

        byte[] file =
                vehicleWarehouseExportService
                        .generateDangKiGiaoDichDamBao(vehicles);

        return buildResponse(file, "GiaoDichDamBao.docx");
    }

    private ResponseEntity<byte[]> buildResponse(byte[] file, String fileName) {

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=" + fileName)
                .body(file);
    }
}
