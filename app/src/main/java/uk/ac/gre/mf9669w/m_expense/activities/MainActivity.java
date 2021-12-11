package uk.ac.gre.mf9669w.m_expense.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import uk.ac.gre.mf9669w.m_expense.R;
import uk.ac.gre.mf9669w.m_expense.database.DBHelper;
import uk.ac.gre.mf9669w.m_expense.databinding.ActivityMainBinding;
import uk.ac.gre.mf9669w.m_expense.models.Trip;
import uk.ac.gre.mf9669w.m_expense.retrofit.IPostTrip;
import uk.ac.gre.mf9669w.m_expense.retrofit.RetrofitHelper;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private String nameOfPlace, destination, dateOfTrip, description, startTime, endTime;
    private boolean riskAssessment;
    private DBHelper dbHelper;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dbHelper = new DBHelper(this);
        Intent intent = getIntent();
        Trip trip = intent.getParcelableExtra("trip");
        if (trip == null) {
            binding.dateEt.setOnFocusChangeListener((v, hasFocus) -> {
                if (v.getId() == R.id.date_et && hasFocus) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this);
                    datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) ->
                            binding.dateEt.setText(String.format(Locale.getDefault(), "%d-%d-%d", dayOfMonth, month, year)));
                    datePickerDialog.create();
                    datePickerDialog.show();
                }
            });
            binding.startAtEt.setOnFocusChangeListener((view, hasFocus) -> {
                if (view.getId() == R.id.start_at_et && hasFocus) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view12, hourOfDay, minute) ->
                            binding.startAtEt.setText(String.format(Locale.getDefault(), "%d:%d", hourOfDay, minute)),
                            Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);
                    timePickerDialog.show();
                }
            });
            binding.endTimeEt.setOnFocusChangeListener((view, hasFocus) -> {
                if (view.getId() == R.id.end_time_et && hasFocus) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view13, hourOfDay, minute) ->
                            binding.endTimeEt.setText(String.format(Locale.getDefault(), "%d:%d", hourOfDay, minute)),
                            Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);
                    timePickerDialog.show();
                }
            });
            binding.saveDetailsBtn.setOnClickListener(v -> saveDetails());
        } else {
            binding.nameEt.setText(trip.getNameOfPlace());
            binding.destinationEt.setText(trip.getDestination());
            binding.descriptionEt.setText(trip.getDescription());
            binding.dateEt.setText(trip.getDateOfTrip());
            binding.startAtEt.setText(String.valueOf(trip.getStartTime()));
            binding.endTimeEt.setText(String.valueOf(trip.getEndTime()));
            if (binding.dateEt.hasFocus()) {
                binding.dateEt.setOnFocusChangeListener((v, hasFocus) -> {
                    if (v.getId() == R.id.date_et && hasFocus) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this);
                        datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) ->
                                binding.dateEt.setText(String.format(Locale.getDefault(), "%d-%d-%d", dayOfMonth, month, year)));
                        datePickerDialog.create();
                        datePickerDialog.show();
                    }
                });
            }
            if (trip.isRiskAssessment()) {
                binding.yes.setSelected(true);
            } else {
                binding.no.setSelected(true);
            }
            binding.saveDetailsBtn.setText(R.string.update);
            binding.saveDetailsBtn.setOnClickListener(v -> updateDetails(trip));
        }

    }

    private void updateDetails(Trip tr) {
        nameOfPlace = Objects.requireNonNull(binding.nameEt.getText()).toString().trim();
        destination = Objects.requireNonNull(binding.destinationEt.getText()).toString().trim();
        dateOfTrip = Objects.requireNonNull(binding.dateEt.getText()).toString().trim();
        description = Objects.requireNonNull(binding.descriptionEt.getText()).toString().trim();
        startTime = Objects.requireNonNull(binding.startAtEt.getText()).toString().trim();
        endTime = Objects.requireNonNull(binding.endTimeEt.getText()).toString().trim();
        if (nameOfPlace.isEmpty()) {
            binding.nameEtLayout.setError("Cannot be empty");
        } else {
            if (destination.isEmpty()) {
                binding.destinationEtLayout.setError("Cannot be empty");
            } else {
                if (dateOfTrip.isEmpty()) {
                    binding.dateEtLayout.setError("Cannot be empty");
                } else {
                    binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> riskAssessment = checkedId == binding.yes.getId());
                    Trip trip = new Trip(tr.getId(), nameOfPlace, destination, dateOfTrip, description, riskAssessment, startTime, startTime);
                    boolean status = dbHelper.updateTrip(trip);
                    if (status)
                        Snackbar.make(binding.getRoot(), "Record updated successfully", Snackbar.LENGTH_LONG).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();
                    binding.nameEt.setText("");
                    binding.destinationEt.setText("");
                    binding.dateEt.setText("");
                    binding.descriptionEt.setText("");
                    binding.startAtEt.setText("");
                    binding.endTimeEt.setText("");
                }
            }
        }
    }

    private void saveDetails() {
        nameOfPlace = Objects.requireNonNull(binding.nameEt.getText()).toString().trim();
        destination = Objects.requireNonNull(binding.destinationEt.getText()).toString().trim();
        dateOfTrip = Objects.requireNonNull(binding.dateEt.getText()).toString().trim();
        description = Objects.requireNonNull(binding.descriptionEt.getText()).toString().trim();
        startTime = Objects.requireNonNull(binding.startAtEt.getText()).toString().trim();
        endTime = Objects.requireNonNull(binding.endTimeEt.getText()).toString().trim();
        if (nameOfPlace.isEmpty()) {
            binding.nameEtLayout.setError("Cannot be empty");
        } else {
            if (destination.isEmpty()) {
                binding.destinationEtLayout.setError("Cannot be empty");
            } else {
                if (dateOfTrip.isEmpty()) {
                    binding.dateEtLayout.setError("Cannot be empty");
                } else {
                    int checked = binding.radioGroup.getCheckedRadioButtonId();
                    riskAssessment = checked == R.id.yes;
                    Trip trip = new Trip(0, nameOfPlace, destination, dateOfTrip, description, riskAssessment, startTime, endTime);
                    boolean status = dbHelper.insertTrip(trip);
                    if (status)
                        Snackbar.make(binding.getRoot(), "Record inserted successfully", Snackbar.LENGTH_LONG).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();
                    binding.nameEt.setText("");
                    binding.destinationEt.setText("");
                    binding.dateEt.setText("");
                    binding.descriptionEt.setText("");
                    binding.startAtEt.setText("");
                    binding.endTimeEt.setText("");
                    Retrofit helper = new RetrofitHelper().getRetrofit("https://stuiis.cms.gre.ac.uk/COMP1424CoreWS/");
                    IPostTrip service = helper.create(IPostTrip.class);
                    service.postTrip(trip).enqueue(new Callback<Trip>() {
                        @Override
                        public void onResponse(@NonNull Call<Trip> call, @NonNull Response<Trip> response) {
                            if (response.isSuccessful() && response.code() == 200) {
                                Snackbar.make(binding.getRoot(), "Record inserted successfully", Snackbar.LENGTH_LONG).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();
                            } else {
                                Snackbar.make(binding.getRoot(), "Error", Snackbar.LENGTH_LONG).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();

                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Trip> call, @NonNull Throwable t) {
                            Snackbar.make(binding.getRoot(), Objects.requireNonNull(t.getMessage()), Snackbar.LENGTH_LONG).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();
                        }
                    });

                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.view_trips) {
            startActivity(new Intent(this, TripsActivity.class));
        } else if (item.getItemId() == R.id.delete_all_data) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("M-Expenses");
            builder.setMessage("Are you sure you want to delete all data?");
            builder.setPositiveButton("Yes", ((dialog, which) -> {
                boolean status = dbHelper.deleteAllData();
                if (status)
                    Snackbar.make(binding.getRoot(), "Records deleted successfully", Snackbar.LENGTH_LONG).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();
            }));
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return true;
    }
}