package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.VehicleDTO;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.IOException;
import java.util.List;

public interface VehicleExportService {

        void replaceVehicleTable(XWPFDocument doc, List<VehicleDTO> vehicles);

        byte[] exportVehicleExcel(
                String chassisNumber,
                String status,
                String manufacturer,
                String ref
        );

        byte[] generatePNK(List<VehicleDTO> vehicles) throws IOException;

        byte[] generateBaoCaoDinhGia(List<VehicleDTO> vehicles) throws IOException;

        byte[] generateBienBanDinhGia(List<VehicleDTO> vehicles) throws IOException;

        byte[] generatePhuLucHyundai(List<VehicleDTO> vehicles) throws IOException;

        byte[] generatePhuLucVinfast(List<VehicleDTO> vehicles) throws IOException;
        public byte[] generatePhuLucHopDongTheChap(
                List<VehicleDTO> vehicles
        ) throws IOException;

}
