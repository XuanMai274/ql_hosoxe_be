package com.bidv.asset.vehicle.Utill;

import com.bidv.asset.vehicle.Repository.GuaranteeLetterRepository;
import com.bidv.asset.vehicle.entity.GuaranteeLetterEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
