package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.MortgageContractDTO;

import java.util.List;

public interface MortgageContractService {
    MortgageContractDTO create(MortgageContractDTO dto);

    MortgageContractDTO update(Long id, MortgageContractDTO dto);

    void delete(Long id);

    MortgageContractDTO getById(Long id);

    List<MortgageContractDTO> getAll();
}
