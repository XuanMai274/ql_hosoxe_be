package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.entity.MortgageContractEntity;

public interface MortgageNumberService {
    public String generateGuaranteeNumber(MortgageContractEntity mortgage);
    public String generateWarehouseNumber(MortgageContractEntity mortgage);
}
