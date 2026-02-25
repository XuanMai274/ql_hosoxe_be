package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.LoanDTO;
import com.bidv.asset.vehicle.Mapper.LoanMapper;
import com.bidv.asset.vehicle.entity.*;
import com.bidv.asset.vehicle.Repository.*;
import com.bidv.asset.vehicle.Service.LoanService;
import com.bidv.asset.vehicle.enums.LoanStatus;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanServiceImplement implements LoanService {

    @Autowired LoanRepository loanRepository;
    @Autowired CustomerRepository customerRepository;
    @Autowired VehicleRepository vehicleRepository;
    @Autowired GuaranteeLetterRepository guaranteeRepository;
    @Autowired CreditContractRepository creditContractRepository;
    @Autowired LoanMapper loanMapper;

    @Override
    @Transactional
    public List<LoanDTO> createBatchLoans(List<LoanDTO> dtos) {

        List<LoanDTO> results = new ArrayList<>();

        for (LoanDTO dto : dtos) {
            results.add(createLoan(dto));
        }

        return results;
    }
    @Transactional
    @Override
    public LoanDTO createLoan(LoanDTO dto) {

        /* ================= LOAD DATA ================= */

        VehicleEntity vehicle = vehicleRepository.findByIdForUpdate(dto.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        GuaranteeLetterEntity guaranteeLetterEntity = vehicle.getGuaranteeLetter();

        if (guaranteeLetterEntity == null) {
            throw new RuntimeException("Vehicle not linked to guarantee letter");
        }
        // Số hợp đồng tín dụng cụ thể tự tăng
        // 1️⃣ Lock hợp đồng gốc
        CreditContractEntity credit =
                creditContractRepository.findByIdForUpdate(
                        guaranteeLetterEntity.getCreditContract().getId()
                ).orElseThrow(() -> new RuntimeException("Không tìm thấy HĐTD"));

        // 2️⃣ Lấy số lớn nhất hiện tại
        Integer maxSeq = loanRepository
                .findMaxChildSequence(credit.getId());

        int nextSeq = maxSeq + 1;

        // 3️⃣ Format 01, 02, 03
        String seqFormatted = String.format("%02d", nextSeq);

        // 4️⃣ Build số hợp đồng
        String masterNumber = credit.getContractNumber();
        // ví dụ: 01/2025/10987477/HDTD

        String rootCode = masterNumber.split("/")[0]; // 01
        String rest = masterNumber.substring(masterNumber.indexOf("/"));

        String loanContractNumber =
                rootCode + "." + seqFormatted
                        + rest.replace("HDTD", "HDTDCT");

//        CreditContractEntity credit = guaranteeLetterEntity.getCreditContract();

        if (credit == null) {
            throw new RuntimeException("Guarantee not linked to credit contract");
        }

        CustomerEntity customer = customerRepository
                .findById(dto.getCustomerDTO().getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        BigDecimal loanAmount = vehicle.getGuaranteeAmount();

        if (loanAmount == null || loanAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid loan amount");
        }


        /* ================= VALIDATE ================= */

        if (credit.getGuaranteeBalance().compareTo(loanAmount) < 0) {
            throw new RuntimeException("Not enough credit guarantee balance");
        }
        /* =========================================================
       =============== UPDATE GUARANTEE LETTER =================
       ========================================================= */

//        // giảm tổng bảo lãnh thực tế
//        guaranteeLetterEntity.setTotalGuaranteeAmount(
//                nvl(guaranteeLetterEntity.getTotalGuaranteeAmount()).subtract(loanAmount)
//        );
//
//        // tăng bảo lãnh còn lại
//        guaranteeLetterEntity.setRemainingAmount(
//                nvl(guaranteeLetterEntity.getRemainingAmount()).add(loanAmount)
//        );
//
//        // giảm số tiền đã sử dụng
//        guaranteeLetterEntity.setUsedAmount(
//                nvl(guaranteeLetterEntity.getUsedAmount()).subtract(loanAmount)
//        );
//
//        // giảm số lượng xe đã nhập
//        guaranteeLetterEntity.setImportedVehicleCount(
//                nvlInt(guaranteeLetterEntity.getImportedVehicleCount()) - 1
//        );
        // tăng lên số dư xe nhập kho
        guaranteeLetterEntity.setVehicleWarehouseCount(
                guaranteeLetterEntity.getVehicleWarehouseCount() == null
                        ? 1
                        : guaranteeLetterEntity.getVehicleWarehouseCount() + 1
        );
        // tăng tiền xe đã giải ngân
        guaranteeLetterEntity.setDisbursement(
                guaranteeLetterEntity.getDisbursement() == null
                        ? loanAmount
                        : guaranteeLetterEntity.getDisbursement().add(loanAmount)
        );

        guaranteeLetterEntity.setUpdatedAt(LocalDateTime.now());

          /* =========================================================
       ================= UPDATE CREDIT CONTRACT =================
       ========================================================= */

        // giảm dư bảo lãnh phát hành
        credit.setGuaranteeBalance(
                nvl(credit.getIssuedGuaranteeBalance()).subtract(loanAmount)
        );
        // giảm dư bảo lãnh thực tế
        credit.setGuaranteeBalance(nvl(credit.getGuaranteeBalance().subtract(loanAmount)));
        // tăng dư nợ vay xe
        credit.setVehicleLoanBalance(
                nvl(credit.getVehicleLoanBalance()).add(loanAmount)
        );
        // tính lại hạn mức đã sử dụng=dư nợ vay xe+dư bảo lãnh+dư nợ vay BDS
        credit.setUsedLimit(
                nvl(credit.getRealEstateLoanBalance().add(credit.getVehicleLoanBalance()).add(credit.getGuaranteeBalance()))
        );
        // Tính số dư bảo lãnh chênh lệch
        credit.setOutstandingGuaranteeAmount(credit.getIssuedGuaranteeBalance().subtract(credit.getGuaranteeBalance()));
        // tính lại hạn mức còn lại= tổng hạn mức - hạn mức đã sử dụng
        credit.setRemainingLimit(
                nvl(credit.getCreditLimit()).subtract(credit.getUsedLimit())
        );

        credit.setUpdatedAt(LocalDateTime.now());
        /* ================= UPDATE VEHICLE ================= */

        vehicle.setStatus("Giữ trong kho");
        vehicle.setImportDate(LocalDate.now());
        /* ================= CREATE LOAN ================= */

        LoanEntity entity = loanMapper.toEntity(dto);
        entity.setChildSequence(nextSeq);
        entity.setLoanContractNumber(loanContractNumber);
        entity.setLoanAmount(loanAmount);
        entity.setCustomer(
                customerRepository.findById(dto.getCustomerDTO().getId())
                        .orElseThrow(() -> new RuntimeException("Customer not found"))
        );
        entity.setVehicle(vehicle);
        entity.setCreditContract(credit);
        entity.setGuaranteeLetter(guaranteeLetterEntity);

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        LoanEntity saved = loanRepository.save(entity);

        return loanMapper.toDto(saved);
    }

    @Override
    public LoanDTO updateLoan(Long id, LoanDTO dto) {

        LoanEntity existing = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        CreditContractEntity credit = existing.getCreditContract();
        GuaranteeLetterEntity oldGuarantee = existing.getGuaranteeLetter();
        VehicleEntity oldVehicle = existing.getVehicle();

        BigDecimal oldAmount = existing.getLoanAmount();

        /* ================= ROLLBACK OLD ================= */

        credit.setGuaranteeBalance(
                credit.getGuaranteeBalance().add(oldAmount)
        );

        credit.setVehicleLoanBalance(
                credit.getVehicleLoanBalance().subtract(oldAmount)
        );

        credit.setUsedLimit(
                credit.getUsedLimit().subtract(oldAmount)
        );

        credit.setRemainingLimit(
                credit.getCreditLimit().subtract(credit.getUsedLimit())
        );

        oldGuarantee.setRemainingAmount(
                oldGuarantee.getRemainingAmount().add(oldAmount)
        );

        oldGuarantee.setUsedAmount(
                oldGuarantee.getUsedAmount().subtract(oldAmount)
        );

        oldVehicle.setStatus("Giữ trong kho");

        /* ================= APPLY NEW ================= */

        VehicleEntity newVehicle = vehicleRepository.findById(dto.getVehicleDTO().getId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        GuaranteeLetterEntity newGuarantee = newVehicle.getGuaranteeLetter();

        BigDecimal newAmount = newVehicle.getPrice();

        if (credit.getGuaranteeBalance().compareTo(newAmount) < 0) {
            throw new RuntimeException("Not enough credit guarantee balance");
        }

        if (newGuarantee.getRemainingAmount().compareTo(newAmount) < 0) {
            throw new RuntimeException("Not enough guarantee remaining amount");
        }

        credit.setGuaranteeBalance(
                credit.getGuaranteeBalance().subtract(newAmount)
        );

        credit.setVehicleLoanBalance(
                credit.getVehicleLoanBalance().add(newAmount)
        );

        credit.setUsedLimit(
                credit.getUsedLimit().add(newAmount)
        );

        credit.setRemainingLimit(
                credit.getCreditLimit().subtract(credit.getUsedLimit())
        );

        newGuarantee.setRemainingAmount(
                newGuarantee.getRemainingAmount().subtract(newAmount)
        );

        newGuarantee.setUsedAmount(
                newGuarantee.getUsedAmount().add(newAmount)
        );

        newVehicle.setStatus("Giữ trong kho");

        /* ================= UPDATE LOAN ================= */

        existing.setLoanAmount(newAmount);
        existing.setVehicle(newVehicle);
        existing.setGuaranteeLetter(newGuarantee);

        existing.setAccountNumber(dto.getAccountNumber());
        existing.setLoanContractNumber(dto.getLoanContractNumber());
        existing.setLoanTerm(dto.getLoanTerm());
        existing.setLoanDate(dto.getLoanDate());
        existing.setDueDate(dto.getDueDate());
        existing.setLoanStatus(dto.getLoanStatus());
        existing.setLoanType(dto.getLoanType());
        existing.setUpdatedAt(LocalDateTime.now());

        return loanMapper.toDto(existing);
    }
    @Override
    public Page<LoanDTO> getAllLoans(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return loanRepository.findAll(pageable)
                .map(loanMapper::toDtoList);
    }

    @Override
    public Page<LoanDTO> getLoansByStatus(LoanStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return loanRepository.findAllByLoanStatus(status, pageable)
                .map(loanMapper::toDtoList);
    }

    @Override
    public Page<LoanDTO> searchLoans(
            String loanContractNumber,
            String chassisNumber,
            LoanStatus status,
            String docId,
            Integer dueInDays,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (loanContractNumber != null && !loanContractNumber.isBlank()) {
            loanContractNumber = "%" + loanContractNumber + "%";
        }

        if (chassisNumber != null && !chassisNumber.isBlank()) {
            chassisNumber = "%" + chassisNumber + "%";
        }

        if (docId != null && !docId.isBlank()) {
            docId = "%" + docId + "%";
        }

        LocalDate dueDateFrom = null;
        LocalDate dueDateTo = null;

        if (dueInDays != null) {
            dueDateFrom = LocalDate.now();
            dueDateTo = LocalDate.now().plusDays(dueInDays);
        }

        Page<LoanEntity> loans = loanRepository.searchLoans(
                loanContractNumber,
                chassisNumber,
                status,
                docId,
                dueDateFrom,
                dueDateTo,
                pageable
        );

        return loans.map(loanMapper::toDto);
    }
    @Override
    public LoanDTO getDetail(Long id) {

        LoanEntity entity = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khoản vay"));

        return loanMapper.toDto(entity);
    }

    // hàm bổ trợ
    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Integer nvlInt(Integer value) {
        return value == null ? 0 : value;
    }
}