package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.OcrResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface OcrClient {

    OcrResponseDTO extract(MultipartFile file);

}
