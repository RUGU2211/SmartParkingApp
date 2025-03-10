package com.smartparking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.button.MaterialButton;
import com.smartparking.R;
import com.smartparking.models.ParkingSlot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class ParkingSlotAdapterTest {

    private ParkingSlotAdapter adapter;
    private Context context;

    @Mock
    private ParkingSlotAdapter.OnSlotClickListener mockListener;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        MockitoAnnotations.initMocks(this);
        adapter = new ParkingSlotAdapter(mockListener);
    }

    @Test
    public void testAdapterInitialization() {
        assertNotNull(adapter);
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testSubmitList() {
        List<ParkingSlot> slots = createTestParkingSlots();
        adapter.submitList(slots);
        
        // Wait for async operation
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        assertEquals(slots.size(), adapter.getItemCount());
    }

    @Test
    public void testViewHolderBinding() {
        List<ParkingSlot> slots = createTestParkingSlots();
        adapter.submitList(slots);

        // Create ViewHolder
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_parking_slot, null, false);
        ParkingSlotAdapter.ParkingSlotViewHolder holder = 
                adapter.onCreateViewHolder(new RecyclerView.ViewGroup(context) {
                    @Override
                    protected void onLayout(boolean changed, int l, int t, int r, int b) {}
                }, 0);

        // Bind first slot
        adapter.onBindViewHolder(holder, 0);

        // Verify views are updated correctly
        TextView slotNumberText = itemView.findViewById(R.id.slotNumberText);
        TextView locationText = itemView.findViewById(R.id.locationText);
        TextView priceText = itemView.findViewById(R.id.priceText);
        MaterialButton bookButton = itemView.findViewById(R.id.bookButton);

        ParkingSlot firstSlot = slots.get(0);
        assertEquals("Slot " + firstSlot.getSlotNumber(), slotNumberText.getText().toString());
        assertEquals(firstSlot.getLocation(), locationText.getText().toString());
        assertTrue(bookButton.isEnabled());
    }

    @Test
    public void testUpdateSlotAvailability() {
        List<ParkingSlot> slots = createTestParkingSlots();
        adapter.submitList(slots);

        // Update first slot availability
        adapter.updateSlotAvailability(slots.get(0).getId(), false);

        // Create and bind ViewHolder
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_parking_slot, null, false);
        ParkingSlotAdapter.ParkingSlotViewHolder holder = 
                adapter.onCreateViewHolder(new RecyclerView.ViewGroup(context) {
                    @Override
                    protected void onLayout(boolean changed, int l, int t, int r, int b) {}
                }, 0);

        adapter.onBindViewHolder(holder, 0);

        // Verify book button is disabled
        MaterialButton bookButton = itemView.findViewById(R.id.bookButton);
        assertFalse(bookButton.isEnabled());
    }

    @Test
    public void testClickListeners() {
        List<ParkingSlot> slots = createTestParkingSlots();
        adapter.submitList(slots);

        // Create and bind ViewHolder
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_parking_slot, null, false);
        ParkingSlotAdapter.ParkingSlotViewHolder holder = 
                adapter.onCreateViewHolder(new RecyclerView.ViewGroup(context) {
                    @Override
                    protected void onLayout(boolean changed, int l, int t, int r, int b) {}
                }, 0);

        adapter.onBindViewHolder(holder, 0);

        // Simulate clicks
        itemView.performClick();
        verify(mockListener).onSlotClick(any(ParkingSlot.class));

        MaterialButton bookButton = itemView.findViewById(R.id.bookButton);
        bookButton.performClick();
        verify(mockListener).onBookClick(any(ParkingSlot.class));
    }

    private List<ParkingSlot> createTestParkingSlots() {
        List<ParkingSlot> slots = new ArrayList<>();

        ParkingSlot slot1 = new ParkingSlot();
        slot1.setId(1);
        slot1.setSlotNumber("A1");
        slot1.setLocation("North Wing");
        slot1.setPrice(5.00);
        slot1.setAvailable(true);
        slots.add(slot1);

        ParkingSlot slot2 = new ParkingSlot();
        slot2.setId(2);
        slot2.setSlotNumber("A2");
        slot2.setLocation("South Wing");
        slot2.setPrice(4.50);
        slot2.setAvailable(false);
        slots.add(slot2);

        return slots;
    }

    @Test
    public void testDiffUtil() {
        List<ParkingSlot> oldSlots = createTestParkingSlots();
        adapter.submitList(oldSlots);

        // Create new list with one modified item
        List<ParkingSlot> newSlots = new ArrayList<>(oldSlots);
        ParkingSlot modifiedSlot = newSlots.get(0);
        modifiedSlot.setPrice(6.00);
        modifiedSlot.setAvailable(false);

        adapter.submitList(newSlots);

        // Wait for async operation
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(newSlots.size(), adapter.getItemCount());
    }

    @Test
    public void testEmptyList() {
        adapter.submitList(null);
        assertEquals(0, adapter.getItemCount());

        adapter.submitList(new ArrayList<>());
        assertEquals(0, adapter.getItemCount());
    }
}
