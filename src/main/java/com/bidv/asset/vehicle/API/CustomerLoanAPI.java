package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.LoanDTO;
import com.bidv.asset.vehicle.Mapper.LoanMapper;
import com.bidv.asset.vehicle.Repository.LoanRepository;
import com.bidv.asset.vehicle.Repository.UserAccountRepository;
import com.bidv.asset.vehicle.entity.CustomerEntity;
import com.bidv.asset.vehicle.entity.LoanEntity;
import com.bidv.asset.vehicle.entity.UserAccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * API dành cho Customer để xem danh sách khoản vay (giải ngân) của mình
 */
@RestController
@RequestMapping("/customer/loans")
@RequiredArgsConstructor
public class CustomerLoanAPI {

    private final LoanRepository loanRepository;
    private final UserAccountRepository userAccountRepository;
    private final LoanMapper loanMapper;

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
     * Lấy danh sách khoản vay (giải ngân) của khách hàng đang đăng nhập
     * GET /customer/loans
     */
    @GetMapping
    public ResponseEntity<List<LoanDTO>> getMyLoans() {
        CustomerEntity customer = getCustomerFromToken();

        List<LoanEntity> entities = loanRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId());

        List<LoanDTO> result = entities.stream()
                .map(loanMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /**
     * Lấy chi tiết khoản vay theo ID
     * GET /customer/loans/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<LoanDTO> getDetail(@PathVariable Long id) {
        LoanEntity entity = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khoản vay với id: " + id));

        return ResponseEntity.ok(loanMapper.toDto(entity));
    }
}
