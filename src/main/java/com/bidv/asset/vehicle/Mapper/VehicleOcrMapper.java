package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.OcrResponseDTO;
import com.bidv.asset.vehicle.DTO.VehicleDTO;

import java.util.ArrayList;
import java.util.List;

public class VehicleOcrMapper {

    public static List<VehicleDTO> mapAndValidateVehicles(OcrResponseDTO ocr) {

        List<VehicleDTO> result = new ArrayList<>();

        if (ocr == null || ocr.getData() == null || ocr.getData().getVehicle_list() == null) {
            return result;
        }

        ocr.getData().getVehicle_list().forEach(v -> {
            VehicleDTO dto = new VehicleDTO();
            dto.setChassisNumber(v.getChassis_number());
            dto.setEngineNumber(v.getEngine_number());
            dto.setColor(v.getColor());
            dto.setSeats(
                    v.getNumber_of_seats() != null
                            ? Integer.parseInt(v.getNumber_of_seats())
                            : null
            );
            dto.setDescription(v.getVehicle_description());
            result.add(dto);
        });

        return result;
    }
}
