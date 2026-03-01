package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.WarehouseExportDTO;
import com.bidv.asset.vehicle.Service.WarehouseExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WarehouseExportAPI {

    private final WarehouseExportService warehouseExportService;

    @PostMapping("/customer/warehouse-export/request")
    public ResponseEntity<?> requestExport(@RequestBody WarehouseExportDTO dto) {
        try {
            WarehouseExportDTO result = warehouseExportService.requestExport(dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/officer/warehouse-export/approve")
    public ResponseEntity<?> approveExport(
            @RequestBody WarehouseExportDTO requestDTO) {
        try {
            WarehouseExportDTO result =
                    warehouseExportService.approveExport(requestDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/officer/warehouse-export/reject/{id}")
    public ResponseEntity<?> rejectExport(@PathVariable Long id) {
        try {
            WarehouseExportDTO result = warehouseExportService.rejectExport(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/officer/warehouse-export/pending")
    public ResponseEntity<?> getPendingRequests() {
        try {
            return ResponseEntity.ok(warehouseExportService.getPendingRequests());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
