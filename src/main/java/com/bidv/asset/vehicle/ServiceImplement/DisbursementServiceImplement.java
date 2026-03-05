package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.Utill.MoneyUtil;
import com.bidv.asset.vehicle.DTO.DisbursementDTO;
import com.bidv.asset.vehicle.DTO.LoanDTO;
import com.bidv.asset.vehicle.DTO.UpdateInterestRequest;
import com.bidv.asset.vehicle.Mapper.DisbursementMapper;
import com.bidv.asset.vehicle.Repository.*;
import com.bidv.asset.vehicle.Service.DisbursementService;
import com.bidv.asset.vehicle.entity.CreditContractEntity;
import com.bidv.asset.vehicle.entity.DisbursementEntity;
import com.bidv.asset.vehicle.entity.LoanEntity;
import com.bidv.asset.vehicle.entity.MortgageContractEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

        // 🔹 Tính toán thông tin mới
//        BigDecimal rate = new BigDecimal("0.0114"); // (7% - 5.86%) = 1.14% = 0.0114
//        entity.setInterestRate(rate);
//
//        if (entity.getDisbursementAmount() != null && entity.getLoanTerm() != null) {
//            BigDecimal interest = entity.getDisbursementAmount()
//                    .multiply(rate)
//                    .divide(new BigDecimal("365"), 10, java.math.RoundingMode.HALF_UP)
//                    .multiply(new BigDecimal(entity.getLoanTerm()));
//            entity.setInterestAmount(interest.setScale(2, java.math.RoundingMode.HALF_UP));
//        }

        entity.setStatus("ACTIVE");
        entity.setTotalAmountPaid(BigDecimal.ZERO);
        entity.setWithdrawnVehiclesCount(0);

        // Link loans if provided and set totalVehiclesCount
//        if (dto.getLoanIds() != null && !dto.getLoanIds().isEmpty()) {
//            java.util.List<com.bidv.asset.vehicle.entity.LoanEntity> loans = loanRepository.findAllById(dto.getLoanIds());
//            for (com.bidv.asset.vehicle.entity.LoanEntity loan : loans) {
//                loan.setDisbursement(entity);
//            }
//            entity.setLoans(loans);
//            entity.setTotalVehiclesCount(loans.size());
//        } else {
//            entity.setTotalVehiclesCount(0);
//        }

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
            existingEntity.setCollateralValueAfterFactor(MoneyUtil.format(dto.getTotalCollateralValue().multiply(new java.math.BigDecimal("0.85"))));
        }
        if (dto.getRealEstateValue() != null) {
            existingEntity.setRealEstateValueAfterFactor(MoneyUtil.format(dto.getRealEstateValue().multiply(new java.math.BigDecimal("0.8"))));
        }

        existingEntity.setUpdatedAt(LocalDateTime.now());

