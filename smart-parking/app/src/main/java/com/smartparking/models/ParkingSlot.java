package com.smartparking.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "parking_slots")
public class ParkingSlot {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String slotNumber;
    private String location;
    private double price;
    private boolean isAvailable;
    private String description;
    private double latitude;
    private double longitude;

    public ParkingSlot(String slotNumber, String location, double price, String description, 
                      double latitude, double longitude) {
        this.slotNumber = slotNumber;
        this.location = location;
        this.price = price;
        this.isAvailable = true;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
