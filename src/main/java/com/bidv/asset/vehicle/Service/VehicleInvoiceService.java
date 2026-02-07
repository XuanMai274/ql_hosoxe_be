package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.CreateInvoiceVehicleRequest;
import com.bidv.asset.vehicle.DTO.InvoiceDTO;
import com.bidv.asset.vehicle.DTO.VehicleDTO;

import java.util.List;

public interface VehicleInvoiceService {
    List<VehicleDTO> createInvoiceWithVehicles(CreateInvoiceVehicleRequest request);
    List<InvoiceDTO> findAll();

}
