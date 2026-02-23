package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.CreditContractDTO;
import com.bidv.asset.vehicle.Mapper.CreditContractMapper;
import com.bidv.asset.vehicle.Repository.CreditContractRepository;
import com.bidv.asset.vehicle.Service.CreditContractService;
import com.bidv.asset.vehicle.entity.CreditContractEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CreditContractServiceImplement implements CreditContractService {
    @Autowired
    CreditContractRepository creditContractRepository;
    @Autowired
    CreditContractMapper creditContractMapper;

    @Override
    public CreditContractDTO createCreditContract(CreditContractDTO creditContractDTO) {
        CreditContractEntity creditContract = creditContractMapper.toEntity(creditContractDTO);
        creditContract.setCreatedAt(LocalDateTime.now());
        try {
            CreditContractEntity creditContract1 = creditContractRepository.save(creditContract);
            return creditContractMapper.toDto(creditContract1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CreditContractDTO> findAll() {
        List<CreditContractEntity> creditContractEntities = creditContractRepository.findAll();
        List<CreditContractDTO> creditContractDTOS = new ArrayList<>();
        for (CreditContractEntity creditContract : creditContractEntities) {
            creditContractDTOS.add(creditContractMapper.toDto(creditContract));
        }
        return creditContractDTOS;
    }

    @Override
    public CreditContractDTO updateCreditContract(Long id, CreditContractDTO creditContractDTO) {
        CreditContractEntity existingEntity = creditContractRepository.findById(id).orElse(null);
        if (existingEntity != null) {
            existingEntity.setContractNumber(creditContractDTO.getContractNumber());
            existingEntity.setContractDate(creditContractDTO.getContractDate());
            existingEntity.setStatus(creditContractDTO.getStatus());
            existingEntity.setCreditLimit(creditContractDTO.getCreditLimit());
            existingEntity.setUsedLimit(creditContractDTO.getUsedLimit());
            existingEntity.setRemainingLimit(creditContractDTO.getRemainingLimit());
            existingEntity.setGuaranteeBalance(creditContractDTO.getGuaranteeBalance());
            existingEntity.setVehicleLoanBalance(creditContractDTO.getVehicleLoanBalance());
            existingEntity.setRealEstateLoanBalance(creditContractDTO.getRealEstateLoanBalance());
            existingEntity.setUpdatedAt(LocalDateTime.now());

            CreditContractEntity updatedEntity = creditContractRepository.save(existingEntity);
            return creditContractMapper.toDto(updatedEntity);
        }
        return null;
    }
}
