package com.smartparking.fragments;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smartparking.R;
import com.smartparking.models.ParkingSlot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class MapViewFragmentTest {

    private FragmentScenario<MapViewFragment> scenario;
    private final CountDownLatch mapReadyLatch = new CountDownLatch(1);
    private GoogleMap googleMap;

    @Before
    public void setup() {
        scenario = FragmentScenario.launchInContainer(MapViewFragment.class, new Bundle());
    }

    @Test
    public void testMapIsDisplayed() {
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void testLegendCardIsDisplayed() {
        onView(withId(R.id.legendCard)).check(matches(isDisplayed()));
    }

    @Test
    public void testInfoCardIsInitiallyHidden() {
        onView(withId(R.id.infoCard))
                .check(matches(ViewMatchers.withEffectiveVisibility(
                        ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void testUpdateSlots() throws InterruptedException {
        final List<ParkingSlot> testSlots = createTestParkingSlots();
        
        scenario.onFragment(fragment -> {
            // Wait for map to be ready
            SupportMapFragment mapFragment = (SupportMapFragment) fragment
                    .getChildFragmentManager()
                    .findFragmentById(R.id.map);
            
            assertNotNull(mapFragment);
            
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    googleMap = map;
                    mapReadyLatch.countDown();
                }
            });
            
            // Update slots after map is ready
            assertTrue(mapReadyLatch.await(10, TimeUnit.SECONDS));
            fragment.updateSlots(testSlots);
        });
    }

    @Test
    public void testFocusOnSlot() throws InterruptedException {
        final ParkingSlot testSlot = createTestParkingSlot();
        
        scenario.onFragment(fragment -> {
            // Wait for map to be ready
            SupportMapFragment mapFragment = (SupportMapFragment) fragment
                    .getChildFragmentManager()
                    .findFragmentById(R.id.map);
            
            assertNotNull(mapFragment);
            
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    googleMap = map;
                    mapReadyLatch.countDown();
                }
            });
            
            // Focus on slot after map is ready
            assertTrue(mapReadyLatch.await(10, TimeUnit.SECONDS));
            fragment.focusOnSlot(testSlot);
        });
    }

    private List<ParkingSlot> createTestParkingSlots() {
        List<ParkingSlot> slots = new ArrayList<>();
        
        ParkingSlot slot1 = new ParkingSlot();
        slot1.setSlotNumber("A1");
        slot1.setLocation("North Wing");
        slot1.setLatitude(37.7749);
        slot1.setLongitude(-122.4194);
        slot1.setAvailable(true);
        slots.add(slot1);
        
        ParkingSlot slot2 = new ParkingSlot();
        slot2.setSlotNumber("A2");
        slot2.setLocation("South Wing");
        slot2.setLatitude(37.7750);
        slot2.setLongitude(-122.4195);
        slot2.setAvailable(false);
        slots.add(slot2);
        
        return slots;
    }

    private ParkingSlot createTestParkingSlot() {
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber("B1");
        slot.setLocation("East Wing");
        slot.setLatitude(37.7751);
        slot.setLongitude(-122.4196);
        slot.setAvailable(true);
        return slot;
    }

    @Test
    public void testMarkerClick() throws InterruptedException {
        final ParkingSlot testSlot = createTestParkingSlot();
        
        scenario.onFragment(fragment -> {
            SupportMapFragment mapFragment = (SupportMapFragment) fragment
                    .getChildFragmentManager()
                    .findFragmentById(R.id.map);
            
            assertNotNull(mapFragment);
            
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    googleMap = map;
                    // Add marker
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(testSlot.getLatitude(), testSlot.getLongitude()))
                            .title("Slot " + testSlot.getSlotNumber());
                    map.addMarker(markerOptions);
                    mapReadyLatch.countDown();
                }
            });
            
            assertTrue(mapReadyLatch.await(10, TimeUnit.SECONDS));
        });
    }
}
