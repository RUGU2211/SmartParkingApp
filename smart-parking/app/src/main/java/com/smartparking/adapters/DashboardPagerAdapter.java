package com.smartparking.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.smartparking.fragments.ListViewFragment;
import com.smartparking.fragments.MapViewFragment;

public class DashboardPagerAdapter extends FragmentStateAdapter {

    public DashboardPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the appropriate fragment based on position
        return position == 0 ? new ListViewFragment() : new MapViewFragment();
    }

    @Override
    public int getItemCount() {
        return 2; // List view and Map view
    }
}
