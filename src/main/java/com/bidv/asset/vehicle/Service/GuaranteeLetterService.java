package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.GuaranteeLetterDTO;
import com.bidv.asset.vehicle.entity.GuaranteeLetterEntity;
import com.bidv.asset.vehicle.entity.VehicleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface GuaranteeLetterService {
    GuaranteeLetterDTO createGuaranteeLetter(GuaranteeLetterDTO dto);
    public Page<GuaranteeLetterDTO> getGuaranteeLetters(
            String manufacturerCode,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    );
    public Page<GuaranteeLetterDTO> search(
            String keyword,
            String manufacturerCode,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    );
    public GuaranteeLetterDTO findById(long id);
    public GuaranteeLetterDTO updateGuaranteeLetter(Long id, GuaranteeLetterDTO dto);
    public List<GuaranteeLetterDTO> suggest(
            String keyword,
            String manufacturerCode
    );
    void updateAfterVehicleImported(GuaranteeLetterEntity gl, VehicleEntity vehicle);

}
