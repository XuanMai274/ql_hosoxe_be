package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.GuaranteeLetterDTO;
import com.bidv.asset.vehicle.Mapper.GuaranteeLetterMapper;
import com.bidv.asset.vehicle.Repository.CreditContractRepository;
import com.bidv.asset.vehicle.Repository.GuaranteeLetterRepository;
import com.bidv.asset.vehicle.Service.GuaranteeLetterService;
import com.bidv.asset.vehicle.entity.CreditContractEntity;
import com.bidv.asset.vehicle.entity.GuaranteeLetterEntity;
import lombok.Setter;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class GuaranteeLetterServiceImplement implements GuaranteeLetterService {
    @Autowired
    GuaranteeLetterMapper guaranteeLetterMapper;
    @Autowired
    GuaranteeLetterRepository guaranteeLetterRepository;
    @Autowired
    CreditContractRepository creditContractRepository;
    @Override
    public GuaranteeLetterDTO createGuaranteeLetter(GuaranteeLetterDTO dto) {
        // 1. Validate request
        if (dto == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Request body không được null"
            );
        }

        if (dto.getCreditContractId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "creditContractId không được null"
            );
        }

        // 2. Kiểm tra CreditContract tồn tại
        CreditContractEntity creditContract = creditContractRepository
                .findById(dto.getCreditContractId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy CreditContract với id = " + dto.getCreditContractId()
                        )
                );

        // 3. Map DTO -> Entity
        GuaranteeLetterEntity entity = guaranteeLetterMapper.toEntity(dto);
        entity.setGuaranteeContractDate(LocalDate.now());
        entity.setCreditContract(creditContract);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        // 4. Save DB
        GuaranteeLetterEntity saved = guaranteeLetterRepository.save(entity);

        return guaranteeLetterMapper.toDto(saved);
    }
}
