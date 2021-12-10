package uk.ac.gre.mf9669w.m_expense.activities;

import static com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_FADE;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
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
    private DBHelper dbHelper;
    private static final String TAG = "TripsActivity";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTripsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dbHelper = new DBHelper(this);
        binding.tripsRecycler.setHasFixedSize(true);
        trips = dbHelper.getTrips();
        populateRecycler(trips);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void populateRecycler(List<Trip> trips) {
        TripsAdapter adapter = new TripsAdapter(trips,this);
        binding.tripsRecycler.setAdapter(adapter);
//        adapter.setOnItemLongClickListener(position -> {
//
//            PopupMenu popupMenu = new PopupMenu(TripsActivity.this, binding.getRoot());
//            popupMenu.getMenu().add("Edit");
//            popupMenu.getMenu().add("Delete");
//            popupMenu.getMenu().add("Add expense");
//            popupMenu.setOnMenuItemClickListener(item -> {
//                if (item.getTitle().equals("Edit")) {
//                    startActivity(new Intent(TripsActivity.this, MainActivity.class).putExtra("trip", trips.get(position)));
//                } else if (item.getTitle().equals("Delete")) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setTitle("M-Expenses")
//                            .setMessage("Are you sure you want to delete this trip?")
//                            .setPositiveButton("Yes", (dialog, which) -> {
//                                int status = dbHelper.deleteUser(trips.get(position).getId());
//                                if (status == trips.get(position).getId())
//                                    Snackbar.make(binding.getRoot(), "Record deleted successfully", Snackbar.LENGTH_LONG).setAnimationMode(ANIMATION_MODE_FADE).show();
//                            })
//                            .setNegativeButton("No", (which, dialog) -> {
//
//                            });
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
//                } else if (item.getTitle().equals("Add expense")) {
//                    View view = LayoutInflater.from(this).inflate(R.layout.expense_dialog, binding.getRoot(), false);
//                    ExpenseDialogBinding expenseDialogBinding = ExpenseDialogBinding.bind(view);
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
//                            .setView(expenseDialogBinding.getRoot());
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
//                    expenseDialogBinding.timeOfExpenseEt.setOnFocusChangeListener((v, hasFocus) -> {
//                        TimePickerDialog timePickerDialog = new TimePickerDialog(TripsActivity.this,
//                                (view1, hourOfDay, minute) ->
//                                        expenseDialogBinding.timeOfExpenseEt.setText(String.format(Locale.getDefault(), "%d:%d", hourOfDay, minute)), Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);
//                        timePickerDialog.create();
//                        timePickerDialog.show();
//                    });
//                    expenseDialogBinding.button.setOnClickListener(v -> {
//                        String typeOfExpense = Objects.requireNonNull(expenseDialogBinding.timeOfExpenseEt.getText()).toString().trim();
//                        String amountOfExpense = Objects.requireNonNull(expenseDialogBinding.amountOfExpenseEt.getText()).toString().trim();
//                        String timeOfExpense = Objects.requireNonNull(expenseDialogBinding.timeOfExpenseEt.getText()).toString().trim();
//                        Expense expense = new Expense(0,String.valueOf(trips.get(position).getId()), typeOfExpense, amountOfExpense, timeOfExpense);
//                        Trip trip = trips.get(position);
//                        boolean status = dbHelper.addExpense(expense);
//                        if (status)
//                            Snackbar.make(binding.getRoot(), "Record updated successfully", Snackbar.LENGTH_LONG).setAnimationMode(ANIMATION_MODE_FADE).show();
//                        expenseDialogBinding.typeOfExpenseEt.setText("");
//                        expenseDialogBinding.amountOfExpenseEt.setText("");
//                        expenseDialogBinding.timeOfExpenseEt.setText("");
//                        alertDialog.dismiss();
//                    });
//                }
//                return true;
//            });
//            popupMenu.show();
//        });
//        adapter.setOnItemClickListener(position -> {
//            startActivity(new Intent(TripsActivity.this, DetailActivity.class).putExtra("trip_extra", trips.get(position)));
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trips_menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Search....");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onQueryTextChange(String newText) {
                List<Trip> searches = new ArrayList<>();
                for (Trip tr : trips) {
                    if (tr.getNameOfPlace().contains(newText) || tr.getDestination().contains(newText) || tr.getDescription().contains(newText) || tr.getDateOfTrip().contains(newText)) {
                        searches.add(tr);
                        populateRecycler(searches);
                    }
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return true;
    }
}