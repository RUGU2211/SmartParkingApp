package com.smartparking.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.smartparking.database.dao.UserDao;
import com.smartparking.database.dao.ParkingSlotDao;
import com.smartparking.database.dao.BookingDao;
import com.smartparking.models.User;
import com.smartparking.models.ParkingSlot;
import com.smartparking.models.Booking;

@Database(entities = {User.class, ParkingSlot.class, Booking.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "smart_parking_db";
    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract ParkingSlotDao parkingSlotDao();
    public abstract BookingDao bookingDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
        }
        return instance;
    }
}
