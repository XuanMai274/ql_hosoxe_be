package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.DisbursementDTO;
import com.bidv.asset.vehicle.DTO.DisbursementExportRequest;
import com.bidv.asset.vehicle.DTO.UpdateInterestRequest;
import com.bidv.asset.vehicle.Service.DisbursementExportService;
import com.bidv.asset.vehicle.Service.DisbursementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("officer/disbursements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DisbursementAPI {

    private final DisbursementService disbursementService;
    private final DisbursementExportService disbursementExportService;

    @PostMapping
    public ResponseEntity<DisbursementDTO> createDisbursement(@RequestBody DisbursementDTO dto) {
        return ResponseEntity.ok(disbursementService.createDisbursement(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisbursementDTO> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(disbursementService.getDetail(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisbursementDTO> updateDisbursement(@PathVariable Long id, @RequestBody DisbursementDTO dto) {
        return ResponseEntity.ok(disbursementService.updateDisbursement(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisbursement(@PathVariable Long id) {
        disbursementService.deleteDisbursement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DisbursementDTO>> searchDisbursements(
            @RequestParam(required = false) String loanContractNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate disbursementDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate disbursementDateTo,
            @RequestParam(required = false) Long creditContractId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(disbursementService.searchDisbursements(
                loanContractNumber, disbursementDateFrom, disbursementDateTo, creditContractId, page, size));
    }

    @GetMapping("/preview")
    public ResponseEntity<DisbursementDTO> previewDisbursement() {
        return ResponseEntity.ok(disbursementService.previewDisbursement());
    }
    // kiểm tra tất toán
    @PostMapping("/check-paidOff")
    public ResponseEntity<List<DisbursementDTO>> checkDisbursementWillBeClosed(
            @RequestBody List<Long> loanIds) {

        List<DisbursementDTO> result =
                disbursementService.checkDisbursementWillBeClosed(loanIds);

        return ResponseEntity.ok(result);
    }
    // cập nhật lãi
    @PostMapping("/update-interest")
    public ResponseEntity<Void> updateInterest(
            @RequestBody List<UpdateInterestRequest> requests) {

        disbursementService.updateInterestBatch(requests);
        return ResponseEntity.ok().build();
    }

}
