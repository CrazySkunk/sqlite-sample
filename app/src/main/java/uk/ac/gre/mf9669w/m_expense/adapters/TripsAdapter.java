package uk.ac.gre.mf9669w.m_expense.adapters;

import static com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_FADE;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import uk.ac.gre.mf9669w.m_expense.R;
import uk.ac.gre.mf9669w.m_expense.activities.DetailActivity;
import uk.ac.gre.mf9669w.m_expense.activities.MainActivity;
import uk.ac.gre.mf9669w.m_expense.database.DBHelper;
import uk.ac.gre.mf9669w.m_expense.databinding.ExpenseDialogBinding;
import uk.ac.gre.mf9669w.m_expense.databinding.TripItemBinding;
import uk.ac.gre.mf9669w.m_expense.models.Expense;
import uk.ac.gre.mf9669w.m_expense.models.Trip;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.TripsViewHolder> {
    private List<Trip> trips;
    private OnItemClick click;
    private final Context context;
    public DBHelper dbHelper;

    public interface OnItemClick{
        void onItemClick(int position);
    }
    public void setList(List<Trip>trips){
        this.trips=trips;
    }


    public TripsAdapter(List<Trip> trips, Context context) {
        this.trips = trips;
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public TripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TripsViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_item, parent, false),
                context,click
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TripsViewHolder holder, int position) {
        holder.bind(trips.get(position));
        holder.itemView.setOnClickListener(v -> {
            context.startActivity(new Intent(context, DetailActivity.class).putExtra("trip_extra", trips.get(position)));
        });
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public  class TripsViewHolder extends RecyclerView.ViewHolder {
         final TripItemBinding binding;
         private Trip trip;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public TripsViewHolder(View itemView, Context context, OnItemClick click) {
            super(itemView);
            binding = TripItemBinding.bind(itemView);
            binding.getRoot().setOnLongClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, binding.getRoot());
                popupMenu.getMenu().add("Edit");
                popupMenu.getMenu().add("Delete");
                popupMenu.getMenu().add("Add expense");
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().equals("Edit")) {
                        context.startActivity(new Intent(context, MainActivity.class).putExtra("trip", trip));
                    } else if (item.getTitle().equals("Delete")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("M-Expenses")
                                .setMessage("Are you sure you want to delete this trip?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    int status = dbHelper.deleteUser(trip.getId());
                                    if (status == trip.getId())
                                        Snackbar.make(binding.getRoot(), "Record deleted successfully", Snackbar.LENGTH_LONG).setAnimationMode(ANIMATION_MODE_FADE).show();
                                })
                                .setNegativeButton("No", (which, dialog) -> {

                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else if (item.getTitle().equals("Add expense")) {
                        View view = LayoutInflater.from(context).inflate(R.layout.expense_dialog, binding.getRoot(), false);
                        ExpenseDialogBinding expenseDialogBinding = ExpenseDialogBinding.bind(view);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                .setView(expenseDialogBinding.getRoot());
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        expenseDialogBinding.typeOfExpenseEt.setOnFocusChangeListener((vue,hasFocus)->{
                            if (vue.getId()==R.id.type_of_expense_et && hasFocus){
                                PopupMenu popup = new PopupMenu(context,expenseDialogBinding.typeOfExpenseEt);
                                popup.getMenu().add("Travel");
                                popup.getMenu().add("Food");
                                popup.getMenu().add("Other");
                                popup.setOnMenuItemClickListener(item1 -> {
                                    if (item1.getTitle().equals("Travel")){
                                        expenseDialogBinding.typeOfExpenseEt.setText("Travel");
                                    }else if (item1.getTitle().equals("Food")){
                                        expenseDialogBinding.typeOfExpenseEt.setText("Travel");
                                    }else {
                                        expenseDialogBinding.typeOfExpenseEt.setText("Other");
                                    }
                                    return true;
                                });
                                popup.show();
                            }
                        });
                        expenseDialogBinding.timeOfExpenseEt.setOnFocusChangeListener((vie, hasFocus) -> {
                            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                                    (view1, hourOfDay, minute) ->
                                            expenseDialogBinding.timeOfExpenseEt.setText(String.format(Locale.getDefault(), "%d:%d", hourOfDay, minute)), Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);
                            timePickerDialog.create();
                            timePickerDialog.show();
                        });
                        expenseDialogBinding.button.setOnClickListener(vi -> {
                            String typeOfExpense = Objects.requireNonNull(expenseDialogBinding.typeOfExpenseEt.getText()).toString().trim();
                            String amountOfExpense = Objects.requireNonNull(expenseDialogBinding.amountOfExpenseEt.getText()).toString().trim();
                            String timeOfExpense = Objects.requireNonNull(expenseDialogBinding.timeOfExpenseEt.getText()).toString().trim();
                            Expense expense = new Expense(0,trip.getId(), typeOfExpense, amountOfExpense, timeOfExpense);
                            boolean status = dbHelper.addExpense(expense);
                            if (status)
                                Snackbar.make(binding.getRoot(), "Record updated successfully", Snackbar.LENGTH_LONG).setAnimationMode(ANIMATION_MODE_FADE).show();
                            expenseDialogBinding.typeOfExpenseEt.setText("");
                            expenseDialogBinding.amountOfExpenseEt.setText("");
                            expenseDialogBinding.timeOfExpenseEt.setText("");
                            alertDialog.dismiss();
                        });
                    }
                    return true;
                });
                popupMenu.show();
                return false;
            });
        }

        public void bind(Trip trip) {
            this.trip =trip;
            binding.titleTv.setText(trip.getNameOfPlace());
            binding.destinationTv.setText(String.format("Destination: %s", trip.getDestination()));
            binding.coordsTv.setText(String.format("Start: %s\nEnd: %s", trip.getStartTime(), trip.getEndTime()));
            binding.descriptionTv.setText(trip.getDescription());
            binding.dateTv.setText(trip.getDateOfTrip());
        }
    }
}
