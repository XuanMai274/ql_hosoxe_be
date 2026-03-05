package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.CreditContractDTO;
import com.bidv.asset.vehicle.Service.CreditContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/officer/credit-contract")
public class CreditContractAPI {
    @Autowired
    CreditContractService creditContractService;

    @GetMapping("/findAll")
    public ResponseEntity<Map<String, Object>> findAll() {
        Map<String, Object> response = new HashMap<>();
        List<CreditContractDTO> creditContractDTOS = creditContractService.findAll();
        if (creditContractDTOS != null) {
            response.put("success", true);
            response.put("creditContract", creditContractDTOS);
            return ResponseEntity.ok(response);
        }
        response.put("success", false);
        response.put("creditContract", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/{id}")
    public CreditContractDTO getById(@PathVariable Long id) {
        return creditContractService.findById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addCreditContract(@RequestBody CreditContractDTO creditContractDTO) {
        Map<String, Object> response = new HashMap<>();
        CreditContractDTO creditContractDTO1 = creditContractService.createCreditContract(creditContractDTO);
        if (creditContractDTO1 != null) {
            response.put("success", true);
            response.put("creditContract", creditContractDTO1);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("creditContract", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateCreditContract(@PathVariable Long id,
            @RequestBody CreditContractDTO creditContractDTO) {
        Map<String, Object> response = new HashMap<>();
        CreditContractDTO updatedDTO = creditContractService.updateCreditContract(id, creditContractDTO);
        if (updatedDTO != null) {
            response.put("success", true);
            response.put("creditContract", updatedDTO);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Hợp đồng không tồn tại");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
