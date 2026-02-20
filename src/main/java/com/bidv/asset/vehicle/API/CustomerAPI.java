package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.CustomerCreateRequestDTO;
import com.bidv.asset.vehicle.DTO.CustomerDTO;
import com.bidv.asset.vehicle.Service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerAPI {

    private final CustomerService service;

    // =====================================================
    // ===================== ADMIN ==========================
    // =====================================================

    // ===== CREATE =====
    @PostMapping("/admin/customers")
    public ResponseEntity<CustomerDTO> create(
            @RequestBody CustomerCreateRequestDTO dto) {

        CustomerDTO result = service.createCustomerWithAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // ===== UPDATE =====
    @PutMapping("/admin/customers/{id}")
    public ResponseEntity<CustomerDTO> update(
            @PathVariable Long id,
            @RequestBody CustomerDTO dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }

    // ===== DELETE =====
    @DeleteMapping("/admin/customers/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }


    // =====================================================
    // ===================== OFFICER ========================
    // =====================================================

    // ===== GET BY ID =====
    @GetMapping({"/admin/customers/{id}", "/officer/customers/{id}"})
    public ResponseEntity<CustomerDTO> getById(@PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    // ===== GET ALL =====
    @GetMapping({"/admin/customers", "/officer/customers"})
    public ResponseEntity<List<CustomerDTO>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }
}
