package com.bidv.asset.vehicle.API;


import com.bidv.asset.vehicle.DTO.BatchLoanResponse;
import com.bidv.asset.vehicle.DTO.LoanDTO;
import com.bidv.asset.vehicle.Service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/officer/loans")
public class LoanAPI {
    @Autowired
    LoanService loanService;
    /* ================= CREATE SINGLE ================= */
    @PostMapping
    public ResponseEntity<LoanDTO> createLoan(
            @RequestBody LoanDTO dto
    ) {
        LoanDTO result = loanService.createLoan(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    /* ================= CREATE BATCH ================= */
    @PostMapping("/batch")
    public ResponseEntity<BatchLoanResponse> createBatchLoans(
            @RequestBody List<LoanDTO> dtos
    ) {
        List<LoanDTO> loans = loanService.createBatchLoans(dtos);

        BigDecimal totalAmount = loans.stream()
                .map(LoanDTO::getLoanAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BatchLoanResponse response = new BatchLoanResponse();
        response.setTotal(loans.size());
        response.setTotalAmount(totalAmount);
        response.setLoans(loans);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    /* ================= UPDATE ================= */
    @PutMapping("/{id}")
    public ResponseEntity<LoanDTO> updateLoan(
            @PathVariable Long id,
            @RequestBody LoanDTO dto
    ) {
        LoanDTO result = loanService.updateLoan(id, dto);
        return ResponseEntity.ok(result);
    }
}
