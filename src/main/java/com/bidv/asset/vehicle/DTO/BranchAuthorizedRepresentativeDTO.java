package com.bidv.asset.vehicle.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BranchAuthorizedRepresentativeDTO {
    private Long id;
    private String branchCode;
    private String branchName;
    private String representativeName;
    private String representativeTitle;
    private String authorizationDocNo;
    private LocalDate authorizationDocDate;
    private String authorizationIssuer;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private Boolean isActive;
    List<Long> guaranteeLetterDTOS;
}
