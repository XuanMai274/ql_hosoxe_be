package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.BranchAuthorizedRepresentativeDTO;
import com.bidv.asset.vehicle.DTO.ManufacturerDTO;
import com.bidv.asset.vehicle.Service.ManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ManufacturerAPI {
    @Autowired
    ManufacturerService manufacturerService;
    @PostMapping("/officer/manufacturer/add")
    public ResponseEntity<ManufacturerDTO> add(
            @RequestBody ManufacturerDTO dto
    ) {
        ManufacturerDTO result =
                manufacturerService.addManufacturer(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }
    @GetMapping("/officer/manufacturer/findAll")
    public ResponseEntity<Map<String,Object>> findAll(){
        Map<String, Object> response = new HashMap<>();
        List<ManufacturerDTO> manufacturerDTO=manufacturerService.findAll();
        if(manufacturerDTO!=null){
            response.put("success", true);
            response.put("manufacturerDTO",manufacturerDTO);
            return ResponseEntity.ok(response);
        }
        response.put("success", false);
        response.put("manufacturerDTO", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @GetMapping("/customer/manufacturer/findAll")
    public ResponseEntity<Map<String,Object>> findAllCustomer(){
        Map<String, Object> response = new HashMap<>();
        List<ManufacturerDTO> manufacturerDTO=manufacturerService.findAll();
        if(manufacturerDTO!=null){
            response.put("success", true);
            response.put("manufacturerDTO",manufacturerDTO);
            return ResponseEntity.ok(response);
        }
        response.put("success", false);
        response.put("manufacturerDTO", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
