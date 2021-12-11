package uk.ac.gre.mf9669w.m_expense.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import uk.ac.gre.mf9669w.m_expense.adapters.TripsAdapter;
import uk.ac.gre.mf9669w.m_expense.database.DBHelper;
import uk.ac.gre.mf9669w.m_expense.databinding.ActivityTripsBinding;
import uk.ac.gre.mf9669w.m_expense.models.Trip;

public class TripsActivity extends AppCompatActivity {
    private ActivityTripsBinding binding;
    private List<Trip> trips;
    private TripsAdapter adapter;
    private String criteria;
    private final List<Trip>search = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTripsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DBHelper dbHelper = new DBHelper(this);
        binding.tripsRecycler.setHasFixedSize(true);
        trips = dbHelper.getTrips();
        populateRecycler(trips);
        List<String> list =new ArrayList<>();
        list.add("Name");
        list.add("Date");
        list.add("Description");
        list.add("Risk");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,list);
        binding.spinnerTrips.setAdapter(arrayAdapter);
        binding.spinnerTrips.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                criteria = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.searchTripBtn.setOnClickListener(v -> {
            String term = Objects.requireNonNull(binding.searchEt.getText()).toString().trim();
            switch (criteria) {
                case "Name":
                    search.clear();
                    for (Trip ex : trips) {
                        if (ex.getNameOfPlace().toLowerCase(Locale.ROOT).contains(term)) {
                            search.add(ex);
                        }
                    }
                    break;
                case "Date":
                    search.clear();
                    for (Trip ex : trips) {
                        if (ex.getDateOfTrip().toLowerCase(Locale.ROOT).contains(term)) {
                            search.add(ex);
                        }
                    }
                    break;
                case "Description":
                    search.clear();
                    for (Trip ex : trips) {
                        if (ex.getDescription().toLowerCase(Locale.ROOT).contains(term)) {
                            search.add(ex);
                        }
                    }
                    break;
                case "Risk":
                    search.clear();
                    for (Trip ex : trips) {
                        if (ex.isRiskAssessment()) {
                            search.add(ex);
                        }
                    }
                    break;
            }
            binding.tripsRecycler.setHasFixedSize(true);
            adapter = new TripsAdapter(search,this);
            binding.tripsRecycler.setAdapter(adapter);
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void populateRecycler(List<Trip> trips) {
        adapter = new TripsAdapter(trips,this);
        binding.tripsRecycler.setAdapter(adapter);
    }
}