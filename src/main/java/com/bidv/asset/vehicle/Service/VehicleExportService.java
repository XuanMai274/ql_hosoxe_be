package com.bidv.asset.vehicle.Service;

public interface VehicleExportService {
    byte[] exportVehicleExcel(
            String chassisNumber,
            String status,
            String manufacturer,
            String guaranteeContractNumber
    );
}
