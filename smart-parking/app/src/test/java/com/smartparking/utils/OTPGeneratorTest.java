package com.smartparking.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class OTPGeneratorTest {

    @Test
    public void generateOTP_ReturnsCorrectLength() {
        String otp = OTPGenerator.generateOTP();
        assertEquals("OTP should be 6 digits long", 6, otp.length());
    }

    @Test
    public void generateOTP_ContainsOnlyDigits() {
        String otp = OTPGenerator.generateOTP();
        assertTrue("OTP should contain only digits", otp.matches("\\d+"));
    }

    @Test
    public void isOTPValid_ValidOTP_ReturnsTrue() {
        assertTrue(OTPGenerator.isOTPValid("123456"));
    }

    @Test
    public void isOTPValid_InvalidLength_ReturnsFalse() {
        assertFalse(OTPGenerator.isOTPValid("12345")); // Too short
        assertFalse(OTPGenerator.isOTPValid("1234567")); // Too long
    }

    @Test
    public void isOTPValid_NonDigits_ReturnsFalse() {
        assertFalse(OTPGenerator.isOTPValid("12a456")); // Contains letter
        assertFalse(OTPGenerator.isOTPValid("12#456")); // Contains special character
    }

    @Test
    public void isOTPValid_NullOTP_ReturnsFalse() {
        assertFalse(OTPGenerator.isOTPValid(null));
    }

    @Test
    public void isOTPExpired_NotExpired_ReturnsFalse() {
        long currentTime = System.currentTimeMillis();
        assertFalse(OTPGenerator.isOTPExpired(currentTime));
    }

    @Test
    public void isOTPExpired_Expired_ReturnsTrue() {
        long expiredTime = System.currentTimeMillis() - (6 * 60 * 1000); // 6 minutes ago
        assertTrue(OTPGenerator.isOTPExpired(expiredTime));
    }

    @Test
    public void generateOTP_UniqueValues() {
        String otp1 = OTPGenerator.generateOTP();
        String otp2 = OTPGenerator.generateOTP();
        assertNotEquals("Generated OTPs should be unique", otp1, otp2);
    }

    @Test
    public void isOTPValid_EmptyString_ReturnsFalse() {
        assertFalse(OTPGenerator.isOTPValid(""));
    }
}
