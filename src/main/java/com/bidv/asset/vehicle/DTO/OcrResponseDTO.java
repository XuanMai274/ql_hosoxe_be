package com.bidv.asset.vehicle.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OcrResponseDTO {
    private String status;
    private String layout_detected;
    private OcrDataDTO data;
}
