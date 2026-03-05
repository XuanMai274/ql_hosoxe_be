package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.GuaranteeApplicationDTO;
import com.bidv.asset.vehicle.Service.GuaranteeApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import com.bidv.asset.vehicle.entity.CustomerEntity;
import com.bidv.asset.vehicle.entity.UserAccountEntity;
import com.bidv.asset.vehicle.Repository.UserAccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping({ "/customer/guarantee-applications", "/officer/guarantee-applications" })
@RequiredArgsConstructor
public class GuaranteeApplicationAPI {
    private final GuaranteeApplicationService service;

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

    // =====================================================
    // ===================== CUSTOMER =======================
    // =====================================================

    // UPDATE GUARANTEE APPLICATION
    @PutMapping("/{id}")
    public ResponseEntity<GuaranteeApplicationDTO> update(
            @PathVariable Long id,
            @RequestBody GuaranteeApplicationDTO dto) {

        GuaranteeApplicationDTO result = service.update(id, dto);
        return ResponseEntity.ok(result);
    }

    // =====================================================
    // ===================== OFFICER ========================
    // =====================================================

    // LIST ALL APPLICATIONS (For Officer/Customer with filter)
    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<GuaranteeApplicationDTO>> getAll(
            @RequestParam(required = false) Long manufacturerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            org.springframework.data.domain.Pageable pageable,
            HttpServletRequest request) {
        Long customerId = getCustomerIdIfCustomer(request);
        return ResponseEntity.ok(service.search(customerId, manufacturerId, status, fromDate, toDate, pageable));
    }

    // LIST APPLICATIONS EXCLUDING REJECTED
    @GetMapping("/exclude-rejected")
    public ResponseEntity<org.springframework.data.domain.Page<GuaranteeApplicationDTO>> getExcludeRejected(
            @RequestParam(required = false) Long manufacturerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            org.springframework.data.domain.Pageable pageable,
            HttpServletRequest request) {
        Long customerId = getCustomerIdIfCustomer(request);
        return ResponseEntity
                .ok(service.searchExcludeRejected(customerId, manufacturerId, status, fromDate, toDate, pageable));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<GuaranteeApplicationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // APPROVE
    @PostMapping("/{id}/approve")
    public ResponseEntity<GuaranteeApplicationDTO> approve(@PathVariable Long id) {
        return ResponseEntity.ok(service.approve(id));
    }

    // REJECT
    @PostMapping("/{id}/reject")
    public ResponseEntity<GuaranteeApplicationDTO> reject(@PathVariable Long id) {
        return ResponseEntity.ok(service.reject(id));
    }

    // STATISTICS
    @GetMapping("/statistics")
    public ResponseEntity<com.bidv.asset.vehicle.DTO.GuaranteeStatisticsDTO> getStatistics() {
        return ResponseEntity.ok(service.getStatistics());
    }
}
