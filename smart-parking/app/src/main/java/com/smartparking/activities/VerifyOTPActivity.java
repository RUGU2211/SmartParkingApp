package com.smartparking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.smartparking.R;
import com.smartparking.database.AppDatabase;
import com.smartparking.database.dao.UserDao;
import com.smartparking.models.User;
import com.smartparking.utils.EmailService;
import com.smartparking.utils.OTPGenerator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VerifyOTPActivity extends AppCompatActivity {
    private EditText[] otpDigits = new EditText[6];
    private TextView emailText, timerText;
    private MaterialButton verifyButton, resendButton;
    private ProgressBar progressBar;
    private String userEmail;
    private UserDao userDao;
    private EmailService emailService;
    private CountDownTimer resendTimer;
    private static final long TIMER_DURATION = 300000; // 5 minutes in milliseconds
    private static final long TIMER_INTERVAL = 1000; // 1 second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null) {
            finish();
            return;
        }

        userDao = AppDatabase.getInstance(this).userDao();
        emailService = new EmailService();

        initializeViews();
        setupOTPInputs();
        setupClickListeners();
        startResendTimer();

        emailText.setText(userEmail);
    }

    private void initializeViews() {
        otpDigits[0] = findViewById(R.id.otpDigit1);
        otpDigits[1] = findViewById(R.id.otpDigit2);
        otpDigits[2] = findViewById(R.id.otpDigit3);
        otpDigits[3] = findViewById(R.id.otpDigit4);
        otpDigits[4] = findViewById(R.id.otpDigit5);
        otpDigits[5] = findViewById(R.id.otpDigit6);
        
        emailText = findViewById(R.id.emailText);
        timerText = findViewById(R.id.timerText);
        verifyButton = findViewById(R.id.verifyButton);
        resendButton = findViewById(R.id.resendButton);
        progressBar = findViewById(R.id.progressBar);
        
        resendButton.setEnabled(false);
    }

    private void setupOTPInputs() {
        for (int i = 0; i < otpDigits.length; i++) {
            final int currentIndex = i;
            otpDigits[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && currentIndex < otpDigits.length - 1) {
                        otpDigits[currentIndex + 1].requestFocus();
                    } else if (s.length() == 0 && currentIndex > 0) {
                        otpDigits[currentIndex - 1].requestFocus();
                    }
                    
                    // Enable verify button if all digits are filled
                    verifyButton.setEnabled(isOTPComplete());
                }
            });
        }
    }

    private void setupClickListeners() {
        verifyButton.setOnClickListener(v -> verifyOTP());
        resendButton.setOnClickListener(v -> resendOTP());
    }

    private void startResendTimer() {
        resendButton.setEnabled(false);
        if (resendTimer != null) {
            resendTimer.cancel();
        }

        resendTimer = new CountDownTimer(TIMER_DURATION, TIMER_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                timerText.setText(String.format("Resend OTP in %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                timerText.setText("OTP expired");
                resendButton.setEnabled(true);
            }
        }.start();
    }

    private boolean isOTPComplete() {
        for (EditText digit : otpDigits) {
            if (digit.getText().toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String getEnteredOTP() {
        StringBuilder otp = new StringBuilder();
        for (EditText digit : otpDigits) {
            otp.append(digit.getText().toString());
        }
        return otp.toString();
    }

    private void verifyOTP() {
        String enteredOTP = getEnteredOTP();
        setLoading(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                User user = userDao.verifyOTP(userEmail, enteredOTP);
                
                if (user != null) {
                    if (OTPGenerator.isOTPExpired(user.getOtpGeneratedTime())) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "OTP has expired. Please request a new one.", 
                                Toast.LENGTH_SHORT).show();
                            setLoading(false);
                        });
                        return;
                    }

                    // Update user verification status
                    userDao.updateEmailVerification(userEmail, true);
                    
                    // Send welcome email
                    emailService.sendWelcomeEmail(userEmail, user.getName());

                    runOnUiThread(() -> {
                        Toast.makeText(this, getString(R.string.otp_verified), 
                            Toast.LENGTH_SHORT).show();
                        navigateToLogin();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, getString(R.string.invalid_otp), 
                            Toast.LENGTH_SHORT).show();
                        setLoading(false);
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Verification failed: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                    setLoading(false);
                });
            }
        });
    }

    private void resendOTP() {
        setLoading(true);
        String newOTP = OTPGenerator.generateOTP();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // Update OTP in database
                userDao.updateOTP(userEmail, newOTP, System.currentTimeMillis());
                
                // Send new OTP email
                boolean emailSent = emailService.sendOTP(userEmail, newOTP);
                
                runOnUiThread(() -> {
                    if (emailSent) {
                        Toast.makeText(this, "New OTP sent to your email", 
                            Toast.LENGTH_SHORT).show();
                        startResendTimer();
                        clearOTPFields();
                    } else {
                        Toast.makeText(this, "Failed to send OTP. Please try again.", 
                            Toast.LENGTH_SHORT).show();
                    }
                    setLoading(false);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Failed to resend OTP: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                    setLoading(false);
                });
            }
        });
    }

    private void clearOTPFields() {
        for (EditText digit : otpDigits) {
            digit.setText("");
        }
        otpDigits[0].requestFocus();
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        verifyButton.setEnabled(!isLoading && isOTPComplete());
        resendButton.setEnabled(!isLoading && !resendTimer.isRunning());
        
        for (EditText digit : otpDigits) {
            digit.setEnabled(!isLoading);
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(VerifyOTPActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resendTimer != null) {
            resendTimer.cancel();
        }
    }
}
