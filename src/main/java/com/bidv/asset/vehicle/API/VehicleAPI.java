package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.VehicleDTO;
import com.bidv.asset.vehicle.DTO.VehicleDetailDTO;
import com.bidv.asset.vehicle.DTO.VehicleListDTO;
import com.bidv.asset.vehicle.Mapper.VehicleMapper;
import com.bidv.asset.vehicle.Service.VehicleService;
import com.bidv.asset.vehicle.Repository.UserAccountRepository;
import com.bidv.asset.vehicle.entity.CustomerEntity;
import com.bidv.asset.vehicle.entity.UserAccountEntity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController

@RequiredArgsConstructor
public class VehicleAPI {
    private final VehicleService vehicleService;
    private final UserAccountRepository userAccountRepository;

    private Long getCustomerIdIfCustomer(HttpServletRequest request) {
        if (request.getRequestURI().contains("/customer/")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                String username = auth.getName();
                if (username != null) {
                    return userAccountRepository.findByUsername(username)
                            .map(UserAccountEntity::getCustomer)
                            .map(CustomerEntity::getId)
                            .orElse(null);
                }
            }
        }
        return null; // Officer or not a customer
    }

    @GetMapping({ "/officer/vehicles", "/customer/vehicles" })
    public Page<VehicleListDTO> getVehicles(
            @RequestParam(required = false) String chassisNumber,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) String ref,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        Long customerId = getCustomerIdIfCustomer(request);
        chassisNumber = normalize(chassisNumber);
        manufacturer = normalize(manufacturer);
        ref = normalize(ref);
        manufacturer = (manufacturer == null || manufacturer.isBlank())
                ? null
                : manufacturer.trim();
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return vehicleService.getVehicles(
                customerId,
                chassisNumber,
                status,
                manufacturer,
                ref,
                pageable);
    }

    private String normalize(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        return input.trim();
    }

    @GetMapping("/officer/vehicles/status/{status}")
    public List<VehicleDTO> getByStatus(@PathVariable String status) {
        return vehicleService.getVehiclesByStatus(status);
    }

    @GetMapping("/customer/vehicles/status/{status}")
    public List<VehicleDTO> getByStatusCustomer(@PathVariable String status) {
        return vehicleService.getVehiclesByStatus(status);
    }

    // @GetMapping
    // public Page<VehicleListDTO> getVehicles(
    // @RequestParam(required = false) String chassisNumber,
    // @RequestParam(required = false) String status,
    // @RequestParam(required = false) BigDecimal minPrice,
    // @RequestParam(required = false) BigDecimal maxPrice,
    // @RequestParam(defaultValue = "0") int page,
    // @RequestParam(defaultValue = "10") int size
    // ) {
    // chassisNumber = (chassisNumber == null || chassisNumber.isBlank())
    // ? null
    // : chassisNumber.trim().toLowerCase();
    //
    // Pageable pageable = PageRequest.of(
    // page,
    // size,
    // Sort.by(Sort.Direction.DESC, "createdAt")
    // );
    // return vehicleService.getVehicles(
    // chassisNumber,
    // status,
    // minPrice,
    // maxPrice,
    // pageable
    // );
    // }
    @GetMapping("/officer/vehicles/{id}")
    public VehicleDTO getVehicleDetail(@PathVariable Long id) {
        return vehicleService.getVehicleDetail(id);
    }

    @PutMapping("/officer/vehicles/{id}")
    public ResponseEntity<VehicleDTO> updateVehicle(
            @PathVariable Long id,
            @RequestBody VehicleDTO dto) {

        return ResponseEntity.ok(vehicleService.updateVehicle(id, dto));
    }
    // @GetMapping("/export")
    // public ResponseEntity<byte[]> exportExcel() {
    // byte[] file = exportService.exportVehicleExcel();
    //
    // return ResponseEntity.ok()
    // .header(HttpHeaders.CONTENT_DISPOSITION,
    // "attachment; filename=vehicles.xlsx")
    // .body(file);
    // }

    // Danh sách xe khách hàng có thể chọn đề rút hồ sơ (chưa nằm trong đơn khác)
    @GetMapping("/customer/vehicles/available-for-export/{status}")
    public Page<VehicleDTO> getAvailableForExport(
            @PathVariable String status,
            @RequestParam(required = false) String chassisNumber,
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) String loanContractNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        chassisNumber = normalize(chassisNumber);
        manufacturer = normalize(manufacturer);
        loanContractNumber = normalize(loanContractNumber);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return vehicleService.getCustomerAvailableVehicles(status, chassisNumber, manufacturer, loanContractNumber,
                pageable);
    }
    // lấy lên danh sách xe vinfast chưa nhập kho
    @GetMapping("officer/vehicles/vinfast/in-safe")
    public ResponseEntity<List<VehicleDTO>> getVinfastInSafeVehicles() {
        return ResponseEntity.ok(
                vehicleService.getVinfastInSafeVehicles()
        );
    }
    @PutMapping("/officer/vehicles/update-safe")
    public ResponseEntity<?> updateVehicleInSafe(
            @RequestBody List<Long> vehicleIds,
            @RequestParam Boolean inSafe
    ) {

        int updatedCount = vehicleService.updateVehicleInSafe(vehicleIds, inSafe);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "updatedCount", updatedCount,
                "message", "Cập nhật thành công " + updatedCount + " xe"
        ));
    }


}
