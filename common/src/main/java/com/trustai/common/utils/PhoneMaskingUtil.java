package com.trustai.common.utils;

public class PhoneMaskingUtil {
    public static String maskPhoneNumber(String phoneNumber) {
        if ( phoneNumber == null || phoneNumber.length() < 10) {
            return phoneNumber;
        }
        if (phoneNumber == null || phoneNumber.length() != 10 || !phoneNumber.matches("\\d{10}")) {
            throw new IllegalArgumentException("Invalid phone number. Must be a 10-digit number.");
        }

        return phoneNumber.substring(0, 2) + "******" + phoneNumber.substring(8);
    }

    public static void main(String[] args) {
        String phone = "9876543210";
        String masked = maskPhoneNumber(phone);
        System.out.println(masked); // Output: 98******10
    }
}
