package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.WarehouseExportDTO;
import com.bidv.asset.vehicle.Mapper.WarehouseExportMapper;
import com.bidv.asset.vehicle.Repository.*;
import com.bidv.asset.vehicle.Service.WarehouseExportService;
import com.bidv.asset.vehicle.entity.*;
import com.bidv.asset.vehicle.enums.LoanStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class WarehouseExportServiceImplement implements WarehouseExportService {

        private final WarehouseExportRepository warehouseExportRepository;
        private final VehicleRepository vehicleRepository;
        private final WarehouseExportMapper warehouseExportMapper;
        private final MortgageContractRepository mortgageContractRepository;
        private final MortgageContractSequenceRepository sequenceRepository;
        private final LoanRepository loanRepository;
        private final DisbursementRepository disbursementRepository;
        @Autowired
        CreditContractRepository creditContractRepository;

        @Override
        @Transactional
        public WarehouseExportDTO requestExport(WarehouseExportDTO dto) {
                if (dto.getVehicleIds() == null || dto.getVehicleIds().isEmpty()) {
                        throw new RuntimeException("Danh sách xe yêu cầu xuất không được để trống");
                }

                // 1. Lấy danh sách xe và kiểm tra tính hợp lệ
                List<VehicleEntity> vehicles = vehicleRepository.findAllById(dto.getVehicleIds());

                for (VehicleEntity v : vehicles) {
                        if (v.getWarehouseExport() != null) {
                                throw new RuntimeException("Xe số khung " + v.getChassisNumber()
                                                + " đã nằm trong một đơn đề nghị xuất kho khác.");
                        }
                }

                // 2. Tính tổng tiền thu nợ (Tổng giá trị bảo lãnh của các xe)
                java.math.BigDecimal totalAmount = vehicles.stream()
                                .map(v -> v.getGuaranteeAmount() != null ? v.getGuaranteeAmount()
                                                : java.math.BigDecimal.ZERO)
                                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

                // 3. Tạo đề nghị xuất kho (PENDING)
                WarehouseExportEntity exportEntity = warehouseExportMapper.toEntity(dto);
                exportEntity.setRequestDate(LocalDateTime.now());
                exportEntity.setStatus("PENDING");
                exportEntity.setVehicleCount(vehicles.size());
                exportEntity.setTotalDebtCollection(totalAmount);
                exportEntity.setCreatedAt(LocalDateTime.now());

                WarehouseExportEntity savedExport = warehouseExportRepository.save(exportEntity);

                // 4. Liên kết tạm thời (Để officer thấy danh sách xe khi duyệt)
                for (VehicleEntity v : vehicles) {
                        v.setWarehouseExport(savedExport);
                }
                vehicleRepository.saveAll(vehicles);

                return warehouseExportMapper.toDto(savedExport);
        }

        @Override
        @Transactional
        public WarehouseExportDTO approveExport(WarehouseExportDTO dto) {

                WarehouseExportEntity exportEntity = warehouseExportRepository.findById(dto.getId())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề nghị xuất kho"));

                if (!"PENDING".equals(exportEntity.getStatus())) {
                        throw new RuntimeException(
                                        "Đề nghị này đã được xử lý (Trạng thái: "
                                                        + exportEntity.getStatus() + ")");
                }

                // 🔹 Cập nhật 2 giá trị từ frontend
                exportEntity.setTotalCollateralValue(dto.getTotalCollateralValue());
                exportEntity.setRealEstateValue(dto.getRealEstateValue());

                List<VehicleEntity> vehicles = exportEntity.getVehicles();
                if (vehicles == null || vehicles.isEmpty()) {
                        throw new RuntimeException("Không có xe nào trong danh sách xuất kho");
                }

                VehicleEntity firstVehicle = vehicles.get(0);
                Long customerId = firstVehicle.getGuaranteeLetter().getCustomer().getId();
                Long manufacturerId = firstVehicle.getManufacturerEntity().getId();

                MortgageContractEntity mortgage = mortgageContractRepository
                                .findFirstByCustomerIdAndManufacturerIdAndStatus(
                                                customerId, manufacturerId, "ACTIVE")
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy HĐBD phù hợp"));

                MortgageContractSequenceEntity sequence = sequenceRepository.findByMortgageContractId(mortgage.getId())
                                .orElseGet(() -> {
                                        MortgageContractSequenceEntity newSeq = new MortgageContractSequenceEntity();
                                        newSeq.setMortgageContract(mortgage);
                                        newSeq.setWarehouseRunningNo(0);
                                        newSeq.setGuaranteeRunningNo(0);
                                        return newSeq;
                                });

                Integer nextNo = (sequence.getGuaranteeRunningNo() == null
                                ? 0
                                : sequence.getGuaranteeRunningNo()) + 1;

                sequence.setGuaranteeRunningNo(nextNo);
                sequenceRepository.save(sequence);

                String baseNumber = mortgage.getContractNumber();
                String[] parts = baseNumber.split("/", 2);
                String exportNumber = parts[0] + "." + String.format("%02d", nextNo)
                                + "/" + parts[1].replace("HDBDCT", "XUAT");

                CreditContractEntity credit = creditContractRepository.findByIdForUpdate(
                                firstVehicle.getGuaranteeLetter()
                                                .getCreditContract().getId())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy HĐ tín dụng"));

                for (VehicleEntity v : vehicles) {

                        BigDecimal loanAmount = v.getGuaranteeAmount() != null
                                        ? v.getGuaranteeAmount()
                                        : BigDecimal.ZERO;

                        credit.setVehicleLoanBalance(
                                        nvl(credit.getVehicleLoanBalance())
                                                        .subtract(loanAmount));

                        v.setStatus("Đã trả khách hàng");
//                        v.setInSafe(false);
                        v.setExportDate(LocalDate.now());

                        // 🔹 Xử lý khoản vay liên quan
                        if (v.getLoans() != null && !v.getLoans().isEmpty()) {
                                // Giả định mỗi xe có 1 khoản vay ACTIVE
                                v.getLoans().stream()
                                                .filter(l -> Objects.equals(l.getLoanStatus(), "ACTIVE"))
                                                .findFirst()
                                                .ifPresent(loan -> {
                                                        loan.setLoanStatus("PAID_OFF");
                                                        loan.setLastPaymentDate(LocalDate.now());
                                                        loan.setTotalPaidAmount(loan.getLoanAmount());

                                                        // 🔹 Cập nhật Disbursement
                                                        DisbursementEntity db = loan.getDisbursement();
                                                        if (db != null) {
                                                                // Cập nhật số xe đã rút (+1 đơn vị vì xe đã rời kho)
                                                                int currentWithdrawn = nvlInt(
                                                                                db.getWithdrawnVehiclesCount());
                                                                db.setWithdrawnVehiclesCount(currentWithdrawn + 1);

                                                                // Cập nhật tổng tiền đã trả
                                                                BigDecimal paidAmount = nvl(db.getTotalAmountPaid());
                                                                db.setTotalAmountPaid(paidAmount
                                                                                .add(nvl(loan.getLoanAmount())));

                                                                // Kiểm tra trạng thái Disbursement
                                                                if (nvl(db.getDisbursementAmount()).compareTo(
                                                                                nvl(db.getTotalAmountPaid())) <= 0) {
                                                                        db.setStatus("PAID_OFF");
                                                                }
                                                                disbursementRepository.save(db);
                                                        }
                                                        loanRepository.save(loan);
                                                });
                        }
                }

                credit.setUsedLimit(
                                nvl(credit.getRealEstateLoanBalance())
                                                .add(nvl(credit.getVehicleLoanBalance()))
                                                .add(nvl(credit.getIssuedGuaranteeBalance())));

                credit.setRemainingLimit(
                                nvl(credit.getCreditLimit())
                                                .subtract(credit.getUsedLimit()));

                credit.setUpdatedAt(LocalDateTime.now());
                creditContractRepository.save(credit);

                exportEntity.setExportNumber(exportNumber);
                exportEntity.setExportDate(LocalDateTime.now());
                exportEntity.setStatus("APPROVED");

                vehicleRepository.saveAll(vehicles);

                return warehouseExportMapper.toDto(
                                warehouseExportRepository.save(exportEntity));
        }

        @Override
        @Transactional
        public WarehouseExportDTO rejectExport(Long id) {
                WarehouseExportEntity exportEntity = warehouseExportRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề nghị xuất kho"));

                if (!"PENDING".equals(exportEntity.getStatus())) {
                        throw new RuntimeException("Chỉ có thể từ chối đơn ở trạng thái PENDING");
                }

                // 1. Giải phóng xe (Set warehouse_export_id = NULL)
                List<VehicleEntity> vehicles = vehicleRepository.findByWarehouseExportId(id);
                for (VehicleEntity v : vehicles) {
                        v.setWarehouseExport(null);
                }
                vehicleRepository.saveAll(vehicles);

                // 2. Cập nhật trạng thái đơn
                exportEntity.setStatus("REJECTED");
                // exportEntity.setUpdatedAt(LocalDateTime.now());

                return warehouseExportMapper.toDto(warehouseExportRepository.save(exportEntity));
        }

        @Override
        public List<WarehouseExportDTO> getPendingRequests() {

                BigDecimal collateralFromDb = vehicleRepository.sumPriceByStatus("Giữ trong kho");

                final BigDecimal totalCollateral = collateralFromDb != null ? collateralFromDb : BigDecimal.ZERO;

                final BigDecimal realEstate = new BigDecimal("8910000000");

                return warehouseExportRepository.findByStatus("PENDING")
                                .stream()
                                .map(entity -> {
                                        WarehouseExportDTO dto = warehouseExportMapper.toDto(entity);
                                        dto.setTotalCollateralValue(totalCollateral);
                                        dto.setRealEstateValue(realEstate);
                                        return dto;
                                })
                                .toList();
        }

        @Override
        public org.springframework.data.domain.Page<WarehouseExportDTO> getAll(String exportNumber,
                        org.springframework.data.domain.Pageable pageable) {
                return warehouseExportRepository.findAllWithFilter(exportNumber, pageable)
                                .map(warehouseExportMapper::toDto);
        }

        @Override
        public WarehouseExportDTO getById(Long id) {
                WarehouseExportEntity entity = warehouseExportRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu xuất kho"));
                return warehouseExportMapper.toDto(entity);
        }

        @Override
        @Transactional
        public WarehouseExportDTO updateWarehouseExport(Long id, WarehouseExportDTO dto) {
                WarehouseExportEntity entity = warehouseExportRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu xuất kho"));

                if (dto.getExportNumber() != null) {
                        entity.setExportNumber(dto.getExportNumber());
                }
                if (dto.getExportDate() != null) {
                        entity.setExportDate(dto.getExportDate());
                }
                if (dto.getDescription() != null) {
                        entity.setDescription(dto.getDescription());
                }

                return warehouseExportMapper.toDto(warehouseExportRepository.save(entity));
        }

        private java.math.BigDecimal nvl(java.math.BigDecimal val) {
                return val == null ? java.math.BigDecimal.ZERO : val;
        }

        private Integer nvlInt(Integer val) {
                return val == null ? 0 : val;
        }
}
