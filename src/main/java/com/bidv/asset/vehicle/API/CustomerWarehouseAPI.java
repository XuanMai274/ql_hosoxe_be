package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.WarehouseImportDTO;
import com.bidv.asset.vehicle.Mapper.WarehouseImportMapper;
import com.bidv.asset.vehicle.Repository.UserAccountRepository;
import com.bidv.asset.vehicle.Repository.WarehouseImportRepository;
import com.bidv.asset.vehicle.entity.CustomerEntity;
import com.bidv.asset.vehicle.entity.UserAccountEntity;
import com.bidv.asset.vehicle.entity.WarehouseImportEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * API dành cho Customer để xem danh sách phiếu nhập kho của mình
 */
@RestController
@RequestMapping("/customer/warehouse-imports")
@RequiredArgsConstructor
public class CustomerWarehouseAPI {

    private final WarehouseImportRepository warehouseImportRepository;
    private final UserAccountRepository userAccountRepository;
    private final WarehouseImportMapper warehouseImportMapper;
    private final com.bidv.asset.vehicle.Repository.VehicleRepository vehicleRepository;

    /**
     * Lấy Customer từ JWT token (username → UserAccount → customer)
     */
    private CustomerEntity getCustomerFromToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserAccountEntity userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản: " + username));

        CustomerEntity customer = userAccount.getCustomer();
        if (customer == null) {
            throw new RuntimeException("Tài khoản '" + username + "' không phải là khách hàng");
        }
        return customer;
    }

    /**
     * Lấy danh sách phiếu nhập kho của khách hàng đang đăng nhập (phân trang)
     * GET /customer/warehouse-imports
     */
    @GetMapping
    public ResponseEntity<Page<WarehouseImportDTO>> getMyWarehouseImports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        CustomerEntity customer = getCustomerFromToken();
        Pageable pageable = PageRequest.of(page, size);

        Page<WarehouseImportEntity> entities = warehouseImportRepository.findByCustomerId(customer.getId(), pageable);

        Page<WarehouseImportDTO> result = entities.map(warehouseImportMapper::toDTO);

        return ResponseEntity.ok(result);
    }

    /**
     * Lấy chi tiết phiếu nhập kho theo ID
     * GET /customer/warehouse-imports/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseImportDTO> getDetail(@PathVariable Long id) {
        WarehouseImportEntity entity = warehouseImportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu nhập kho với id: " + id));

        // Tường minh lấy danh sách xe từ bảng vehicles theo warehouse_import_id
        List<com.bidv.asset.vehicle.entity.VehicleEntity> vehicles = vehicleRepository.findByWarehouseImportId(id);
        entity.setVehicles(vehicles);

        return ResponseEntity.ok(warehouseImportMapper.toDTO(entity));
    }
}
