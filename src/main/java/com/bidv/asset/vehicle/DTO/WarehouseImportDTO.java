package com.bidv.asset.vehicle.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class WarehouseImportDTO {

    private Long id;
    // số HDBD chi tiết
    private String importNumber;
    // Manufacturer
    private ManufacturerDTO manufacturerDTO;
    // Mortgage Contract
    private MortgageContractDTO mortgageContractDTO;
    // Danh sách vehicle id
    private List<Long> vehicleIds;
    // Danh sách vehicle đầy đủ (dùng cho view chi tiết)
    private List<VehicleDTO> vehicles;

    private LocalDateTime createdAt;
}
