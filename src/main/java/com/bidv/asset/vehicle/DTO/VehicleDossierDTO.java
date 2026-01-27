package com.bidv.asset.vehicle.DTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDossierDTO {
    private Long id;
    private Long vehicleId;

    private String status;
    private LocalDate importDate;
    private LocalDate exportDate;

    private String originalCopy;
    private String importDocs;

    private String registrationOrderNumber;
    private LocalDate docsDeliveryDate;
    private LocalDateTime createdAt;
}
