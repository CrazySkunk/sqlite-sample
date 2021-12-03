package uk.ac.gre.mf9669w.m_expense.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import uk.ac.gre.mf9669w.m_expense.adapters.ExpenseAdapter;
import uk.ac.gre.mf9669w.m_expense.databinding.ActivityDetailActivtyBinding;
import uk.ac.gre.mf9669w.m_expense.models.Trip;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailActivtyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailActivtyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Trip trip = getIntent().getParcelableExtra("trip_extra");
        if (trip != null) {
            binding.details.setText(String.format("Title: %s\nDestination: %s\nDate: %s", trip.getNameOfPlace(), trip.getDestination(), trip.getDateOfTrip()));
            binding.expenseRecycler.setHasFixedSize(true);
            ExpenseAdapter adapter = new ExpenseAdapter(trip.getExpenses());
            binding.expenseRecycler.setAdapter(adapter);
        }
    }
}