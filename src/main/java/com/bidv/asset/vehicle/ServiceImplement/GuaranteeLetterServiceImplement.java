package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.GuaranteeLetterDTO;
import com.bidv.asset.vehicle.Mapper.GuaranteeLetterMapper;
import com.bidv.asset.vehicle.Repository.*;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    @Autowired
    MortgageContractRepository mortgageContractRepository;
    @Autowired CustomerRepository customerRepository;

    @Transactional
    @Override
    public GuaranteeLetterDTO createGuaranteeLetter(GuaranteeLetterDTO dto) {

        // =====================================================
        // 1. VALIDATE REQUEST
        // =====================================================
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request null");
        }

        if (dto.getCustomerDTO() == null || dto.getCustomerDTO().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customer không được null");
        }

        if (dto.getManufacturerDTO() == null || dto.getManufacturerDTO().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "manufacturer không được null");
        }

        if (dto.getBranchAuthorizedRepresentativeDTO() == null ||
                dto.getBranchAuthorizedRepresentativeDTO().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "authorizedRepresentative không được null");
        }

        if (dto.getExpectedGuaranteeAmount() == null
                || dto.getExpectedGuaranteeAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số tiền bảo lãnh không hợp lệ");
        }

        Long customerId = dto.getCustomerDTO().getId();
        Long manufacturerId = dto.getManufacturerDTO().getId();
        Long repId = dto.getBranchAuthorizedRepresentativeDTO().getId();

        // =====================================================
        // 2. LOAD CUSTOMER
        // =====================================================
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy customer"
                ));

        // =====================================================
        // 3. AUTO LOAD HDTD ACTIVE
        // =====================================================
        CreditContractEntity creditContract = creditContractRepository
                .findFirstByCustomerIdAndStatus(customerId, "ACTIVE")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Khách hàng chưa có HDTD ACTIVE"
                ));

        // =====================================================
        // 4. LOAD MANUFACTURER
        // =====================================================
        ManufacturerEntity manufacturer = manufacturerRepository
                .findById(manufacturerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy hãng xe"
                ));

        // =====================================================
        // 5. AUTO LOAD HĐBD ACTIVE
        // =====================================================
        MortgageContractEntity mortgageContract =
                mortgageContractRepository
                        .findFirstByCustomerIdAndManufacturerIdAndStatus(
                                customerId,
                                manufacturerId,
                                "ACTIVE"
                        )
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Không có HĐBD ACTIVE phù hợp với hãng xe"
                        ));

        // =====================================================
        // 6. LOAD AUTHORIZED REP
        // =====================================================
        BranchAuthorizedRepresentativeEntity authorizedRep =
                branchAuthorizedRepresentativeRepository
                        .findById(repId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy người đại diện"
                        ));

        // =====================================================
        // 7. CHECK CREDIT LIMIT
        // =====================================================
//        BigDecimal creditLimit = creditContract.getCreditLimit();
//
//        BigDecimal currentUsedLimit =
//                creditContract.getUsedLimit() == null
//                        ? BigDecimal.ZERO
//                        : creditContract.getUsedLimit();
//
//        BigDecimal newUsedLimit =
//                currentUsedLimit.add(dto.getExpectedGuaranteeAmount());
//
//        if (newUsedLimit.compareTo(creditLimit) > 0) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "Vượt hạn mức tín dụng"
//            );
//        }
//
//        BigDecimal newRemainingLimit =
//                creditLimit.subtract(newUsedLimit);

        // =====================================================
        // 8. MAP ENTITY
        // =====================================================
        GuaranteeLetterEntity entity = guaranteeLetterMapper.toEntity(dto);

        entity.setCustomer(customer);
        entity.setCreditContract(creditContract);
        entity.setMortgageContract(mortgageContract);
        entity.setManufacturer(manufacturer);
        entity.setAuthorizedRepresentative(authorizedRep);

        entity.setGuaranteeContractDate(LocalDate.now());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setStatus("ACTIVE");

        GuaranteeLetterEntity saved =
                guaranteeLetterRepository.save(entity);

        // =====================================================
        // 9. UPDATE CREDIT CONTRACT
        // =====================================================
