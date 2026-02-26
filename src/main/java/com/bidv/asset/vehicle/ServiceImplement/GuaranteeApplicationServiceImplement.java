package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.GuaranteeApplicationDTO;
import com.bidv.asset.vehicle.Mapper.CustomerMapper;
import com.bidv.asset.vehicle.Mapper.GuaranteeApplicationMapper;
import com.bidv.asset.vehicle.Repository.*;
import com.bidv.asset.vehicle.Service.GuaranteeApplicationService;
import com.bidv.asset.vehicle.Service.MortgageNumberService;
import com.bidv.asset.vehicle.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class GuaranteeApplicationServiceImplement implements GuaranteeApplicationService {
    @Autowired
    GuaranteeApplicationRepository repository;
    @Autowired
    ManufacturerRepository manufacturerRepository;
    @Autowired
    CreditContractRepository creditContractRepository;
    @Autowired
    MortgageContractRepository mortgageContractRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    GuaranteeApplicationMapper mapper;
    @Autowired
    MortgageContractSequenceRepository mortgageContractSequenceRepository;
    @Autowired
    MortgageNumberService mortgageNumberService;

    @Override
    @Transactional
    public GuaranteeApplicationDTO create(GuaranteeApplicationDTO dto) {

        // ===== 1. GET CURRENT CUSTOMER =====
        CustomerEntity customer =customerRepository.getReferenceById(dto.getCustomerDTO().getId());

        // ===== 2. FIND ACTIVE CONTRACTS =====
        CreditContractEntity credit =
                creditContractRepository
                        .findFirstByCustomerIdAndStatus(customer.getId(),"ACTIVE")
                        .orElseThrow(() -> new RuntimeException("No active credit contract"));

        MortgageContractEntity mortgage =
                mortgageContractRepository
                        .findFirstByCustomerIdAndManufacturerIdAndStatus(customer.getId(), dto.getManufacturerDTO().getId(), "ACTIVE")
                        .orElseThrow(() -> new RuntimeException("No active mortgage contract"));

        // ===== 3. LOAD MANUFACTURER =====
        ManufacturerEntity manufacturer =
                manufacturerRepository.findById(
                                dto.getManufacturerDTO().getId())
                        .orElseThrow(() -> new RuntimeException("Manufacturer not found"));

        // ===== 4. GENERATE SUB NUMBER =====
        String subNumber = mortgageNumberService.generateGuaranteeNumber(mortgage);

        // ===== 5. MAP ENTITY =====
        GuaranteeApplicationEntity entity =
                mapper.toEntity(dto, manufacturer, credit, mortgage, customer);

        entity.setCreatedAt(LocalDateTime.now());
        entity.setStatus("PENDING_APPROVAL");
        entity.setSubGuaranteeContractNumber(subNumber);

        // ==================================================
        // HANDLE VEHICLES
        // ==================================================
        int maxTermDays = 0;

        if (entity.getVehicles() != null && !entity.getVehicles().isEmpty()) {

            BigDecimal guaranteeRate =
                    manufacturer.getGuaranteeRate() == null
                            ? BigDecimal.ZERO
                            : manufacturer.getGuaranteeRate();

            for (GuaranteeApplicationVehicleEntity vehicle : entity.getVehicles()) {

                vehicle.setGuaranteeApplication(entity);

                BigDecimal price =
                        vehicle.getVehiclePrice() == null
                                ? BigDecimal.ZERO
                                : vehicle.getVehiclePrice();

                // ===== AUTO CALCULATE GUARANTEE AMOUNT =====
                BigDecimal guaranteeAmount = price.multiply(guaranteeRate);
                vehicle.setGuaranteeAmount(guaranteeAmount);

                // ===== AUTO CALCULATE TERM =====
                int term = calculateTermDays(manufacturer.getName(), vehicle.getVehicleName());

                if (term > maxTermDays) {
                    maxTermDays = term;
                }
            }
        }

        // ===== SET TERM & EXPIRY DATE =====
        entity.setGuaranteeTermDays(maxTermDays);
        entity.setExpiryDate(LocalDateTime.now()
                .toLocalDate()
                .plusDays(maxTermDays));

        // ===== CALCULATE TOTAL =====
        calculateTotals(entity);

        GuaranteeApplicationEntity saved = repository.save(entity);

        return mapper.toDTO(saved);
    }

    @Override
    public Page<GuaranteeApplicationDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDTO);
    }

    @Override
    public GuaranteeApplicationDTO getById(Long id) {
        GuaranteeApplicationEntity entity = repository.findById(id.longValue());
        if (entity == null) {
            throw new RuntimeException("Guarantee Application not found");
        }
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public GuaranteeApplicationDTO approve(Long id) {
        GuaranteeApplicationEntity entity = repository.findById(id.longValue());
        if (entity == null) {
            throw new RuntimeException("Guarantee Application not found");
        }

        // Chỉ duyệt đơn đang chờ
        if (!"PENDING_APPROVAL".equalsIgnoreCase(entity.getStatus())) {
            throw new RuntimeException("Only applications with PENDING_APPROVAL status can be approved");
        }

        entity.setStatus("APPROVED");
        entity.setApprovedAt(LocalDateTime.now());

        return mapper.toDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public GuaranteeApplicationDTO reject(Long id) {
        GuaranteeApplicationEntity entity = repository.findById(id.longValue());
        if (entity == null) {
            throw new RuntimeException("Guarantee Application not found");
        }

        if (!"PENDING_APPROVAL".equalsIgnoreCase(entity.getStatus())) {
            throw new RuntimeException("Only applications with PENDING_APPROVAL status can be rejected");
        }

        entity.setStatus("REJECTED");

        return mapper.toDTO(repository.save(entity));
    }
    // =====================================================
    // AUTO CALCULATE TOTAL
    // =====================================================
    private void calculateTotals(GuaranteeApplicationEntity entity) {

        if (entity.getVehicles() == null || entity.getVehicles().isEmpty()) {
            entity.setTotalVehicleCount(0);
            entity.setTotalVehicleAmount(BigDecimal.ZERO);
            entity.setTotalGuaranteeAmount(BigDecimal.ZERO);
            return;
        }

        entity.setTotalVehicleCount(entity.getVehicles().size());

        BigDecimal totalVehicle = entity.getVehicles().stream()
                .map(v -> v.getVehiclePrice() == null ?
                        BigDecimal.ZERO : v.getVehiclePrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGuarantee = entity.getVehicles().stream()
                .map(v -> v.getGuaranteeAmount() == null ?
                        BigDecimal.ZERO : v.getGuaranteeAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        entity.setTotalVehicleAmount(totalVehicle);
        entity.setTotalGuaranteeAmount(totalGuarantee);
    }
    private int calculateTermDays(String manufacturerName, String vehicleName) {

        if (manufacturerName.equalsIgnoreCase("VINFAST")) {
            return 29;
        }

        if (manufacturerName.equalsIgnoreCase("HYUNDAI")) {

            String name = vehicleName.toLowerCase();

            // ===== 15 DAYS =====
            if (name.contains("tucson")
                    || name.contains("creta")
                    || name.contains("ioniq")
                    || name.contains("staria")) {
                return 15;
            }

            // ===== 30 DAYS =====
            if (name.contains("grand i10")
                    || name.contains("venue")
                    || name.contains("custin")
                    || name.contains("palisade")) {
                return 30;
            }

            // ===== 60 DAYS =====
            if (name.contains("accent")
                    || name.contains("elantra")
                    || name.contains("stargazer")
                    || name.contains("santa fe")) {
                return 60;
            }
        }

        // default
        return 30;
    }

}
