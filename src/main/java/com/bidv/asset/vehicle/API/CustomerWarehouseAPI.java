package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.WarehouseImportDTO;
import com.bidv.asset.vehicle.Mapper.WarehouseImportMapper;
import com.bidv.asset.vehicle.Repository.UserAccountRepository;
import com.bidv.asset.vehicle.Repository.WarehouseImportRepository;
import com.bidv.asset.vehicle.entity.CustomerEntity;
import com.bidv.asset.vehicle.entity.UserAccountEntity;
import com.bidv.asset.vehicle.entity.WarehouseImportEntity;
import lombok.RequiredArgsConstructor;
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
     * Lấy danh sách phiếu nhập kho của khách hàng đang đăng nhập
     * GET /customer/warehouse-imports
     */
    @GetMapping
    public ResponseEntity<List<WarehouseImportDTO>> getMyWarehouseImports() {
        CustomerEntity customer = getCustomerFromToken();

        List<WarehouseImportEntity> entities = warehouseImportRepository.findByCustomerId(customer.getId());

        List<WarehouseImportDTO> result = entities.stream()
                .map(warehouseImportMapper::toDTO)
                .collect(Collectors.toList());

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

        return ResponseEntity.ok(warehouseImportMapper.toDTO(entity));
    }
}
