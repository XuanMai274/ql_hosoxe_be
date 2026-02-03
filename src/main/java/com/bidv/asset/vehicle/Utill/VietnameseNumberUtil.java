package com.bidv.asset.vehicle.Utill;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class VietnameseNumberUtil {

    private VietnameseNumberUtil() {}

    private static final String[] DIGITS = {
            "không", "một", "hai", "ba", "bốn",
            "năm", "sáu", "bảy", "tám", "chín"
    };

    private static final String[] UNITS = {
            "", "nghìn", "triệu", "tỷ",
            "nghìn tỷ", "triệu tỷ", "tỷ tỷ"
    };

    /* =====================================================
     * ================= PUBLIC API ========================
     * ===================================================== */

    /** Dùng cho BigDecimal (chuẩn nghiệp vụ) */
    public static String toVietnamese(BigDecimal value) {
        if (value == null) return "";
        long number = value.setScale(0, RoundingMode.HALF_UP).longValue();
        return toVietnamese(number);
    }

    /** Dùng cho long */
    public static String toVietnamese(long number) {
        if (number == 0) return "Không đồng";

        boolean negative = number < 0;
        long abs = Math.abs(number);

        String text = readNumber(abs);

        text = normalize(text);

        if (negative) {
            text = "Âm " + text;
        }

        return capitalize(text) + " đồng";
    }

    /* =====================================================
     * ================= CORE LOGIC ========================
     * ===================================================== */

    private static String readNumber(long number) {
        StringBuilder sb = new StringBuilder();
        int unitIndex = 0;

        while (number > 0) {
            int block = (int) (number % 1000);
            if (block != 0) {
                String blockText = readThreeDigits(block);
                if (!blockText.isEmpty()) {
                    sb.insert(0, blockText + " " + UNITS[unitIndex] + " ");
                }
            }
            number /= 1000;
            unitIndex++;
        }

        return sb.toString();
    }

    /**
     * Đọc số từ 0 → 999
     */
    private static String readThreeDigits(int number) {
        int hundred = number / 100;
        int ten = (number % 100) / 10;
        int unit = number % 10;

        StringBuilder sb = new StringBuilder();

        if (hundred > 0) {
            sb.append(DIGITS[hundred]).append(" trăm");
        }

        if (ten > 1) {
            appendSpace(sb);
            sb.append(DIGITS[ten]).append(" mươi");
            if (unit == 1) {
                appendSpace(sb);
                sb.append("mốt");
            } else if (unit == 5) {
                appendSpace(sb);
                sb.append("lăm");
            } else if (unit > 0) {
                appendSpace(sb);
                sb.append(DIGITS[unit]);
            }
        } else if (ten == 1) {
            appendSpace(sb);
            sb.append("mười");
            if (unit == 5) {
                appendSpace(sb);
                sb.append("lăm");
            } else if (unit > 0) {
                appendSpace(sb);
                sb.append(DIGITS[unit]);
            }
        } else if (ten == 0 && unit > 0) {
            if (hundred > 0) {
                appendSpace(sb);
                sb.append("linh");
            }
            appendSpace(sb);
            sb.append(DIGITS[unit]);
        }

        return sb.toString();
    }

    /* =====================================================
     * ================= STRING UTIL =======================
     * ===================================================== */

    private static void appendSpace(StringBuilder sb) {
        if (sb.length() > 0) sb.append(" ");
    }

    /** Chuẩn hóa dấu cách */
    private static String normalize(String text) {
        return text.replaceAll("\\s+", " ").trim();
    }

    /** Viết hoa chữ cái đầu */
    private static String capitalize(String text) {
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}
