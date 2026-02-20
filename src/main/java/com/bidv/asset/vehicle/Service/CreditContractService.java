package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.CreditContractDTO;

import java.util.List;

public interface CreditContractService {
    public CreditContractDTO createCreditContract(CreditContractDTO creditContractDTO);
    public List<CreditContractDTO> findAll();
}
