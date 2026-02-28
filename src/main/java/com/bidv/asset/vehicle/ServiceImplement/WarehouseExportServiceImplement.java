package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.WarehouseExportDTO;
import com.bidv.asset.vehicle.Mapper.WarehouseExportMapper;
import com.bidv.asset.vehicle.Repository.*;
import com.bidv.asset.vehicle.Service.WarehouseExportService;
import com.bidv.asset.vehicle.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseExportServiceImplement implements WarehouseExportService {

    private final WarehouseExportRepository warehouseExportRepository;
    private final VehicleRepository vehicleRepository;
    private final WarehouseExportMapper warehouseExportMapper;
    private final MortgageContractRepository mortgageContractRepository;
    private final MortgageContractSequenceRepository sequenceRepository;
    @Autowired CreditContractRepository creditContractRepository;
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
                throw new RuntimeException("Xe số khung " + v.getChassisNumber() + " đã nằm trong một đơn đề nghị xuất kho khác.");
            }
        }
        
        // 2. Tính tổng tiền thu nợ (Tổng giá trị bảo lãnh của các xe)
        java.math.BigDecimal totalAmount = vehicles.stream()
                .map(v -> v.getGuaranteeAmount() != null ? v.getGuaranteeAmount() : java.math.BigDecimal.ZERO)
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
    public WarehouseExportDTO approveExport(Long id) {
        WarehouseExportEntity exportEntity = warehouseExportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề nghị xuất kho"));

        if (!"PENDING".equals(exportEntity.getStatus())) {
            throw new RuntimeException("Đề nghị này đã được xử lý (Trạng thái: " + exportEntity.getStatus() + ")");
        }

        List<VehicleEntity> vehicles = exportEntity.getVehicles();
        if (vehicles == null || vehicles.isEmpty()) {
            throw new RuntimeException("Không có xe nào trong danh sách xuất kho");
        }

        // 1. Lấy thông tin Hợp đồng bảo đảm từ xe đầu tiên để sinh số
        VehicleEntity firstVehicle = vehicles.get(0);
        Long customerId = firstVehicle.getGuaranteeLetter().getCustomer().getId();
        Long manufacturerId = firstVehicle.getManufacturerEntity().getId();

        MortgageContractEntity mortgage = mortgageContractRepository
                .findFirstByCustomerIdAndManufacturerIdAndStatus(customerId, manufacturerId, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy HĐBD phù hợp"));

        // 2. Sinh số xuất kho
        MortgageContractSequenceEntity sequence = sequenceRepository.findByMortgageContractId(mortgage.getId())
                .orElseGet(() -> {
                    MortgageContractSequenceEntity newSeq = new MortgageContractSequenceEntity();
                    newSeq.setMortgageContract(mortgage);
                    newSeq.setWarehouseRunningNo(0);
                    newSeq.setGuaranteeRunningNo(0);
                    return newSeq;
                });

        Integer nextNo = (sequence.getGuaranteeRunningNo() == null ? 0 : sequence.getGuaranteeRunningNo()) + 1;
        sequence.setGuaranteeRunningNo(nextNo);
        sequenceRepository.save(sequence);

        String baseNumber = mortgage.getContractNumber();
        String[] parts = baseNumber.split("/", 2);
        String exportNumber = parts[0] + "." + String.format("%02d", nextNo) + "/" + parts[1].replace("HDBD", "XUAT");

        // 3. Cập nhật Hợp đồng tín dụng (Giảm dư nợ vay xe)
        CreditContractEntity credit = creditContractRepository.findByIdForUpdate(
                firstVehicle.getGuaranteeLetter().getCreditContract().getId()
        ).orElseThrow(() -> new RuntimeException("Không tìm thấy HĐ tín dụng"));

        for (VehicleEntity v : vehicles) {
            java.math.BigDecimal loanAmount = v.getGuaranteeAmount() != null ? v.getGuaranteeAmount() : java.math.BigDecimal.ZERO;
            
            // Giảm dư nợ vay xe
            credit.setVehicleLoanBalance(credit.getVehicleLoanBalance().subtract(loanAmount));
            
            // Cập nhật trạng thái xe
            v.setStatus("Đã trả khách hàng");
            v.setExportDate(LocalDate.now());
        }

        // 4. Tính toán lại hạn mức HĐTD
        credit.setUsedLimit(
                nvl(credit.getRealEstateLoanBalance())
                        .add(nvl(credit.getVehicleLoanBalance()))
                        .add(nvl(credit.getIssuedGuaranteeBalance()))
        );
        credit.setRemainingLimit(
                nvl(credit.getCreditLimit()).subtract(credit.getUsedLimit())
        );
        credit.setUpdatedAt(LocalDateTime.now());
        creditContractRepository.save(credit);

        // 5. Hoàn tất thông tin xuất kho
        exportEntity.setExportNumber(exportNumber);
        exportEntity.setExportDate(LocalDateTime.now());
        exportEntity.setStatus("APPROVED");

        
        vehicleRepository.saveAll(vehicles);
        return warehouseExportMapper.toDto(warehouseExportRepository.save(exportEntity));
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
//        exportEntity.setUpdatedAt(LocalDateTime.now());
        
        return warehouseExportMapper.toDto(warehouseExportRepository.save(exportEntity));
    }

    @Override
    public List<WarehouseExportDTO> getPendingRequests() {
        // Có thể bổ sung filter theo status "PENDING"
        return warehouseExportRepository.findAll().stream()
                .filter(e -> "PENDING".equals(e.getStatus()))
                .map(warehouseExportMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    private java.math.BigDecimal nvl(java.math.BigDecimal val) {
        return val == null ? java.math.BigDecimal.ZERO : val;
    }
}
