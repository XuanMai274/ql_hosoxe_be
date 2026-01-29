package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.GuaranteeLetterDTO;
import com.bidv.asset.vehicle.Service.GuaranteeLetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/officer/guarantee-letters")
public class GuaranteeLetterAPI {
    @Autowired
    GuaranteeLetterService guaranteeLetterService;
    @PostMapping("/add")
    public ResponseEntity<GuaranteeLetterDTO> addGuarantee(
            @RequestBody GuaranteeLetterDTO dto) {

        GuaranteeLetterDTO result =
                guaranteeLetterService.createGuaranteeLetter(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }
    @GetMapping("/findByDate")
    public ResponseEntity<Page<GuaranteeLetterDTO>> getGuaranteeLetters(
            @RequestParam(required = false) String manufacturerCode,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "guaranteeContractDate")
        );

        return ResponseEntity.ok(
                guaranteeLetterService.getGuaranteeLetters(
                        manufacturerCode,
                        fromDate,
                        toDate,
                        pageable
                )
        );
    }
    @GetMapping("/search")
    public ResponseEntity<Page<GuaranteeLetterDTO>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String manufacturerCode,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }
        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by(Sort.Direction.DESC, "guaranteeContractDate")
        );

        return ResponseEntity.ok(
                guaranteeLetterService.search(keyword, manufacturerCode, fromDate, toDate, pageable)
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<GuaranteeLetterDTO> getById(@PathVariable long id) {
        GuaranteeLetterDTO dto = guaranteeLetterService.findById(id);
        return ResponseEntity.ok(dto);
    }
    @PutMapping("/{id}")
    public ResponseEntity<GuaranteeLetterDTO> update(
            @PathVariable Long id,
            @RequestBody GuaranteeLetterDTO dto
    ) {
        return ResponseEntity.ok(
                guaranteeLetterService.updateGuaranteeLetter(id, dto)
        );
    }
}
