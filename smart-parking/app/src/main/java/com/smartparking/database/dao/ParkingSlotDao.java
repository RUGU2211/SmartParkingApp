package com.smartparking.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.smartparking.models.ParkingSlot;

import java.util.List;

@Dao
public interface ParkingSlotDao {
    @Insert
    long insert(ParkingSlot parkingSlot);

    @Update
    void update(ParkingSlot parkingSlot);

    @Delete
    void delete(ParkingSlot parkingSlot);

    @Query("SELECT * FROM parking_slots")
    LiveData<List<ParkingSlot>> getAllParkingSlots();

    @Query("SELECT * FROM parking_slots WHERE isAvailable = 1")
    LiveData<List<ParkingSlot>> getAvailableParkingSlots();

    @Query("SELECT * FROM parking_slots WHERE id = :slotId")
    ParkingSlot getParkingSlotById(int slotId);

    @Query("UPDATE parking_slots SET isAvailable = :isAvailable WHERE id = :slotId")
    void updateSlotAvailability(int slotId, boolean isAvailable);

    @Query("SELECT * FROM parking_slots WHERE location LIKE '%' || :searchQuery || '%'")
    LiveData<List<ParkingSlot>> searchParkingSlots(String searchQuery);

    @Query("SELECT COUNT(*) FROM parking_slots WHERE isAvailable = 1")
    int getAvailableSlotsCount();

    // For initial data population
    @Query("SELECT COUNT(*) FROM parking_slots")
    int getTotalSlotsCount();

    // Batch insert for initial data
    @Insert
    void insertAll(List<ParkingSlot> parkingSlots);

    // Clear all slots (useful for testing or resetting)
    @Query("DELETE FROM parking_slots")
    void deleteAll();

    // Get slots by price range
    @Query("SELECT * FROM parking_slots WHERE price BETWEEN :minPrice AND :maxPrice AND isAvailable = 1")
    LiveData<List<ParkingSlot>> getSlotsByPriceRange(double minPrice, double maxPrice);

    // Get slots near a location (simplified distance calculation)
    @Query("SELECT * FROM parking_slots WHERE " +
           "ABS(latitude - :targetLat) < :radius AND " +
           "ABS(longitude - :targetLong) < :radius AND " +
           "isAvailable = 1")
    LiveData<List<ParkingSlot>> getNearbySlots(double targetLat, double targetLong, double radius);
}
