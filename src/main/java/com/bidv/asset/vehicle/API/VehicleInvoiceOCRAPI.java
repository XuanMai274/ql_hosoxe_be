package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.OcrResponseDTO;
import com.bidv.asset.vehicle.DTO.VehicleDTO;
import com.bidv.asset.vehicle.Mapper.VehicleOcrMapper;
import com.bidv.asset.vehicle.ServiceImplement.PythonOcrClientImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/officer/vehicle-invoice")
@RequiredArgsConstructor
public class VehicleInvoiceOCRAPI {

    private final PythonOcrClientImpl pythonOcrClient;

    @PostMapping(value = "/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> ocrInvoice(
            @RequestPart("file") MultipartFile file
    ) {

        OcrResponseDTO ocrResponse = pythonOcrClient.extract(file);

        List<VehicleDTO> vehicles =
                VehicleOcrMapper.mapAndValidateVehicles(ocrResponse);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("ocr", ocrResponse);   // ✅ raw OCR
        res.put("vehicles", vehicles); // ✅ mapped for FE

        return ResponseEntity.ok(res);
    }
}
