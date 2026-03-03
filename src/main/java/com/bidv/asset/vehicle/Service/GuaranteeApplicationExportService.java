package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.GuaranteeApplicationDTO;

import java.io.IOException;
import java.util.Map;

public interface GuaranteeApplicationExportService {
    public Map<String, byte[]> exportAll(GuaranteeApplicationDTO dto)
            throws IOException;
//    byte[] exportDeNghiCapBaoLanh(Long applicationId) throws Exception;
//
//    byte[] exportDanhSachXeBaoLanh(Long applicationId) throws Exception;
}
