package uk.ac.gre.mf9669w.m_expense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.ac.gre.mf9669w.m_expense.R;
import uk.ac.gre.mf9669w.m_expense.databinding.TripItemBinding;
import uk.ac.gre.mf9669w.m_expense.models.Trip;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.TripsViewHolder> {
    private final List<Trip> trips;
    private OnItemLongClick listener;
    private OnItemClick click;

    public interface OnItemLongClick {
        void onItemLongClick(int position);
    }

    public interface OnItemClick{
        void onItemClick(int position);
    }

    public void setOnItemLongClickListener(OnItemLongClick listener) {
        this.listener = listener;
    }
    public void setOnItemClickListener(OnItemClick click){
        this.click = click;
    }

    public TripsAdapter(List<Trip> trips) {
        this.trips = trips;
    }

    @NonNull
    @Override
    public TripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TripsViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_item, parent, false),
                listener,click
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TripsViewHolder holder, int position) {
        holder.bind(trips.get(position));
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public static class TripsViewHolder extends RecyclerView.ViewHolder {
        private final TripItemBinding binding;

        public TripsViewHolder(View itemView, OnItemLongClick listener,OnItemClick click) {
            super(itemView);
            binding = TripItemBinding.bind(itemView);
            binding.getRoot().setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(position);
                }
                return false;
            });
            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position!=RecyclerView.NO_POSITION){
                    click.onItemClick(position);
                }
            });

        }

        public void bind(Trip trip) {
            binding.titleTv.setText(trip.getNameOfPlace());
            binding.destinationTv.setText(String.format("Destination: %s", trip.getDestination()));
            binding.coordsTv.setText(String.format("Lat: %s\nLong: %s", trip.getLatitude(), trip.getLongitude()));
            binding.descriptionTv.setText(trip.getDescription());
            binding.dateTv.setText(trip.getDateOfTrip());
        }
    }
}
