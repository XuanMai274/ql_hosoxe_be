package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.GuaranteeLetterFileDTO;
import org.springframework.web.multipart.MultipartFile;

public interface GuaranteeLetterFileService {
    GuaranteeLetterFileDTO uploadFile(Long guaranteeLetterId, MultipartFile file);

    byte[] loadFile(Long fileId);

    GuaranteeLetterFileDTO getMeta(Long fileId);
    public void deleteFile(Long fileId);
}
