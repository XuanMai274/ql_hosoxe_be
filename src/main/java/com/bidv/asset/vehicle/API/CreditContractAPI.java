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
@RequestMapping("officer/credit-contract")
public class CreditContractAPI {
    @Autowired
    CreditContractService creditContractService;
    @GetMapping("/findAll")
    public ResponseEntity<Map<String,Object>> findAll(){
        Map<String, Object> response = new HashMap<>();
        List<CreditContractDTO> creditContractDTOS=creditContractService.findAll();
        if(creditContractDTOS!=null){
            response.put("success", true);
            response.put("creditContract",creditContractDTOS);
            return ResponseEntity.ok(response);
        }
        response.put("success", false);
        response.put("creditContract", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @PostMapping("/add")
    public ResponseEntity<Map<String,Object>> addCreditContract(@RequestBody CreditContractDTO creditContractDTO){
        Map<String, Object> response = new HashMap<>();
        CreditContractDTO creditContractDTO1=creditContractService.createCreditContract(creditContractDTO);
        if(creditContractDTO1!=null){
            response.put("success", true);
            response.put("creditContract",creditContractDTO1);
            return ResponseEntity.ok(response);
        }else{
            response.put("success", false);
            response.put("creditContract", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
