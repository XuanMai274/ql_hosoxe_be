package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.DocumentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VehicleDocumentService {
    public List<DocumentDTO> uploadVehicleDocuments(
            List<MultipartFile> files,
            Long vehicleId);

    List<DocumentDTO> getDocumentsByVehicle(Long vehicleId);

    public void deleteDocumentManually(Long documentId);

    byte[] loadFile(Long documentId);

    DocumentDTO getMeta(Long id);

    DocumentDTO findLatestDocumentByVehicle(Long vehicleId);
}
