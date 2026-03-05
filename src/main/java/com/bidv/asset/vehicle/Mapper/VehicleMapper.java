package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.*;
import com.bidv.asset.vehicle.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class VehicleMapper {

    @Autowired
    private DocumentMapper documentMapper;
    @Autowired private ManufacturerMapper manufacturerMapper;
    @Autowired private GuaranteeLetterMapper guaranteeLetterMapper;
    @Autowired DisbursementMapper disbursementMapper;
    // Sử dụng lazy injection hoặc truyền tham số để tránh circular dependency nếu LoanMapper cần VehicleMapper
    @Autowired
    private org.springframework.context.ApplicationContext applicationContext;

    private LoanMapper getLoanMapper() {
        return applicationContext.getBean(LoanMapper.class);
    }
    /* ===================== ENTITY → DTO ===================== */
    public VehicleDTO toDto(VehicleEntity entity) {
        if (entity == null) return null;

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
        dto.setImportDossier(entity.getImportDossier());
        dto.setOriginalCopy(entity.getOriginalCopy());
        dto.setImportDocs(entity.getImportDocs());
        dto.setRegistrationOrderNumber(entity.getRegistrationOrderNumber());
        dto.setDocsDeliveryDate(entity.getDocsDeliveryDate());
        dto.setImportDossier(entity.getImportDossier());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setGuaranteeAmount(entity.getGuaranteeAmount());
        dto.setInSafe(entity.getInSafe());
        /* ===== INVOICE ===== */
        if (entity.getInvoice() != null) {
            InvoiceEntity invoice = entity.getInvoice();

            InvoiceDTO invoiceDTO = new InvoiceDTO();
            invoiceDTO.setId(invoice.getId());
            invoiceDTO.setInvoiceNumber(invoice.getInvoiceNumber());
            invoiceDTO.setInvoiceDate(invoice.getInvoiceDate());
            invoiceDTO.setTotalAmount(invoice.getTotalAmount());

            dto.setInvoiceId(invoiceDTO);
        }

        /* ===== DOSSIERS ===== */
        if (entity.getDossiers() != null) {
            dto.setDossierIds(
                    entity.getDossiers()
                            .stream()
                            .map(VehicleDossierEntity::getId)
                            .collect(Collectors.toList())
            );
        }
        if (entity.getLoans() != null) {
            dto.setLoanIds(
                    entity.getLoans()
                            .stream()
                            .map(LoanEntity::getId)
                            .collect(Collectors.toList())
            );
            if (!entity.getLoans().isEmpty()) {
                LoanEntity firstLoan = entity.getLoans().get(0);
                dto.setLoanContractNumber(firstLoan.getLoanContractNumber());
                
                // Map detailed loan info
                LoanDTO loanDto = new LoanDTO();
                loanDto.setId(firstLoan.getId());
                loanDto.setDocId(firstLoan.getDocId());
                loanDto.setLoanContractNumber(firstLoan.getLoanContractNumber());
                loanDto.setAccountNumber(firstLoan.getAccountNumber());
                loanDto.setLoanAmount(firstLoan.getLoanAmount());
                loanDto.setLoanDate(firstLoan.getLoanDate());
                loanDto.setDueDate(firstLoan.getDueDate());
                loanDto.setLoanTerm(firstLoan.getLoanTerm());
                loanDto.setLoanStatus(firstLoan.getLoanStatus());
                loanDto.setLoanType(firstLoan.getLoanType());
                DisbursementDTO disbursementDTO= new DisbursementDTO();
                loanDto.setDisbursementDTO(disbursementMapper.toDtoSimple(firstLoan.getDisbursement()));

                dto.setLoan(loanDto);
            }
        }

        if (entity.getWarehouseImport() != null) {
            dto.setWarehouseImportId(entity.getWarehouseImport().getId());
        }
        if (entity.getWarehouseExport() != null) {
            dto.setWarehouseExportId(entity.getWarehouseExport().getId());
        }
        /* ===== DOCUMENTS ===== */
        if (entity.getDocuments() != null) {
            dto.setDocuments(
                    entity.getDocuments()
                            .stream()
                            .map(documentMapper::toDto)
                            .collect(Collectors.toList())
            );
        }
        // loại xe
        if(entity.getManufacturerEntity()!=null){
           ManufacturerDTO manufacturerDTO=new ManufacturerDTO();
           manufacturerDTO.setId(entity.getManufacturerEntity().getId());
           manufacturerDTO.setCode(entity.getManufacturerEntity().getCode());
           manufacturerDTO.setTemplateCode(entity.getManufacturerEntity().getTemplateCode());
           manufacturerDTO.setName(entity.getManufacturerEntity().getName());
           dto.setManufacturerDTO(manufacturerDTO);

        }

        /* ===== GUARANTEE LETTER ===== */
        if (entity.getGuaranteeLetter() != null) {
            dto.setGuaranteeLetterDTO(guaranteeLetterMapper.toDto(entity.getGuaranteeLetter()));
//            GuaranteeLetterEntity g = entity.getGuaranteeLetter();
//            GuaranteeLetterDTO guaranteeDTO = new GuaranteeLetterDTO();
//            guaranteeDTO.setId(g.getId());
//            guaranteeDTO.setGuaranteeContractNumber(g.getGuaranteeContractNumber());
//            guaranteeDTO.setTotalGuaranteeAmount(g.getTotalGuaranteeAmount());
//            guaranteeDTO.setRemainingAmount(g.getRemainingAmount());
//            guaranteeDTO.setReferenceCode(g.getReferenceCode());
//            guaranteeDTO.setGuaranteeNoticeNumber(g.getGuaranteeNoticeNumber());
//            ManufacturerDTO manufacturerDTO=new ManufacturerDTO();
//            manufacturerDTO.setCode(g.getManufacturer().getCode());
//            manufacturerDTO.setTemplateCode(g.getManufacturer().getTemplateCode());
//            manufacturerDTO.setId(g.getManufacturer().getId());
//            manufacturerDTO.setName(g.getManufacturer().getName());
////            guaranteeDTO.setManufacturerDTO(manufacturerDTO);
//            CreditContractDTO creditContractDTO=new CreditContractDTO();
//            creditContractDTO.setContractNumber(entity.getGuaranteeLetter().getCreditContract().getContractNumber());
//            creditContractDTO.setContractDate(entity.getGuaranteeLetter().getCreditContract().getContractDate());
//            guaranteeDTO.setCreditContractDTO(creditContractDTO);
//            dto.setGuaranteeLetterDTO(guaranteeDTO);
        }

        return dto;
    }

    /* ===================== DTO → ENTITY ===================== */
    public VehicleEntity toEntity(VehicleDTO dto) {
        if (dto == null) return null;

        VehicleEntity entity = new VehicleEntity();

        entity.setId(dto.getId());
        entity.setStt(dto.getStt());
        entity.setVehicleName(dto.getVehicleName());
        entity.setStatus(dto.getStatus());
        entity.setFundingSource(dto.getFundingSource());

        entity.setImportDate(dto.getImportDate());
        entity.setExportDate(dto.getExportDate());

        entity.setAssetName(dto.getAssetName());
        entity.setChassisNumber(dto.getChassisNumber());
        entity.setEngineNumber(dto.getEngineNumber());
        entity.setModelType(dto.getModelType());
        entity.setColor(dto.getColor());
        entity.setSeats(dto.getSeats());
        entity.setPrice(dto.getPrice());
        entity.setOriginalCopy(dto.getOriginalCopy());
        entity.setImportDocs(dto.getImportDocs());
        entity.setRegistrationOrderNumber(dto.getRegistrationOrderNumber());
        entity.setDocsDeliveryDate(dto.getDocsDeliveryDate());
        entity.setGuaranteeAmount(dto.getGuaranteeAmount());
        entity.setInSafe(dto.getInSafe());
        entity.setDescription(dto.getDescription());
        entity.setCreatedAt(dto.getCreatedAt());

        /* ===== INVOICE (ID ONLY) ===== */
        if (dto.getInvoiceId() != null && dto.getInvoiceId().getId() != null) {
            InvoiceEntity invoice = new InvoiceEntity();
            invoice.setId(dto.getInvoiceId().getId());
            entity.setInvoice(invoice);
        }

        /* ===== GUARANTEE LETTER (ID ONLY) ===== */
        if (dto.getGuaranteeLetterDTO() != null &&
                dto.getGuaranteeLetterDTO().getId() != null) {

            GuaranteeLetterEntity g = new GuaranteeLetterEntity();
            g.setId(dto.getGuaranteeLetterDTO().getId());
            entity.setGuaranteeLetter(g);
        }
        // loại xe
        if(dto.getManufacturerDTO()!=null){
            ManufacturerEntity manufacturerEntity= new ManufacturerEntity();
            manufacturerEntity.setId(dto.getManufacturerDTO().getId());
            manufacturerEntity.setCode(dto.getManufacturerDTO().getCode());
            manufacturerEntity.setTemplateCode(dto.getManufacturerDTO().getTemplateCode());
            entity.setManufacturerEntity(manufacturerEntity);
        }

        return entity;
    }

    public VehicleDTO toDtoSimple(VehicleEntity entity) {
        if (entity == null) return null;
        VehicleDTO dto = new VehicleDTO();
        dto.setId(entity.getId());
        dto.setVehicleName(entity.getVehicleName());
        dto.setChassisNumber(entity.getChassisNumber());
        dto.setEngineNumber(entity.getEngineNumber());
        dto.setStatus(entity.getStatus());
        dto.setPrice(entity.getPrice());
        return dto;
    }
}
