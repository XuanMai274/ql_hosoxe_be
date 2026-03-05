package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.GuaranteeLetterDTO;
import com.bidv.asset.vehicle.Service.GuaranteeLetterService;
import com.bidv.asset.vehicle.Repository.UserAccountRepository;
import com.bidv.asset.vehicle.entity.CustomerEntity;
import com.bidv.asset.vehicle.entity.UserAccountEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
// @RequestMapping({"/officer/guarantee-letters",
// "/customer/guarantee-letters"})
public class GuaranteeLetterAPI {
        @Autowired
        GuaranteeLetterService guaranteeLetterService;

        @Autowired
        private UserAccountRepository userAccountRepository;

        private Long getCustomerIdIfCustomer(HttpServletRequest request) {
                if (request.getRequestURI().contains("/customer/")) {
                        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                        if (auth != null && auth.isAuthenticated()) {
                                String username = auth.getName();
                                if (username != null) {
                                        return userAccountRepository.findByUsername(username)
                                                        .map(UserAccountEntity::getCustomer)
                                                        .map(CustomerEntity::getId)
                                                        .orElse(null);
                                }
                        }
                }
                return null;
        }

        @GetMapping("/customer/guarantee-letters/active")
        public ResponseEntity<Page<GuaranteeLetterDTO>> getActiveGuarantees(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        HttpServletRequest request) {
                Long customerId = getCustomerIdIfCustomer(request);
                if (customerId == null) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
                return ResponseEntity.ok(guaranteeLetterService.getActiveGuaranteesForCustomer(customerId, pageable));
        }

        // Default GET mapping for base path (handles /customer/guarantee-letters or
        // /officer/guarantee-letters)
        @GetMapping("/officer/guarantee-letters")
        public ResponseEntity<Page<GuaranteeLetterDTO>> list(
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String manufacturerCode,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                        @RequestParam(required = false) Boolean hasLetterNumber,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                return search(keyword, manufacturerCode, fromDate, toDate, hasLetterNumber, page, size);
        }

        @PostMapping("/officer/guarantee-letters/add")
        public ResponseEntity<GuaranteeLetterDTO> addGuarantee(
                        @RequestBody GuaranteeLetterDTO dto) {

                GuaranteeLetterDTO result = guaranteeLetterService.createGuaranteeLetter(dto);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(result);
        }

        @GetMapping("/officer/guarantee-letters/findByDate")
        public ResponseEntity<Page<GuaranteeLetterDTO>> getGuaranteeLetters(
                        @RequestParam(required = false) String manufacturerCode,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                Pageable pageable = PageRequest.of(
                                page,
                                size,
                                Sort.by(Sort.Direction.DESC, "guaranteeContractDate"));

                return ResponseEntity.ok(
                                guaranteeLetterService.getGuaranteeLetters(
                                                manufacturerCode,
                                                fromDate,
                                                toDate,
                                                pageable));
        }

        @GetMapping("/officer/guarantee-letters/search")
        public ResponseEntity<Page<GuaranteeLetterDTO>> search(
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String manufacturerCode,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                        @RequestParam(required = false) Boolean hasLetterNumber,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                if (keyword != null && keyword.trim().isEmpty()) {
                        keyword = null;
                }
                Pageable pageable = PageRequest.of(
                                page, size,
                                Sort.by(Sort.Direction.DESC, "guaranteeContractDate"));

                return ResponseEntity.ok(
                                guaranteeLetterService.search(keyword, manufacturerCode, fromDate, toDate,
                                                hasLetterNumber, pageable));
        }

        @GetMapping("/officer/guarantee-letters/{id}")
        public ResponseEntity<GuaranteeLetterDTO> getById(@PathVariable long id) {
                GuaranteeLetterDTO dto = guaranteeLetterService.findById(id);
                return ResponseEntity.ok(dto);
        }

        @PutMapping("/officer/guarantee-letters/{id}")
        public ResponseEntity<GuaranteeLetterDTO> update(
                        @PathVariable Long id,
                        @RequestBody GuaranteeLetterDTO dto) {
                return ResponseEntity.ok(
                                guaranteeLetterService.updateGuaranteeLetter(id, dto));
        }

        // api lấy lên danh sách thư bảo lãnh
        @GetMapping("/officer/guarantee-letters/findAll")
        public ResponseEntity<List<GuaranteeLetterDTO>> findAll() {
                List<GuaranteeLetterDTO> result = guaranteeLetterService.findAll();

                return ResponseEntity.ok(result);
        }

        @GetMapping("/officer/guarantee-letters/suggest")
        public ResponseEntity<List<GuaranteeLetterDTO>> suggest(
                        @RequestParam String keyword,
                        @RequestParam String manufacturerCode) {
                return ResponseEntity.ok(
                                guaranteeLetterService.suggest(keyword, manufacturerCode));
        }

}
