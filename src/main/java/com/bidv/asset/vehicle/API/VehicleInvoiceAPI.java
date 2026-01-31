package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.CreateInvoiceVehicleRequest;
import com.bidv.asset.vehicle.DTO.VehicleDTO;
import com.bidv.asset.vehicle.Service.VehicleInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/officer/vehicle-invoice")
@RequiredArgsConstructor
public class VehicleInvoiceAPI {
    @Autowired
    VehicleInvoiceService vehicleInvoiceService;
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createInvoiceWithVehicles(
            @RequestBody CreateInvoiceVehicleRequest request
    ) {
        List<VehicleDTO> vehicles =
                vehicleInvoiceService.createInvoiceWithVehicles(request);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("vehicles", vehicles);

        return ResponseEntity.ok(res);
    }

}
