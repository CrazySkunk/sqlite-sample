package uk.ac.gre.mf9669w.m_expense.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import uk.ac.gre.mf9669w.m_expense.adapters.ExpenseAdapter;
import uk.ac.gre.mf9669w.m_expense.database.DBHelper;
import uk.ac.gre.mf9669w.m_expense.databinding.ActivityDetailActivtyBinding;
import uk.ac.gre.mf9669w.m_expense.models.Expense;
import uk.ac.gre.mf9669w.m_expense.models.Trip;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailActivtyBinding binding;
    private DBHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailActivtyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = new DBHelper(this);
        Trip trip = getIntent().getParcelableExtra("trip_extra");
        List<Expense> expenses = database.getTripExpenses(trip.getId());
        binding.details.setText(String.format("Title: %s\nDestination: %s\nDate: %s", trip.getNameOfPlace(), trip.getDestination(), trip.getDateOfTrip()));

        if (trip != null) {
            binding.expenseRecycler.setHasFixedSize(true);
            ExpenseAdapter adapter = new ExpenseAdapter(expenses);
            binding.expenseRecycler.setAdapter(adapter);
        }
    }
}