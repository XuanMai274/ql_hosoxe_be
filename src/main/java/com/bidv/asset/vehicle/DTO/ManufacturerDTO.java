package com.bidv.asset.vehicle.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManufacturerDTO {
    private Long id;
    private String code;
    private String name;
    private LocalDateTime createdAt;
    private List<Long> vehicleIds;
}
