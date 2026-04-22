package com.isms.identity.util;
public class MaskingUtils {

    private MaskingUtils() {}

    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) return null;

        String cleaned = phoneNumber.trim();
        String prefix  = cleaned.startsWith("+") ? "+" : "";
        String digits  = cleaned.replaceAll("[^0-9]", "");

        if (digits.length() < 6) return "****";

        String visible = digits.substring(digits.length() - 4);
        String masked  = "*".repeat(digits.length() - 4);

        return prefix + masked + visible;
    }
}