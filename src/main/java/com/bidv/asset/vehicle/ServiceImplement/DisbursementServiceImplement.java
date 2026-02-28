package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.DisbursementDTO;
import com.bidv.asset.vehicle.Mapper.DisbursementMapper;
import com.bidv.asset.vehicle.Repository.*;
import com.bidv.asset.vehicle.Service.DisbursementService;
import com.bidv.asset.vehicle.entity.CreditContractEntity;
import com.bidv.asset.vehicle.entity.DisbursementEntity;
import com.bidv.asset.vehicle.entity.MortgageContractEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DisbursementServiceImplement implements DisbursementService {

    private final DisbursementRepository disbursementRepository;
    private final DisbursementMapper disbursementMapper;
    private final CreditContractRepository creditContractRepository;
    private final VehicleRepository vehicleRepository;
    @Autowired
    MortgageContractRepository mortgageContractRepository;
    @Autowired
    LoanRepository loanRepository;
    @Override
    @Transactional
    public DisbursementDTO createDisbursement(DisbursementDTO dto) {

        DisbursementEntity entity = disbursementMapper.toEntity(dto);
        MortgageContractEntity mortgageContract =
                mortgageContractRepository.findById(dto.getMortgageContractId())
                        .orElseThrow(() ->
                                new RuntimeException("Không tìm thấy Mortgage Contract"));
        CreditContractEntity credit = creditContractRepository
                .findFirstByStatus("ACTIVE")
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy Credit Contract ACTIVE"));

        // 🔹 Lấy max sequence hiện tại
        Integer maxSeq = disbursementRepository
                .findMaxChildSequence(credit.getId());

        int nextSeq = (maxSeq == null ? 1 : maxSeq + 1);

        // 🔹 Build contract number
        String masterNumber = credit.getContractNumber();

        String rootCode = masterNumber.split("/")[0];
        String rest = masterNumber.substring(masterNumber.indexOf("/"));

        String seqFormatted = String.format("%02d", nextSeq);

        String loanContractNumber =
                rootCode + "." + seqFormatted
                        + rest.replace("HDTD", "HDTDCT");

        // 🔹 Set dữ liệu
        entity.setMortgageContract(mortgageContract);
        entity.setCreditContract(credit);
        entity.setChildSequence(nextSeq);
        entity.setLoanContractNumber(loanContractNumber);
        entity.setCreditContract(credit);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        DisbursementEntity saved = disbursementRepository.save(entity);

        return disbursementMapper.toDto(saved);
    }

    @Override
    public DisbursementDTO getDetail(Long id) {
        DisbursementEntity entity = disbursementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disbursement not found with id: " + id));
        return disbursementMapper.toDto(entity);
    }

    @Override
    @Transactional
    public DisbursementDTO updateDisbursement(Long id, DisbursementDTO dto) {
        DisbursementEntity existingEntity = disbursementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disbursement not found with id: " + id));
        
        // Cập nhật các trường thông tin
        existingEntity.setLoanContractNumber(dto.getLoanContractNumber());
        existingEntity.setUsedLimit(dto.getUsedLimit());
        existingEntity.setRemainingLimit(dto.getRemainingLimit());
        existingEntity.setIssuedGuaranteeBalance(dto.getIssuedGuaranteeBalance());
        existingEntity.setVehicleLoanBalance(dto.getVehicleLoanBalance());
        existingEntity.setRealEstateLoanBalance(dto.getRealEstateLoanBalance());
        existingEntity.setTotalCollateralValue(dto.getTotalCollateralValue());
        existingEntity.setRealEstateValue(dto.getRealEstateValue());
        existingEntity.setDisbursementDate(dto.getDisbursementDate());
        
        // Logic tính toán lại hệ số (như đã định nghĩa trong Mapper hoặc có thể gọi Mapper để reuse)
        if (dto.getTotalCollateralValue() != null) {
            existingEntity.setCollateralValueAfterFactor(dto.getTotalCollateralValue().multiply(new java.math.BigDecimal("0.85")));
        }
        if (dto.getRealEstateValue() != null) {
            existingEntity.setRealEstateValueAfterFactor(dto.getRealEstateValue().multiply(new java.math.BigDecimal("0.8")));
        }

        existingEntity.setUpdatedAt(LocalDateTime.now());
        
        DisbursementEntity updatedEntity = disbursementRepository.save(existingEntity);
        return disbursementMapper.toDto(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteDisbursement(Long id) {
        if (!disbursementRepository.existsById(id)) {
            throw new RuntimeException("Disbursement not found with id: " + id);
        }
        disbursementRepository.deleteById(id);
    }

    @Override
    public Page<DisbursementDTO> searchDisbursements(
            String loanContractNumber,
            LocalDate disbursementDateFrom,
            LocalDate disbursementDateTo,
            Long creditContractId,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DisbursementEntity> entityPage = disbursementRepository.searchDisbursements(
                loanContractNumber, disbursementDateFrom, disbursementDateTo, creditContractId, pageable);
        
        return entityPage.map(disbursementMapper::toDto);
    }

    @Override
    public DisbursementDTO previewDisbursement(long customerId) {
        DisbursementDTO dto = new DisbursementDTO();
        // 1. Lấy CreditContract bằng findFirstByCustomerIdAndStatus cho khách hàng mặc định (ví dụ ID: 1) và trạng thái ACTIVE
        creditContractRepository.findFirstByStatus("ACTIVE").ifPresent(creditContract -> {
            dto.setCreditContractId(creditContract.getId());
            dto.setLoanContractNumber(creditContract.getContractNumber());
            dto.setUsedLimit(creditContract.getUsedLimit());
            dto.setRemainingLimit(creditContract.getRemainingLimit());
            dto.setIssuedGuaranteeBalance(creditContract.getIssuedGuaranteeBalance());
            dto.setVehicleLoanBalance(creditContract.getVehicleLoanBalance());
            dto.setRealEstateLoanBalance(creditContract.getRealEstateLoanBalance());
            dto.setCreditLimit(creditContract.getCreditLimit());
        });
        
        // 2. Tính totalCollateralValue (Tổng giá trị xe "Giữ trong kho")
        java.math.BigDecimal totalCollateral = vehicleRepository.sumPriceByStatus("Giữ trong kho");
        if (totalCollateral == null) totalCollateral = java.math.BigDecimal.ZERO;
        dto.setTotalCollateralValue(totalCollateral);
        
        // 3. Giá trị realEstateValue mặc định
        java.math.BigDecimal realEstate = new java.math.BigDecimal("8910000000");
        dto.setRealEstateValue(realEstate);
        
        // 4. Tính toán hệ số
        dto.setCollateralValueAfterFactor(totalCollateral.multiply(new java.math.BigDecimal("0.85")));
        dto.setRealEstateValueAfterFactor(realEstate.multiply(new java.math.BigDecimal("0.8")));
        
        return dto;
    }
}
