package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.CreateInvoiceVehicleRequest;
import com.bidv.asset.vehicle.DTO.GuaranteeLetterDTO;
import com.bidv.asset.vehicle.DTO.InvoiceDTO;
import com.bidv.asset.vehicle.DTO.VehicleDTO;
import com.bidv.asset.vehicle.Service.VehicleInvoiceService;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        System.out.println("API Đang tạo hóa đơn số: "+request.getInvoice().getInvoiceNumber()+" với số lượng xe là: "+request.getVehicles().size());
        List<VehicleDTO> vehicles =
                vehicleInvoiceService.createInvoiceWithVehicles(request);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("vehicles", vehicles);

        return ResponseEntity.ok(res);
    }
    @GetMapping("/findAll")
    public ResponseEntity<List<InvoiceDTO>> findAll(){
        List<InvoiceDTO> result = vehicleInvoiceService.findAll();

        return ResponseEntity.ok(result);
    }

}
