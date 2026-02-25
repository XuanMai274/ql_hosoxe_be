package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.GuaranteeApplicationDTO;
import com.bidv.asset.vehicle.Service.GuaranteeApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer/guarantee-applications")
@RequiredArgsConstructor
public class GuaranteeApplicationAPI {
    @Autowired
    GuaranteeApplicationService service;

    // ===============================
    // CREATE GUARANTEE APPLICATION
    // ===============================
    @PostMapping
    public ResponseEntity<GuaranteeApplicationDTO> create(
            @RequestBody GuaranteeApplicationDTO dto) {

        GuaranteeApplicationDTO result = service.create(dto);

        return ResponseEntity.ok(result);
    }
}
