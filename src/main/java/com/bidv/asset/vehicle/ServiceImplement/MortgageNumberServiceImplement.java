package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.Repository.MortgageContractSequenceRepository;
import com.bidv.asset.vehicle.Service.MortgageNumberService;
import com.bidv.asset.vehicle.entity.MortgageContractEntity;
import com.bidv.asset.vehicle.entity.MortgageContractSequenceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MortgageNumberServiceImplement implements MortgageNumberService {
    @Autowired
    MortgageContractSequenceRepository sequenceRepo;
    @Override
    @Transactional
    public String generateGuaranteeNumber(MortgageContractEntity mortgage) {

        // ===== GET OR CREATE SEQUENCE =====
        MortgageContractSequenceEntity seq =
                sequenceRepo.findByIdForUpdate(mortgage.getId())
                        .orElseGet(() -> createSequence(mortgage.getId()));

        int next = seq.getGuaranteeRunningNo() + 1;
        seq.setGuaranteeRunningNo(next);

        sequenceRepo.save(seq);

        String base = mortgage.getContractNumber();
        // 01/2025/10987477/HDBD

        String prefix = base.split("/")[0]; // 01
        String body = base.substring(base.indexOf("/") + 1);

        return prefix + "." + String.format("%02d", next)
                + "/" + body.replace("HDBD", "HDBLCT");
    }

    @Override
    @Transactional
    public String generateWarehouseNumber(MortgageContractEntity mortgage) {

        MortgageContractSequenceEntity seq =
                sequenceRepo.findByIdForUpdate(mortgage.getId())
                        .orElseGet(() -> createSequence(mortgage.getId()));

        int next = seq.getWarehouseRunningNo() + 1;
        seq.setWarehouseRunningNo(next);

        sequenceRepo.save(seq);

        String base = mortgage.getContractNumber();

        String prefix = base.split("/")[0];
        String body = base.substring(base.indexOf("/") + 1);

        return prefix + "." + String.format("%02d", next)
                + "/" + body;
    }
    // =====================================================
    // CREATE SEQUENCE IF NOT EXISTS
    // =====================================================
    private MortgageContractSequenceEntity createSequence(Long mortgageId) {

        MortgageContractSequenceEntity seq =
                new MortgageContractSequenceEntity();

        seq.setMortgageContractId(mortgageId);
        seq.setGuaranteeRunningNo(0);
        seq.setWarehouseRunningNo(0);

        return sequenceRepo.save(seq);
    }
}
