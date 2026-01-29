package com.bidv.asset.vehicle.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
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
    private String logo;
    private String description;
    private LocalDateTime createdAt;
    private BigDecimal guaranteeRate;
    private String templateCode;
    // CHỈ LƯU ID, KHÔNG MAP FULL
    private List<Long> guaranteeLetterIds;
}

