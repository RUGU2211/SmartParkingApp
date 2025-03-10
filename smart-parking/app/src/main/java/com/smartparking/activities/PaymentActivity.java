package com.smartparking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.smartparking.R;
import com.smartparking.database.AppDatabase;
import com.smartparking.database.dao.BookingDao;
import com.smartparking.database.dao.ParkingSlotDao;
import com.smartparking.models.Booking;
import com.smartparking.models.ParkingSlot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaymentActivity extends AppCompatActivity {

    private TextView slotDetailsText, durationText, amountText;
    private TextInputLayout cardNumberLayout, expiryDateLayout, cvvLayout;
    private TextInputEditText cardNumberInput, expiryDateInput, cvvInput;
    private MaterialButton payButton;
    private ProgressBar progressBar;

    private BookingDao bookingDao;
    private ParkingSlotDao parkingSlotDao;
    private Booking booking;
    private ParkingSlot parkingSlot;

    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize Room Database
        AppDatabase db = AppDatabase.getInstance(this);
        bookingDao = db.bookingDao();
        parkingSlotDao = db.parkingSlotDao();

        initializeViews();
        setupToolbar();
        setupInputValidation();
        loadBookingDetails();
    }

    private void initializeViews() {
        slotDetailsText = findViewById(R.id.slotDetailsText);
        durationText = findViewById(R.id.durationText);
        amountText = findViewById(R.id.amountText);
        
        cardNumberLayout = findViewById(R.id.cardNumberLayout);
        expiryDateLayout = findViewById(R.id.expiryDateLayout);
        cvvLayout = findViewById(R.id.cvvLayout);
        
        cardNumberInput = findViewById(R.id.cardNumberInput);
        expiryDateInput = findViewById(R.id.expiryDateInput);
        cvvInput = findViewById(R.id.cvvInput);
        
        payButton = findViewById(R.id.payButton);
        progressBar = findViewById(R.id.progressBar);

        payButton.setOnClickListener(v -> processPayment());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.payment);
        }
    }

    private void setupInputValidation() {
        cardNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateCardNumber(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePayButtonState();
            }
        });

        expiryDateInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 2 && before == 1 && !s.toString().contains("/")) {
                    expiryDateInput.setText(s + "/");
                    expiryDateInput.setSelection(3);
                }
                validateExpiryDate(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePayButtonState();
            }
        });

        cvvInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateCVV(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePayButtonState();
            }
        });
    }

    private void loadBookingDetails() {
        long bookingId = getIntent().getLongExtra("booking_id", -1);
        if (bookingId == -1) {
            finish();
            return;
        }

        setLoading(true);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            booking = bookingDao.getBookingById((int) bookingId);
            if (booking != null) {
                parkingSlot = parkingSlotDao.getParkingSlotById(booking.getSlotId());
                runOnUiThread(() -> {
                    if (parkingSlot != null) {
                        updateBookingDetails();
                    } else {
                        Toast.makeText(this, "Failed to load parking slot details", 
                            Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    setLoading(false);
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Failed to load booking details", 
                        Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void updateBookingDetails() {
        slotDetailsText.setText(String.format("Slot %s - %s", 
            parkingSlot.getSlotNumber(), parkingSlot.getLocation()));
        
        durationText.setText(String.format("%s - %s",
            dateFormat.format(booking.getStartTime()),
            dateFormat.format(booking.getEndTime())));
        
        amountText.setText(currencyFormat.format(booking.getAmount()));
    }

    private void validateCardNumber(String cardNumber) {
        if (cardNumber.length() < 16) {
            cardNumberLayout.setError("Enter valid card number");
        } else {
            cardNumberLayout.setError(null);
        }
    }

    private void validateExpiryDate(String expiryDate) {
        if (expiryDate.length() < 5) {
            expiryDateLayout.setError("Enter valid expiry date");
        } else {
            expiryDateLayout.setError(null);
        }
    }

    private void validateCVV(String cvv) {
        if (cvv.length() < 3) {
            cvvLayout.setError("Enter valid CVV");
        } else {
            cvvLayout.setError(null);
        }
    }

    private void updatePayButtonState() {
        boolean isValid = cardNumberLayout.getError() == null &&
                         expiryDateLayout.getError() == null &&
                         cvvLayout.getError() == null &&
                         cardNumberInput.length() == 16 &&
                         expiryDateInput.length() == 5 &&
                         cvvInput.length() == 3;
        
        payButton.setEnabled(isValid);
    }

    private void processPayment() {
        setLoading(true);

        // Simulate payment processing
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // Simulate network delay
                Thread.sleep(2000);

                // Update booking status
                booking.setPaymentStatus("PAID");
                booking.setPaymentMethod("CREDIT_CARD");
                booking.setTransactionId(UUID.randomUUID().toString());
                booking.setPaymentTime(System.currentTimeMillis());
                booking.setStatus("CONFIRMED");

                // Update parking slot availability
                parkingSlot.setAvailable(false);

                // Save changes to database
                bookingDao.update(booking);
                parkingSlotDao.update(parkingSlot);

                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.payment_success, Toast.LENGTH_SHORT).show();
                    showBookingConfirmation();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Payment failed: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                    setLoading(false);
                });
            }
        });
    }

    private void showBookingConfirmation() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        payButton.setEnabled(!isLoading);
        cardNumberInput.setEnabled(!isLoading);
        expiryDateInput.setEnabled(!isLoading);
        cvvInput.setEnabled(!isLoading);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
