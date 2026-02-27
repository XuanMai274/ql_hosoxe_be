package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.DisbursementDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface DisbursementService {
    DisbursementDTO createDisbursement(DisbursementDTO dto);
    DisbursementDTO getDetail(Long id);
    DisbursementDTO updateDisbursement(Long id, DisbursementDTO dto);
    void deleteDisbursement(Long id);
    Page<DisbursementDTO> searchDisbursements(
            String loanContractNumber,
            LocalDate disbursementDateFrom,
            LocalDate disbursementDateTo,
            Long creditContractId,
            int page,
            int size
    );
    DisbursementDTO previewDisbursement(long customerId);
}
