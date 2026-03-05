package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.ExportPNKRequest;
import com.bidv.asset.vehicle.DTO.VehicleDTO;
import com.bidv.asset.vehicle.Service.VehicleWarehouseExportService;
import com.bidv.asset.vehicle.Service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
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

    @PostMapping("/officer/vehicles/nhapkho/exportAll")
    public ResponseEntity<byte[]> exportHoSoNhapKhoZip(
            @RequestBody ExportPNKRequest request
    ) throws IOException {

        List<VehicleDTO> vehicles =
                vehicleService.findByIds(request.getVehicleIds());

        if (vehicles == null || vehicles.isEmpty()) {
            throw new RuntimeException("Danh sách xe trống");
        }

        // ===== Kiểm tra loại xe và xử lý logic riêng =====
        String manufacturerCode = vehicles.get(0).getManufacturerDTO().getCode();
        boolean isVinfast = "VINFAST".equalsIgnoreCase(manufacturerCode);

        byte[] pnk = null;
        byte[] baoCao = null;
        byte[] bienBan = null;
        byte[] phuLuc = null;
        byte[] dangKy = null;
        byte[] nhapket=null;

        if (isVinfast) {
            // Vinfast: 4 files và cập nhật trạng thái is_in_safe
            baoCao = vehicleWarehouseExportService.generateBaoCaoDinhGia(vehicles, request.getImportNumber());
            bienBan = vehicleWarehouseExportService.generateBienBanDinhGia(vehicles, request.getImportNumber());
            phuLuc = vehicleWarehouseExportService.generatePhuLucHopDongTheChap(vehicles, request.getImportNumber());
            nhapket=vehicleWarehouseExportService.generateNhapKet(vehicles,request.getImportNumber());

        } else {
            // Hyundai và các loại khác: giữ nguyên logic gốc
            pnk = vehicleWarehouseExportService.generatePNK(vehicles, request.getImportNumber());
            baoCao = vehicleWarehouseExportService.generateBaoCaoDinhGia(vehicles, request.getImportNumber());
            bienBan = vehicleWarehouseExportService.generateBienBanDinhGia(vehicles, request.getImportNumber());
            phuLuc = vehicleWarehouseExportService.generatePhuLucHopDongTheChap(vehicles, request.getImportNumber());
            dangKy = vehicleWarehouseExportService.generateDangKiGiaoDichDamBao(vehicles, request.getImportNumber());
        }

        // ===== Tạo tên file zip =====
        String importNumber = request.getImportNumber();

        if (importNumber == null || importNumber.isBlank()) {
            importNumber = "UNKNOWN";
        }

        // sanitize tên file
        importNumber = importNumber.replaceAll("[^a-zA-Z0-9-_\\.]", "_");

        String zipFileName = "HO_SO_NHAP_KHO_" + importNumber + ".zip";

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             java.util.zip.ZipOutputStream zos =
                     new java.util.zip.ZipOutputStream(baos)) {

            if (pnk != null) addToZip(zos, "PNK.docx", pnk);
            if (baoCao != null) addToZip(zos, "BAO_CAO_DINH_GIA.docx", baoCao);
            if (bienBan != null) addToZip(zos, "BIEN_BAN_DINH_GIA.docx", bienBan);
            if (phuLuc != null) addToZip(zos, "PHU_LUC_HOP_DONG_THE_CHAP.docx", phuLuc);
            if (dangKy != null) addToZip(zos, "DANG_KY_GIAO_DICH_DAM_BAO.docx", dangKy);
            if (nhapket != null) addToZip(zos, "TO_TRINH_NHAP_KET.docx", nhapket);

            zos.finish();

            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=\"" + zipFileName + "\"")
                    .header("Content-Type", "application/zip")
                    .body(baos.toByteArray());
        }
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
            fileName = request.getImportNumber();
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
    //api xuất file nhập kho riêng cho VINFAST
    @PostMapping("/officer/vehicles/nhapkho/export-vinfast")
    public ResponseEntity<byte[]> exportVinfastNhapKho(
            @RequestBody ExportPNKRequest request
    ) throws IOException {

        List<VehicleDTO> vehicles =
                vehicleService.findByIds(request.getVehicleIds());

        if (vehicles == null || vehicles.isEmpty()) {
            throw new RuntimeException("Danh sách xe trống");
        }

        // (Optional nhưng nên có) Validate đúng Vinfast
        String manufacturerCode = vehicles.get(0)
                .getManufacturerDTO()
                .getCode();

        if (!"VINFAST".equalsIgnoreCase(manufacturerCode)) {
            throw new RuntimeException("Chỉ áp dụng cho xe VINFAST");
        }

        // ===== Generate 2 file =====
        byte[] pnk =
                vehicleWarehouseExportService
                        .generatePNK(vehicles, request.getImportNumber());

        byte[] dangKy =
                vehicleWarehouseExportService
                        .generateDangKiGiaoDichDamBao(vehicles, request.getImportNumber());

        String fileName = request.getImportNumber();

        if (fileName == null || fileName.isBlank()) {
            fileName = request.getImportNumber()+"_";
        }

        fileName = fileName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             java.util.zip.ZipOutputStream zos =
                     new java.util.zip.ZipOutputStream(baos)) {

            addToZip(zos, "PHIEU_NHAP_KHO.docx", pnk);
            addToZip(zos, "DON_DANG_KY_GIAO_DICH_DAM_BAO.docx", dangKy);

            zos.finish();

            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=\"" + fileName + "_VINFAST.zip\"")
                    .header("Content-Type", "application/zip")
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