//        creditContract.setUsedLimit(newUsedLimit);
//        creditContract.setRemainingLimit(newRemainingLimit);
//        creditContract.setUpdatedAt(LocalDateTime.now());

//        creditContractRepository.save(creditContract);

        // =====================================================
        // 10. RETURN DTO
        // =====================================================
        return guaranteeLetterMapper.toDto(saved);
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

    @Transactional
    @Override
    public GuaranteeLetterDTO updateGuaranteeLetter(Long id, GuaranteeLetterDTO dto) {

        // =====================================================
        // 1. LOAD EXISTING ENTITY
        // =====================================================
        GuaranteeLetterEntity entity = guaranteeLetterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thư bảo lãnh"));

        CreditContractEntity creditContract = entity.getCreditContract();

        if (creditContract == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Guarantee Letter chưa gắn HDTD"
            );
        }

        // =====================================================
        // 2. HANDLE CREDIT LIMIT CHANGE
        // =====================================================
        BigDecimal oldAmount = entity.getExpectedGuaranteeAmount() == null
                ? BigDecimal.ZERO
                : entity.getExpectedGuaranteeAmount();

        BigDecimal newAmount = dto.getExpectedGuaranteeAmount() == null
                ? BigDecimal.ZERO
                : dto.getExpectedGuaranteeAmount();

        BigDecimal diff = newAmount.subtract(oldAmount);

        if (diff.compareTo(BigDecimal.ZERO) != 0) {

            BigDecimal usedLimit = creditContract.getUsedLimit() == null
                    ? BigDecimal.ZERO
                    : creditContract.getUsedLimit();

            BigDecimal newUsedLimit = usedLimit.add(diff);

            if (newUsedLimit.compareTo(creditContract.getCreditLimit()) > 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Vượt hạn mức tín dụng"
                );
            }

            creditContract.setUsedLimit(newUsedLimit);
            creditContract.setRemainingLimit(
                    creditContract.getCreditLimit().subtract(newUsedLimit)
            );
            creditContract.setUpdatedAt(LocalDateTime.now());

            creditContractRepository.save(creditContract);
        }

        // =====================================================
        // 3. UPDATE BASIC FIELD (DÙNG MAPPER)
        // =====================================================
        guaranteeLetterMapper.updateEntity(entity, dto);

        // =====================================================
        // 4. UPDATE MANUFACTURER + AUTO LOAD HĐBD
        // =====================================================
        if (dto.getManufacturerDTO() != null &&
                dto.getManufacturerDTO().getId() != null &&
                (entity.getManufacturer() == null ||
                        !dto.getManufacturerDTO().getId().equals(entity.getManufacturer().getId()))) {

            ManufacturerEntity manufacturer = manufacturerRepository
                    .findById(dto.getManufacturerDTO().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hãng xe"));

            entity.setManufacturer(manufacturer);

            // ===== Load lại HĐBD theo manufacturer =====
            MortgageContractEntity mortgageContract =
                    mortgageContractRepository
                            .findFirstByCustomerIdAndManufacturerIdAndStatus(
                                    entity.getCustomer().getId(),
                                    manufacturer.getId(),
                                    "ACTIVE"
                            )
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST,
                                    "Không có HĐBD ACTIVE phù hợp với hãng xe"
                            ));

            entity.setMortgageContract(mortgageContract);
        }

        // =====================================================
        // 5. UPDATE AUTHORIZED REPRESENTATIVE
        // =====================================================
        if (dto.getBranchAuthorizedRepresentativeDTO() != null &&
                dto.getBranchAuthorizedRepresentativeDTO().getId() != null) {

            BranchAuthorizedRepresentativeEntity rep =
                    branchAuthorizedRepresentativeRepository
                            .findById(dto.getBranchAuthorizedRepresentativeDTO().getId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Không tìm thấy người đại diện"
                            ));

            entity.setAuthorizedRepresentative(rep);
        }

        // =====================================================
        // 6. UPDATE CUSTOMER (NẾU CHO PHÉP ĐỔI)
        // =====================================================
        if (dto.getCustomerDTO() != null &&
                dto.getCustomerDTO().getId() != null &&
                !dto.getCustomerDTO().getId().equals(entity.getCustomer().getId())) {

            CustomerEntity customer = customerRepository
                    .findById(dto.getCustomerDTO().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy customer"));

            entity.setCustomer(customer);
        }

        entity.setUpdatedAt(LocalDateTime.now());

        // =====================================================
        // 7. SAVE
        // =====================================================
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAfterVehicleImported(Long glId,
                                           BigDecimal guaranteeAmount) {

        // ===== 1. Load GL có lock =====
        GuaranteeLetterEntity gl = guaranteeLetterRepository
                .findByIdForUpdate(glId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thư bảo lãnh"));

        // ===== 2. Chuẩn hóa scale 2 =====
        guaranteeAmount = nvl(guaranteeAmount).setScale(2, RoundingMode.HALF_UP);

        BigDecimal expected = nvl(gl.getExpectedGuaranteeAmount())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal used = nvl(gl.getUsedAmount())
                .setScale(2, RoundingMode.HALF_UP);

        // ===== 3. Tính newUsed =====
        BigDecimal newUsed = used.add(guaranteeAmount);

        // ===== 4. Check vượt hạn mức (so sánh raw, KHÔNG round lại) =====
        if (newUsed.compareTo(expected) > 0) {
            throw new RuntimeException(
                    "Vượt hạn mức thư bảo lãnh | expected=" + expected
                            + " | used=" + used
                            + " | new=" + newUsed);
        }

        // ===== 5. Update GuaranteeLetter =====
        gl.setUsedAmount(newUsed);
        gl.setRemainingAmount(expected.subtract(newUsed));
        gl.setImportedVehicleCount(
                nvlInteger(gl.getImportedVehicleCount()) + 1
        );
        gl.setTotalGuaranteeAmount(
                nvl(gl.getTotalGuaranteeAmount()).add(guaranteeAmount)
        );
        gl.setUpdatedAt(LocalDateTime.now());

        guaranteeLetterRepository.saveAndFlush(gl);

        // ====================================================
        // ===== 6. UPDATE CREDIT CONTRACT SONG SONG =========
        // ====================================================

        CreditContractEntity contract = creditContractRepository
                .findByIdForUpdate(gl.getCreditContract().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy HĐTD"));

        BigDecimal contractUsed = nvl(contract.getUsedLimit())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal creditLimit = nvl(contract.getCreditLimit())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal guaranteeBalance = nvl(contract.getGuaranteeBalance())
                .setScale(2, RoundingMode.HALF_UP);

        // ===== Cộng bảo lãnh =====
        BigDecimal newGuaranteeBalance = guaranteeBalance.add(guaranteeAmount);

        // ===== UsedLimit = tổng sử dụng =====
        BigDecimal newUsedLimit = contractUsed.add(guaranteeAmount);

        // ===== Check vượt hạn mức tín dụng =====
        if (newUsedLimit.compareTo(creditLimit) > 0) {
            throw new RuntimeException(
                    "Vượt hạn mức tín dụng | creditLimit=" + creditLimit
                            + " | newUsed=" + newUsedLimit);
        }

        // ===== Remaining =====
        BigDecimal newRemaining = creditLimit.subtract(newUsedLimit);

        // ===== Set lại =====
        contract.setGuaranteeBalance(newGuaranteeBalance);
        contract.setUsedLimit(newUsedLimit);
        contract.setRemainingLimit(newRemaining);
        contract.setUpdatedAt(LocalDateTime.now());

        creditContractRepository.saveAndFlush(contract);

    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Integer safeInt(Integer value) {
        return value == null ? 0 : value;
    }
    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
    private Integer nvlInteger(Integer value) {
        return value == null ? 0 : value;
    }
    // tính tỉ lệ bảo lãnh theo hãng
    private BigDecimal getGuaranteeRate(VehicleEntity vehicle) {
        String brand = vehicle.getGuaranteeLetter().getManufacturer().getCode(); // hoặc manufacturerCode

        return switch (brand) {
            case "VINFAST" -> new BigDecimal("0.75");
            case "HYUNDAI" -> new BigDecimal("0.85");
            default -> BigDecimal.ONE; // fallback
        };
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
