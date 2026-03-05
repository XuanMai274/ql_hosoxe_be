package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.*;
import com.bidv.asset.vehicle.Mapper.GuaranteeLetterMapper;
import com.bidv.asset.vehicle.Mapper.InvoiceMapper;
import com.bidv.asset.vehicle.Mapper.VehicleMapper;
import com.bidv.asset.vehicle.Repository.GuaranteeLetterRepository;
import com.bidv.asset.vehicle.Repository.InvoiceRepository;
import com.bidv.asset.vehicle.Repository.MortgageContractSequenceRepository;
import com.bidv.asset.vehicle.Repository.VehicleRepository;
import com.bidv.asset.vehicle.Service.VehicleService;
import com.bidv.asset.vehicle.entity.GuaranteeLetterEntity;
import com.bidv.asset.vehicle.entity.InvoiceEntity;
import com.bidv.asset.vehicle.entity.MortgageContractSequenceEntity;
import com.bidv.asset.vehicle.entity.VehicleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class VehicleServiceImplement implements VehicleService {

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    VehicleMapper vehicleMapper;

    @Autowired
    GuaranteeLetterMapper guaranteeLetterMapper;

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    GuaranteeLetterRepository guaranteeLetterRepository;

    @Autowired
    InvoiceMapper invoiceMapper;

    @Autowired
    MortgageContractSequenceRepository sequenceRepo;

    @Override
    public Page<VehicleListDTO> getVehicles(
            Long customerId,
            String chassisNumber,
            String status,
            String manufacturer,
            String ref,
            Pageable pageable) {
        return vehicleRepository.searchVehicles(
                customerId,
                chassisNumber,
                status,
                manufacturer,
                ref,
                pageable);
    }

    /*
     * =========================================================
     * DETAIL
     * =========================================================
     */

    @Override
    @Transactional(readOnly = true)
    public VehicleDTO getVehicleDetail(Long vehicleId) {

        VehicleEntity entity = vehicleRepository.findDetailById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe"));

        return vehicleMapper.toDto(entity);
    }

    @Transactional
    @Override
    public VehicleDTO updateVehicle(Long id, VehicleDTO dto) {

        VehicleEntity vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        vehicle.setVehicleName(dto.getVehicleName());
        vehicle.setStatus(dto.getStatus());
        vehicle.setFundingSource(dto.getFundingSource());
        vehicle.setImportDate(dto.getImportDate());
        vehicle.setExportDate(dto.getExportDate());
        vehicle.setAssetName(dto.getAssetName());
        vehicle.setChassisNumber(dto.getChassisNumber());
        vehicle.setEngineNumber(dto.getEngineNumber());
        vehicle.setModelType(dto.getModelType());
        vehicle.setColor(dto.getColor());
        vehicle.setSeats(dto.getSeats());
        vehicle.setPrice(dto.getPrice());
        vehicle.setOriginalCopy(dto.getOriginalCopy());
        vehicle.setImportDocs(dto.getImportDocs());
        vehicle.setRegistrationOrderNumber(dto.getRegistrationOrderNumber());
        vehicle.setDocsDeliveryDate(dto.getDocsDeliveryDate());
        vehicle.setDescription(dto.getDescription());

        if (dto.getInvoiceId() != null && dto.getInvoiceId().getId() != null) {
            InvoiceEntity invoice = invoiceRepository.findById(dto.getInvoiceId().getId())
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));
            vehicle.setInvoice(invoice);
        }

        if (dto.getGuaranteeLetterDTO() != null &&
                dto.getGuaranteeLetterDTO().getId() != null) {

            GuaranteeLetterEntity guarantee = guaranteeLetterRepository
                    .findById(dto.getGuaranteeLetterDTO().getId())
                    .orElseThrow(() -> new RuntimeException("Guarantee letter not found"));

            vehicle.setGuaranteeLetter(guarantee);

            // ===== UPDATE SEQUENCE COUNTERS IF PASSED =====
            if (dto.getGuaranteeLetterDTO() != null
                    && dto.getGuaranteeLetterDTO().getMortgageContractDTO() != null
                    && guarantee.getMortgageContract() != null) {

                MortgageContractDTO mcDTO = dto.getGuaranteeLetterDTO().getMortgageContractDTO();
                if (mcDTO.getGuaranteeRunningNo() != null || mcDTO.getWarehouseRunningNo() != null) {
                    MortgageContractSequenceEntity seq = sequenceRepo.findById(guarantee.getMortgageContract().getId())
                            .orElse(null);
                    if (seq != null) {
                        if (mcDTO.getGuaranteeRunningNo() != null)
                            seq.setGuaranteeRunningNo(mcDTO.getGuaranteeRunningNo());
                        if (mcDTO.getWarehouseRunningNo() != null)
                            seq.setWarehouseRunningNo(mcDTO.getWarehouseRunningNo());
                        sequenceRepo.save(seq);
                    }
                }
            }
        }

        VehicleEntity saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toDto(saved);
    }

    /*
     * =========================================================
     * DEADLINE LOGIC
     * =========================================================
     */

