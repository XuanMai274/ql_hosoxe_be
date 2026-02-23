package com.bidv.asset.vehicle.API;


import com.bidv.asset.vehicle.DTO.BatchLoanResponse;
import com.bidv.asset.vehicle.DTO.LoanDTO;
import com.bidv.asset.vehicle.Service.LoanService;
import com.bidv.asset.vehicle.enums.LoanStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
//    @GetMapping
//    public ResponseEntity<Page<LoanDTO>> getAllLoans(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        Page<LoanDTO> result;
//
//        result = loanService.getAllLoans(page, size);
//
//        return ResponseEntity.ok(result);
//    }
    @GetMapping
    public ResponseEntity<Page<LoanDTO>> searchLoans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,

            @RequestParam(required = false) String loanContractNumber,
            @RequestParam(required = false) String chassisNumber,
            @RequestParam(required = false) LoanStatus status,
            @RequestParam(required = false) String docId,
            @RequestParam(required = false) Integer dueInDays   // lọc gần đến hạn
    ) {

        Page<LoanDTO> result = loanService.searchLoans(
                loanContractNumber,
                chassisNumber,
                status,
                docId,
                dueInDays,
                page,
                size
        );

        return ResponseEntity.ok(result);
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
    // ================= VIEW DETAIL =================
    @GetMapping("/{id}")
    public ResponseEntity<LoanDTO> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getDetail(id));
    }
}
