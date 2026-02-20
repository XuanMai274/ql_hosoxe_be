package com.bidv.asset.vehicle.Validation;

import java.util.regex.Pattern;

public final class VinValidator {

    // VIN chuẩn ISO 3779:
    // - Độ dài 17
    // - Không chứa I, O, Q
    // - Chữ + số
    private static final Pattern VIN_PATTERN =
            Pattern.compile("^[A-HJ-NPR-Z0-9]{17}$");

    private VinValidator() {
        // Utility class – không cho new
    }

    /**
     * Chuẩn hóa VIN từ OCR
     * - Uppercase
     * - Remove space
     * - Fix lỗi OCR thường gặp (O→0, I→1, Q→0)
     */
    public static String normalizeVin(String raw) {
        if (raw == null) return null;

        return raw
                .toUpperCase()
                .replaceAll("\\s+", "")
                // OCR common mistakes
                .replace('O', '0')
                .replace('I', '1')
                .replace('Q', '0');
    }

    /**
     * Check VIN hợp lệ
     */
    public static boolean isValidVin(String vin) {
        if (vin == null) return false;
        return VIN_PATTERN.matcher(vin).matches();
    }

    /**
     * Validate & throw nếu sai (dùng trong service layer)
     */
    public static void validateOrThrow(String vin) {
        if (!isValidVin(vin)) {
            throw new IllegalArgumentException(
                    "VIN không hợp lệ: phải đủ 17 ký tự, không chứa I/O/Q"
            );
        }
    }
}
