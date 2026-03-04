package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.BranchAuthorizedRepresentativeDTO;
import com.bidv.asset.vehicle.DTO.ManufacturerDTO;
import com.bidv.asset.vehicle.Mapper.ManufacturerMapper;
import com.bidv.asset.vehicle.Repository.ManufacturerRepository;
import com.bidv.asset.vehicle.Service.ManufacturerService;
import com.bidv.asset.vehicle.entity.BranchAuthorizedRepresentativeEntity;
import com.bidv.asset.vehicle.entity.ManufacturerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ManufacturerServiceImplement implements ManufacturerService {
    @Autowired
    ManufacturerRepository manufacturerRepository;
    @Autowired
    ManufacturerMapper manufacturerMapper;

    @Override
    public ManufacturerDTO addManufacturer(ManufacturerDTO manufacturerDTO) {
        ManufacturerEntity manufacturerEntity = manufacturerMapper.toEntity(manufacturerDTO);
        manufacturerEntity.setCreatedAt(LocalDateTime.now());
        ManufacturerEntity manufacturerEntity1 = manufacturerRepository.save(manufacturerEntity);
        return manufacturerMapper.toDto(manufacturerEntity1);
    }

    @Override
    public List<ManufacturerDTO> findAll() {
        List<ManufacturerEntity> manufacturerEntityList = manufacturerRepository.findAll();
        List<ManufacturerDTO> manufacturerDTOS = new ArrayList<>();
        for (ManufacturerEntity manufacturerEntity : manufacturerEntityList) {
            manufacturerDTOS.add(manufacturerMapper.toDto(manufacturerEntity));
        }
        return manufacturerDTOS;
    }

    @Override
    public ManufacturerDTO findByCode(String code) {

        ManufacturerEntity entity = manufacturerRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hãng với code: " + code));

        return manufacturerMapper.toDto(entity);
    }

    @Override
    public ManufacturerDTO updateManufacturer(Long id, ManufacturerDTO dto) {
        ManufacturerEntity entity = manufacturerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hãng với id: " + id));
        manufacturerMapper.updateEntity(entity, dto);
        ManufacturerEntity updated = manufacturerRepository.save(entity);
        return manufacturerMapper.toDto(updated);
    }

    @Override
    public ManufacturerDTO findById(Long id) {
        ManufacturerEntity entity = manufacturerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hãng với id: " + id));
        return manufacturerMapper.toDto(entity);
    }
}
