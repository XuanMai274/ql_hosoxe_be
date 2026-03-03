package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.VehicleDTO;
import com.bidv.asset.vehicle.DTO.WarehouseExportDTO;
import com.bidv.asset.vehicle.Service.VehicleService;
import com.bidv.asset.vehicle.Service.WarehouseExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WarehouseExportAPI {

    private final WarehouseExportService warehouseExportService;
    @Autowired
    VehicleService vehicleService;

    @PostMapping("/customer/warehouse-export/request")
    public ResponseEntity<?> requestExport(@RequestBody WarehouseExportDTO dto) {
        try {
            WarehouseExportDTO result = warehouseExportService.requestExport(dto);
            System.out.println(("Dữ liệu trả về sau khi đề nghị xuất kho: " + result));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping("/officer/warehouse-export/approve")
    public ResponseEntity<?> approveExport(
            @RequestBody WarehouseExportDTO requestDTO) {
        try {
            WarehouseExportDTO result = warehouseExportService.approveExport(requestDTO);
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

    @GetMapping("/officer/warehouse-export/all")
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String exportNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page,
                    size);
            return ResponseEntity.ok(warehouseExportService.getAll(exportNumber, pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/officer/warehouse-export/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(warehouseExportService.getById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/officer/warehouse-export/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody WarehouseExportDTO dto) {
        try {
            return ResponseEntity.ok(warehouseExportService.updateWarehouseExport(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        // // Danh sách xe trong một đơn đề nghị rút hồ sơ cụ thể (Officer xem)
        // @GetMapping("/officer/vehicles/warehouse-export/{exportId}")
        // public ResponseEntity<List<VehicleDTO>> getByExportRequest(@PathVariable Long exportId) {
        //     return ResponseEntity.ok(vehicleService.getVehiclesByExportId(exportId));
        // }
        // // Danh sách xe trong một đơn đề nghị rút hồ sơ cụ thể (Officer xem)
        // @GetMapping("/customer/vehicles/warehouse-export/{exportId}")
        // public ResponseEntity<List<VehicleDTO>> getByExportRequestCustomer(@PathVariable Long exportId) {
        //     return ResponseEntity.ok(vehicleService.getVehiclesByExportId(exportId));
        // }
    }
}
