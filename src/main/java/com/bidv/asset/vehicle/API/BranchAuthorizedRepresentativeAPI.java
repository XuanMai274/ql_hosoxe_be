package com.bidv.asset.vehicle.API;
import com.bidv.asset.vehicle.DTO.BranchAuthorizedRepresentativeDTO;
import com.bidv.asset.vehicle.DTO.CreditContractDTO;
import com.bidv.asset.vehicle.Service.BranchAuthorizedRepresentativeService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/officer/branch-authorized-representatives")
public class BranchAuthorizedRepresentativeAPI {
    @Autowired
    BranchAuthorizedRepresentativeService branchAuthorizedRepresentativeService;
    @PostMapping("/add")
    public ResponseEntity<BranchAuthorizedRepresentativeDTO> add(
            @RequestBody BranchAuthorizedRepresentativeDTO dto
    ) {
        BranchAuthorizedRepresentativeDTO result =
                branchAuthorizedRepresentativeService.addBranchAuthorizedRepresentative(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }
    @GetMapping("/findAll")
    public ResponseEntity<Map<String,Object>> findAll(){
        Map<String, Object> response = new HashMap<>();
        List<BranchAuthorizedRepresentativeDTO> branchAuthorizedRepresentativeDTOS=branchAuthorizedRepresentativeService.findAll();
        if(branchAuthorizedRepresentativeDTOS!=null){
            response.put("success", true);
            response.put("branchAuthorizedRepresentative",branchAuthorizedRepresentativeDTOS);
            return ResponseEntity.ok(response);
        }
        response.put("success", false);
        response.put("branchAuthorizedRepresentative", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
