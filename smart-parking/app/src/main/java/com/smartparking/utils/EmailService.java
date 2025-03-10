package com.smartparking.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {
    private static final String TAG = "EmailService";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_FROM = "your-app-email@gmail.com"; // Replace with your email
    private static final String EMAIL_PASSWORD = "your-app-password"; // Replace with your app password

    public boolean sendOTP(String toEmail, String otp) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
                }
            });

            // For development/testing, you can enable debug mode
            session.setDebug(true);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Smart Parking App - Email Verification");

            // Create HTML content for the email
            String htmlContent = String.format(
                "<div style='font-family: Arial, sans-serif; padding: 20px;'>" +
                "<h2 style='color: #1976D2;'>Email Verification</h2>" +
                "<p>Thank you for registering with Smart Parking App. Please use the following OTP to verify your email address:</p>" +
                "<h1 style='color: #1976D2; font-size: 32px; letter-spacing: 5px; margin: 20px 0;'>%s</h1>" +
                "<p>This OTP will expire in 5 minutes.</p>" +
                "<p style='color: #757575; font-size: 12px;'>If you didn't request this verification, please ignore this email.</p>" +
                "</div>",
                otp
            );

            message.setContent(htmlContent, "text/html; charset=utf-8");

            // Send email in background thread
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        Transport.send(message);
                        return true;
                    } catch (MessagingException e) {
                        Log.e(TAG, "Failed to send email", e);
                        return false;
                    }
                }
            }.execute();

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error setting up email", e);
            return false;
        }
    }

    // For development/testing purposes
    public boolean sendTestEmail(String toEmail) {
        String testOTP = OTPGenerator.generateOTP();
        return sendOTP(toEmail, testOTP);
    }

    // Helper method to validate email format
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Basic email validation
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Method to send a welcome email after successful verification
    public boolean sendWelcomeEmail(String toEmail, String userName) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Welcome to Smart Parking App");

            String htmlContent = String.format(
                "<div style='font-family: Arial, sans-serif; padding: 20px;'>" +
                "<h2 style='color: #1976D2;'>Welcome to Smart Parking App!</h2>" +
                "<p>Dear %s,</p>" +
                "<p>Thank you for joining Smart Parking App. Your account has been successfully verified.</p>" +
                "<p>You can now:</p>" +
                "<ul>" +
                "<li>Search for available parking spots</li>" +
                "<li>Book parking slots in advance</li>" +
                "<li>Manage your bookings</li>" +
                "<li>Make secure payments</li>" +
                "</ul>" +
                "<p>If you have any questions, feel free to contact our support team.</p>" +
                "<p>Best regards,<br>Smart Parking Team</p>" +
                "</div>",
                userName
            );

            message.setContent(htmlContent, "text/html; charset=utf-8");

            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        Transport.send(message);
                        return true;
                    } catch (MessagingException e) {
                        Log.e(TAG, "Failed to send welcome email", e);
                        return false;
                    }
                }
            }.execute();

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error sending welcome email", e);
            return false;
        }
    }
}
