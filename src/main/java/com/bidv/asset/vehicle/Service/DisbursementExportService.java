package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.DisbursementDTO;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DisbursementExportService {
    byte[] exportDocx(String templateName, DisbursementDTO disbursementDTO, List<Long> vehicleIds) throws IOException;
    Map<String, byte[]> exportAll(DisbursementDTO disbursementDTO, List<Long> vehicleIds) throws IOException;
}
