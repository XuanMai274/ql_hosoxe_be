package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.LoanDTO;

import java.util.List;

public interface LoanService {
    LoanDTO createLoan(LoanDTO dto);
    public List<LoanDTO> createBatchLoans(List<LoanDTO> dtos);
    LoanDTO updateLoan(Long id, LoanDTO dto);
}
