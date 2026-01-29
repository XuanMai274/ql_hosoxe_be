package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.BranchAuthorizedRepresentativeDTO;
import com.bidv.asset.vehicle.Mapper.BranchAuthorizedRepresentativeMapper;
import com.bidv.asset.vehicle.Repository.BranchAuthorizedRepresentativeRepository;
import com.bidv.asset.vehicle.Service.BranchAuthorizedRepresentativeService;
import com.bidv.asset.vehicle.entity.BranchAuthorizedRepresentativeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BranchAuthorizedRepresentativeServiceImplement implements BranchAuthorizedRepresentativeService {
    @Autowired
    BranchAuthorizedRepresentativeRepository branchAuthorizedRepresentativeRepository;
    @Autowired
    BranchAuthorizedRepresentativeMapper branchAuthorizedRepresentativeMapper;
    @Override
    public BranchAuthorizedRepresentativeDTO addBranchAuthorizedRepresentative(BranchAuthorizedRepresentativeDTO branchAuthorizedRepresentativeDTO) {
        BranchAuthorizedRepresentativeEntity branchAuthorizedRepresentativeEntity=branchAuthorizedRepresentativeMapper.toEntity(branchAuthorizedRepresentativeDTO);
        // 2. Set field hệ thống (nếu chưa có)
        if (branchAuthorizedRepresentativeEntity.getIsActive() == null) {
            branchAuthorizedRepresentativeEntity.setIsActive(true);
        }

        if (branchAuthorizedRepresentativeEntity.getCreatedAt() == null) {
            branchAuthorizedRepresentativeEntity.setCreatedAt(LocalDateTime.now());
        }

        // 3. Save
        BranchAuthorizedRepresentativeEntity savedEntity =
                branchAuthorizedRepresentativeRepository.save(branchAuthorizedRepresentativeEntity);
        // 4. Map Entity → DTO trả về
        return branchAuthorizedRepresentativeMapper.toDto(savedEntity);
    }

    @Override
    public List<BranchAuthorizedRepresentativeDTO> findAll() {
        List<BranchAuthorizedRepresentativeEntity> branchAuthorizedRepresentativeEntityListList=branchAuthorizedRepresentativeRepository.findAll();
        List<BranchAuthorizedRepresentativeDTO> branchAuthorizedRepresentativeDTOList=new ArrayList<>();
        for(BranchAuthorizedRepresentativeEntity branchAuthorizedRepresentative:branchAuthorizedRepresentativeEntityListList){
            branchAuthorizedRepresentativeDTOList.add(branchAuthorizedRepresentativeMapper.toDto(branchAuthorizedRepresentative));
        }
        return branchAuthorizedRepresentativeDTOList;
    }
}
