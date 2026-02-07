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
    //5 download file
    @GetMapping("/{id}/download")
    public ResponseEntity<ByteArrayResource> download(@PathVariable Long id) {

        byte[] data = documentService.loadFile(id);
        DocumentDTO meta = documentService.getMeta(id);

        MediaType mediaType = resolveMediaType(meta.getFileName());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + meta.getFileName() + "\""
                )
                .body(new ByteArrayResource(data));
    }
    private MediaType resolveMediaType(String fileName) {

        if (fileName == null) return MediaType.APPLICATION_OCTET_STREAM;

        String lower = fileName.toLowerCase();

        if (lower.endsWith(".pdf")) return MediaType.APPLICATION_PDF;

        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg"))
            return MediaType.IMAGE_JPEG;

        if (lower.endsWith(".png"))
            return MediaType.IMAGE_PNG;

        if (lower.endsWith(".doc") || lower.endsWith(".docx"))
            return MediaType.APPLICATION_OCTET_STREAM;

        if (lower.endsWith(".xls") || lower.endsWith(".xlsx"))
            return MediaType.APPLICATION_OCTET_STREAM;

        return MediaType.APPLICATION_OCTET_STREAM;
    }


}