//    // Xác định số ngày nhập kho theo từng xe
//    private int resolveImportDeadlineDays(VehicleEntity vehicle) {
//
//        if (vehicle == null || vehicle.getManufacturerEntity() == null)
//            return 0;
//
//        String brand = vehicle.getManufacturerEntity().getCode() != null
//                ? vehicle.getManufacturerEntity().getCode().toUpperCase()
//                : "";
//
//        String name = vehicle.getVehicleName() != null
//                ? vehicle.getVehicleName().toUpperCase()
//                : "";
//
//        // VINFAST → 3 ngày
//        if ("VINFAST".equals(brand)) {
//            return 3;
//        }
//
//        // HYUNDAI
//        if ("HYUNDAI".equals(brand)) {
//
//            if (name.contains("TUCSON") ||
//                    name.contains("CRETA") ||
//                    name.contains("IONIQ") ||
//                    name.contains("LONIQ") ||
//                    name.contains("STARIA")) {
//
//                return 15;
//            }
//
//            if (name.contains("ACCENT") ||
//                    name.contains("ELANTRA") ||
//                    name.contains("STARGAZER") ||
//                    name.contains("SANTA FE")) {
//
//                return 60;
//            }
//
//            return 30; // mặc định Hyundai
//        }
//
//        return 0;
//    }

    // Deadline nhập kho (dựa vào createdAt)
    private String calculateImportDeadlineLabel(VehicleEntity vehicle) {

        if (vehicle == null || vehicle.getCreatedAt() == null)
            return null;

        LocalDate deadline = vehicle.getCreatedAt()
                .toLocalDate()
                .plusDays(3); // hardcode 3 ngày

        long diff = ChronoUnit.DAYS.between(LocalDate.now(), deadline);

        if (diff > 0)
            return "Còn " + diff + " ngày đến hạn thanh toán";

        if (diff == 0)
            return "Cần thanh toán hôm nay";

        return "Đã quá hạn thanh toán " + Math.abs(diff) + " ngày";
    }

    // Deadline rút hồ sơ (importDate + 60 ngày)
    private String calculateExportDeadlineLabel(LocalDate importDate) {

        if (importDate == null)
            return null;

        LocalDate deadline = importDate.plusDays(60);

        long diff = ChronoUnit.DAYS.between(LocalDate.now(), deadline);

        if (diff > 0)
            return "Còn " + diff + " ngày đến hạn rút hồ sơ";

        if (diff == 0)
            return "Cần rút hồ sơ hôm nay";

        return "Đã quá hạn rút hồ sơ " + Math.abs(diff) + " ngày";
    }

    /*
     * =========================================================
     * LIST APIs
     * =========================================================
     */

    @Override
    public List<VehicleDTO> getVehiclesByStatus(String status) {

        List<VehicleEntity> vehicles = vehicleRepository.findByStatus(status);

        return vehicles.stream()
                .map(vehicle -> {
                    VehicleDTO dto = vehicleMapper.toDto(vehicle);

                    if ("Giữ két".equalsIgnoreCase(status)) {
                        dto.setDeadlineLabel(
                                calculateImportDeadlineLabel(vehicle));
                        // calculateDeadlineLabel(vehicle.getCreatedAt()));
                    }

                    return dto;
                })
                .toList();
    }

    @Override
    public Page<VehicleDTO> getAvailableVehicles(String status,
            String chassisNumber,
            String manufacturerCode,
            String ref,
            Pageable pageable) {

        String chassisSearch = (chassisNumber != null && !chassisNumber.isBlank())
                ? "%" + chassisNumber.toLowerCase() + "%"
                : null;

        String refSearch = (ref != null && !ref.isBlank())
                ? "%" + ref.toLowerCase() + "%"
                : null;

        Page<VehicleEntity> vehicles = vehicleRepository.findAvailableForExport(
                status, chassisSearch, manufacturerCode, refSearch, pageable);

        return vehicles.map(vehicle -> {
            VehicleDTO dto = vehicleMapper.toDto(vehicle);
            dto.setDeadlineLabel(
                    calculateExportDeadlineLabel(vehicle.getImportDate()));
            return dto;
        });
    }

    // @Override
    // public Page<VehicleDTO> getCustomerAvailableVehicles(String status,
    // String chassisNumber,
    // String manufacturerCode,
    // String loanContractNumber,
    // Pageable pageable) {

    // String chassisSearch = (chassisNumber != null && !chassisNumber.isBlank())
    // ? "%" + chassisNumber.toLowerCase() + "%"
    // : null;

    // String loanSearch = (loanContractNumber != null &&
    // !loanContractNumber.isBlank())
    // ? "%" + loanContractNumber.toLowerCase() + "%"
    // : null;

    // Page<VehicleEntity> vehicles =
    // vehicleRepository.findAvailableForExportForCustomer(
    // status, chassisSearch, manufacturerCode, loanSearch, pageable);

    // return vehicles.map(vehicleMapper::toDto);
    // }

    // @Override
    // public List<VehicleDTO> getVehiclesByExportId(Long exportId) {

    // List<VehicleEntity> vehicles =
    // vehicleRepository.findByWarehouseExportId(exportId);

    // return vehicles.stream()
    // .map(vehicleMapper::toDto)
    // .toList();
    // private String calculateDeadlineLabel(LocalDateTime createdAt) {
    // if (createdAt == null)
    // return null;
    // LocalDate deadline = createdAt.toLocalDate().plusDays(3);
    // LocalDate today = LocalDate.now();
    // long diff = ChronoUnit.DAYS.between(today, deadline);

    // if (diff > 0)
    // return "Còn " + diff + " ngày đến hạn nhập kho";
    // if (diff == 0)
    // return "Cần nhập kho hôm nay";
    // return "Đã quá hạn nhập kho " + Math.abs(diff) + " ngày";
    // }

    // private String calculateExportDeadlineLabel(LocalDate importDate) {
    // if (importDate == null)
    // return null;
    // // Giả sử hạn rút hồ sơ là 60 ngày kể từ ngày nhập kho
    // LocalDate deadline = importDate.plusDays(60);
    // LocalDate today = LocalDate.now();
    // long diff = ChronoUnit.DAYS.between(today, deadline);

    // if (diff > 0)
    // return "Còn " + diff + " ngày đến hạn rút";
    // if (diff == 0)
    // return "Cần rút hồ sơ hôm nay";
    // return "Đã quá hạn rút " + Math.abs(diff) + " ngày";
    // }

    @Override
    public List<VehicleDTO> findByIds(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("Danh sách xe trống");
        }

        List<VehicleEntity> vehicles = vehicleRepository.findAllWithGuaranteeByIds(ids);

        return vehicles.stream()
                .map(vehicleMapper::toDto)
                .toList();
    }

    // @Override
    // public Page<VehicleDTO> getAvailableVehicles(String status, String
    // chassisNumber, String manufacturerCode,
    // String ref, Pageable pageable) {
    // String chassisSearch = (chassisNumber != null && !chassisNumber.isBlank())
    // ? "%" + chassisNumber.toLowerCase() + "%"
    // : null;
    // String refSearch = (ref != null && !ref.isBlank()) ? "%" + ref.toLowerCase()
    // + "%" : null;

    // Page<VehicleEntity> vehicles =
    // vehicleRepository.findAvailableForExport(status, chassisSearch,
    // manufacturerCode,
    // refSearch, pageable);
    // return vehicles.map(vehicle -> {
    // VehicleDTO dto = vehicleMapper.toDto(vehicle);
    // dto.setDeadlineLabel(calculateExportDeadlineLabel(vehicle.getImportDate()));
    // return dto;
    // });
    // }

    @Override
    public Page<VehicleDTO> getCustomerAvailableVehicles(String status, String chassisNumber, String manufacturerCode,
            String loanContractNumber, Pageable pageable) {
        String chassisSearch = (chassisNumber != null && !chassisNumber.isBlank())
                ? "%" + chassisNumber.toLowerCase() + "%"
                : null;
        String loanSearch = (loanContractNumber != null && !loanContractNumber.isBlank())
                ? "%" + loanContractNumber.toLowerCase() + "%"
                : null;

        Page<VehicleEntity> vehicles = vehicleRepository.findAvailableForExportForCustomer(status, chassisSearch,
                manufacturerCode, loanSearch, pageable);
        return vehicles.map(vehicleMapper::toDto);
    }

    @Override
    public List<VehicleDTO> getVehiclesByExportId(Long exportId) {
        List<VehicleEntity> vehicles = vehicleRepository.findByWarehouseExportId(exportId);
        return vehicles.stream()
                .map(vehicleMapper::toDto)
                .toList();
    }

    @Override
    public List<VehicleDTO> getVinfastInSafeVehicles() {

        List<VehicleEntity> vehicles =
                vehicleRepository.findVinfastInSafe(
                        "VINFAST",
                        "Giữ trong kho"
                );

        return vehicles.stream()
                .map(vehicle -> {
                    VehicleDTO dto = vehicleMapper.toDto(vehicle);
                    dto.setDeadlineLabel(
                            calculateImportDeadlineLabelForVin(vehicle)
                    );
                    return dto;
                })
                .toList();
    }
    private String calculateImportDeadlineLabelForVin(VehicleEntity vehicle) {

        if (vehicle == null || vehicle.getCreatedAt() == null)
            return null;

        LocalDate deadline = vehicle.getCreatedAt()
                .toLocalDate()
                .plusDays(15); // hardcode 3 ngày

        long diff = ChronoUnit.DAYS.between(LocalDate.now(), deadline);

        if (diff > 0)
            return "Còn " + diff + " ngày đến hạn nhập kho";

        if (diff == 0)
            return "Cần nhập kho hôm nay";

        return "Đã quá hạn nhập kho " + Math.abs(diff) + " ngày";
    }
    @Override
    @Transactional
    public int updateVehicleInSafe(List<Long> vehicleIds, Boolean inSafe) {

        if (vehicleIds == null || vehicleIds.isEmpty()) {
            throw new RuntimeException("Danh sách xe không được để trống");
        }

        return vehicleRepository.updateInSafeByIds(vehicleIds, inSafe);
    }
}
