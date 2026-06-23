package com.prayertimes.app.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prayertimes.app.R;
import com.prayertimes.app.location.CityDatabase;
import com.prayertimes.app.location.CityEntry;
import com.prayertimes.app.utils.AppPreferences;

import java.util.List;

/**
 * Lets the user search for any city by name and set it as their location manually.
 * Fully offline — queries the bundled city database (no internet needed).
 */
public class LocationSearchActivity extends AppCompatActivity
        implements CityResultAdapter.OnCityClickListener {

    public static final String RESULT_REFRESHED = "location_refreshed";

    private EditText         etSearch;
    private RecyclerView     rvResults;
    private ProgressBar      progress;
    private TextView         tvEmpty;
    private CityResultAdapter adapter;
    private CityDatabase     cityDatabase;
    private AppPreferences   prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("تحديد الموقع");
        }

        prefs        = new AppPreferences(this);
        cityDatabase = new CityDatabase();

        bindViews();
        setupRecyclerView();
        setupSearch();
        showCurrentLocation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }

    // ─── Bind ─────────────────────────────────────────────────────────────────

    private void bindViews() {
        etSearch  = findViewById(R.id.et_search);
        rvResults = findViewById(R.id.rv_results);
        progress  = findViewById(R.id.progress_search);
        tvEmpty   = findViewById(R.id.tv_empty);
    }

    private void setupRecyclerView() {
        adapter = new CityResultAdapter(this);
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(adapter);
    }

    private void showCurrentLocation() {
        if (prefs.isLocationSet()) {
            String loc = prefs.getLocationName();
            etSearch.setHint("Current: " + loc + "  —  search to change");
        }
    }

    // ─── Search ───────────────────────────────────────────────────────────────

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.length() >= 2) {
                    search(query);
                } else {
                    adapter.setResults(null);
                    tvEmpty.setVisibility(View.GONE);
                }
            }
        });
    }

    private void search(String query) {
        progress.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        // Run on background thread — database query can be slow for large sets
        new Thread(() -> {
            List<CityEntry> results = cityDatabase.search(query);
            runOnUiThread(() -> {
                progress.setVisibility(View.GONE);
                if (results.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("No cities found for \"" + query + "\"");
                } else {
                    tvEmpty.setVisibility(View.GONE);
                }
                adapter.setResults(results);
            });
        }).start();
    }

    // ─── City selected ───────────────────────────────────────────────────────

    @Override
    public void onCityClick(CityEntry city) {
        prefs.saveLocation(city.latitude, city.longitude, city.elevation, city.displayName());
        Toast.makeText(this,
            "Location set to " + city.displayName(), Toast.LENGTH_SHORT).show();

        // Tell MainActivity to refresh
        setResult(RESULT_OK);
        finish();
    }
}
