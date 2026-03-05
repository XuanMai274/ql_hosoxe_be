package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.MortgageContractDTO;
import com.bidv.asset.vehicle.Mapper.MortgageContractMapper;
import com.bidv.asset.vehicle.Repository.CreditContractRepository;
import com.bidv.asset.vehicle.Repository.ManufacturerRepository;
import com.bidv.asset.vehicle.Repository.MortgageContractRepository;
import com.bidv.asset.vehicle.Service.MortgageSequenceService;
import com.bidv.asset.vehicle.entity.*;
import com.bidv.asset.vehicle.Repository.*;
import com.bidv.asset.vehicle.Service.MortgageContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class MortgageContractServiceImplement implements MortgageContractService {

    @Autowired
    MortgageContractRepository mortgageRepo;
    @Autowired
    CustomerRepository customerRepo;
    @Autowired
    ManufacturerRepository manufacturerRepo;
    @Autowired
    CreditContractRepository creditRepo;
    @Autowired
    MortgageContractMapper mortgageContractMapper;
    @Autowired
    MortgageSequenceService mortgageSequenceService;
    @Autowired
    MortgageContractSequenceRepository sequenceRepo;

    // ===== CREATE =====
    @Override
    @Transactional
    public MortgageContractDTO create(MortgageContractDTO dto) {

        if (mortgageRepo.existsByContractNumber(dto.getContractNumber())) {
            throw new RuntimeException("Contract number already exists");
        }

        CustomerEntity customer = customerRepo.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        ManufacturerEntity manufacturer = null;
        if (dto.getManufacturerDTO() != null) {
            manufacturer = manufacturerRepo.findById(dto.getManufacturerDTO().getId())
                    .orElseThrow(() -> new RuntimeException("Manufacturer not found"));
        }

        List<CreditContractEntity> creditContracts = null;
        if (dto.getCreditContractIds() != null) {
            creditContracts = creditRepo.findAllById(dto.getCreditContractIds());
        }

        MortgageContractEntity entity = mortgageContractMapper.toEntity(
                dto, customer, manufacturer, creditContracts);

        if (entity.getContractDate() != null) {
            entity.setExpiryDate(entity.getContractDate().plusYears(1));
        }

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        MortgageContractEntity saved = mortgageRepo.save(entity);

        // tạo sequence trong cùng transaction
        mortgageSequenceService.createSequence(saved);

        return mortgageContractMapper.toDTO(saved);
    }

    // ===== UPDATE =====
    @Override
    public MortgageContractDTO update(Long id, MortgageContractDTO dto) {

        MortgageContractEntity entity = mortgageRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mortgage contract not found"));

        CustomerEntity customer = customerRepo.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        ManufacturerEntity manufacturer = null;
        if (dto.getManufacturerDTO() != null) {
            manufacturer = manufacturerRepo.findById(dto.getManufacturerDTO().getId())
                    .orElseThrow(() -> new RuntimeException("Manufacturer not found"));
        }

        List<CreditContractEntity> creditContracts = null;
        if (dto.getCreditContractIds() != null) {
            creditContracts = creditRepo.findAllById(dto.getCreditContractIds());
        }

        entity.setContractNumber(dto.getContractNumber());
        entity.setContractDate(dto.getContractDate());
        if (entity.getContractDate() != null) {
            entity.setExpiryDate(entity.getContractDate().plusYears(1));
        }
        entity.setTotalCollateralValue(dto.getTotalCollateralValue());
        entity.setRemainingCollateralValue(dto.getRemainingCollateralValue());

        entity.setCustomer(customer);
        entity.setManufacturer(manufacturer);
        entity.setCreditContracts(creditContracts);

        entity.setUpdatedAt(LocalDateTime.now());

        MortgageContractEntity saved = mortgageRepo.save(entity);
        mortgageSequenceService.createSequence(saved);

        // ===== SYNC SEQUENCE COUNTERS =====
        if (dto.getGuaranteeRunningNo() != null || dto.getWarehouseRunningNo() != null) {
            MortgageContractSequenceEntity seq = sequenceRepo.findById(saved.getId()).orElse(null);
            if (seq != null) {
                if (dto.getGuaranteeRunningNo() != null)
                    seq.setGuaranteeRunningNo(dto.getGuaranteeRunningNo());
                if (dto.getWarehouseRunningNo() != null)
                    seq.setWarehouseRunningNo(dto.getWarehouseRunningNo());
                sequenceRepo.save(seq);
            }
        }

        return mortgageContractMapper.toDTO(saved);
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
                .map(mortgageContractMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Mortgage contract not found"));
    }

    // ===== GET ALL =====
    @Override
    public List<MortgageContractDTO> getAll() {

        return mortgageRepo.findAll()
                .stream()
                .map(mortgageContractMapper::toDTO)
                .collect(Collectors.toList());
    }
}