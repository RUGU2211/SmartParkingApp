package com.smartparking.utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class EmailServiceTest {

    private EmailService emailService;

    @Mock
    private Session mockSession;

    @Mock
    private Transport mockTransport;

    @Mock
    private MimeMessage mockMessage;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        emailService = new EmailService();
    }

    @Test
    public void testSendOTPEmail() throws MessagingException {
        // Setup test data
        String recipientEmail = "test@example.com";
        String otp = "123456";

        // Mock behavior
        when(mockSession.getTransport("smtp")).thenReturn(mockTransport);
        when(mockSession.createMimeMessage()).thenReturn(mockMessage);
        doNothing().when(mockTransport).connect(any(), any(), any());
        doNothing().when(mockTransport).sendMessage(any(), any());

        // Test email sending
        boolean result = emailService.sendOTPEmail(recipientEmail, otp);

        // Verify
        assertTrue(result);
        verify(mockTransport).connect(any(), any(), any());
        verify(mockTransport).sendMessage(any(), any());
        verify(mockTransport).close();
    }

    @Test
    public void testSendBookingConfirmation() throws MessagingException {
        // Setup test data
        String recipientEmail = "test@example.com";
        String bookingId = "BOOK123";
        String slotNumber = "A1";
        String startTime = "2023-08-01 10:00";
        String endTime = "2023-08-01 12:00";
        double amount = 10.00;

        // Mock behavior
        when(mockSession.getTransport("smtp")).thenReturn(mockTransport);
        when(mockSession.createMimeMessage()).thenReturn(mockMessage);
        doNothing().when(mockTransport).connect(any(), any(), any());
        doNothing().when(mockTransport).sendMessage(any(), any());

        // Test email sending
        boolean result = emailService.sendBookingConfirmation(
            recipientEmail, bookingId, slotNumber, startTime, endTime, amount);

        // Verify
        assertTrue(result);
        verify(mockTransport).connect(any(), any(), any());
        verify(mockTransport).sendMessage(any(), any());
        verify(mockTransport).close();
    }

    @Test
    public void testCreateSession() {
        // Test session creation
        Session session = emailService.createSession();
        
        // Verify
        assertNotNull(session);
        assertEquals("smtp.gmail.com", session.getProperty("mail.smtp.host"));
        assertEquals("587", session.getProperty("mail.smtp.port"));
        assertEquals("true", session.getProperty("mail.smtp.auth"));
        assertEquals("true", session.getProperty("mail.smtp.starttls.enable"));
    }

    @Test
    public void testCreateMessage() throws MessagingException {
        // Setup test data
        String recipientEmail = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";

        // Create message
        Message message = emailService.createMessage(recipientEmail, subject, content);

        // Verify
        assertNotNull(message);
        assertEquals(subject, message.getSubject());
        assertEquals(new InternetAddress(recipientEmail), message.getRecipients(Message.RecipientType.TO)[0]);
        assertTrue(message instanceof MimeMessage);
    }

    @Test
    public void testInvalidEmail() throws MessagingException {
        // Test with invalid email
        boolean result = emailService.sendOTPEmail("invalid-email", "123456");
        
        // Verify
        assertFalse(result);
    }

    @Test
    public void testInvalidOTP() throws MessagingException {
        // Test with invalid OTP
        boolean result = emailService.sendOTPEmail("test@example.com", null);
        
        // Verify
        assertFalse(result);
    }

    @Test
    public void testConnectionFailure() throws MessagingException {
        // Mock connection failure
        when(mockSession.getTransport("smtp")).thenReturn(mockTransport);
        doNothing().when(mockTransport).connect(any(), any(), any());
        when(mockTransport.isConnected()).thenReturn(false);

        // Test email sending
        boolean result = emailService.sendOTPEmail("test@example.com", "123456");

        // Verify
        assertFalse(result);
    }

    @Test
    public void testMessageCreationFailure() throws MessagingException {
        // Mock message creation failure
        when(mockSession.createMimeMessage()).thenThrow(new RuntimeException("Failed to create message"));

        // Test email sending
        boolean result = emailService.sendOTPEmail("test@example.com", "123456");

        // Verify
        assertFalse(result);
    }

    @Test
    public void testSendPaymentConfirmation() throws MessagingException {
        // Setup test data
        String recipientEmail = "test@example.com";
        String bookingId = "BOOK123";
        String transactionId = "TXN456";
        double amount = 10.00;

        // Mock behavior
        when(mockSession.getTransport("smtp")).thenReturn(mockTransport);
        when(mockSession.createMimeMessage()).thenReturn(mockMessage);
        doNothing().when(mockTransport).connect(any(), any(), any());
        doNothing().when(mockTransport).sendMessage(any(), any());

        // Test email sending
        boolean result = emailService.sendPaymentConfirmation(
            recipientEmail, bookingId, transactionId, amount);

        // Verify
        assertTrue(result);
        verify(mockTransport).connect(any(), any(), any());
        verify(mockTransport).sendMessage(any(), any());
        verify(mockTransport).close();
    }
}
