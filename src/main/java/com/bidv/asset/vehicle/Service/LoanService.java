package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.LoanDTO;
import com.bidv.asset.vehicle.enums.LoanStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LoanService {
    public LoanDTO createLoan(
            LoanDTO dto,
            Integer childSequence,
            String loanContractNumber
    );
    LoanDTO getDetail(Long id);
    public List<LoanDTO> createBatchLoans(List<LoanDTO> dtos);
    LoanDTO updateLoan(Long id, LoanDTO dto);
    Page<LoanDTO> getAllLoans(int page, int size);
    Page<LoanDTO> getLoansByStatus(LoanStatus status, int page, int size);
    Page<LoanDTO> searchLoans(
            String loanContractNumber,
            String chassisNumber,
            LoanStatus status,
            String docId,
            Integer dueInDays,
            int page,
            int size
    );
}
