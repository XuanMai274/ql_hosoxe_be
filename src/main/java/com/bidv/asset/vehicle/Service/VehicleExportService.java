package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.VehicleDTO;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.IOException;
import java.util.List;

public interface VehicleExportService {
    byte[] exportVehicleExcel(
            String chassisNumber,
            String status,
            String manufacturer,
            String guaranteeContractNumber
    );
    public byte[] generatePNK(List<VehicleDTO> vehicles) throws IOException;
    void replaceVehicleTable(XWPFDocument doc, List<VehicleDTO> vehicles);
    public byte[] generatePhuLucVinfast(List<VehicleDTO> vehicles) throws IOException;
    public byte[] generatePhuLucHyundai(List<VehicleDTO> vehicles) throws IOException;
    public byte[] generateBaoCaoDinhGia(List<VehicleDTO> vehicles) throws IOException;
    public byte[] generateBienBanDinhGia(List<VehicleDTO> vehicles) throws IOException;

}
