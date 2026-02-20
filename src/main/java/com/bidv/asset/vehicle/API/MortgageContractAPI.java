package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.MortgageContractDTO;
import com.bidv.asset.vehicle.Service.MortgageContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/officer/mortgage-contracts")
@RequiredArgsConstructor
public class MortgageContractAPI {

   @Autowired MortgageContractService service;

    // CREATE
    @PostMapping
    public ResponseEntity<MortgageContractDTO> create(@RequestBody MortgageContractDTO dto) {
        MortgageContractDTO result = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<MortgageContractDTO> update(
            @PathVariable Long id,
            @RequestBody MortgageContractDTO dto) {

        MortgageContractDTO result = service.update(id, dto);
        return ResponseEntity.ok(result);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<MortgageContractDTO> getById(@PathVariable Long id) {
        MortgageContractDTO result = service.getById(id);
        return ResponseEntity.ok(result);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<MortgageContractDTO>> getAll() {
        List<MortgageContractDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }
}