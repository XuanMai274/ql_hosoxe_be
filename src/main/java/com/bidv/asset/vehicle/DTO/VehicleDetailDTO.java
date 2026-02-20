package com.bidv.asset.vehicle.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDetailDTO {

    private VehicleDTO vehicle;
    private GuaranteeLetterDTO guaranteeLetter;
    private InvoiceDTO invoice;

}