package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.ManufacturerDTO;

import java.util.List;

public interface ManufacturerService {
    ManufacturerDTO addManufacturer(ManufacturerDTO manufacturerDTO);

    List<ManufacturerDTO> findAll();

    ManufacturerDTO findByCode(String code);

    ManufacturerDTO updateManufacturer(Long id, ManufacturerDTO dto);

    ManufacturerDTO findById(Long id);
}
