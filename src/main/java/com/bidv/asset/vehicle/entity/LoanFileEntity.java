package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "loan_files",
        indexes = {
                @Index(name = "idx_loan_file_loan", columnList = "loan_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_file_id_seq")
    @SequenceGenerator(name = "loan_file_id_seq", sequenceName = "loan_file_id_seq")
    private Long id;

    private String fileName;
    private String filePath;   // đường dẫn lưu trữ
    private String fileType;   // LOAN_CONTRACT, GUARANTEE, APPENDIX...
    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanEntity loan;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime uploadedAt;
}
