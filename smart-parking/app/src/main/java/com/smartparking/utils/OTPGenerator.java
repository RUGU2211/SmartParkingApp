package com.smartparking.utils;

import java.security.SecureRandom;

public class OTPGenerator {
    private static final int OTP_LENGTH = 6;
    private static final String OTP_CHARS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateOTP() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(OTP_CHARS.charAt(RANDOM.nextInt(OTP_CHARS.length())));
        }
        return otp.toString();
    }

    public static boolean isOTPValid(String otp) {
        if (otp == null || otp.length() != OTP_LENGTH) {
            return false;
        }
        // Check if OTP contains only digits
        try {
            Long.parseLong(otp);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isOTPExpired(long otpGeneratedTime) {
        // OTP expires after 5 minutes
        long currentTime = System.currentTimeMillis();
        long otpValidityDuration = 5 * 60 * 1000; // 5 minutes in milliseconds
        return (currentTime - otpGeneratedTime) > otpValidityDuration;
    }
}
