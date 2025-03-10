package com.smartparking.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smartparking.R;
import com.smartparking.activities.DashboardActivity;
import com.smartparking.models.ParkingSlot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Map<Marker, ParkingSlot> markerSlotMap = new HashMap<>();
    private static final LatLng DEFAULT_LOCATION = new LatLng(37.7749, -122.4194); // San Francisco
    private static final float DEFAULT_ZOOM = 15f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) 
            getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        
        // Configure map settings
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        
        // Move camera to default location
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
        
        // Set marker click listener
        googleMap.setOnMarkerClickListener(marker -> {
            ParkingSlot slot = markerSlotMap.get(marker);
            if (slot != null && getActivity() instanceof DashboardActivity) {
                ((DashboardActivity) getActivity()).onSlotClick(slot);
            }
            return true;
        });
    }

    public void updateSlots(List<ParkingSlot> slots) {
        if (googleMap == null) return;

        // Clear existing markers
        googleMap.clear();
        markerSlotMap.clear();

        // Add markers for each parking slot
        for (ParkingSlot slot : slots) {
            LatLng position = new LatLng(slot.getLatitude(), slot.getLongitude());
            
            MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title("Slot " + slot.getSlotNumber())
                .snippet(String.format("$%.2f/hour", slot.getPrice()))
                .icon(BitmapDescriptorFactory.defaultMarker(
                    slot.isAvailable() ? BitmapDescriptorFactory.HUE_GREEN : 
                        BitmapDescriptorFactory.HUE_RED));

            Marker marker = googleMap.addMarker(markerOptions);
            if (marker != null) {
                markerSlotMap.put(marker, slot);
            }
        }

        // If this is the first update, move camera to show all markers
        if (!slots.isEmpty()) {
            ParkingSlot firstSlot = slots.get(0);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(firstSlot.getLatitude(), firstSlot.getLongitude()), 
                DEFAULT_ZOOM));
        }
    }

    public void focusOnSlot(ParkingSlot slot) {
        if (googleMap != null) {
            LatLng position = new LatLng(slot.getLatitude(), slot.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));
        }
    }
}
