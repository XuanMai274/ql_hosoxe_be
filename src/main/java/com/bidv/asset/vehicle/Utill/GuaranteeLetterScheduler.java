package com.bidv.asset.vehicle.Utill;

import com.bidv.asset.vehicle.Repository.GuaranteeLetterRepository;
import com.bidv.asset.vehicle.entity.GuaranteeLetterEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler tự động chuyển trạng thái GuaranteeLetter sang EXPIRED
 * khi expiryDate đã qua (expiryDate < ngày hiện tại) và status vẫn là ACTIVE.
 *
 * Lịch chạy: mỗi ngày lúc 01:00 sáng (cron = "0 0 1 * * *")
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GuaranteeLetterScheduler {

    private final GuaranteeLetterRepository guaranteeLetterRepository;
    private final com.bidv.asset.vehicle.Repository.CreditContractRepository creditContractRepository;

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    /**
     * Job tự động hết hạn thư bảo lãnh.
     * Cron: giây phút giờ ngày tháng thứ → "0 0 1 * * *" = 01:00:00 mỗi ngày
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void autoExpireGuaranteeLetters() {
        LocalDate today = LocalDate.now();
        log.info("[GuaranteeScheduler] Bắt đầu kiểm tra thư bảo lãnh hết hạn - ngày: {}", today);

        List<GuaranteeLetterEntity> expiredList =
                guaranteeLetterRepository.findExpiredActiveGuarantees(today);

        if (expiredList.isEmpty()) {
            log.info("[GuaranteeScheduler] Không có thư bảo lãnh nào cần chuyển sang EXPIRED.");
            return;
        }

        log.info("[GuaranteeScheduler] Tìm thấy {} thư bảo lãnh cần chuyển EXPIRED.", expiredList.size());

        for (GuaranteeLetterEntity gl : expiredList) {
            BigDecimal remaining = nvl(gl.getRemainingAmount());
            
            // Nếu còn dư hạn mức bảo lãnh chưa dùng hết
            if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                com.bidv.asset.vehicle.entity.CreditContractEntity cc = gl.getCreditContract();
                if (cc != null) {
                    log.info("[GuaranteeScheduler] Hoàn trả hạn mức - GL id={} | amount={}", gl.getId(), remaining);
                    
                    // 1. Giảm dư bảo lãnh phát hành
                    BigDecimal newIssuedBalance = nvl(cc.getIssuedGuaranteeBalance()).subtract(remaining);
                    cc.setIssuedGuaranteeBalance(newIssuedBalance);
                    
                    // 2. Tính toán lại Used Limit: = Dư BL phát hành + Dư nợ vay xe + Dư vay BĐS
                    BigDecimal newUsedLimit = newIssuedBalance
                            .add(nvl(cc.getVehicleLoanBalance()))
                            .add(nvl(cc.getRealEstateLoanBalance()));
                    cc.setUsedLimit(newUsedLimit);
                    
                    // 3. Tính toán lại Remaining Limit = Tổng hạn mức - Used Limit
                    cc.setRemainingLimit(nvl(cc.getCreditLimit()).subtract(newUsedLimit));
                    
                    // 4. Update Outstanding Guarantee Amount
                    cc.setOutstandingGuaranteeAmount(newIssuedBalance.subtract(nvl(cc.getGuaranteeBalance())));
                    
                    cc.setUpdatedAt(LocalDateTime.now());
                    creditContractRepository.save(cc);
                }
                // Cuối cùng cho giá trị trong remaining_amount về 0.0
                gl.setRemainingAmount(BigDecimal.ZERO);
            }

            gl.setStatus("EXPIRED");
            gl.setUpdatedAt(LocalDateTime.now());
            log.info("[GuaranteeScheduler] Chuyển EXPIRED - id={} | contractNumber={} | expiryDate={}",
                    gl.getId(),
                    gl.getGuaranteeContractNumber(),
                    gl.getExpiryDate());
        }

        guaranteeLetterRepository.saveAll(expiredList);
        log.info("[GuaranteeScheduler] Hoàn thành: đã chuyển {} thư bảo lãnh sang EXPIRED.", expiredList.size());
    }
}
