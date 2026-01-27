package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.GuaranteeLetterDTO;
import com.bidv.asset.vehicle.Service.GuaranteeLetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("officer/guarantee-letters")
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
}
