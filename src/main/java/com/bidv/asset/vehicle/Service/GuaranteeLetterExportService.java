package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.ExportDeXuatRequest;
import com.bidv.asset.vehicle.DTO.GuaranteeLetterDTO;
import com.bidv.asset.vehicle.DTO.XuatDeXuatBaoLanh;

import java.io.IOException;

public interface GuaranteeLetterExportService {
    public byte[] generateThuBaoLanh(GuaranteeLetterDTO dto,String template) throws IOException;
    public byte[] generateDeXuatBaoLanh(ExportDeXuatRequest exportDeXuatRequest,String template) throws IOException;
    public byte[] generateXetDuyet(GuaranteeLetterDTO dto,String template) throws IOException;
    public byte[] generateYKien(GuaranteeLetterDTO dto, String template)throws IOException;
}
