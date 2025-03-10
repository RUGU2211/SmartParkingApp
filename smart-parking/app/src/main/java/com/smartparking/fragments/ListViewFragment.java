package com.smartparking.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.smartparking.R;
import com.smartparking.activities.DashboardActivity;
import com.smartparking.adapters.ParkingSlotAdapter;
import com.smartparking.models.ParkingSlot;

import java.util.List;

public class ListViewFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ParkingSlotAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        
        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ParkingSlotAdapter((DashboardActivity) requireActivity());
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh parking slots
            if (getActivity() instanceof DashboardActivity) {
                ((DashboardActivity) getActivity()).loadParkingSlots();
            }
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    public void updateSlots(List<ParkingSlot> slots) {
        if (adapter != null) {
            adapter.submitList(slots);
        }
    }

    public void filterSlots(String query) {
        // TODO: Implement slot filtering
    }
}
