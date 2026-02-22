package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.CustomerCreateRequestDTO;
import com.bidv.asset.vehicle.DTO.CustomerDTO;
import com.bidv.asset.vehicle.Mapper.CustomerMapper;
import com.bidv.asset.vehicle.Repository.CustomerRepository;
import com.bidv.asset.vehicle.Repository.UserAccountRepository;
import com.bidv.asset.vehicle.Service.CustomerService;
import com.bidv.asset.vehicle.Service.UserAccountService;
import com.bidv.asset.vehicle.entity.CustomerEntity;
import com.bidv.asset.vehicle.entity.UserAccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImplement implements CustomerService {

        @Autowired
        CustomerRepository customerRepo;
        @Autowired
        UserAccountRepository userRepo;
        @Autowired
        UserAccountService userAccountService;
        @Autowired
        CustomerMapper customerMapper;

        @Override
        @Transactional
        public CustomerDTO createCustomerWithAccount(CustomerCreateRequestDTO request) {

                // ===== tạo account =====
                UserAccountEntity account = userAccountService.create(
                                request.getUsername(),
                                request.getPassword(),
                                request.getRoleId());

                // ===== tạo customer =====
                CustomerEntity customer = customerMapper.toEntity(request.getCustomer(), account);

                customer.setCreatedAt(LocalDateTime.now());
                customer.setUpdatedAt(LocalDateTime.now());

                return customerMapper.toDTO(customerRepo.save(customer));
        }

        // ===== CREATE =====
        @Override
        public CustomerDTO create(CustomerDTO dto) {

                if (customerRepo.existsByCif(dto.getCif())) {
                        throw new RuntimeException("CIF already exists");
                }

                UserAccountEntity user = userRepo.findById(dto.getUserAccountId())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                CustomerEntity entity = customerMapper.toEntity(dto, user);

                entity.setCreatedAt(LocalDateTime.now());
                entity.setUpdatedAt(LocalDateTime.now());

                return customerMapper.toDTO(customerRepo.save(entity));
        }

        // ===== UPDATE =====
        @Override
        public CustomerDTO update(Long id, CustomerDTO dto) {

                CustomerEntity entity = customerRepo.findById(id)
                                .orElseThrow(() -> new RuntimeException("Customer not found"));

                UserAccountEntity user = userRepo.findById(dto.getUserAccountId())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                entity.setCustomerName(dto.getCustomerName());
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
                entity.setUserAccount(user);

                entity.setUpdatedAt(LocalDateTime.now());

                return customerMapper.toDTO(customerRepo.save(entity));
        }

        // ===== DELETE =====
        @Override
        public void delete(Long id) {

                if (!customerRepo.existsById(id)) {
                        throw new RuntimeException("Customer not found");
                }

                customerRepo.deleteById(id);
        }

        // ===== GET BY ID =====
        @Override
        public CustomerDTO getById(Long id) {

                return customerRepo.findById(id)
                                .map(customerMapper::toDTO)
                                .orElseThrow(() -> new RuntimeException("Customer not found"));
        }

        @Override
        public List<CustomerDTO> getAll() {

                return customerRepo.findAll()
                                .stream()
                                .map(customerMapper::toDTO)
                                .collect(Collectors.toList());
        }

}