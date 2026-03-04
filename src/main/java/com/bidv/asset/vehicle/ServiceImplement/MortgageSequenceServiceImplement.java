package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.Repository.MortgageContractSequenceRepository;
import com.bidv.asset.vehicle.Service.MortgageSequenceService;
import com.bidv.asset.vehicle.entity.MortgageContractEntity;
import com.bidv.asset.vehicle.entity.MortgageContractSequenceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MortgageSequenceServiceImplement implements MortgageSequenceService {
    @Autowired
    MortgageContractSequenceRepository repository;

    @Override
    @Transactional
    public void createSequence(MortgageContractEntity mortgageContract) {

        if (repository.existsById(mortgageContract.getId())) {
            return; // đã tồn tại thì không tạo lại
        }

        MortgageContractSequenceEntity entity = new MortgageContractSequenceEntity();
        entity.setMortgageContract(mortgageContract);
        entity.setGuaranteeRunningNo(0);
        entity.setWarehouseRunningNo(0);

        repository.save(entity);
    }
}
