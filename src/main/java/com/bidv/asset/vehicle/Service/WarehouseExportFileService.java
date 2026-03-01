package com.bidv.asset.vehicle.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface WarehouseExportFileService {
    Map<String, byte[]> exportAll(Long exportId, List<Long> vehicleIds) throws IOException;
    Map<String, byte[]> exportSpecific(Long exportId, List<Long> vehicleIds) throws IOException;
}
