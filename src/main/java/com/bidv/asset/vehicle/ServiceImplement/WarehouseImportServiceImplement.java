package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.*;
import com.bidv.asset.vehicle.Mapper.WarehouseImportMapper;
import com.bidv.asset.vehicle.Repository.*;
import com.bidv.asset.vehicle.Service.DisbursementService;
import com.bidv.asset.vehicle.Service.LoanService;
import com.bidv.asset.vehicle.Service.WarehouseImportService;
import com.bidv.asset.vehicle.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        @Autowired
        DisbursementService disbursementService;
        @Autowired
        LoanService loanService;
        @Transactional
        @Override
        public WarehouseImportDTO importWarehouse(WarehouseImportRequestDTO request) {

                if (request.getVehicleIds() == null || request.getVehicleIds().isEmpty()) {
                        throw new RuntimeException("Danh sách xe không được để trống");
                }

                // ===== 1. LẤY DANH SÁCH XE =====
                List<VehicleEntity> vehicles = vehicleRepository.findAllById(request.getVehicleIds());

                if (vehicles == null || vehicles.isEmpty()) {
                        throw new RuntimeException("Danh sách xe không hợp lệ");
                }

                VehicleEntity firstVehicle = vehicles.get(0);

                // ===== 2. LẤY MANUFACTURER =====
                ManufacturerEntity manufacturer = manufacturerRepository.findById(
                                firstVehicle.getManufacturerEntity().getId())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy hãng xe"));

                // ===== 3. LẤY CUSTOMER =====
                Long customerId = firstVehicle.getGuaranteeLetter()
                                .getCustomer()
                                .getId();

                // ===== 4. TÌM HĐBD =====
                MortgageContractEntity mortgage = mortgageContractRepository
                                .findFirstByCustomerIdAndManufacturerIdAndStatus(
                                                customerId,
                                                manufacturer.getId(),
                                                "ACTIVE")
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy HĐBD phù hợp"));

                // ===== 5. LẤY SEQUENCE =====
                MortgageContractSequenceEntity sequence = sequenceRepository.findByMortgageContractId(mortgage.getId())
                                .orElseGet(() -> {
                                        MortgageContractSequenceEntity newSeq = new MortgageContractSequenceEntity();
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

                String newImportNumber = parts[0] + "." + String.format("%02d", nextNo) + "/" + parts[1];

                // ===== 7. SAVE =====
                WarehouseImportEntity entity = WarehouseImportEntity.builder()
                                .importNumber(newImportNumber)
                                .manufacturer(manufacturer)
                                .mortgageContract(mortgage)
                                .vehicles(vehicles)
                                .createdAt(LocalDateTime.now())
                                .totalOutstandingBalance(request.getTotalOutstandingBalance())
                                .totalCollateralValue(request.getTotalCollateralValue())
                                .build();

                WarehouseImportEntity savedEntity = warehouseImportRepository.save(entity);

                // ===== 8. CẬP NHẬT ID NHẬP KHO VÀO BẢNG XE =====
                for (VehicleEntity vehicle : vehicles) {
                        vehicle.setWarehouseImport(savedEntity);
                }
                vehicleRepository.saveAll(vehicles);

                // ===== 9. MAP → DTO =====
                return warehouseImportMapper.toDTO(savedEntity);
        }

        @Override
        public org.springframework.data.domain.Page<WarehouseImportDTO> getAll(String importNumber,
                        org.springframework.data.domain.Pageable pageable) {
                return warehouseImportRepository.findAllWithFilter(importNumber, pageable)
                                .map(warehouseImportMapper::toDTO);
        }

        @Override
        public WarehouseImportDTO getById(Long id) {
                return warehouseImportRepository.findById(id)
                                .map(warehouseImportMapper::toDTO)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu nhập kho với ID: " + id));
        }

        @Override
        @Transactional
        public WarehouseImportDTO updateWarehouseImport(Long id, WarehouseImportDTO dto) {
                WarehouseImportEntity entity = warehouseImportRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu nhập kho với ID: " + id));

                // Cập nhật số phiếu
                if (dto.getImportNumber() != null) {
                        entity.setImportNumber(dto.getImportNumber());
                }

                // Cập nhật ngày tạo nếu cần
                if (dto.getCreatedAt() != null) {
                        entity.setCreatedAt(dto.getCreatedAt());
                }

                WarehouseImportEntity saved = warehouseImportRepository.save(entity);
                return warehouseImportMapper.toDTO(saved);
        }

//        @Override
//        @Transactional(rollbackFor = Exception.class)
//        public void executeFullProcess(FullProcessNKGNRequest request) {
//
//                validateRequest(request);
//
//                // 1️⃣ Nhập kho
//                WarehouseImportDTO warehouseResult =
//                        warehouseImportService.importWarehouse(
//                                request.getWarehouseRequest()
//                        );
//
//                Long mortgageId =
//                        warehouseResult.getMortgageContractDTO().getId();
//
//                // 2️⃣ Gắn mortgageContractId
//                request.getDisbursementRequest()
//                        .setMortgageContractId(mortgageId);
//
//                // 3️⃣ Tạo giải ngân
//                DisbursementDTO disbursement =
//                        disbursementService.createDisbursement(
//                                request.getDisbursementRequest()
//                        );
//
//                // 4️⃣ Tạo khoản vay
//                if (disbursement.getLoans() != null && !disbursement.getLoans().isEmpty()) {
//                        loanService.createBatchLoans(disbursement.getLoans());
//                }
//
//                // Không catch → nếu lỗi sẽ rollback toàn bộ
//        }
//
//        private void validateRequest(FullProcessNKGNRequest request) {
//                if (request == null
//                        || request.getWarehouseRequest() == null
//                        || request.getDisbursementRequest() == null) {
//                        throw new IllegalArgumentException("Request không hợp lệ");
//                }
//        }
@Transactional(rollbackFor = Exception.class)
public FullProcessResponse executeFullProcess(
        FullProcessNKGNRequest request) {

        validateRequest(request);

        // 1️ Nhập kho
        WarehouseImportDTO warehouseResult =
                importWarehouse(
                        request.getWarehouseRequest()
                );

        Long mortgageId =
                warehouseResult.getMortgageContractDTO().getId();

        // 2️ Gắn mortgageContractId
        request.getDisbursementRequest()
                .setMortgageContractId(mortgageId);

        // 3️ Tạo giải ngân
        DisbursementDTO disbursement =
                disbursementService.createDisbursement(
                        request.getDisbursementRequest()
                );

        // 4️ Tạo khoản vay
        List<LoanDTO> loans = new ArrayList<>();
        List<Long> vehicleIds = request.getWarehouseRequest().getVehicleIds();
        
        // Lấy danh sách xe đã nạp vào database để có thông tin khách hàng
        List<VehicleEntity> vehicles = vehicleRepository.findAllById(vehicleIds);
        
        for (VehicleEntity vehicle : vehicles) {
                LoanDTO loanDTO = new LoanDTO();
                
                // Gán thông tin xe và khách hàng
                loanDTO.setVehicleDTO(new VehicleDTO());
                loanDTO.getVehicleDTO().setId(vehicle.getId());
                
                loanDTO.setCustomerDTO(new CustomerDTO());
                loanDTO.getCustomerDTO().setId(vehicle.getGuaranteeLetter().getCustomer().getId());
                
                // Gán thông tin giải ngân (Quan trọng: Lấy LoanContractNumber từ Disbursement)
                loanDTO.setDisbursementDTO(new DisbursementDTO());
                loanDTO.getDisbursementDTO().setId(disbursement.getId());
                
                // Lấy số hợp đồng vay từ giải ngân vừa tạo
                loanDTO.setLoanContractNumber(disbursement.getLoanContractNumber());
                
                // Các thông tin mặc định từ disbursement request
                loanDTO.setLoanDate(request.getDisbursementRequest().getDisbursementDate());
                loanDTO.setLoanTerm(request.getDisbursementRequest().getLoanTerm());
                
                // Trạng thái và loại khoản vay mặc định
                loanDTO.setLoanStatus("ACTIVE");
                loanDTO.setLoanType("VEHICLE");
                
                // Tạo từng loan
                loans.add(loanService.createLoan(loanDTO));
        }

        return new FullProcessResponse(
                warehouseResult,
                disbursement,
                loans
        );
}
        private void validateRequest(FullProcessNKGNRequest request) {
                if (request == null
                        || request.getWarehouseRequest() == null
                        || request.getDisbursementRequest() == null) {
                        throw new IllegalArgumentException("Request không hợp lệ");
                }
        }
}
