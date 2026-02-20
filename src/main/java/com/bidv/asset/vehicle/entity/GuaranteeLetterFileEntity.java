package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "guarantee_letter_file",
        indexes = {
                @Index(name = "idx_gl_file_gl", columnList = "guarantee_letter_id")
        })
@AllArgsConstructor
@NoArgsConstructor
public class GuaranteeLetterFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guarantee_letter_file_id_seq")
    @SequenceGenerator(
            name = "guarantee_letter_file_id_seq",
            sequenceName = "guarantee_letter_file_id_seq",
            allocationSize = 1
    )
    private Long id;

    // =========================
    // FK -> Guarantee Letter
    // =========================
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guarantee_letter_id", nullable = false, unique = true)
    private GuaranteeLetterEntity guaranteeLetter;

    // =========================
    // File Information
    // =========================
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;   // đường dẫn NAS / Object Storage

    @Column(name = "file_type")
    private String fileType;   // PDF

    @Column(name = "file_size")
    private Long fileSize;

    // Hash để kiểm soát chỉnh sửa file
    @Column(name = "file_hash")
    private String fileHash;

    // Version file (phòng mở rộng)
    @Column(name = "version")
    private Integer version = 1;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
