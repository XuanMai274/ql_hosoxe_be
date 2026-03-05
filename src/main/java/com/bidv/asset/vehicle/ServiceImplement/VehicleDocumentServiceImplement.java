package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.DocumentDTO;
import com.bidv.asset.vehicle.Repository.DocumentRepository;
import com.bidv.asset.vehicle.Service.VehicleDocumentService;
import com.bidv.asset.vehicle.entity.DocumentEntity;
import com.bidv.asset.vehicle.entity.VehicleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class VehicleDocumentServiceImplement implements VehicleDocumentService {
    @Autowired
    DocumentRepository documentRepository;
    private static final String ROOT_DIR = "/data/vehicle-docs";

    @Override
    @Transactional
    public List<DocumentDTO> uploadVehicleDocuments(
            List<MultipartFile> files,
            Long vehicleId) {
        try {
            // 1. Tạo thư mục theo ngày (chuẩn audit)
            String folder = LocalDate.now().toString();
            Path dir = Paths.get(ROOT_DIR, folder);
            Files.createDirectories(dir);

            List<DocumentDTO> result = new ArrayList<>();

            for (MultipartFile file : files) {

                // 2. Tạo tên file an toàn
                String storedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = dir.resolve(storedName);

                // 3. Lưu file vật lý
                Files.copy(
                        file.getInputStream(),
                        filePath,
                        StandardCopyOption.REPLACE_EXISTING);

                // 4. Lưu metadata DB
                DocumentEntity doc = new DocumentEntity();
                doc.setFileName(file.getOriginalFilename());
                doc.setFileType(file.getContentType());
                doc.setFilePath(filePath.toString());
                doc.setFileSize(file.getSize());
                doc.setStatus(vehicleId == null ? "TEMP" : "ATTACHED");
                doc.setUploadDate(LocalDate.now());
                doc.setCreatedAt(LocalDateTime.now());

                if (vehicleId != null) {
                    VehicleEntity vehicle = new VehicleEntity();
                    vehicle.setId(vehicleId);
                    doc.setVehicle(vehicle);
                }

                doc = documentRepository.save(doc);

                result.add(mapToDto(doc));
            }

            return result;

        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu file", e);
        }
    }

    @Override
    public List<DocumentDTO> getDocumentsByVehicle(Long vehicleId) {
        return documentRepository.findByVehicleId(vehicleId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public byte[] loadFile(Long documentId) {
        try {
            DocumentEntity doc = documentRepository.findById(documentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu"));

            Path path = Paths.get(doc.getFilePath());
            if (!Files.exists(path)) {
                throw new RuntimeException("File không tồn tại");
            }
            return Files.readAllBytes(path);

        } catch (IOException e) {
            throw new RuntimeException("Không đọc được file", e);
        }
    }

    // xóa file
    @Override
    public void deleteDocumentManually(Long documentId) {
        DocumentEntity doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy document"));

        // 1. Xóa file vật lý trên NAS / SAN
        try {
            Path path = Paths.get(doc.getFilePath());
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Xóa file vật lý thất bại", e);
        }

        // 2. Xóa metadata trong DB
        documentRepository.delete(doc);
    }

    @Override
    public DocumentDTO getMeta(Long id) {

        DocumentEntity entity = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy metadata"));

        return mapToDto(entity);
    }

    @Override
    public DocumentDTO findLatestDocumentByVehicle(Long vehicleId) {
        List<DocumentEntity> docs = documentRepository.findByVehicleId(vehicleId);
        if (docs.isEmpty()) {
            return null;
        }
        // Try to find COC or VAT in filename
        return docs.stream()
                .filter(d -> {
                    String name = d.getFileName().toUpperCase();
                    return name.contains("COC") || name.contains("VAT");
                })
                .findFirst()
                .map(this::mapToDto)
                .orElse(mapToDto(docs.get(0))); // Fallback to first one
    }

    private DocumentDTO mapToDto(DocumentEntity doc) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(doc.getId());
        dto.setFileName(doc.getFileName());
        dto.setFileType(doc.getFileType());
        dto.setFilePath(doc.getFilePath());
        dto.setUploadDate(doc.getUploadDate());
        dto.setCreatedAt(doc.getCreatedAt());
        dto.setVehicleId(doc.getVehicle() != null ? doc.getVehicle().getId() : null);
        return dto;
    }
}
