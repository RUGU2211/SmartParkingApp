package com.smartparking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.smartparking.R;
import com.smartparking.adapters.ParkingSlotAdapter;
import com.smartparking.database.AppDatabase;
import com.smartparking.database.dao.ParkingSlotDao;
import com.smartparking.models.ParkingSlot;
import com.smartparking.fragments.ListViewFragment;
import com.smartparking.fragments.MapViewFragment;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardActivity extends AppCompatActivity implements 
        ParkingSlotAdapter.OnSlotClickListener {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private View emptyView;
    private ProgressBar progressBar;
    private FloatingActionButton filterFab;
    
    private ParkingSlotDao parkingSlotDao;
    private LiveData<List<ParkingSlot>> parkingSlotsLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Room Database
        parkingSlotDao = AppDatabase.getInstance(this).parkingSlotDao();

        initializeViews();
        setupToolbar();
        setupViewPager();
        setupClickListeners();
        loadParkingSlots();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        emptyView = findViewById(R.id.emptyView);
        progressBar = findViewById(R.id.progressBar);
        filterFab = findViewById(R.id.filterFab);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.available_slots);
        }
    }

    private void setupViewPager() {
        DashboardPagerAdapter pagerAdapter = new DashboardPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "List View" : "Map View");
        }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                filterFab.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void setupClickListeners() {
        filterFab.setOnClickListener(v -> showFilterDialog());
        
        findViewById(R.id.refreshButton).setOnClickListener(v -> {
            loadParkingSlots();
        });
    }

    private void loadParkingSlots() {
        setLoading(true);
        
        // Check if there are any slots in the database
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            int totalSlots = parkingSlotDao.getTotalSlotsCount();
            
            if (totalSlots == 0) {
                // Add some dummy slots for testing
                addDummyParkingSlots();
            }
            
            // Observe parking slots
            runOnUiThread(() -> {
                parkingSlotsLiveData = parkingSlotDao.getAvailableParkingSlots();
                parkingSlotsLiveData.observe(this, slots -> {
                    setLoading(false);
                    updateUI(slots);
                });
            });
        });
    }

    private void updateUI(List<ParkingSlot> slots) {
        if (slots == null || slots.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            
            // Update fragments
            ListViewFragment listFragment = (ListViewFragment) 
                getSupportFragmentManager().findFragmentByTag("f0");
            if (listFragment != null) {
                listFragment.updateSlots(slots);
            }

            MapViewFragment mapFragment = (MapViewFragment) 
                getSupportFragmentManager().findFragmentByTag("f1");
            if (mapFragment != null) {
                mapFragment.updateSlots(slots);
            }
        }
    }

    private void addDummyParkingSlots() {
        ParkingSlot[] dummySlots = {
            new ParkingSlot("A1", "North Wing, Level 1", 5.00, 
                "Near main entrance", 37.7749, -122.4194),
            new ParkingSlot("A2", "North Wing, Level 1", 5.00, 
                "Near elevator", 37.7749, -122.4194),
            new ParkingSlot("B1", "South Wing, Level 2", 4.50, 
                "Near restrooms", 37.7749, -122.4194),
            new ParkingSlot("B2", "South Wing, Level 2", 4.50, 
                "Near exit", 37.7749, -122.4194)
        };

        for (ParkingSlot slot : dummySlots) {
            parkingSlotDao.insert(slot);
        }
    }

    private void showFilterDialog() {
        new MaterialAlertDialogBuilder(this)
            .setTitle(R.string.filter_slots)
            .setItems(new String[]{"Price: Low to High", "Price: High to Low", 
                "Distance: Nearest", "Distance: Farthest"}, (dialog, which) -> {
                // Handle filter selection
                filterSlots(which);
            })
            .show();
    }

    private void filterSlots(int filterType) {
        // TODO: Implement filtering logic
        Toast.makeText(this, "Filtering... (To be implemented)", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookClick(ParkingSlot slot) {
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra("slot_id", slot.getId());
        startActivity(intent);
    }

    @Override
    public void onSlotClick(ParkingSlot slot) {
        // Show slot details dialog
        new MaterialAlertDialogBuilder(this)
            .setTitle("Slot " + slot.getSlotNumber())
            .setMessage("Location: " + slot.getLocation() + "\n\n" +
                       "Description: " + slot.getDescription() + "\n\n" +
                       "Price: $" + String.format("%.2f", slot.getPrice()) + "/hour")
            .setPositiveButton(R.string.book_slot, (dialog, which) -> onBookClick(slot))
            .setNegativeButton(R.string.close, null)
            .show();
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        viewPager.setVisibility(isLoading ? View.GONE : 
            (emptyView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search text change
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_my_bookings:
                startActivity(new Intent(this, MyBookingsActivity.class));
                return true;
            case R.id.action_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_logout:
                showLogoutConfirmation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(this)
            .setTitle(R.string.logout)
            .setMessage(R.string.logout_confirmation)
            .setPositiveButton(R.string.logout, (dialog, which) -> {
                // TODO: Clear user session
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
                    Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            })
            .setNegativeButton(R.string.cancel, null)
            .show();
    }
}
