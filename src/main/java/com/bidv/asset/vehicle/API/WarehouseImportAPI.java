package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.WarehouseImportDTO;
import com.bidv.asset.vehicle.DTO.WarehouseImportRequestDTO;
import com.bidv.asset.vehicle.DTO.WarehouseImportRequestDTO;
import com.bidv.asset.vehicle.Service.WarehouseImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/officer/warehouse")
@RequiredArgsConstructor
public class WarehouseImportAPI {

    @Autowired
    WarehouseImportService warehouseImportService;

    @PostMapping("/import")
    public ResponseEntity<?> importWarehouse(
            @RequestBody WarehouseImportRequestDTO request) {

        try {

            WarehouseImportDTO result =
                    warehouseImportService.importWarehouse(request);

            return ResponseEntity.ok(result);

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }
}
