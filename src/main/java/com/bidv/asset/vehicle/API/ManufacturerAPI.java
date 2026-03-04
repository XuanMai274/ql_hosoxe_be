package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.BranchAuthorizedRepresentativeDTO;
import com.bidv.asset.vehicle.DTO.ManufacturerDTO;
import com.bidv.asset.vehicle.Service.ManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ManufacturerAPI {
    @Autowired
    ManufacturerService manufacturerService;

    @PostMapping("/officer/manufacturer/add")
    public ResponseEntity<ManufacturerDTO> add(
            @RequestBody ManufacturerDTO dto) {
        ManufacturerDTO result = manufacturerService.addManufacturer(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    @GetMapping("/officer/manufacturer/code/{code}")
    public ResponseEntity<ManufacturerDTO> getByCode(@PathVariable String code) {
        ManufacturerDTO result = manufacturerService.findByCode(code);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/officer/manufacturer/findAll")
    public ResponseEntity<Map<String, Object>> findAll() {
        Map<String, Object> response = new HashMap<>();
        List<ManufacturerDTO> manufacturerDTO = manufacturerService.findAll();
        if (manufacturerDTO != null) {
            response.put("success", true);
            response.put("manufacturerDTO", manufacturerDTO);
            return ResponseEntity.ok(response);
        }
        response.put("success", false);
        response.put("manufacturerDTO", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/officer/manufacturer/{id}")
    public ResponseEntity<ManufacturerDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(manufacturerService.findById(id));
    }

    @PutMapping("/officer/manufacturer/{id}")
    public ResponseEntity<ManufacturerDTO> update(
            @PathVariable Long id,
            @RequestBody ManufacturerDTO dto) {
        return ResponseEntity.ok(manufacturerService.updateManufacturer(id, dto));
    }

    @PostMapping("/officer/manufacturer/upload-logo")
    public ResponseEntity<Map<String, String>> uploadLogo(@RequestParam("file") MultipartFile file) {
        try {
            String uploadDir = "uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists())
                dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            Map<String, String> response = new HashMap<>();
            response.put("fileName", fileName);
            response.put("url", "/uploads/" + fileName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/customer/manufacturer/findAll")
    public ResponseEntity<Map<String, Object>> findAllCustomer() {
        Map<String, Object> response = new HashMap<>();
        List<ManufacturerDTO> manufacturerDTO = manufacturerService.findAll();
        if (manufacturerDTO != null) {
            response.put("success", true);
            response.put("manufacturerDTO", manufacturerDTO);
            return ResponseEntity.ok(response);
        }
        response.put("success", false);
        response.put("manufacturerDTO", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
