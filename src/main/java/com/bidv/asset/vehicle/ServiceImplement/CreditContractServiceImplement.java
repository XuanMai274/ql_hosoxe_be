package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.Utill.MoneyUtil;
import com.bidv.asset.vehicle.DTO.CreditContractDTO;
import com.bidv.asset.vehicle.Mapper.CreditContractMapper;
import com.bidv.asset.vehicle.Repository.CreditContractRepository;
import com.bidv.asset.vehicle.Repository.CustomerRepository;
import com.bidv.asset.vehicle.Service.CreditContractService;
import com.bidv.asset.vehicle.entity.CreditContractEntity;
import com.bidv.asset.vehicle.entity.CustomerEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CreditContractServiceImplement implements CreditContractService {
    @Autowired
    CreditContractRepository creditContractRepository;
    @Autowired
    CreditContractMapper creditContractMapper;
    @Autowired
    CustomerRepository customerRepository;

    @Override
    public CreditContractDTO createCreditContract(CreditContractDTO creditContractDTO) {
        CreditContractEntity creditContract = creditContractMapper.toEntity(creditContractDTO);
        creditContract.setUsedLimit(MoneyUtil.format(BigDecimal.ZERO));
        creditContract.setIssuedGuaranteeBalance(MoneyUtil.format(BigDecimal.ZERO));
        creditContract.setOutstandingGuaranteeAmount(MoneyUtil.format(BigDecimal.ZERO));
        creditContract.setGuaranteeBalance(MoneyUtil.format(BigDecimal.ZERO));
        creditContract.setRemainingLimit(MoneyUtil.format(creditContract.getCreditLimit()));
        creditContract.setVehicleLoanBalance(MoneyUtil.format(BigDecimal.ZERO));
        creditContract.setRealEstateLoanBalance(MoneyUtil.format(BigDecimal.ZERO));
        if (creditContractDTO.getCustomerId() != null) {
            CustomerEntity customer = customerRepository.findById(creditContractDTO.getCustomerId())
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
            creditContract.setCustomer(customer);
        }

        if (creditContract.getContractDate() != null) {
            creditContract.setExpiryDate(creditContract.getContractDate().plusYears(1));
        }
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
            if (existingEntity.getContractDate() != null) {
                existingEntity.setExpiryDate(existingEntity.getContractDate().plusYears(1));
            }
            existingEntity.setStatus(creditContractDTO.getStatus());
            existingEntity.setCreditLimit(MoneyUtil.format(creditContractDTO.getCreditLimit()));
            existingEntity.setUsedLimit(MoneyUtil.format(creditContractDTO.getUsedLimit()));
            existingEntity.setRemainingLimit(MoneyUtil.format(creditContractDTO.getRemainingLimit()));
            existingEntity.setGuaranteeBalance(MoneyUtil.format(creditContractDTO.getGuaranteeBalance()));
            existingEntity.setVehicleLoanBalance(MoneyUtil.format(creditContractDTO.getVehicleLoanBalance()));
            existingEntity.setRealEstateLoanBalance(MoneyUtil.format(creditContractDTO.getRealEstateLoanBalance()));
            existingEntity.setCreditLimit(creditContractDTO.getCreditLimit());

            // Cập nhật các giá trị số dư
            BigDecimal issued = nvl(creditContractDTO.getIssuedGuaranteeBalance());
            BigDecimal vehicleLoan = nvl(creditContractDTO.getVehicleLoanBalance());
            BigDecimal realEstateLoan = nvl(creditContractDTO.getRealEstateLoanBalance());
            BigDecimal creditLimit = nvl(creditContractDTO.getCreditLimit());
            BigDecimal actualGuarantee = nvl(creditContractDTO.getGuaranteeBalance());

            existingEntity.setIssuedGuaranteeBalance(issued);
            existingEntity.setVehicleLoanBalance(vehicleLoan);
            existingEntity.setRealEstateLoanBalance(realEstateLoan);

            // Tự động tính Used Limit
            BigDecimal usedLimit = issued.add(vehicleLoan).add(realEstateLoan);
            existingEntity.setUsedLimit(usedLimit);

            // Tự động tính Remaining Limit
            existingEntity.setRemainingLimit(creditLimit.subtract(usedLimit));

            // Cập nhật Dư bảo lãnh thực tế (lấy từ DTO)
            existingEntity.setGuaranteeBalance(actualGuarantee);

            // Cập nhật Dư bảo lãnh phát hành (Outstanding) = BL phát hành - BL thực tế
            existingEntity.setOutstandingGuaranteeAmount(issued.subtract(actualGuarantee));

            if (creditContractDTO.getCustomerId() != null) {
                CustomerEntity customer = customerRepository.findById(creditContractDTO.getCustomerId())
                        .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
                existingEntity.setCustomer(customer);
            }

            existingEntity.setUpdatedAt(LocalDateTime.now());

            CreditContractEntity updatedEntity = creditContractRepository.save(existingEntity);
            return creditContractMapper.toDto(updatedEntity);
        }
        return null;
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    @Override
    public CreditContractDTO findById(Long id) {

        CreditContractEntity entity = creditContractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CreditContract not found with id: " + id));

        return creditContractMapper.toDto(entity);
    }
}
