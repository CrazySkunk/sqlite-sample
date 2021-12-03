package uk.ac.gre.mf9669w.m_expense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupMenu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;

import uk.ac.gre.mf9669w.m_expense.R;
import uk.ac.gre.mf9669w.m_expense.adapters.TripsAdapter;
import uk.ac.gre.mf9669w.m_expense.database.DBHelper;
import uk.ac.gre.mf9669w.m_expense.databinding.ActivityTripsBinding;
import uk.ac.gre.mf9669w.m_expense.databinding.ExpenseDialogBinding;
import uk.ac.gre.mf9669w.m_expense.models.Expense;
import uk.ac.gre.mf9669w.m_expense.models.Trip;

public class TripsActivity extends AppCompatActivity {
    private ActivityTripsBinding binding;
    private List<Trip> trips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTripsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DBHelper dbHelper = new DBHelper(this);
        binding.tripsRecycler.setHasFixedSize(true);
        trips = dbHelper.getTrips();
        TripsAdapter adapter = new TripsAdapter(trips);
        binding.tripsRecycler.setAdapter(adapter);
        adapter.setOnItemLongClickListener(position -> {
            PopupMenu popupMenu = new PopupMenu(TripsActivity.this, binding.tripsRecycler);
            popupMenu.getMenu().add("Edit");
            popupMenu.getMenu().add("Delete");
            popupMenu.getMenu().add("Add expense");
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Edit")) {
                    startActivity(new Intent(TripsActivity.this, MainActivity.class).putExtra("trip", trips.get(position)));
                } else if (item.getTitle().equals("Delete")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("M-Expenses")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                int status = dbHelper.deleteUser(trips.get(position).getId());
                                if (status == trips.get(position).getId())
                                    Snackbar.make(binding.getRoot(), "Record deleted successfully", Snackbar.LENGTH_LONG).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();
                            })
                            .setNegativeButton("No", (which, dialog) -> {

                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else if (item.getTitle().equals("Add expense")) {
                    View view = LayoutInflater.from(this).inflate(R.layout.expense_dialog, binding.getRoot(), false);
                    ExpenseDialogBinding expenseDialogBinding = ExpenseDialogBinding.bind(view);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(expenseDialogBinding.getRoot());
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    expenseDialogBinding.button.setOnClickListener(v -> {
                        String typeOfExpense = Objects.requireNonNull(expenseDialogBinding.timeOfExpenseEt.getText()).toString().trim();
                        String amountOfExpense = Objects.requireNonNull(expenseDialogBinding.amountOfExpenseEt.getText()).toString().trim();
                        String timeOfExpense = Objects.requireNonNull(expenseDialogBinding.timeOfExpenseEt.getText()).toString().trim();
                        Expense expense = new Expense(typeOfExpense, amountOfExpense, timeOfExpense);
                        Trip trip = trips.get(position);
                        trip.getExpenses().add(expense);
                        boolean status = dbHelper.updateTrip(trip);
                        if (status)
                            Snackbar.make(binding.getRoot(), "Record updated successfully", Snackbar.LENGTH_LONG).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();
                    });
                }
                return true;
            });
        });
        adapter.setOnItemClickListener(position ->
                startActivity(new Intent(TripsActivity.this, DetailActivity.class).putExtra("trip_extra", trips.get(position))));
    }
}