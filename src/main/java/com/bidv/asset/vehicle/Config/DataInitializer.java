package com.bidv.asset.vehicle.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.bidv.asset.vehicle.entity.*;
import com.bidv.asset.vehicle.Repository.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // Kiểm tra đã có admin chưa
        if (userAccountRepository.existsByUsername("admin")) {
            return;
        }

        // Lấy role ADMIN từ DB
        RoleEntity adminRole = roleRepository.findByCode("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        // 1️⃣ Tạo UserAccount
        UserAccountEntity adminAccount = new UserAccountEntity();
        adminAccount.setUsername("admin");
        adminAccount.setPasswordHash(passwordEncoder.encode("123456"));
        adminAccount.setStatus("ACTIVE");
        adminAccount.setAccountType("EMPLOYEE");
        adminAccount.setCreateAt(LocalDateTime.now());
        adminAccount.setRole(adminRole);

        userAccountRepository.save(adminAccount);

        // 2️⃣ Tạo Employee
        EmployeeEntity employee = new EmployeeEntity();
        employee.setEmployeeCode("EMP001");
        employee.setFullName("System Administrator");
        employee.setPosition("ADMIN");
        employee.setEmail("admin@system.local");
        employee.setStatus("ACTIVE");

        employee.setUserAccount(adminAccount);

        employeeRepository.save(employee);

        System.out.println(">>> DEFAULT ADMIN CREATED SUCCESSFULLY");
    }
}