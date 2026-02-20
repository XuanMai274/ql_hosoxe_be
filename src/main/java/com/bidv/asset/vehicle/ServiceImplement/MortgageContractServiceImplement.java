package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.MortgageContractDTO;
import com.bidv.asset.vehicle.Mapper.MortgageContractMapper;
import com.bidv.asset.vehicle.Repository.CreditContractRepository;
import com.bidv.asset.vehicle.Repository.ManufacturerRepository;
import com.bidv.asset.vehicle.Repository.MortgageContractRepository;
import com.bidv.asset.vehicle.entity.*;
import com.bidv.asset.vehicle.Repository.*;
import com.bidv.asset.vehicle.Service.MortgageContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MortgageContractServiceImplement implements MortgageContractService {

    @Autowired
    MortgageContractRepository mortgageRepo;
    @Autowired CustomerRepository customerRepo;
    @Autowired
    ManufacturerRepository manufacturerRepo;
    @Autowired
    CreditContractRepository creditRepo;

    // ===== CREATE =====
    @Override
    public MortgageContractDTO create(MortgageContractDTO dto) {

        if (mortgageRepo.existsByContractNumber(dto.getContractNumber())) {
            throw new RuntimeException("Contract number already exists");
        }

        CustomerEntity customer = customerRepo.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        ManufacturerEntity manufacturer = null;
        if (dto.getManufacturerId() != null) {
            manufacturer = manufacturerRepo.findById(dto.getManufacturerId())
                    .orElseThrow(() -> new RuntimeException("Manufacturer not found"));
        }

        List<CreditContractEntity> creditContracts = null;
        if (dto.getCreditContractIds() != null) {
            creditContracts = creditRepo.findAllById(dto.getCreditContractIds());
        }

        MortgageContractEntity entity = MortgageContractMapper.toEntity(
                dto, customer, manufacturer, creditContracts
        );

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        return MortgageContractMapper.toDTO(mortgageRepo.save(entity));
    }

    // ===== UPDATE =====
    @Override
    public MortgageContractDTO update(Long id, MortgageContractDTO dto) {

        MortgageContractEntity entity = mortgageRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mortgage contract not found"));

        CustomerEntity customer = customerRepo.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        ManufacturerEntity manufacturer = null;
        if (dto.getManufacturerId() != null) {
            manufacturer = manufacturerRepo.findById(dto.getManufacturerId())
                    .orElseThrow(() -> new RuntimeException("Manufacturer not found"));
        }

        List<CreditContractEntity> creditContracts = null;
        if (dto.getCreditContractIds() != null) {
            creditContracts = creditRepo.findAllById(dto.getCreditContractIds());
        }

        entity.setContractNumber(dto.getContractNumber());
        entity.setContractDate(dto.getContractDate());
        entity.setTotalCollateralValue(dto.getTotalCollateralValue());
        entity.setRemainingCollateralValue(dto.getRemainingCollateralValue());

        entity.setCustomer(customer);
        entity.setManufacturer(manufacturer);
        entity.setCreditContracts(creditContracts);

        entity.setUpdatedAt(LocalDateTime.now());

        return MortgageContractMapper.toDTO(mortgageRepo.save(entity));
    }

    // ===== DELETE =====
    @Override
    public void delete(Long id) {

        if (!mortgageRepo.existsById(id)) {
            throw new RuntimeException("Mortgage contract not found");
        }

        mortgageRepo.deleteById(id);
    }

    // ===== GET BY ID =====
    @Override
    public MortgageContractDTO getById(Long id) {

        return mortgageRepo.findById(id)
                .map(MortgageContractMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Mortgage contract not found"));
    }

    // ===== GET ALL =====
    @Override
    public List<MortgageContractDTO> getAll() {

        return mortgageRepo.findAll()
                .stream()
                .map(MortgageContractMapper::toDTO)
                .collect(Collectors.toList());
    }
}