package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.EmployeeCreateRequestDTO;
import com.bidv.asset.vehicle.DTO.EmployeeDTO;
import com.bidv.asset.vehicle.Service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/admin/employees")
public class EmployeeAPI {
    @Autowired
    EmployeeService service;

    // ===== CREATE EMPLOYEE + ACCOUNT =====
    @PostMapping("/create-with-account")
    public ResponseEntity<EmployeeDTO> createWithAccount(
            @RequestBody EmployeeCreateRequestDTO request) {

        EmployeeDTO result = service.createEmployeeWithAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // ===== UPDATE =====
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> update(
            @PathVariable Long id,
            @RequestBody EmployeeDTO dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }

    // ===== DELETE =====
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ===== GET BY ID =====
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // ===== GET ALL (with search & pagination) =====
    @GetMapping
    public ResponseEntity<Page<EmployeeDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getAll(pageable, keyword));
    }
}
