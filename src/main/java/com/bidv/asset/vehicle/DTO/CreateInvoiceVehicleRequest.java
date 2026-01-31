package com.bidv.asset.vehicle.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class CreateInvoiceVehicleRequest {

    private InvoiceDTO invoice;
    private List<VehicleDTO> vehicles;

}