package com.bidv.asset.vehicle.Utill;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyUtil {
    public static final int MONEY_SCALE = 2;
    public static final RoundingMode MONEY_ROUND = RoundingMode.HALF_UP;

    /**
     * Chuẩn hóa số tiền theo chuẩn ngân hàng (2 số thập phân, làm tròn nửa lên)
     */
    public static BigDecimal format(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, MONEY_ROUND);
        }
        return value.setScale(MONEY_SCALE, MONEY_ROUND);
    }

    /**
     * Chuẩn hóa tỷ lệ (Rate) - thường dùng 4 hoặc 2 số thập phân.
     * Tạm thời để 2 theo yêu cầu gần nhất của người dùng, hoặc 4 để an toàn hơn.
     * Theo yêu cầu "một chuẩn duy nhất", ta sẽ dùng chung format nếu người dùng muốn tuyệt đối.
     */
    public static BigDecimal rate(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, MONEY_ROUND);
        }
        return value.setScale(2, MONEY_ROUND);
    }
}
