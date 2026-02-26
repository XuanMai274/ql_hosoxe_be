package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.GuaranteeApplicationDTO;
import com.bidv.asset.vehicle.Service.GuaranteeApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/customer/guarantee-applications", "/officer/guarantee-applications"})
@RequiredArgsConstructor
public class GuaranteeApplicationAPI {
    @Autowired
    GuaranteeApplicationService service;

    // =====================================================
    // ===================== CUSTOMER =======================
    // =====================================================

    // CREATE GUARANTEE APPLICATION
    @PostMapping
    public ResponseEntity<GuaranteeApplicationDTO> create(
            @RequestBody GuaranteeApplicationDTO dto) {

        GuaranteeApplicationDTO result = service.create(dto);
        return ResponseEntity.ok(result);
    }

    // =====================================================
    // ===================== OFFICER ========================
    // =====================================================

    // LIST ALL APPLICATIONS (For Officer)
    @GetMapping
    public ResponseEntity<Page<GuaranteeApplicationDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
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
}
