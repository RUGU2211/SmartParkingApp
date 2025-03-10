package com.smartparking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.smartparking.R;
import com.smartparking.database.AppDatabase;
import com.smartparking.database.dao.UserDao;
import com.smartparking.models.User;
import com.smartparking.utils.EmailService;
import com.smartparking.utils.OTPGenerator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignupActivity extends AppCompatActivity {
    private TextInputLayout nameLayout, emailLayout, passwordLayout, confirmPasswordLayout;
    private TextInputEditText nameInput, emailInput, passwordInput, confirmPasswordInput;
    private MaterialButton signupButton;
    private TextView loginLink;
    private ProgressBar progressBar;
    private UserDao userDao;
    private EmailService emailService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Room Database
        userDao = AppDatabase.getInstance(this).userDao();
        emailService = new EmailService();

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        
        signupButton = findViewById(R.id.signupButton);
        loginLink = findViewById(R.id.loginLink);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        signupButton.setOnClickListener(v -> handleSignup());
        
        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void handleSignup() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        // Reset errors
        nameLayout.setError(null);
        emailLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);

        // Validate inputs
        if (!validateInputs(name, email, password, confirmPassword)) {
            return;
        }

        // Show progress
        setLoading(true);

        // Check if email already exists
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                boolean emailExists = userDao.isEmailExists(email);
                
                if (emailExists) {
                    runOnUiThread(() -> {
                        emailLayout.setError(getString(R.string.error_email_exists));
                        setLoading(false);
                    });
                    return;
                }

                // Generate OTP
                String otp = OTPGenerator.generateOTP();
                
                // Create new user
                User newUser = new User(email, password, name);
                newUser.setVerificationOTP(otp);
                newUser.setOtpGeneratedTime(System.currentTimeMillis());
                
                // Send OTP email
                boolean emailSent = emailService.sendOTP(email, otp);
                
                if (emailSent) {
                    // Save user to database
                    userDao.insert(newUser);
                    
                    runOnUiThread(() -> {
                        Toast.makeText(SignupActivity.this, 
                            getString(R.string.otp_sent), Toast.LENGTH_SHORT).show();
                            
                        // Navigate to OTP verification
                        Intent intent = new Intent(SignupActivity.this, VerifyOTPActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(SignupActivity.this, 
                            "Failed to send OTP email", Toast.LENGTH_SHORT).show();
                        setLoading(false);
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(SignupActivity.this, 
                        "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    setLoading(false);
                });
            }
        });
    }

    private boolean validateInputs(String name, String email, String password, String confirmPassword) {
        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            nameLayout.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError(getString(R.string.error_invalid_email));
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError(getString(R.string.error_invalid_password));
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordLayout.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError(getString(R.string.error_passwords_not_match));
            isValid = false;
        }

        return isValid;
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        signupButton.setEnabled(!isLoading);
        nameInput.setEnabled(!isLoading);
        emailInput.setEnabled(!isLoading);
        passwordInput.setEnabled(!isLoading);
        confirmPasswordInput.setEnabled(!isLoading);
    }
}
