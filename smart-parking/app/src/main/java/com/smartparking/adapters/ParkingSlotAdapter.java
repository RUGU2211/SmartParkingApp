package com.smartparking.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.smartparking.R;
import com.smartparking.models.ParkingSlot;

import java.text.NumberFormat;
import java.util.Locale;

public class ParkingSlotAdapter extends ListAdapter<ParkingSlot, ParkingSlotAdapter.ParkingSlotViewHolder> {
    
    private final OnSlotClickListener listener;
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    public interface OnSlotClickListener {
        void onBookClick(ParkingSlot slot);
        void onSlotClick(ParkingSlot slot);
    }

    public ParkingSlotAdapter(OnSlotClickListener listener) {
        super(new DiffUtil.ItemCallback<ParkingSlot>() {
            @Override
            public boolean areItemsTheSame(@NonNull ParkingSlot oldItem, @NonNull ParkingSlot newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull ParkingSlot oldItem, @NonNull ParkingSlot newItem) {
                return oldItem.isAvailable() == newItem.isAvailable() &&
                       oldItem.getPrice() == newItem.getPrice() &&
                       oldItem.getLocation().equals(newItem.getLocation());
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public ParkingSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_parking_slot, parent, false);
        return new ParkingSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingSlotViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ParkingSlotViewHolder extends RecyclerView.ViewHolder {
        private final TextView slotNumberText;
        private final TextView availabilityChip;
        private final TextView locationText;
        private final TextView priceText;
        private final TextView distanceText;
        private final MaterialButton bookButton;

        ParkingSlotViewHolder(View itemView) {
            super(itemView);
            slotNumberText = itemView.findViewById(R.id.slotNumberText);
            availabilityChip = itemView.findViewById(R.id.availabilityChip);
            locationText = itemView.findViewById(R.id.locationText);
            priceText = itemView.findViewById(R.id.priceText);
            distanceText = itemView.findViewById(R.id.distanceText);
            bookButton = itemView.findViewById(R.id.bookButton);
        }

        void bind(ParkingSlot slot) {
            slotNumberText.setText(String.format("Slot %s", slot.getSlotNumber()));
            
            // Set availability status
            if (slot.isAvailable()) {
                availabilityChip.setText(R.string.available);
                availabilityChip.setBackgroundResource(R.drawable.bg_availability_chip);
                bookButton.setEnabled(true);
            } else {
                availabilityChip.setText(R.string.occupied);
                availabilityChip.setBackgroundResource(R.drawable.bg_unavailable_chip);
                bookButton.setEnabled(false);
            }

            locationText.setText(slot.getLocation());
            priceText.setText(currencyFormat.format(slot.getPrice()) + "/hour");

            // Set distance if available
            if (slot.getLatitude() != 0 && slot.getLongitude() != 0) {
                // TODO: Calculate distance from current location
                distanceText.setVisibility(View.VISIBLE);
                distanceText.setText("2.5 km away"); // Placeholder
            } else {
                distanceText.setVisibility(View.GONE);
            }

            // Set click listeners
            itemView.setOnClickListener(v -> listener.onSlotClick(slot));
            bookButton.setOnClickListener(v -> listener.onBookClick(slot));
        }
    }

    public void updateSlotAvailability(int slotId, boolean isAvailable) {
        for (int i = 0; i < getCurrentList().size(); i++) {
            ParkingSlot slot = getCurrentList().get(i);
            if (slot.getId() == slotId) {
                slot.setAvailable(isAvailable);
                notifyItemChanged(i);
                break;
            }
        }
    }
}