//        // Cập nhật lại lãi suất nếu amount hoặc term thay đổi
//        if (existingEntity.getDisbursementAmount() != null && existingEntity.getLoanTerm() != null) {
//            BigDecimal interest = existingEntity.getDisbursementAmount()
//                    .multiply(rate)
//                    .divide(new BigDecimal("365"), 10, java.math.RoundingMode.HALF_UP)
//                    .multiply(new BigDecimal(existingEntity.getLoanTerm()));
//            existingEntity.setInterestAmount(interest.setScale(2, java.math.RoundingMode.HALF_UP));
//        }

        if (dto.getStatus() != null) {
            existingEntity.setStatus(dto.getStatus());
        }
        if (dto.getTotalAmountPaid() != null) {
            existingEntity.setTotalAmountPaid(dto.getTotalAmountPaid());
        }
        
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
    public DisbursementDTO previewDisbursement() {
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
        dto.setCollateralValueAfterFactor(MoneyUtil.format(totalCollateral.multiply(new java.math.BigDecimal("0.85"))));
        dto.setRealEstateValueAfterFactor(MoneyUtil.format(realEstate.multiply(new java.math.BigDecimal("0.8"))));

        
//        // Tính lãi suất dựa trên DisbursementAmount (nếu có, hoặc mặc định từ hạn mức)
//        // Giả sử lấy DisbursementAmount từ đâu đó hoặc UI gửi lên, ở đây ta có thể tính dựa trên usedLimit hoặc creditLimit để demo
//        if (dto.getDisbursementAmount() != null && dto.getLoanTerm() != null) {
//            BigDecimal interest = dto.getDisbursementAmount()
//                    .multiply(rate)
//                    .divide(new BigDecimal("365"), 10, java.math.RoundingMode.HALF_UP)
//                    .multiply(new BigDecimal(dto.getLoanTerm()));
//            dto.setInterestAmount(interest.setScale(2, java.math.RoundingMode.HALF_UP));
//        }
//
//        // Đếm số xe đang "Giữ trong kho"
//        List<com.bidv.asset.vehicle.entity.VehicleEntity> storedVehicles = vehicleRepository.findByStatus("Giữ trong kho");
//        dto.setTotalVehiclesCount(storedVehicles != null ? storedVehicles.size() : 0);

        return dto;
    }


    //KIỂM TRA XEM HỢP ĐỒNG NÀY CÓ TẤT TOÁN HAY KHÔNG
    @Override
    @Transactional(readOnly = true)
    public List<DisbursementDTO> checkDisbursementWillBeClosed(List<Long> loanIds) {

        if (loanIds == null || loanIds.isEmpty()) {
            return List.of();
        }

        // 1️⃣ Load loan + disbursement
        List<LoanEntity> targetLoans =
                loanRepository.findAllWithDisbursementByIdIn(loanIds);

        if (targetLoans.isEmpty()) {
            return List.of();
        }

        // 2️⃣ Group loan đang truyền theo disbursement
        Map<Long, List<LoanEntity>> groupedByDisbursement =
                targetLoans.stream()
                        .collect(Collectors.groupingBy(
                                l -> l.getDisbursement().getId()
                        ));

        List<Long> disbursementIds =
                new ArrayList<>(groupedByDisbursement.keySet());

        // 3️⃣ Load toàn bộ loan thuộc các disbursement đó
        List<LoanEntity> allLoansOfDisbursements =
                loanRepository.findByDisbursementIds(disbursementIds);

        Map<Long, List<LoanEntity>> allLoansGrouped =
                allLoansOfDisbursements.stream()
                        .collect(Collectors.groupingBy(
                                l -> l.getDisbursement().getId()
                        ));

        List<DisbursementDTO> result = new ArrayList<>();

        for (Long disbursementId : groupedByDisbursement.keySet()) {

            List<LoanEntity> loansBeingPaid =
                    groupedByDisbursement.get(disbursementId);

            List<LoanEntity> allLoans =
                    allLoansGrouped.get(disbursementId);

            if (allLoans == null || allLoans.isEmpty()) {
                continue;
            }

            long activeLoansCount = allLoans.stream()
                    .filter(l -> "ACTIVE".equalsIgnoreCase(l.getLoanStatus()))
                    .count();

            long activeLoansBeingPaid = loansBeingPaid.stream()
                    .filter(l -> "ACTIVE".equalsIgnoreCase(l.getLoanStatus()))
                    .count();

            if (activeLoansCount > 0 &&
                    activeLoansCount == activeLoansBeingPaid) {

                DisbursementEntity disbursement =
                        loansBeingPaid.get(0).getDisbursement();

                result.add(disbursementMapper.toDto(disbursement));
            }
        }

        return result;
    }

    @Override
    @Transactional
    public void updateInterestBatch(List<UpdateInterestRequest> requests) {

        List<Long> ids = requests.stream()
                .map(UpdateInterestRequest::getDisbursementId)
                .toList();

        List<DisbursementEntity> entities =
                disbursementRepository.findAllById(ids);

        Map<Long, BigDecimal> interestMap = requests.stream()
                .collect(Collectors.toMap(
                        UpdateInterestRequest::getDisbursementId,
                        UpdateInterestRequest::getInterestAmount
                ));

        for (DisbursementEntity entity : entities) {
            entity.setInterestAmount(interestMap.get(entity.getId()));
        }

        disbursementRepository.saveAll(entities);
    }
}
