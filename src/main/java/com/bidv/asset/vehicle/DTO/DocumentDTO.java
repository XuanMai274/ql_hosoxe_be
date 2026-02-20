package com.bidv.asset.vehicle.DTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDTO {
    private Long id;
    private Long vehicleId;

    private String fileName;
    private String fileType;
    private String filePath;

    private LocalDate uploadDate;
    private LocalDateTime createdAt;

}
