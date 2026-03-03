package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.DisbursementDTO;
import com.bidv.asset.vehicle.DTO.LoanDTO;
import com.bidv.asset.vehicle.DTO.UpdateInterestRequest;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

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
    DisbursementDTO previewDisbursement();
    // kiểm tra xem hợp đồng này có tất toán hay không
    List<DisbursementDTO> checkDisbursementWillBeClosed(List<Long> loanDTOs);
    public void updateInterestBatch(List<UpdateInterestRequest> requests);
}
