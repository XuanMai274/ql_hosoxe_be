package com.bidv.asset.vehicle.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GuaranteeLetterDTO {
    private Long id;
    private Long creditContractId;
    // ===== GUARANTEE CONTRACT =====
    private String guaranteeContractNumber;
    private LocalDate guaranteeContractDate;
    private String guaranteeNoticeNumber;
    private LocalDate guaranteeNoticeDate;
    private String referenceCode;
    // ===== GUARANTEE AMOUNT =====
    private BigDecimal expectedGuaranteeAmount; // số tiền bảo lãnh dự kiến ban đầu được nhập khi tạo bảo lãnh
    private BigDecimal totalGuaranteeAmount; // tổng số tiền bảo lãnh thực tế(được cộng thêm hoặc trừ đi khi nhập/xuất xe)
    private BigDecimal usedAmount; // số tiền đã sử dụng
    private BigDecimal remainingAmount; // còn lại
    // ===== VEHICLE COUNT =====
    private Integer expectedVehicleCount;// số xe dự kiến
    private Integer importedVehicleCount;// số xe đã nhập
    private Integer exportedVehicleCount;// số xe đã xuất
    // ===== SALE CONTRACT (HỢP ĐỒNG MUA BÁN) =====
    private String saleContract;          // Tên HĐ mua bán
    private BigDecimal saleContractAmount;      // Giá trị HĐ mua bán (tổng tiền hóa đơn)
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
