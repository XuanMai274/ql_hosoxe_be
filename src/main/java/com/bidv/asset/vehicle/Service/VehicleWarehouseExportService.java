package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.VehicleDTO;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.IOException;
import java.util.List;

public interface VehicleWarehouseExportService {
    void replaceVehicleTable(XWPFDocument doc, List<VehicleDTO> vehicles);
    byte[] generatePNK(List<VehicleDTO> vehicles,String importNumber) throws IOException;

    byte[] generateBaoCaoDinhGia(List<VehicleDTO> vehicles,String importNumber) throws IOException;

    byte[] generateBienBanDinhGia(List<VehicleDTO> vehicles,String importNumber) throws IOException;

    byte[] generatePhuLucHyundai(List<VehicleDTO> vehicles,String importNumber) throws IOException;

    byte[] generatePhuLucVinfast(List<VehicleDTO> vehicles,String importNumber) throws IOException;
    public byte[] generatePhuLucHopDongTheChap(
            List<VehicleDTO> vehicles,String importNumber
    ) throws IOException;
    public byte[] generateDangKiGiaoDichDamBao(
            List<VehicleDTO> vehicles,String importNumber
    ) throws IOException;
}
