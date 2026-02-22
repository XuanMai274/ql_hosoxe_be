package com.bidv.asset.vehicle.Mapper;

import com.bidv.asset.vehicle.DTO.CustomerDTO;
import com.bidv.asset.vehicle.entity.CustomerEntity;
import com.bidv.asset.vehicle.entity.UserAccountEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    // ===== ENTITY -> DTO =====
    public  CustomerDTO toDTO(CustomerEntity entity) {
        if (entity == null) return null;

        CustomerDTO dto = new CustomerDTO();

        dto.setId(entity.getId());
        dto.setCustomerName(entity.getCustomerName());
        dto.setCif(entity.getCif());
        dto.setCustomerType(entity.getCustomerType());

        dto.setBusinessRegistrationNo(entity.getBusinessRegistrationNo());
        dto.setTaxCode(entity.getTaxCode());

        dto.setAddress(entity.getAddress());
        dto.setPhone(entity.getPhone());
        dto.setFax(entity.getFax());
        dto.setEmail(entity.getEmail());

        dto.setRepresentativeName(entity.getRepresentativeName());
        dto.setRepresentativeTitle(entity.getRepresentativeTitle());

        dto.setBankAccountNo(entity.getBankAccountNo());
        dto.setBankName(entity.getBankName());

        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getUserAccount() != null) {
            dto.setUserAccountId(entity.getUserAccount().getId());
        }

        return dto;
    }

    // ===== DTO -> ENTITY =====
    public  CustomerEntity toEntity(CustomerDTO dto, UserAccountEntity userAccount) {
        if (dto == null) return null;

        CustomerEntity entity = new CustomerEntity();

        entity.setId(dto.getId());
        entity.setCustomerName(dto.getCustomerName());
        entity.setCif(dto.getCif());
        entity.setCustomerType(dto.getCustomerType());

        entity.setBusinessRegistrationNo(dto.getBusinessRegistrationNo());
        entity.setTaxCode(dto.getTaxCode());

        entity.setAddress(dto.getAddress());
        entity.setPhone(dto.getPhone());
        entity.setFax(dto.getFax());
        entity.setEmail(dto.getEmail());

        entity.setRepresentativeName(dto.getRepresentativeName());
        entity.setRepresentativeTitle(dto.getRepresentativeTitle());

        entity.setBankAccountNo(dto.getBankAccountNo());
        entity.setBankName(dto.getBankName());

        entity.setStatus(dto.getStatus());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        entity.setUserAccount(userAccount);

        return entity;
    }
}
