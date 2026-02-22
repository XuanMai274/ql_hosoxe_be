package com.bidv.asset.vehicle.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
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

    private List<Long> guaranteeLetterIds;
}
