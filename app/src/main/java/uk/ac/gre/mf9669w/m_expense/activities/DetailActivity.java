package uk.ac.gre.mf9669w.m_expense.activities;

import static com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_FADE;

import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import uk.ac.gre.mf9669w.m_expense.R;
import uk.ac.gre.mf9669w.m_expense.adapters.ExpenseAdapter;
import uk.ac.gre.mf9669w.m_expense.database.DBHelper;
import uk.ac.gre.mf9669w.m_expense.databinding.ActivityDetailActivtyBinding;
import uk.ac.gre.mf9669w.m_expense.databinding.ExpenseDialogBinding;
import uk.ac.gre.mf9669w.m_expense.models.Expense;
import uk.ac.gre.mf9669w.m_expense.models.Trip;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailActivtyBinding binding;
    private DBHelper database;
    private String criteria;
    private List<Expense> expenses;
    private List<Expense> search;
    private ExpenseAdapter adapter;
    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailActivtyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = new DBHelper(this);
        trip = getIntent().getParcelableExtra("trip_extra");
        expenses = database.getTripExpenses(trip.getId());
        search = new ArrayList<>();
        binding.details.setText(String.format("Title: %s\nDestination: %s\nDate: %s", trip.getNameOfPlace(), trip.getDestination(), trip.getDateOfTrip()));

        if (trip != null) {
            binding.expenseRecycler.setHasFixedSize(true);
            ExpenseAdapter adapter = new ExpenseAdapter(expenses,this);
            binding.expenseRecycler.setAdapter(adapter);
        }
        List<String> list =new ArrayList<>();
        list.add("Name");
        list.add("Date");
        list.add("Amount");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,list);
        binding.spinnerExpense.setAdapter(arrayAdapter);
        binding.spinnerExpense.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                criteria = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.searchExpenseBtn.setOnClickListener(v -> {
            String term = Objects.requireNonNull(binding.searchByEt.getText()).toString().trim();
            if (criteria.equals("Name")) {
                for (Expense ex : expenses) {
                    if (ex.getTypeOfExpense().contains(term)) {
                        search.add(ex);
                    }
                }
            } else if (criteria.equals("Date")) {
                for (Expense ex : expenses) {
                    if (ex.getTimeOfExpense().contains(term)) {
                        search.add(ex);
                    }
                }
            } else {
                for (Expense ex : expenses) {
                    if (ex.getAmountOfExpense().contains(term)) {
                        search.add(ex);
                    }
                }
            }
            binding.expenseRecycler.setHasFixedSize(true);
            adapter = new ExpenseAdapter(expenses,this);
            binding.expenseRecycler.setAdapter(adapter);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_all_expense) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("M Expenses")
                    .setMessage("Are you sure you want to delete all expense?")
                    .setPositiveButton("Ok", (dialog, which) -> {
                        boolean status = database.deleteAllExpense();
                        if (status)
                            Snackbar.make(binding.getRoot(), "All expenses deleted successfully", Snackbar.LENGTH_LONG).setAnimationMode(ANIMATION_MODE_FADE).show();
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else if (item.getItemId() == R.id.add_expense) {
            View view = LayoutInflater.from(this).inflate(R.layout.expense_dialog, binding.getRoot(), false);
            ExpenseDialogBinding expenseDialogBinding = ExpenseDialogBinding.bind(view);
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setView(expenseDialogBinding.getRoot());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            expenseDialogBinding.typeOfExpenseEt.setOnFocusChangeListener((vue, hasFocus) -> {
                if (vue.getId() == R.id.type_of_expense_et && hasFocus) {
                    android.widget.PopupMenu popup = new android.widget.PopupMenu(this, expenseDialogBinding.typeOfExpenseEt);
                    popup.getMenu().add("Travel");
                    popup.getMenu().add("Food");
                    popup.getMenu().add("Other");
                    popup.setOnMenuItemClickListener(item1 -> {
                        if (item1.getTitle().equals("Travel")) {
                            expenseDialogBinding.typeOfExpenseEt.setText("Travel");
                        } else if (item1.getTitle().equals("Food")) {
                            expenseDialogBinding.typeOfExpenseEt.setText("Travel");
                        } else {
                            expenseDialogBinding.typeOfExpenseEt.setText("Other");
                        }
                        return true;
                    });
                    popup.show();
                }
            });
            expenseDialogBinding.timeOfExpenseEt.setOnFocusChangeListener((vie, hasFocus) -> {
                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        (view1, hourOfDay, minute) ->
                                expenseDialogBinding.timeOfExpenseEt.setText(String.format(Locale.getDefault(), "%d:%d", hourOfDay, minute)), Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);
                timePickerDialog.create();
                timePickerDialog.show();
            });
            expenseDialogBinding.button.setOnClickListener(vi -> {
                String typeOfExpense = Objects.requireNonNull(expenseDialogBinding.timeOfExpenseEt.getText()).toString().trim();
                String amountOfExpense = Objects.requireNonNull(expenseDialogBinding.amountOfExpenseEt.getText()).toString().trim();
                String timeOfExpense = Objects.requireNonNull(expenseDialogBinding.timeOfExpenseEt.getText()).toString().trim();
                Expense expense = new Expense(0, trip.getId(), typeOfExpense, amountOfExpense, timeOfExpense);
                boolean status = database.addExpense(expense);
                if (status)
                    Snackbar.make(binding.getRoot(), "Record updated successfully", Snackbar.LENGTH_LONG).setAnimationMode(ANIMATION_MODE_FADE).show();
                expenseDialogBinding.typeOfExpenseEt.setText("");
                expenseDialogBinding.amountOfExpenseEt.setText("");
                expenseDialogBinding.timeOfExpenseEt.setText("");
                alertDialog.dismiss();
            });
        }
        return true;
    }
}