package com.smartparking.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.smartparking.R;
import com.smartparking.database.AppDatabase;
import com.smartparking.database.dao.BookingDao;
import com.smartparking.database.dao.ParkingSlotDao;
import com.smartparking.models.Booking;
import com.smartparking.models.ParkingSlot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BookingActivity extends AppCompatActivity {

    private TextView slotNumberText, locationText, priceText;
    private TextInputEditText startTimeInput, endTimeInput;
    private TextView durationText, parkingFeeText, totalCostText;
    private MaterialButton proceedButton;
    private View progressBar;

    private ParkingSlotDao parkingSlotDao;
    private BookingDao bookingDao;
    private ParkingSlot parkingSlot;
    private Calendar startTime, endTime;
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Initialize Room Database
        AppDatabase db = AppDatabase.getInstance(this);
        parkingSlotDao = db.parkingSlotDao();
        bookingDao = db.bookingDao();

        initializeViews();
        setupToolbar();
        loadParkingSlot();
    }

    private void initializeViews() {
        slotNumberText = findViewById(R.id.slotNumberText);
        locationText = findViewById(R.id.locationText);
        priceText = findViewById(R.id.priceText);
        startTimeInput = findViewById(R.id.startTimeInput);
        endTimeInput = findViewById(R.id.endTimeInput);
        durationText = findViewById(R.id.durationText);
        parkingFeeText = findViewById(R.id.parkingFeeText);
        totalCostText = findViewById(R.id.totalCostText);
        proceedButton = findViewById(R.id.proceedButton);
        progressBar = findViewById(R.id.progressBar);

        startTimeInput.setOnClickListener(v -> showDateTimePicker(true));
        endTimeInput.setOnClickListener(v -> showDateTimePicker(false));
        proceedButton.setOnClickListener(v -> proceedToPayment());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.book_slot);
        }
    }

    private void loadParkingSlot() {
        int slotId = getIntent().getIntExtra("slot_id", -1);
        if (slotId == -1) {
            finish();
            return;
        }

        setLoading(true);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            parkingSlot = parkingSlotDao.getParkingSlotById(slotId);
            runOnUiThread(() -> {
                if (parkingSlot != null) {
                    updateSlotDetails();
                } else {
                    Toast.makeText(this, "Failed to load parking slot details", Toast.LENGTH_SHORT).show();
                    finish();
                }
                setLoading(false);
            });
        });
    }

    private void updateSlotDetails() {
        slotNumberText.setText(String.format("Slot %s", parkingSlot.getSlotNumber()));
        locationText.setText(parkingSlot.getLocation());
        priceText.setText(String.format("%s/hour", currencyFormat.format(parkingSlot.getPrice())));
    }

    private void showDateTimePicker(boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        if (isStartTime && startTime != null) {
            calendar = startTime;
        } else if (!isStartTime && endTime != null) {
            calendar = endTime;
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                new TimePickerDialog(
                    this,
                    (view1, hourOfDay, minute) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        if (isStartTime) {
                            startTime = calendar;
                            startTimeInput.setText(dateTimeFormat.format(startTime.getTime()));
                        } else {
                            endTime = calendar;
                            endTimeInput.setText(dateTimeFormat.format(endTime.getTime()));
                        }

                        updateDurationAndCost();
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date as today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateDurationAndCost() {
        if (startTime != null && endTime != null) {
            long durationMillis = endTime.getTimeInMillis() - startTime.getTimeInMillis();
            if (durationMillis <= 0) {
                Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show();
                endTime = null;
                endTimeInput.setText("");
                proceedButton.setEnabled(false);
                return;
            }

            // Calculate duration in hours
            double durationHours = TimeUnit.MILLISECONDS.toHours(durationMillis);
            if (durationHours < 1) {
                durationHours = 1; // Minimum 1 hour
            }

            durationText.setText(String.format(Locale.getDefault(), 
                "Duration: %.1f hours", durationHours));

            // Calculate cost
            double totalCost = durationHours * parkingSlot.getPrice();
            parkingFeeText.setText(currencyFormat.format(totalCost));
            totalCostText.setText(currencyFormat.format(totalCost));

            proceedButton.setEnabled(true);
        }
    }

    private void proceedToPayment() {
        if (startTime == null || endTime == null || parkingSlot == null) {
            return;
        }

        setLoading(true);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // Check if slot is still available
                if (!parkingSlot.isAvailable()) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "This slot is no longer available", 
                            Toast.LENGTH_SHORT).show();
                        setLoading(false);
                        finish();
                    });
                    return;
                }

                // Check for booking conflicts
                boolean hasConflict = bookingDao.hasTimeConflict(parkingSlot.getId(), 
                    startTime.getTimeInMillis(), endTime.getTimeInMillis());
                
                if (hasConflict) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Selected time slot is not available", 
                            Toast.LENGTH_SHORT).show();
                        setLoading(false);
                    });
                    return;
                }

                // Create booking
                Booking booking = new Booking(
                    "user@email.com", // TODO: Get logged-in user's email
                    parkingSlot.getId(),
                    startTime.getTimeInMillis(),
                    endTime.getTimeInMillis(),
                    calculateTotalCost()
                );

                long bookingId = bookingDao.insert(booking);

                runOnUiThread(() -> {
                    Intent intent = new Intent(this, PaymentActivity.class);
                    intent.putExtra("booking_id", bookingId);
                    startActivity(intent);
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Failed to create booking: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                    setLoading(false);
                });
            }
        });
    }

    private double calculateTotalCost() {
        long durationMillis = endTime.getTimeInMillis() - startTime.getTimeInMillis();
        double durationHours = TimeUnit.MILLISECONDS.toHours(durationMillis);
        if (durationHours < 1) {
            durationHours = 1; // Minimum 1 hour
        }
        return durationHours * parkingSlot.getPrice();
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        proceedButton.setEnabled(!isLoading && startTime != null && endTime != null);
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
