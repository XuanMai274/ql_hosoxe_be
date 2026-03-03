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
public class VehicleWarehouseAPI {
    @Autowired
    VehicleWarehouseExportService vehicleWarehouseExportService;
    @Autowired
    VehicleService vehicleService;
    @PostMapping("/officer/vehicles/nhapkho/export-pnk")
    public ResponseEntity<byte[]> exportPNK(
            @RequestBody ExportPNKRequest request) throws IOException {

        List<VehicleDTO> vehicles =
                vehicleService.findByIds(request.getVehicleIds());

        byte[] file =
                vehicleWarehouseExportService.generatePNK(vehicles,request.getImportNumber());

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=PNK.docx")
                .body(file);
    }
    @PostMapping("/officer/vehicles/nhapkho/export-bao-cao-dinh-gia")
    public ResponseEntity<byte[]> exportBaoCaoDinhGia(
            @RequestBody ExportPNKRequest request) throws IOException {

        List<VehicleDTO> vehicles =
                vehicleService.findByIds(request.getVehicleIds());

        byte[] file =
                vehicleWarehouseExportService.generateBaoCaoDinhGia(vehicles,request.getImportNumber());

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=BAO_CAO_DINH_GIA.docx")
                .body(file);
    }

    @PostMapping("/officer/vehicles/nhapkho/export-bien-ban-dinh-gia")
    public ResponseEntity<byte[]> exportBienBanDinhGia(
            @RequestBody ExportPNKRequest request) throws IOException {

        List<VehicleDTO> vehicles =
                vehicleService.findByIds(request.getVehicleIds());

        byte[] file =
                vehicleWarehouseExportService.generateBienBanDinhGia(vehicles,request.getImportNumber());

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=BIEN_BAN_DINH_GIA.docx")
                .body(file);
    }

    @PostMapping("/officer/vehicles/nhapkho/phu-luc-hop-dong-the-chap")
    public ResponseEntity<byte[]> exportPhuLuc(
            @RequestBody ExportPNKRequest request
    ) throws IOException {

        List<VehicleDTO> vehicles =
                vehicleService.findByIds(request.getVehicleIds());

        byte[] file =
                vehicleWarehouseExportService
                        .generatePhuLucHopDongTheChap(vehicles,request.getImportNumber());

        return buildResponse(file, "PhuLuc.docx");
    }
    @PostMapping("/officer/vehicles/nhapkho/dang-ky-giao-dich-dam-bao")
    public ResponseEntity<byte[]> exportDamBao(
            @RequestBody ExportPNKRequest request
    ) throws IOException {

        List<VehicleDTO> vehicles =
                vehicleService.findByIds(request.getVehicleIds());

        byte[] file =
                vehicleWarehouseExportService
                        .generateDangKiGiaoDichDamBao(vehicles, request.getImportNumber());

        return buildResponse(file, "GiaoDichDamBao.docx");
    }

    private ResponseEntity<byte[]> buildResponse(byte[] file, String fileName) {

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=" + fileName)
                .body(file);
    }
    // XUẤT MẪU XUẤT KHO CHO KHÁCH HÀNG
    @PostMapping("/customer/vehicles/nhapkho/export-ho-so-khach-hang")
    public ResponseEntity<byte[]> exportCustomerZip(
            @RequestBody ExportPNKRequest request
    ) throws IOException {

        List<VehicleDTO> vehicles =
                vehicleService.findByIds(request.getVehicleIds());

        if (vehicles == null || vehicles.isEmpty()) {
            throw new RuntimeException("Danh sách xe trống");
        }

        // ===== generate từng file =====
        byte[] bienBan =
                vehicleWarehouseExportService
                        .generateBienBanDinhGia(vehicles, request.getImportNumber());

        byte[] pnk =
                vehicleWarehouseExportService
                        .generatePNK(vehicles, request.getImportNumber());

        byte[] dangKy =
                vehicleWarehouseExportService
                        .generateDangKiGiaoDichDamBao(vehicles, request.getImportNumber());
        String fileName = request.getImportNumber();

        if (fileName == null || fileName.isBlank()) {
            fileName = "HoSoKhachHang";
        }

        // loại ký tự không hợp lệ trong tên file
        fileName = fileName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");

        try (var baos = new java.io.ByteArrayOutputStream();
             var zos = new java.util.zip.ZipOutputStream(baos)) {

            addToZip(zos, "bien-ban-dinh-gia.docx", bienBan);
            addToZip(zos, "phieu-nhap-kho.docx", pnk);
            addToZip(zos, "don-dang-ky-giao-dich-dam-bao.docx", dangKy);

            zos.finish();

            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=\"" + fileName + ".zip\"")
                    .header("Content-Type",
                            "application/octet-stream")
                    .body(baos.toByteArray());
        }
    }
    private void addToZip(
            java.util.zip.ZipOutputStream zos,
            String fileName,
            byte[] data
    ) throws IOException {

        java.util.zip.ZipEntry entry =
                new java.util.zip.ZipEntry(fileName);

        zos.putNextEntry(entry);
        zos.write(data);
        zos.closeEntry();
    }
}
