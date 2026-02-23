package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.*;
import com.bidv.asset.vehicle.Mapper.GuaranteeLetterMapper;
import com.bidv.asset.vehicle.Mapper.InvoiceMapper;
import com.bidv.asset.vehicle.Mapper.VehicleMapper;
import com.bidv.asset.vehicle.Repository.GuaranteeLetterRepository;
import com.bidv.asset.vehicle.Repository.InvoiceRepository;
import com.bidv.asset.vehicle.Repository.VehicleRepository;
import com.bidv.asset.vehicle.Service.VehicleService;
import com.bidv.asset.vehicle.entity.GuaranteeLetterEntity;
import com.bidv.asset.vehicle.entity.InvoiceEntity;
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
    @Override
    public Page<VehicleListDTO> getVehicles(
            String chassisNumber,
            String status,
            String manufacturer,
            String ref,
            Pageable pageable
    ) {
        System.out.println("Manufacturer param: " + manufacturer);
        return vehicleRepository.searchVehicles(
                chassisNumber,
                status,
                manufacturer,
                ref,
                pageable
        );
    }

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

        /* ================= Update basic fields ================= */

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

        /* ================= Update Invoice ================= */

        if (dto.getInvoiceId() != null && dto.getInvoiceId().getId() != null) {

            InvoiceEntity invoice = invoiceRepository.findById(dto.getInvoiceId().getId())
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));

            vehicle.setInvoice(invoice);
        }

        /* ================= Update Guarantee Letter ================= */

        if (dto.getGuaranteeLetterDTO() != null &&
                dto.getGuaranteeLetterDTO().getId() != null) {

            GuaranteeLetterEntity guarantee = guaranteeLetterRepository
                    .findById(dto.getGuaranteeLetterDTO().getId())
                    .orElseThrow(() -> new RuntimeException("Guarantee letter not found"));

            vehicle.setGuaranteeLetter(guarantee);
        }

        VehicleEntity saved = vehicleRepository.save(vehicle);

        return mapToDTO(saved);
    }
    @Override
    public List<VehicleDTO> getVehiclesByStatus(String status) {

        List<VehicleEntity> vehicles = vehicleRepository.findByStatus(status);

        return vehicles.stream()
                .map(vehicle -> {
                    VehicleDTO dto = vehicleMapper.toDto(vehicle);

                    // 👉 chỉ áp dụng logic với trạng thái Giữ két
                    if ("Giữ két".equalsIgnoreCase(status)) {
                        dto.setDeadlineLabel(
                                calculateDeadlineLabel(vehicle.getCreatedAt())
                        );
                    }

                    return dto;
                })
                .toList();
    }

    private VehicleDTO mapWithDeadlineLabel(VehicleEntity entity) {

        VehicleDTO dto = vehicleMapper.toDto(entity);

        dto.setDeadlineLabel(
                calculateDeadlineLabel(entity.getCreatedAt())
        );

        return dto;
    }

    private String calculateDeadlineLabel(LocalDateTime createdAt) {

        if (createdAt == null) return null;

        LocalDate deadline = createdAt.toLocalDate().plusDays(3);
        LocalDate today = LocalDate.now();

        long diff = ChronoUnit.DAYS.between(today, deadline);

        if (diff > 0) {
            return "Còn " + diff + " ngày đến hạn nhập kho";
        }

        if (diff == 0) {
            return "Cần nhập kho hôm nay";
        }

        return "Đã quá hạn nhập kho " + Math.abs(diff) + " ngày";
    }
    @Override
    public List<VehicleDTO> findByIds(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("Danh sách xe trống");
        }

        List<VehicleEntity> vehicles =
                vehicleRepository.findAllWithGuaranteeByIds(ids);

        return vehicles.stream()
                .map(vehicleMapper::toDto)
                .toList();
    }

    /* ===== Mapper ===== */
    private VehicleDTO mapToDTO(VehicleEntity entity) {
        VehicleDTO dto = new VehicleDTO();

        dto.setId(entity.getId());
        dto.setStt(entity.getStt());
        dto.setVehicleName(entity.getVehicleName());
        dto.setStatus(entity.getStatus());
        dto.setFundingSource(entity.getFundingSource());
        dto.setImportDate(entity.getImportDate());
        dto.setExportDate(entity.getExportDate());
        dto.setAssetName(entity.getAssetName());
        dto.setChassisNumber(entity.getChassisNumber());
        dto.setEngineNumber(entity.getEngineNumber());
        dto.setModelType(entity.getModelType());
        dto.setColor(entity.getColor());
        dto.setSeats(entity.getSeats());
        dto.setPrice(entity.getPrice());
        dto.setOriginalCopy(entity.getOriginalCopy());
        dto.setImportDocs(entity.getImportDocs());
        dto.setRegistrationOrderNumber(entity.getRegistrationOrderNumber());
        dto.setDocsDeliveryDate(entity.getDocsDeliveryDate());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setImportDossier(entity.getImportDossier());
        GuaranteeLetterDTO guaranteeLetterDTO=new GuaranteeLetterDTO();
        ManufacturerDTO manufacturerDTO=new ManufacturerDTO();
        manufacturerDTO.setName(entity.getGuaranteeLetter().getManufacturer().getName());
        CreditContractDTO creditContractDTO=new CreditContractDTO();
        creditContractDTO.setContractNumber(entity.getGuaranteeLetter().getCreditContract().getContractNumber());
        creditContractDTO.setContractDate(entity.getGuaranteeLetter().getCreditContract().getContractDate());
//        guaranteeLetterDTO.setManufacturerDTO(manufacturerDTO);
//        guaranteeLetterDTO.setCreditContractDTO(creditContractDTO);
        return dto;
    }
}
