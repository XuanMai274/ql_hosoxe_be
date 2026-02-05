package com.bidv.asset.vehicle.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GuaranteeLetterFileDTO {

    private Long id;

    // FK logic tới thư bảo lãnh
    private Long guaranteeLetterId;

    // ===== FILE METADATA =====
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;

    // Kiểm soát toàn vẹn file
    private String fileHash;

    // Versioning
    private Integer version;

    private Boolean isActive;

    private LocalDateTime createdAt;
}
