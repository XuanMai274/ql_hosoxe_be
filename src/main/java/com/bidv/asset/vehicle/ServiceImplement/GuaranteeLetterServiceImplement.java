package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.GuaranteeLetterDTO;
import com.bidv.asset.vehicle.Mapper.GuaranteeLetterMapper;
import com.bidv.asset.vehicle.Repository.BranchAuthorizedRepresentativeRepository;
import com.bidv.asset.vehicle.Repository.CreditContractRepository;
import com.bidv.asset.vehicle.Repository.GuaranteeLetterRepository;
import com.bidv.asset.vehicle.Repository.ManufacturerRepository;
import com.bidv.asset.vehicle.Service.GuaranteeLetterService;
import com.bidv.asset.vehicle.entity.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.Setter;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GuaranteeLetterServiceImplement implements GuaranteeLetterService {
    @Autowired
    GuaranteeLetterMapper guaranteeLetterMapper;
    @Autowired
    GuaranteeLetterRepository guaranteeLetterRepository;
    @Autowired
    CreditContractRepository creditContractRepository;
    @Autowired
    BranchAuthorizedRepresentativeRepository branchAuthorizedRepresentativeRepository;
    @Autowired
    ManufacturerRepository manufacturerRepository;


    @Transactional
    @Override
    public GuaranteeLetterDTO createGuaranteeLetter(GuaranteeLetterDTO dto) {
        /* =======================
         * 1. VALIDATE REQUEST
         * ======================= */
        if (dto == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Request body không được null"
            );
        }

        if (dto.getCreditContractDTO() == null
                || dto.getCreditContractDTO().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "creditContractId không được null"
            );
        }

        if (dto.getExpectedGuaranteeAmount() == null
                || dto.getExpectedGuaranteeAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Số tiền bảo lãnh không hợp lệ"
            );
        }

        /* =======================
         * 2. LOAD MASTER DATA
         * ======================= */
        CreditContractEntity creditContract = creditContractRepository
                .findById(dto.getCreditContractDTO().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy CreditContract với id = "
                                + dto.getCreditContractDTO().getId()
                ));

        BranchAuthorizedRepresentativeEntity authorizedRep =
                branchAuthorizedRepresentativeRepository
                        .findById(dto.getBranchAuthorizedRepresentativeDTO().getId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy BranchAuthorizedRepresentative với id = "
                                        + dto.getBranchAuthorizedRepresentativeDTO().getId()
                        ));

        ManufacturerEntity manufacturer =
                manufacturerRepository
                        .findById(dto.getManufacturerDTO().getId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy Manufacturer với id = "
                                        + dto.getManufacturerDTO().getId()
                        ));

        /* =======================
         * 3. CHECK & CALCULATE LIMIT
         * ======================= */
        BigDecimal creditLimit = creditContract.getCreditLimit();

        BigDecimal currentUsedLimit = creditContract.getUsedLimit() == null
                ? BigDecimal.ZERO
                : creditContract.getUsedLimit();

        BigDecimal newUsedLimit =
                currentUsedLimit.add(dto.getExpectedGuaranteeAmount());

        // ❌ Không cho vượt hạn mức
