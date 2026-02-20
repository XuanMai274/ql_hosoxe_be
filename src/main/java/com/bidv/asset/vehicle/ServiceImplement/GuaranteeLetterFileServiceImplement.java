package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.GuaranteeLetterFileDTO;
import com.bidv.asset.vehicle.Mapper.GuaranteeLetterFileMapper;
import com.bidv.asset.vehicle.Repository.GuaranteeLetterFileRepository;
import com.bidv.asset.vehicle.Service.GuaranteeLetterFileService;
import com.bidv.asset.vehicle.entity.GuaranteeLetterEntity;
import com.bidv.asset.vehicle.entity.GuaranteeLetterFileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuaranteeLetterFileServiceImplement implements GuaranteeLetterFileService {

    private final GuaranteeLetterFileRepository repository;
    private final GuaranteeLetterFileMapper mapper;

    private static final String ROOT_DIR = "/data/guarantee-files";

    // =========================================================
    // Upload file
    // =========================================================
    @Override
    @Transactional
    public GuaranteeLetterFileDTO uploadFile(Long guaranteeLetterId, MultipartFile file) {

        validateFile(file);

        try {

            // ===== Create folder by date =====
            String folder = LocalDate.now().toString();
            Path dir = Paths.get(ROOT_DIR, folder);
            Files.createDirectories(dir);

            // ===== Create stored name =====
            String storedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = dir.resolve(storedName);

            // ===== Save file =====
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // ===== Hash =====
            String hash = calculateHash(file.getBytes());

            // ===== Version =====
            Integer version = repository.getMaxVersion(guaranteeLetterId)
                    .orElse(0) + 1;

            // Disable old active files
            repository.disableActiveFiles(guaranteeLetterId);

            // ===== Save entity =====
            GuaranteeLetterFileEntity entity = new GuaranteeLetterFileEntity();
            GuaranteeLetterEntity guaranteeLetterEntity=new GuaranteeLetterEntity();
            guaranteeLetterEntity.setId(guaranteeLetterId);
            entity.setGuaranteeLetter(guaranteeLetterEntity);
            entity.setFileName(file.getOriginalFilename());
            entity.setFileType(file.getContentType());
            entity.setFilePath(filePath.toString());
            entity.setFileSize(file.getSize());
            entity.setFileHash(hash);
            entity.setVersion(version);
            entity.setIsActive(true);
            entity.setCreatedAt(LocalDateTime.now());

            return mapper.toDto(repository.save(entity));

        } catch (IOException e) {
            throw new RuntimeException("Upload file thất bại", e);
        }
    }

    @Override
    public byte[] loadFile(Long fileId) {

        try {
            GuaranteeLetterFileEntity entity = repository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy file thư bảo lãnh"));

            Path path = Paths.get(entity.getFilePath());

            if (!Files.exists(path)) {
                throw new RuntimeException("File không tồn tại trên NAS");
            }

            return Files.readAllBytes(path);

        } catch (IOException e) {
            throw new RuntimeException("Không đọc được file thư bảo lãnh", e);
        }
    }

    // =========================================================
    // Metadata
    // =========================================================
    @Override
    public GuaranteeLetterFileDTO getMeta(Long fileId) {

        GuaranteeLetterFileEntity entity = repository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy metadata"));

        return mapper.toDto(entity);
    }

    // =========================================================
// Delete file (NAS / SAN + DB)
// =========================================================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long fileId) {

        GuaranteeLetterFileEntity entity = repository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy file"));

        Path filePath = Paths.get(entity.getFilePath());

        try {
            // ===== 1. Delete physical file on NAS / SAN =====
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            } else {
                throw new RuntimeException("File vật lý không tồn tại trên NAS/SAN");
            }

            // ===== 2. Delete DB record =====
            repository.delete(entity);

        } catch (IOException e) {
            throw new RuntimeException("Xóa file trên NAS/SAN thất bại", e);
        }
    }

    // =========================================================
    // Validate
    // =========================================================
    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File không hợp lệ");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new RuntimeException("Chỉ cho phép file PDF");
        }
    }

    // =========================================================
    // SHA256 Hash
    // =========================================================
    private String calculateHash(byte[] data) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(data));
        } catch (Exception e) {
            throw new RuntimeException("Không tạo được hash");
        }
    }
}
