package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.DocumentDTO;
import com.bidv.asset.vehicle.Service.VehicleDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/officer/documents")
@RequiredArgsConstructor
public class DocumentAPI {

    @Autowired
    VehicleDocumentService documentService;

    @PostMapping("/upload-multi")
    public List<DocumentDTO> uploadMulti(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(required = false) Long vehicleId
    ) {
        return documentService.uploadVehicleDocuments(files, vehicleId);
    }

    // 2 Danh sách file theo xe
    @GetMapping("/vehicle/{vehicleId}")
    public List<DocumentDTO> getByVehicle(@PathVariable Long vehicleId) {
        return documentService.getDocumentsByVehicle(vehicleId);
    }

    // 3 Xem file
    @GetMapping("/{id}/view")
    public ResponseEntity<ByteArrayResource> view(@PathVariable Long id) {

        byte[] data = documentService.loadFile(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(new ByteArrayResource(data));
    }
    // 4 xóa file
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocumentManually(id);
        return ResponseEntity.ok().build();
    }


}