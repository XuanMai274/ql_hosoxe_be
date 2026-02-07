package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.VehicleDTO;
import com.bidv.asset.vehicle.DTO.VehicleDetailDTO;
import com.bidv.asset.vehicle.DTO.VehicleListDTO;
import com.bidv.asset.vehicle.Mapper.VehicleMapper;
import com.bidv.asset.vehicle.Service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/officer/vehicles")
@RequiredArgsConstructor
public class VehicleAPI {
    @Autowired
    VehicleService vehicleService;
    @GetMapping
    public Page<VehicleListDTO> getVehicles(
            @RequestParam(required = false) String chassisNumber,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) String guaranteeContractNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        chassisNumber = normalize(chassisNumber);
        manufacturer = normalize(manufacturer);
        guaranteeContractNumber = normalize(guaranteeContractNumber);
        manufacturer = (manufacturer == null || manufacturer.isBlank())
                ? null
                : manufacturer.trim().toLowerCase();
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return vehicleService.getVehicles(
                chassisNumber,
                status,
                manufacturer,
                guaranteeContractNumber,
                pageable
        );
    }

    private String normalize(String value) {
        return (value == null || value.isBlank())
                ? null
                : value.trim().toLowerCase();
    }
//    @GetMapping
//    public Page<VehicleListDTO> getVehicles(
//            @RequestParam(required = false) String chassisNumber,
//            @RequestParam(required = false) String status,
//            @RequestParam(required = false) BigDecimal minPrice,
//            @RequestParam(required = false) BigDecimal maxPrice,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        chassisNumber = (chassisNumber == null || chassisNumber.isBlank())
//                ? null
//                : chassisNumber.trim().toLowerCase();
//
//        Pageable pageable = PageRequest.of(
//                page,
//                size,
//                Sort.by(Sort.Direction.DESC, "createdAt")
//        );
//        return vehicleService.getVehicles(
//                chassisNumber,
//                status,
//                minPrice,
//                maxPrice,
//                pageable
//        );
//    }
    @GetMapping("/{id}")
    public VehicleDTO getVehicleDetail(@PathVariable Long id) {
        return vehicleService.getVehicleDetail(id);
    }
    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> updateVehicle(
            @PathVariable Long id,
            @RequestBody VehicleDTO dto
    ) {

        return ResponseEntity.ok(vehicleService.updateVehicle(id, dto));
    }
//    @GetMapping("/export")
//    public ResponseEntity<byte[]> exportExcel() {
//        byte[] file = exportService.exportVehicleExcel();
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=vehicles.xlsx")
//                .body(file);
//    }
}
