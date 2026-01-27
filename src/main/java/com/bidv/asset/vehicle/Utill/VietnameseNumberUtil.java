package com.bidv.asset.vehicle.Utill;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class VietnameseNumberUtil {

    private static final String[] NUMBERS = {
            "không", "một", "hai", "ba", "bốn",
            "năm", "sáu", "bảy", "tám", "chín"
    };

    public static String toVietnamese(BigDecimal amount) {
        if (amount == null) return "";

        long number = amount.setScale(0, RoundingMode.HALF_UP).longValue();

        if (number == 0) return "Không đồng";

        String result = readNumber(number).trim();

        // Viết hoa chữ cái đầu, chuẩn văn bản hành chính
        result = Character.toUpperCase(result.charAt(0)) + result.substring(1);

        return result + " đồng";
    }

    private static String readNumber(long number) {
        if (number == 0) return "";

        StringBuilder sb = new StringBuilder();

        long billion = number / 1_000_000_000;
        long million = (number % 1_000_000_000) / 1_000_000;
        long thousand = (number % 1_000_000) / 1_000;
        long rest = number % 1_000;

        if (billion > 0) {
            sb.append(readThreeDigits(billion)).append(" tỷ ");
        }
        if (million > 0) {
            sb.append(readThreeDigits(million)).append(" triệu ");
        }
        if (thousand > 0) {
            sb.append(readThreeDigits(thousand)).append(" nghìn ");
        }
        if (rest > 0) {
            sb.append(readThreeDigits(rest));
        }

        return sb.toString();
    }

    private static String readThreeDigits(long number) {
        long hundred = number / 100;
        long ten = (number % 100) / 10;
        long unit = number % 10;

        StringBuilder sb = new StringBuilder();

        if (hundred > 0) {
            sb.append(NUMBERS[(int) hundred]).append(" trăm ");
        }

        if (ten > 1) {
            sb.append(NUMBERS[(int) ten]).append(" mươi ");
            if (unit == 1) sb.append("mốt ");
            else if (unit == 5) sb.append("lăm ");
            else if (unit > 0) sb.append(NUMBERS[(int) unit]).append(" ");
        } else if (ten == 1) {
            sb.append("mười ");
            if (unit == 5) sb.append("lăm ");
            else if (unit > 0) sb.append(NUMBERS[(int) unit]).append(" ");
        } else if (ten == 0 && unit > 0) {
            sb.append(NUMBERS[(int) unit]).append(" ");
        }

        return sb.toString();
    }
}
