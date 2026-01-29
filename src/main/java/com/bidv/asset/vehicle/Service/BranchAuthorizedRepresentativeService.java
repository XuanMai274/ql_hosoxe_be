package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.BranchAuthorizedRepresentativeDTO;

import java.util.List;

public interface BranchAuthorizedRepresentativeService {
    BranchAuthorizedRepresentativeDTO addBranchAuthorizedRepresentative(BranchAuthorizedRepresentativeDTO branchAuthorizedRepresentativeDTO);
    List<BranchAuthorizedRepresentativeDTO> findAll();
}
