package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.GuaranteeLetterDTO;

import java.io.IOException;

public interface GuaranteeLetterExportService {
    public byte[] generateWord(GuaranteeLetterDTO dto,String template) throws IOException;
}
