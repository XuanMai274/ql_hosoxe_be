package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.DisbursementDTO;
import com.bidv.asset.vehicle.Mapper.DisbursementMapper;
import com.bidv.asset.vehicle.Repository.DisbursementRepository;
import com.bidv.asset.vehicle.Repository.UserAccountRepository;
import com.bidv.asset.vehicle.entity.CustomerEntity;
import com.bidv.asset.vehicle.entity.DisbursementEntity;
import com.bidv.asset.vehicle.entity.UserAccountEntity;
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
 * API dành cho Customer để xem danh sách các đợt giải ngân (cha) và khoản vay
 * (con)
 */
@RestController
@RequestMapping("/customer/disbursements")
@RequiredArgsConstructor
public class CustomerLoanAPI {

    private final DisbursementRepository disbursementRepository;
    private final UserAccountRepository userAccountRepository;
    private final DisbursementMapper disbursementMapper;

    /**
     * Lấy Customer từ JWT token
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
     * Lấy danh sách đợt giải ngân của khách hàng (phân trang)
     * GET /customer/disbursements
     */
    @GetMapping
    public ResponseEntity<Page<DisbursementDTO>> getMyDisbursements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        CustomerEntity customer = getCustomerFromToken();
        Pageable pageable = PageRequest.of(page, size);

        Page<DisbursementEntity> entities = disbursementRepository.findByCustomerId(customer.getId(), pageable);

        Page<DisbursementDTO> result = entities.map(disbursementMapper::toDto);

        return ResponseEntity.ok(result);
    }

    /**
     * Lấy chi tiết đợt giải ngân (bao gồm danh sách khoản vay con)
     * GET /customer/disbursements/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DisbursementDTO> getDetail(@PathVariable Long id) {
        DisbursementEntity entity = disbursementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt giải ngân với id: " + id));

        return ResponseEntity.ok(disbursementMapper.toDto(entity));
    }
}
