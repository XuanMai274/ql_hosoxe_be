package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.*;
import com.bidv.asset.vehicle.DTO.WarehouseImportRequestDTO;
import com.bidv.asset.vehicle.Service.WarehouseImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            WarehouseImportDTO result = warehouseImportService.importWarehouse(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/full-process")
    public ResponseEntity<FullProcessResponse> executeFullProcess(
            @RequestBody FullProcessNKGNRequest request) {

        FullProcessResponse response =
                warehouseImportService.executeFullProcess(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getWarehouseImports(
            @RequestParam(required = false) String importNumber,
            org.springframework.data.domain.Pageable pageable) {
        try {
            return ResponseEntity.ok(warehouseImportService.getAll(importNumber, pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWarehouseImportById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(warehouseImportService.getById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWarehouseImport(
            @PathVariable Long id,
            @RequestBody WarehouseImportDTO dto) {
        try {
            return ResponseEntity.ok(warehouseImportService.updateWarehouseImport(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
