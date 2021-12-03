package uk.ac.gre.mf9669w.m_expense.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
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
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private ActivityMainBinding binding;
    private String nameOfPlace, destination, dateOfTrip, description;
    private boolean riskAssessment;
    private double latitude, longitude;
    private LocationRequest locationRequest;
    private DBHelper dbHelper;


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
            binding.saveDetailsBtn.setOnClickListener(v -> saveDetails());
            checkPermissions();
            if (isGPSEnabled()) {
                getCoordinates();
            } else {
                locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(5000);
                locationRequest.setFastestInterval(2000);

                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);
                builder.setAlwaysShow(true);

                Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                        .checkLocationSettings(builder.build());

                result.addOnCompleteListener(task -> {

                    try {
                        LocationSettingsResponse response = task.getResult(ApiException.class);
                        Toast.makeText(MainActivity.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();

                    } catch (ApiException e) {

                        switch (e.getStatusCode()) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                try {
                                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                    resolvableApiException.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException ex) {
                                    ex.printStackTrace();
                                }
                                break;

                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                //Device does not have location
                                break;
                        }
                    }
                });
            }
        } else {
            binding.nameEt.setText(trip.getNameOfPlace());
            binding.destinationEt.setText(trip.getDestination());
            binding.descriptionEt.setText(trip.getDescription());
            binding.dateEt.setText(trip.getDateOfTrip());
            binding.latitudeEt.setText(String.valueOf(trip.getLatitude()));
            binding.longitudeEt.setText(String.valueOf(trip.getLongitude()));
            if (trip.isRiskAssessment()) {
                binding.yes.setSelected(true);
            } else {
                binding.no.setSelected(true);
            }
            binding.saveDetailsBtn.setText("Update");
            binding.saveDetailsBtn.setOnClickListener(v -> {
                updateDetails(trip);
            });
        }

    }

    private void updateDetails(Trip tr) {
        nameOfPlace = Objects.requireNonNull(binding.nameEt.getText()).toString().trim();
        destination = Objects.requireNonNull(binding.destinationEt.getText()).toString().trim();
        dateOfTrip = Objects.requireNonNull(binding.dateEt.getText()).toString().trim();
        description = Objects.requireNonNull(binding.descriptionEt.getText()).toString().trim();
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
                    Trip trip = new Trip(tr.getId(), nameOfPlace, destination, dateOfTrip, description, riskAssessment, latitude, longitude, tr.getExpenses());
                    boolean status = dbHelper.updateTrip(trip);
                    if (status)
                        Snackbar.make(binding.getRoot(), "Record updated successfully", Snackbar.LENGTH_LONG).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();
                }
            }
        }
    }

    private void saveDetails() {
        nameOfPlace = Objects.requireNonNull(binding.nameEt.getText()).toString().trim();
        destination = Objects.requireNonNull(binding.destinationEt.getText()).toString().trim();
        dateOfTrip = Objects.requireNonNull(binding.dateEt.getText()).toString().trim();
        description = Objects.requireNonNull(binding.descriptionEt.getText()).toString().trim();
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
                    Trip trip = new Trip(0, nameOfPlace, destination, dateOfTrip, description, riskAssessment, latitude, longitude, new ArrayList<>());
                    boolean status = dbHelper.insertTrip(trip);
                    if (status)
                        Snackbar.make(binding.getRoot(), "Record inserted successfully", Snackbar.LENGTH_LONG).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();
                    Retrofit helper = new RetrofitHelper().getRetrofit("https://stuiis.cms.gre.ac.uk/COMP1424CoreWS/comp1424cw");
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

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("M-Expenses")
                        .setMessage("This permission is necessary")
                        .setPositiveButton("OK", (dialog, which) -> {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                getCoordinates();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200 && Arrays.equals(permissions, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}) && grantResults.length > 0) {
            getCoordinates();
        }
    }

    @SuppressLint("MissingPermission")
    public void getCoordinates() {
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);
                        if (locationResult.getLocations().size() > 0) {
                            int index = locationResult.getLocations().size() - 1;
                            latitude = locationResult.getLocations().get(index).getLatitude();
                            longitude = locationResult.getLocations().get(index).getLongitude();
                            binding.latitudeEt.setText(String.valueOf(latitude));
                            binding.longitudeEt.setText(String.valueOf(longitude));
                        }
                    }
                }, Looper.getMainLooper());
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
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
            boolean status = dbHelper.deleteAllData();
            if (status)
                Snackbar.make(binding.getRoot(), "Records deleted successfully", Snackbar.LENGTH_LONG).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();
        }
        return true;
    }
}