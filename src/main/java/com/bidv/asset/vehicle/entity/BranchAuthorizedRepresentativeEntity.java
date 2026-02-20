package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "branch_authorized_representative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchAuthorizedRepresentativeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branch_code", nullable = false, length = 20)
    private String branchCode;

    @Column(name = "branch_name", nullable = false)
    private String branchName;

    @Column(name = "representative_name", nullable = false)
    private String representativeName;

    @Column(name = "representative_title", nullable = false)
    private String representativeTitle;

    @Column(name = "authorization_doc_no", nullable = false)
    private String authorizationDocNo;

    @Column(name = "authorization_doc_date", nullable = false)
    private LocalDate authorizationDocDate;

    @Column(name = "authorization_issuer", nullable = false)
    private String authorizationIssuer;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;
    @Column(name = "effective_to")
    private LocalDate effectiveTo;
    @Column(name = "is_active")
    private Boolean isActive = true;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "authorizedRepresentative", fetch = FetchType.LAZY)
    private List<GuaranteeLetterEntity> guaranteeLetterEntity;
}