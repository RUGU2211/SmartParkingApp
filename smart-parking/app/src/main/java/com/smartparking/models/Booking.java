package com.smartparking.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "bookings",
        foreignKeys = {
            @ForeignKey(entity = User.class,
                    parentColumns = "email",
                    childColumns = "userEmail",
                    onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = ParkingSlot.class,
                    parentColumns = "id",
                    childColumns = "slotId",
                    onDelete = ForeignKey.CASCADE)
        })
public class Booking {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String userEmail;
    private int slotId;
    private long startTime;
    private long endTime;
    private double amount;
    private String status; // PENDING, CONFIRMED, CANCELLED, COMPLETED
    private String paymentStatus; // PENDING, PAID, REFUNDED
    private String paymentMethod;
    private String transactionId;
    private long bookingTime;
    private long paymentTime;

    public Booking(String userEmail, int slotId, long startTime, long endTime, double amount) {
        this.userEmail = userEmail;
        this.slotId = slotId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.amount = amount;
        this.status = "PENDING";
        this.paymentStatus = "PENDING";
        this.bookingTime = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public long getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(long bookingTime) {
        this.bookingTime = bookingTime;
    }

    public long getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(long paymentTime) {
        this.paymentTime = paymentTime;
    }
}