//        if (newUsedLimit.compareTo(creditLimit) > 0) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "Vượt quá hạn mức tín dụng. Hạn mức còn lại: "
//                            + creditLimit.subtract(currentUsedLimit)
//            );
//        }

        BigDecimal newRemainingLimit =
                creditLimit.subtract(newUsedLimit);

        /* =======================
         * 4. MAP & SAVE GUARANTEE LETTER
         * ======================= */
        GuaranteeLetterEntity entity = guaranteeLetterMapper.toEntity(dto);
        entity.setGuaranteeContractDate(LocalDate.now());
        entity.setCreditContract(creditContract);
        entity.setManufacturer(manufacturer);
        entity.setAuthorizedRepresentative(authorizedRep);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        GuaranteeLetterEntity savedGuarantee =
                guaranteeLetterRepository.save(entity);

        /* =======================
         * 5. UPDATE CREDIT CONTRACT
         * ======================= */
        creditContract.setUsedLimit(newUsedLimit);
        creditContract.setRemainingLimit(newRemainingLimit);
        creditContract.setUpdateAt(LocalDateTime.now());

        creditContractRepository.save(creditContract);

        /* =======================
         * 6. RETURN DTO
         * ======================= */
        return guaranteeLetterMapper.toDto(savedGuarantee);
    }

    @Override
    public Page<GuaranteeLetterDTO> getGuaranteeLetters(
            String manufacturerCode,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    ) {

        Page<GuaranteeLetterEntity> page =
                guaranteeLetterRepository.searchGuaranteeLetters(
                        manufacturerCode,
                        fromDate,
                        toDate,
                        pageable
                );

        return page.map(guaranteeLetterMapper::toDto);
    }
    @Override
    public Page<GuaranteeLetterDTO> search(
            String keyword,
            String manufacturerCode,
            LocalDate fromDate,
            LocalDate toDate,
            Boolean hasLetterNumber,
            Pageable pageable
    ) {
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }
        return guaranteeLetterRepository.search(
                keyword,
                manufacturerCode,
                fromDate,
                toDate,
                hasLetterNumber,
                pageable
        ).map(guaranteeLetterMapper::toDto);
    }

    @Override
    public GuaranteeLetterDTO findById(long id) {
        GuaranteeLetterEntity entity = guaranteeLetterRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy thư bảo lãnh với id = " + id)
                );

        return guaranteeLetterMapper.toDto(entity);
    }

    @Override
    public GuaranteeLetterDTO updateGuaranteeLetter(Long id, GuaranteeLetterDTO dto) {

        GuaranteeLetterEntity entity = guaranteeLetterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thư bảo lãnh"));

        // ===== GUARANTEE CONTRACT =====
        entity.setGuaranteeContractNumber(dto.getGuaranteeContractNumber());
        entity.setGuaranteeContractDate(dto.getGuaranteeContractDate());
        entity.setGuaranteeNoticeNumber(dto.getGuaranteeNoticeNumber());
        entity.setGuaranteeNoticeDate(dto.getGuaranteeNoticeDate());
        entity.setReferenceCode(dto.getReferenceCode());

        // ===== AMOUNT =====
        entity.setExpectedGuaranteeAmount(dto.getExpectedGuaranteeAmount());
        entity.setTotalGuaranteeAmount(dto.getTotalGuaranteeAmount());
        entity.setUsedAmount(dto.getUsedAmount());
        entity.setRemainingAmount(dto.getRemainingAmount());

        // ===== VEHICLE COUNT =====
        entity.setExpectedVehicleCount(dto.getExpectedVehicleCount());
        entity.setImportedVehicleCount(dto.getImportedVehicleCount());
        entity.setExportedVehicleCount(dto.getExportedVehicleCount());

        // ===== SALE CONTRACT =====
        entity.setSaleContract(dto.getSaleContract());
        entity.setSaleContractAmount(dto.getSaleContractAmount());

        // ===== STATUS =====
        entity.setStatus(dto.getStatus());

        // ===== MANUFACTURER =====
        if (dto.getManufacturerDTO() != null) {
            ManufacturerEntity manufacturer = manufacturerRepository
                    .findById(dto.getManufacturerDTO().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hãng xe"));
            entity.setManufacturer(manufacturer);
        }

        // ===== AUTHORIZED REPRESENTATIVE =====
        if (dto.getBranchAuthorizedRepresentativeDTO() != null) {
            BranchAuthorizedRepresentativeEntity rep = branchAuthorizedRepresentativeRepository
                    .findById(dto.getBranchAuthorizedRepresentativeDTO().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người đại diện"));
            entity.setAuthorizedRepresentative(rep);
        }

        entity.setUpdatedAt(LocalDateTime.now());

        GuaranteeLetterEntity saved = guaranteeLetterRepository.save(entity);

        return guaranteeLetterMapper.toDto(saved);

    }

    public List<GuaranteeLetterDTO> suggest(
            String keyword,
            String manufacturerCode
    ) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of(); // chưa đủ ký tự → không search
        }

        Pageable pageable = PageRequest.of(0, 10);

        return guaranteeLetterRepository
                .suggestGuaranteeLetters(keyword.trim(), manufacturerCode, pageable)
                .stream()
                .map(guaranteeLetterMapper::toDto)
                .toList();
    }
    @Override
    public void updateAfterVehicleImported(GuaranteeLetterEntity gl, VehicleEntity vehicle) {

        /* ========= 1. imported_vehicle_count +1 ========= */
        Integer importedCount = gl.getImportedVehicleCount();
        gl.setImportedVehicleCount(
                importedCount == null ? 1 : importedCount + 1
        );

        /* ========= 2. total_guarantee_amount += price ========= */
        BigDecimal vehiclePrice = vehicle.getPrice() != null
                ? vehicle.getPrice()
                : BigDecimal.ZERO;

        BigDecimal totalGuarantee = gl.getTotalGuaranteeAmount();
        gl.setTotalGuaranteeAmount(
                totalGuarantee == null
                        ? vehiclePrice
                        : totalGuarantee.add(vehiclePrice)
        );

        /* ========= 3. used_amount ========= */
        BigDecimal usedAmount = gl.getUsedAmount();
        gl.setUsedAmount(
                usedAmount == null
                        ? vehiclePrice
                        : usedAmount.add(vehiclePrice)
        );

        /* ========= 4. remaining_amount ========= */
        if (gl.getExpectedGuaranteeAmount() != null) {
            gl.setRemainingAmount(
                    gl.getExpectedGuaranteeAmount().subtract(gl.getUsedAmount())
            );
        }

        /* ========= 5. updated_at ========= */
        gl.setUpdatedAt(LocalDateTime.now());

        guaranteeLetterRepository.save(gl);
    }

    @Override
    public List<GuaranteeLetterDTO> findAll() {
        List<GuaranteeLetterEntity> guaranteeLetterEntities= (List<GuaranteeLetterEntity>) guaranteeLetterRepository.findAll();
        List<GuaranteeLetterDTO> guaranteeLetterDTOS=new ArrayList<>();
        for(GuaranteeLetterEntity guaranteeLetterEntity:  guaranteeLetterEntities){
            guaranteeLetterDTOS.add(guaranteeLetterMapper.toDto(guaranteeLetterEntity));
        }
        return guaranteeLetterDTOS;
    }


}
