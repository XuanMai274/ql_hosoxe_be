package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.GuaranteeApplicationDTO;

import com.bidv.asset.vehicle.DTO.GuaranteeApplicationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GuaranteeApplicationService {
    GuaranteeApplicationDTO create(GuaranteeApplicationDTO dto);
    Page<GuaranteeApplicationDTO> findAll(Pageable pageable);
    GuaranteeApplicationDTO getById(Long id);
    GuaranteeApplicationDTO approve(Long id);
    GuaranteeApplicationDTO reject(Long id);
}
