package com.smartparking.database;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.smartparking.database.dao.BookingDao;
import com.smartparking.database.dao.ParkingSlotDao;
import com.smartparking.database.dao.UserDao;
import com.smartparking.models.Booking;
import com.smartparking.models.ParkingSlot;
import com.smartparking.models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ParkingDatabaseTest {
    private AppDatabase db;
    private UserDao userDao;
    private ParkingSlotDao parkingSlotDao;
    private BookingDao bookingDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        userDao = db.userDao();
        parkingSlotDao = db.parkingSlotDao();
        bookingDao = db.bookingDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void insertAndGetUser() {
        // Create test user
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("password123");
        user.setPhoneNumber("1234567890");

        // Insert user
        long userId = userDao.insert(user);
        assertTrue(userId > 0);

        // Retrieve user
        User retrievedUser = userDao.getUserByEmail("test@example.com");
        assertNotNull(retrievedUser);
        assertEquals("Test User", retrievedUser.getName());
        assertEquals("1234567890", retrievedUser.getPhoneNumber());
    }

    @Test
    public void insertAndGetParkingSlot() {
        // Create test parking slot
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber("A1");
        slot.setLocation("North Wing");
        slot.setPrice(5.00);
        slot.setAvailable(true);
        slot.setLatitude(37.7749);
        slot.setLongitude(-122.4194);

        // Insert parking slot
        long slotId = parkingSlotDao.insert(slot);
        assertTrue(slotId > 0);

        // Retrieve parking slot
        ParkingSlot retrievedSlot = parkingSlotDao.getParkingSlotById((int) slotId);
        assertNotNull(retrievedSlot);
        assertEquals("A1", retrievedSlot.getSlotNumber());
        assertEquals("North Wing", retrievedSlot.getLocation());
        assertTrue(retrievedSlot.isAvailable());
    }

    @Test
    public void insertAndGetBooking() {
        // Create test user and parking slot
        User user = new User();
        user.setEmail("test@example.com");
        long userId = userDao.insert(user);

        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber("A1");
        long slotId = parkingSlotDao.insert(slot);

        // Create test booking
        Booking booking = new Booking();
        booking.setUserEmail("test@example.com");
        booking.setSlotId((int) slotId);
        booking.setStartTime(System.currentTimeMillis());
        booking.setEndTime(System.currentTimeMillis() + 3600000); // 1 hour later
        booking.setAmount(5.00);
        booking.setStatus("PENDING");

        // Insert booking
        long bookingId = bookingDao.insert(booking);
        assertTrue(bookingId > 0);

        // Retrieve booking
        Booking retrievedBooking = bookingDao.getBookingById((int) bookingId);
        assertNotNull(retrievedBooking);
        assertEquals("test@example.com", retrievedBooking.getUserEmail());
        assertEquals("PENDING", retrievedBooking.getStatus());
    }

    @Test
    public void updateParkingSlotAvailability() {
        // Create and insert parking slot
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber("A1");
        slot.setAvailable(true);
        long slotId = parkingSlotDao.insert(slot);

        // Update availability
        parkingSlotDao.updateAvailability((int) slotId, false);

        // Verify update
        ParkingSlot updatedSlot = parkingSlotDao.getParkingSlotById((int) slotId);
        assertFalse(updatedSlot.isAvailable());
    }

    @Test
    public void getAvailableParkingSlots() {
        // Create and insert multiple parking slots
        ParkingSlot slot1 = new ParkingSlot();
        slot1.setSlotNumber("A1");
        slot1.setAvailable(true);
        parkingSlotDao.insert(slot1);

        ParkingSlot slot2 = new ParkingSlot();
        slot2.setSlotNumber("A2");
        slot2.setAvailable(false);
        parkingSlotDao.insert(slot2);

        // Get available slots
        List<ParkingSlot> availableSlots = parkingSlotDao.getAvailableParkingSlots().getValue();
        assertNotNull(availableSlots);
        assertEquals(1, availableSlots.size());
        assertEquals("A1", availableSlots.get(0).getSlotNumber());
    }

    @Test
    public void deleteUser() {
        // Create and insert user
        User user = new User();
        user.setEmail("test@example.com");
        userDao.insert(user);

        // Delete user
        userDao.deleteByEmail("test@example.com");

        // Verify deletion
        User deletedUser = userDao.getUserByEmail("test@example.com");
        assertNull(deletedUser);
    }

    @Test
    public void getUserBookings() {
        // Create test data
        User user = new User();
        user.setEmail("test@example.com");
        userDao.insert(user);

        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber("A1");
        long slotId = parkingSlotDao.insert(slot);

        Booking booking1 = new Booking();
        booking1.setUserEmail("test@example.com");
        booking1.setSlotId((int) slotId);
        booking1.setStatus("CONFIRMED");
        bookingDao.insert(booking1);

        Booking booking2 = new Booking();
        booking2.setUserEmail("test@example.com");
        booking2.setSlotId((int) slotId);
        booking2.setStatus("PENDING");
        bookingDao.insert(booking2);

        // Get user bookings
        List<Booking> userBookings = bookingDao.getUserBookings("test@example.com").getValue();
        assertNotNull(userBookings);
        assertEquals(2, userBookings.size());
    }
}
