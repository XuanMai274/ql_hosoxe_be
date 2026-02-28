package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.WarehouseImportDTO;
import com.bidv.asset.vehicle.DTO.WarehouseImportRequestDTO;
import com.bidv.asset.vehicle.Mapper.WarehouseImportMapper;
import com.bidv.asset.vehicle.Repository.*;
import com.bidv.asset.vehicle.Service.WarehouseImportService;
import com.bidv.asset.vehicle.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WarehouseImportServiceImplement implements WarehouseImportService {
    @Autowired
    MortgageContractRepository mortgageContractRepository;
    @Autowired
    MortgageContractSequenceRepository sequenceRepository;
    @Autowired
    ManufacturerRepository manufacturerRepository;
    @Autowired
    VehicleRepository vehicleRepository;
    @Autowired
    WarehouseImportRepository warehouseImportRepository;
    @Autowired
    WarehouseImportMapper warehouseImportMapper;
    @Transactional
    @Override
    public WarehouseImportDTO importWarehouse(WarehouseImportRequestDTO request) {

        // ===== 1. LẤY DANH SÁCH XE =====
        List<VehicleEntity> vehicles =
                vehicleRepository.findAllById(request.getVehicleIds());

        if (vehicles.isEmpty()) {
            throw new RuntimeException("Danh sách xe không hợp lệ");
        }

        VehicleEntity firstVehicle = vehicles.get(0);

        // ===== 2. LẤY MANUFACTURER =====
        ManufacturerEntity manufacturer =
                manufacturerRepository.findById(
                        firstVehicle.getManufacturerEntity().getId()
                ).orElseThrow(() -> new RuntimeException("Không tìm thấy hãng xe"));

        // ===== 3. LẤY CUSTOMER =====
        Long customerId =
                firstVehicle.getGuaranteeLetter()
                        .getCustomer()
                        .getId();

        // ===== 4. TÌM HĐBD =====
        MortgageContractEntity mortgage =
                mortgageContractRepository
                        .findFirstByCustomerIdAndManufacturerIdAndStatus(
                                customerId,
                                manufacturer.getId(),
                                "ACTIVE"
                        )
                        .orElseThrow(() ->
                                new RuntimeException("Không tìm thấy HĐBD phù hợp")
                        );

        // ===== 5. LẤY SEQUENCE =====
        MortgageContractSequenceEntity sequence =
                sequenceRepository.findByMortgageContractId(mortgage.getId())
                        .orElseGet(() -> {
                            MortgageContractSequenceEntity newSeq =
                                    new MortgageContractSequenceEntity();
                            newSeq.setMortgageContract(mortgage);
                            newSeq.setWarehouseRunningNo(0);
                            newSeq.setGuaranteeRunningNo(0);
                            return newSeq;
                        });

        Integer nextNo = sequence.getWarehouseRunningNo() + 1;
        sequence.setWarehouseRunningNo(nextNo);
        sequenceRepository.save(sequence);

        // ===== 6. FORMAT SỐ =====
        String baseNumber = mortgage.getContractNumber();
        String[] parts = baseNumber.split("/", 2);

        String newImportNumber =
                parts[0] + "." + String.format("%02d", nextNo) + "/" + parts[1];

        // ===== 7. SAVE =====
        WarehouseImportEntity entity = WarehouseImportEntity.builder()
                .importNumber(newImportNumber)
                .manufacturer(manufacturer)
                .mortgageContract(mortgage)
                .vehicles(vehicles)
                .createdAt(LocalDateTime.now())
                .build();

        WarehouseImportEntity savedEntity =
                warehouseImportRepository.save(entity);

        // ===== 8. CẬP NHẬT ID NHẬP KHO VÀO BẢNG XE =====
        for (VehicleEntity vehicle : vehicles) {
            vehicle.setWarehouseImport(savedEntity);
        }
        vehicleRepository.saveAll(vehicles);

        // ===== 9. MAP → DTO =====
        return warehouseImportMapper.toDTO(savedEntity);
    }

}
